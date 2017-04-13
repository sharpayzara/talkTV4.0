package com.sumavision.talktv2.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.ui.activity.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by zjx on 2016/9/28.
 */
public class LiveCopyrightActivity extends BaseActivity {

    @BindView(R.id.live_web)
    WebView live_web;
    @BindView(R.id.channel_name)
    TextView channel_name;

    @Override
    protected int getLayoutResId() {
        return R.layout.activty_livecopyright;
    }

    @Override
    protected void initPresenter() {
        Intent intent =  getIntent();
        String url = intent.getStringExtra("liveurl");
//        String url = "http://tv.cctv.com/live/cctv1/";
//        live_web.getSettings().setJavaScriptEnabled(true);
        String channelName = intent.getStringExtra("channelName");
        channel_name.setText(channelName);
//        live_web.getSettings().setBuiltInZoomControls(true);
//        // 设置自适应屏幕的方法
//        live_web.getSettings().setUseWideViewPort(true);
        live_web.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        live_web.getSettings().setUseWideViewPort(true);

        live_web.getSettings().setJavaScriptEnabled(true);
        live_web.getSettings().setPluginState(WebSettings.PluginState.ON);
//        live_web.getSettings().setPluginsEnabled(true);//可以使用插件
        live_web.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        live_web.getSettings().setAllowFileAccess(true);
        live_web.getSettings().setDefaultTextEncodingName("UTF-8");
        live_web.getSettings().setLoadWithOverviewMode(true);
        live_web.loadUrl(url);
    }

    @OnClick(R.id.back)
    public void back() {
        finish();
    }

    @OnClick( R.id.back_btn)
    public void backPlay () {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        live_web.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
