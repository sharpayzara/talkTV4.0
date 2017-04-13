package com.sumavision.talktv2.ui.activity;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.networkbench.agent.impl.NBSAppAgent;
import com.pplive.sdk.PPTVSdkMgr;
import com.pplive.sdk.PPTVSdkParam;
import com.pplive.sdk.PPTVSdkStatusListener;
import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.ShareElement;
import com.sumavision.talktv2.presenter.SplashPresenter;
import com.sumavision.talktv2.ui.activity.base.BaseActivity;
import com.sumavision.talktv2.ui.iview.ISplashView;
import com.sumavision.talktv2.util.CommonUtil;
import com.sumavision.talktv2.util.NetworkUtil;
import com.sumavision.talktv2.util.PushUtils;

import java.io.File;

import butterknife.BindView;


public class SplashActivity extends BaseActivity<SplashPresenter> implements ISplashView {
    SplashPresenter presenter;
    @BindView(R.id.launch_iv)
    ImageView launchIv;
    @BindView(R.id.version_code)
    TextView versionCode;
    @BindView(R.id.logo_iv)
    ImageView logoIv;
    @BindView(R.id.img_rlt)
    RelativeLayout imgRlt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 填充标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        ShareElement.isFirst = true;
        if(NetworkUtil.isConnectedByState(this) && NetworkUtil.getCurrentNetworkType(this)== ConnectivityManager.TYPE_MOBILE) {
            ShareElement.isIgnoreNetChange = 1;
        }
        else
            ShareElement.isIgnoreNetChange = -1;
        NBSAppAgent.setLicenseKey("b2f9905ae5c54d7ca34b38a2ff8b9a16").withLocationServiceEnabled(true).start(this.getApplicationContext());
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.splash_layout;
    }

    @Override
    protected void initPresenter() {
        presenter = new SplashPresenter(this, this);
        presenter.init();
    }

    @Override
    protected void onDestroy() {
        if(bmp != null && !bmp.isRecycled()  ){
            bmp.recycle();  //回收图片所占的内存
             System.gc();//提醒系统及时回收
        }
        presenter.release();
        super.onDestroy();
    }

    //****************************************************************
    // 判断应用是否初次加载，读取SharedPreferences中的guide_activity字段
    //****************************************************************
    public static final String SHAREDPREFERENCES_NAME = "my_pref";
    public static final String KEY_GUIDE_ACTIVITY = "guide_activity";

    private boolean isFirstEnter() {
        String mResultStr = this.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE)
                .getString(KEY_GUIDE_ACTIVITY, "");    //取得所有类名 如 com.my.MainActivity
        if (mResultStr.equalsIgnoreCase("false"))
            return false;
        else
            return true;
    }


    //*************************************************
    // Handler:跳转至不同页面
    //*************************************************
    private final static int SWITCH_MAINACTIVITY = 1000;
    private final static int SWITCH_GUIDACTIVITY = 1001;
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SWITCH_MAINACTIVITY:
                    presenter.clearCacheData();
                    Intent mIntent = new Intent();
                    mIntent.setClass(SplashActivity.this, TVFANActivity.class);
                    SplashActivity.this.startActivity(mIntent);
                    SplashActivity.this.finish();
                    break;
                case SWITCH_GUIDACTIVITY:
                    SharedPreferences preferences = SplashActivity.this.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(KEY_GUIDE_ACTIVITY, "false");
                    editor.commit();
                    Intent intent = new Intent();
                    intent.setClass(SplashActivity.this, GuideActivity.class);
                    SplashActivity.this.startActivity(intent);
                    SplashActivity.this.finish();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public void doNext() {
        logoIv.setImageDrawable(getResources().getDrawable(R.drawable.anim_list));
            AnimationDrawable frameAnimation = (AnimationDrawable) logoIv.getDrawable();
            frameAnimation.start();
            int duration = 0;
            for(int i=0;i<frameAnimation.getNumberOfFrames();i++){
                duration += frameAnimation.getDuration(i);
            }
            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {

                public void run() {
                    if (!isFirstEnter())
                        mHandler.sendEmptyMessage(SWITCH_MAINACTIVITY);
                    else
                        mHandler.sendEmptyMessage(SWITCH_GUIDACTIVITY);
                }

            }, duration);
    }
    Bitmap bmp;
    @Override
    public void initView() {
        presenter.getActionUrl();
        versionCode.setText("当前版本:V " + CommonUtil.getAppVersionName(this));
        propertyValuesHolder();
        Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.visiable);
        File screenFile= new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS), ".screensaver.png");
        try{
            if(screenFile.exists()){
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true; //设置为true, 加载器不会返回图片, 而是设置Options对象中以out开头的字段.即仅仅解码边缘区域
                int imageWidth = opts.outWidth;
                int imageHeight = opts.outHeight;
              // 获取屏幕的宽和高
                Display display = this.getWindowManager().getDefaultDisplay(); // 获取默认窗体显示的对象
                 int screenWidth = display.getWidth();
                 int screenHeight = display.getHeight();
                // 计算缩放比例
                int widthScale = imageWidth / screenWidth;
                 int heightScale = imageHeight / screenHeight;
                 int scale = widthScale > heightScale ? widthScale:heightScale;
                   // 指定加载可以加载出图片.
                 opts.inJustDecodeBounds = false;
                 // 使用计算出来的比例进行缩放
                   opts.inSampleSize = scale;
                bmp = BitmapFactory.decodeFile(screenFile.getPath(), opts);
                launchIv.setImageBitmap(bmp);
               /* Drawable icon = Drawable.createFromPath(screenFile.getPath());
                icon.setBounds(0, 0, CommonUtil.screenWidth(this), CommonUtil.screenHeight(this));
//                Thread.sleep(200);
                launchIv.setImageDrawable(icon);*/
                logoIv.setVisibility(View.GONE);
                versionCode.setVisibility(View.GONE);
            } else {
//                Thread.sleep(200);
            }
        }catch (Exception ex){

        }
        versionCode.startAnimation(animation2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!PushUtils.hasBind(SplashActivity.this.getApplicationContext())) {
                    initWithApiKey();
                } else {
                    PushManager.resumeWork(SplashActivity.this.getApplicationContext());
                }
            }
        }).start();

        // PPTVsdk初始化
        String param = String.format("%s=%s",
                PPTVSdkParam.Config_Tunnel, "2435");
        PPTVSdkMgr.getInstance().init(this.getApplicationContext(), null, param, null, BaseApp.mListener);
        String serviceName = "com.pplive.sdk.service";
        boolean b = PPTVSdkMgr.getInstance().bindService(serviceName);
    }

    private void initWithApiKey() {
        PushManager.startWork(getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY,
                PushUtils.getMetaValue(this, "api_key"));
    }

    private PPTVSdkStatusListener mListener = new PPTVSdkStatusListener() {

        @Override
        public void onInitBegin() {
            Log.e("pptv_sdk", "onInitBegin");
        }

        @Override
        public void onInitEnd(int error) {
            Log.e("pptv_sdk", "onInitEnd: " + error);
        }

    };

    public void propertyValuesHolder() {
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f,
                1.03f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f,
                1.03f);
        ObjectAnimator animator= ObjectAnimator.ofPropertyValuesHolder(imgRlt, pvhY,pvhZ);
        animator.setDuration(2500).start();
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                doNext();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }



}