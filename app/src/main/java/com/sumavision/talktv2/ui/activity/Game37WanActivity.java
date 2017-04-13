package com.sumavision.talktv2.ui.activity;


import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.decor.AppInfoData;
import com.sumavision.talktv2.presenter.Game37WanPresenter;
import com.sumavision.talktv2.ui.activity.base.BaseActivity;
import com.sumavision.talktv2.ui.iview.ISplashView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class Game37WanActivity extends BaseActivity<Game37WanPresenter> implements ISplashView {
    Game37WanPresenter presenter;
    Handler mHandler;
    @BindView(R.id.wv_webview)
    WebView wv_webview;
    private List<AppInfoData> appList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);// 填充标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_37wangame;
    }

    @Override
    protected void initPresenter() {
        presenter = new Game37WanPresenter(this, this);
        presenter.init();
    }

    @Override
    protected void onDestroy() {
        mHandler = null;
        presenter.release();
        super.onDestroy();
        wv_webview.destroy();
    }


    @Override
    public void initView() {
        mHandler = new Handler();
        appList = new ArrayList<>();
        setCurrentAppInstalInfo();
        loadWeb();
    }

    private void loadWeb() {
        wv_webview.setVerticalScrollBarEnabled(false);
        wv_webview.setHorizontalScrollBarEnabled(false);
        wv_webview.requestFocus();

        //支持缩放
        WebSettings webSettings =   wv_webview .getSettings();
        webSettings.setJavaScriptEnabled(true);//加上这句话才能使用javascript方法
//        webSettings.setBuiltInZoomControls(true);//设置出现缩放工具
//        webSettings.setSupportZoom(true);//支持缩放
        //将图片调整到适合webview的大小
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //设置自适应屏幕
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        String url = getIntent().getStringExtra("url");
        wv_webview.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                //实现下载的代码
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });
        //此方法可以在webview中打开链接而不会跳转到外部浏览器
        wv_webview.setWebViewClient(new WebViewClient());
        //注入回调
        wv_webview.addJavascriptInterface(new JavaScriptInterface(),"robot");
        wv_webview.loadUrl(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按返回键时， 不退出程序而是返回上一浏览页面：
        if (keyCode == KeyEvent.KEYCODE_BACK && wv_webview.canGoBack()) {
            wv_webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setCurrentAppInstalInfo() {
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);

        for(int i=0;i<packages.size();i++) {
            PackageInfo packageInfo = packages.get(i);
            AppInfoData appInfo =new AppInfoData();
//            tmpInfo.appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
            appInfo.packageName = packageInfo.packageName;
//            tmpInfo.versionName = packageInfo.versionName;
            appInfo.version = packageInfo.versionCode;
//            tmpInfo.appIcon = packageInfo.applicationInfo.loadIcon(getPackageManager());
            //Only display the non-system app info
            if((packageInfo.applicationInfo.flags& ApplicationInfo.FLAG_SYSTEM)==0)
            {
                appList.add(appInfo);//如果非系统应用，则添加至appList
            }

        }
    }

    final class JavaScriptInterface  {
        JavaScriptInterface(){

        }
        //1.检测应用是否已安装接口 参数为包名
        @JavascriptInterface
        public int detextionAppInstalInfo(String packageName)  {
            int isInstal = 0;
            if (!TextUtils.isEmpty(packageName)){
                for (int i = 0 ;i<appList.size();i++){
                    if (appList.get(i).packageName.equals(packageName)){
                        isInstal =1;
                    }
                }
            }
            return isInstal;
        }
        //2.启动应用的方法,参数 包名
        @JavascriptInterface
        public void startApp(String packageName)  {
            Intent intent = new Intent();
            PackageManager packageManager =getPackageManager();
            intent = packageManager.getLaunchIntentForPackage(packageName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
            startActivity(intent);

        }
        //3.关闭webview界面的方法
        @JavascriptInterface
        public void closeWebView()  {
            mHandler.post(new Runnable() {
                public void run() {
//appView.loadUrl("javascript:wave()");
                    Game37WanActivity.this.finish();
                }
            });
        }
        //4.获取应用版本号的方法
        //如果返回-1,说明不存在;
        @JavascriptInterface
        public int getAppVersionCode(String packageName)  {
            int version = -1;
            for (int i = 0 ;i<appList.size();i++){
                if (appList.get(i).packageName.equals(packageName)){
                    version = appList.get(i).version;
                }
            }
            return version;
        }
    }


}
