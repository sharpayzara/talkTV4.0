package com.sumavision.talktv2.videoplayer.activity;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.ShareElement;
import com.sumavision.talktv2.ui.listener.PlayCompleteListener;
import com.sumavision.talktv2.videoplayer.IPlayerClient;
import com.sumavision.talktv2.videoplayer.view.VodPlayerVideoView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhangyisu on 2016/7/6.
 *
 * 离线缓存播放器
 */
public class CachePlayerActivity extends Activity implements IPlayerClient,PlayCompleteListener {

    @BindView(R.id.cache_player)
    VodPlayerVideoView player;
    String url;
    private String name;
    PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cache_player_activity);
        ButterKnife.bind(this);
        url = getIntent().getStringExtra("playUrl");
        name = getIntent().getStringExtra("programName");
        player.init(this, 0, 1);
        player.setProgramName(name);
        player.handleFullHalfScrren(ShareElement.LANDSCAPE);
        player.registerSensor();
        hideBottomUIMenu();
        player.setCachePlay(true);
        player.setOnBackListener(new VodPlayerVideoView.OnBackListener() {
            @Override
            public void onBack() {
                finish();
            }
        });
        player.startPlay(url);
        player.setCompleteListener(this);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "MyTag");
//        firstIn = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWakeLock.acquire();
        player.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.unRegisterSensor();
        player.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWakeLock.release();
        player.pause();
    }

    @Override
    public void halfFullScreenSwitch(int landscapeState) {
        if(landscapeState == ShareElement.LANDSCAPE){
            player.setBackPlay(true);
            player.setSLPlay(false);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else if(landscapeState == ShareElement.REVERSILANDSCAPE) {
            player.setBackPlay(true);
            player.setSLPlay(false);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }

    }

    @Override
    public AudioManager getAudioManager() {
        return (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void playComplete() {
       player.showReplay(true);
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    public void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

}
