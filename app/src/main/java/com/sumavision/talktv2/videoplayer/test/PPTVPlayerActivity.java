package com.sumavision.talktv2.videoplayer.test;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.pplive.player.PPTVPlayerStatusListener;
import com.pplive.sdk.PPTVSdkMgr;
import com.pplive.sdk.PPTVSdkParam;
import com.pplive.videoplayer.BasePlayerStatusListener;
import com.pplive.videoplayer.PPTVVideoView;
import com.sumavision.talktv2.R;

import java.net.URLEncoder;

/**
 * Created by zhangyisu on 2016/6/27.
 */
public class PPTVPlayerActivity extends Activity {
    private PPTVVideoView mVideoView;
    private int mStatus;
    private ImageView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pptv_activity);
        mVideoView = (PPTVVideoView) findViewById(R.id.video_view);
        adView = (ImageView) findViewById(R.id.ad_imageview);
        PPTVSdkMgr.getInstance().initVideoView(this, mVideoView, adView);
        PPTVSdkMgr.getInstance().setOnPlayerStatusListener(listener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                play();
            }
        }, 10000);
    }

    private void play() {
        if (mStatus == BasePlayerStatusListener.STATUS_PLAY_PAUSE) {
            PPTVSdkMgr.getInstance().resume();
            return;
        }
        String url = "pptv://code=u8EoMWK5xoaBvLJuW6utYtAzhQbllsBA&key=6";
        try {
            url = URLEncoder.encode(url, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        String param = String.format("%s=%s",
                PPTVSdkParam.Player_Encodeurl, url);

        try {
            PPTVSdkMgr.getInstance().play(this, param);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private BasePlayerStatusListener listener = new PPTVPlayerStatusListener();
}
