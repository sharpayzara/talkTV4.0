package com.sumavision.talktv2.ui.activity;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.activity.base.BaseActivity;
import com.sumavision.talktv2.ui.iview.base.IBaseView;
import com.sumavision.talktv2.ui.widget.VideoEnabledWebChromeClient;
import com.sumavision.talktv2.ui.widget.VideoEnabledWebView;

import butterknife.BindView;

import static com.sumavision.talktv2.R.id.webView;


public class WebVedioActivity extends BaseActivity<BasePresenter> implements IBaseView {
    BasePresenter presenter;
    @BindView(webView)
    VideoEnabledWebView wv_webview;
    @BindView(R.id.parent)
    LinearLayout parent;
    @BindView(R.id.videoLayout)
    FrameLayout videoLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);// 填充标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        super.onCreate(savedInstanceState);
    }


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_web_vedio;
    }

    @Override
    protected void initPresenter() {
        presenter = new BasePresenter(this, this) {
            @Override
            public void release() {

            }
        };
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
        wv_webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            }

            @Override
            public void onPageFinished(WebView view, String url) {
            }
        });
        wv_webview.setWebChromeClient(new VideoEnabledWebChromeClient(this, parent, videoLayout));
//load url
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
