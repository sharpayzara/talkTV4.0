package com.sumavision.talktv2.ui.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.presenter.WebADPresenter;
import com.sumavision.talktv2.ui.activity.base.BaseActivity;
import com.sumavision.talktv2.ui.iview.ISplashView;

import butterknife.BindView;


public class WeBADActivity extends BaseActivity<WebADPresenter> implements ISplashView {
    WebADPresenter presenter;
    @BindView(R.id.wv_webview)
    WebView wv_webview;
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
        presenter = new WebADPresenter(this, this);
        presenter.init();
    }

    @Override
    protected void onDestroy() {
        presenter.release();
        super.onDestroy();
        wv_webview.destroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wv_webview.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wv_webview.onResume();
    }

    @Override
    public void initView() {
        loadWeb();
    }

    private void loadWeb() {
        wv_webview.setVerticalScrollBarEnabled(false);
        wv_webview.setHorizontalScrollBarEnabled(false);
        wv_webview.requestFocus();
        WebSettings webSettings =   wv_webview .getSettings();
        //不设置该属性web界面所有的交互无法实现并且bannner图也无法正常表现
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        //设置自适应屏幕
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
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
//        wv_webview.setWebViewClient(new WebViewClient());
        wv_webview.setWebViewClient(new WebViewClient() {
            //这是针对跳转到直接添加qq群链接的特殊处理
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if( url.startsWith("http:") || url.startsWith("https:") ) {
                    return false;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
//  下面这一行保留的时候，原网页仍报错，新网页正常.所以注释掉后，也就没问题了
//          view.loadUrl(url);
                return true;
            }
        });
        //注入回调
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



}
