package com.sumavision.talktv2.ui.activity;


import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.ui.receiver.TANetChangeObserver;
import com.sumavision.talktv2.ui.receiver.TANetworkStateReceiver;
import com.sumavision.talktv2.ui.scanutill.CameraManager;
import com.sumavision.talktv2.ui.scanutill.CameraPreview;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.CommonUtil;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Created by zhoutao on 2016/12/14.
 */

public class NewScanActivity extends Activity {
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private CameraManager mCameraManager;

    private FrameLayout scanPreview;
    private RelativeLayout scanContainer;
    private RelativeLayout scanCropView;
    private ImageView scanLine;
    private TextView tv_scanps;
    private TANetChangeObserver taNetChangeObserver;

    private Rect mCropRect = null;
    private boolean barcodeScanned = false;
    private boolean previewing = true;
    private ImageScanner mImageScanner = null;

    static {
        System.loadLibrary("iconv");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        findViewById();
        initCurrentStateShow();
        netStateChangeListen();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initViews();
    }

    /**
     * 根据当前的网络状态设置相应显示
     */
    private void initCurrentStateShow() {
        if (!CommonUtil.isNetworkConnected(this)){
            //此时说明用户是没有网络的状态
            setPsInfoShow(true);
        }else if (!CommonUtil.getNetWorkType(this)){
            setPsInfoShow(true);
            tv_scanps.setText("此功能非wifi环境下无效");
        }
    }

    private void findViewById() {
        scanPreview = (FrameLayout) findViewById(R.id.capture_preview);
        tv_scanps = (TextView) findViewById(R.id.tv_scanps);
        scanContainer = (RelativeLayout) findViewById(R.id.capture_container);
        scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
        scanLine = (ImageView) findViewById(R.id.capture_scan_line);
    }
    /**
     * 进行流量管理；当网络变化时进行监听
     */
    private void netStateChangeListen() {
        if(taNetChangeObserver == null) {
            taNetChangeObserver = new TANetChangeObserver() {
                @Override
                public void onConnect(int type) {
                    if (type == ConnectivityManager.TYPE_WIFI){
                        setPsInfoShow(false);
                    }else{
                        setPsInfoShow(true);
                        tv_scanps.setText("此功能非wifi环境下无效");
                    }
                }

                @Override
                public void onDisConnect() {
                    setPsInfoShow(true);
                }
            };
        }
        TANetworkStateReceiver.registerObserver(taNetChangeObserver);
    }
    /**
     * 设置提示信息是否显示
     * @param isShow
     */
    private void setPsInfoShow(boolean isShow){
        if (isShow){
            tv_scanps.setVisibility(View. VISIBLE);
        }else{
            tv_scanps.setVisibility(View. GONE);
        }
    }



    private void initViews() {
        mImageScanner = new ImageScanner();
        mImageScanner.setConfig(0, Config.X_DENSITY, 3);
        mImageScanner.setConfig(0, Config.Y_DENSITY, 3);

        autoFocusHandler = new Handler();
        mCameraManager = new CameraManager(this);
        try {
            mCameraManager.openDriver();
        } catch (IOException e) {
            e.printStackTrace();
        }
            mCamera = mCameraManager.getCamera();
        if (mCamera != null){
            mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
            scanPreview.addView(mPreview);
        }

        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.85f);
        animation.setDuration(3000);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.REVERSE);
        scanLine.startAnimation(animation);
    }

    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
        TANetworkStateReceiver.removeRegisterObserver(taNetChangeObserver);
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Size size = camera.getParameters().getPreviewSize();

            // 这里需要将获取的data翻转一下，因为相机默认拿的的横屏的数据
            byte[] rotatedData = new byte[data.length];
            for (int y = 0; y < size.height; y++) {
                for (int x = 0; x < size.width; x++)
                    rotatedData[x * size.height + size.height - y - 1] = data[x
                            + y * size.width];
            }

            // 宽高也要调整
            int tmp = size.width;
            size.width = size.height;
            size.height = tmp;

            initCrop();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(rotatedData);
            barcode.setCrop(mCropRect.left, mCropRect.top, mCropRect.width(),
                    mCropRect.height());

            int result = mImageScanner.scanImage(barcode);
            String resultStr = null;

            if (result != 0) {
                SymbolSet syms = mImageScanner.getResults();
                for (Symbol sym : syms) {
                    resultStr = sym.getData();
                }
            }

            if (!TextUtils.isEmpty(resultStr)) {
                //这里获取到了二维码的字符串信息了
                BusProvider.getInstance().post("sendScanMesseage",resultStr);
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                barcodeScanned = true;
                if (!CommonUtil.isNetworkConnected(getApplicationContext())){
                    BusProvider.getInstance().post("scanNoNet","scanNoNet");
                }else if(!CommonUtil.getNetWorkType(getApplicationContext())){
                    BusProvider.getInstance().post("scan4G","scan4G");
                }else if (resultStr.contains(":38383/1.html")){
                    BusProvider.getInstance().post("scanSuccess","scanSuccess");
                }else{
                    BusProvider.getInstance().post("scanFaild","scanFaild");
                }
                finish();
            }
        }
    };


    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    /**
     * 初始化截取的矩形区域
     */
    private void initCrop() {
        int cameraWidth = mCameraManager.getCameraResolution().y;
        int cameraHeight = mCameraManager.getCameraResolution().x;

        /** 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        scanCropView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1] - getStatusBarHeight();

        int cropWidth = scanCropView.getWidth();
        int cropHeight = scanCropView.getHeight();

        /** 获取布局容器的宽高 */
        int containerWidth = scanContainer.getWidth();
        int containerHeight = scanContainer.getHeight();

        /** 计算最终截取的矩形的左上角顶点x坐标 */
        int x = cropLeft * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的左上角顶点y坐标 */
        int y = cropTop * cameraHeight / containerHeight;

        /** 计算最终截取的矩形的宽度 */
        int width = cropWidth * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的高度 */
        int height = cropHeight * cameraHeight / containerHeight;

        /** 生成最终的截取的矩形 */
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
