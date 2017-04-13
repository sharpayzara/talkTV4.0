package com.sumavision.talktv2.videoplayer.player.impl;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.pplive.sdk.PPTVSdkMgr;
import com.pplive.sdk.PPTVSdkParam;
import com.pplive.videoplayer.BasePlayerStatusListener;
import com.pplive.videoplayer.PPTVVideoView;
import com.sumavision.talktv2.videoplayer.PlayBean;
import com.sumavision.talktv2.videoplayer.player.IPlayer;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Zhangyisu on 2016/6/3.
 */
public class PPTVPlayer implements IPlayer {

    Context context;
    ViewGroup playerHolder;

    OnPreparedListener onPreparedListener;
    OnCompletionListener onCompletionListener;
    OnErrorListener onErrorListener;
    OnInfoListener onInfoListener;

    MyListener statusListener;

    private String urlParam; // PPTV 播放需要的参数
    private int mStatus; // 当前播放状态

    PPTVVideoView player;

    public PPTVPlayer(Context context, RelativeLayout playerHolder) {
        this.playerHolder = playerHolder;
        this.context = context;
    }

    @Override
    public void init() {
        player = new PPTVVideoView(context);
//        player.setEnableAd(false);
        playerHolder.addView(player, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        PPTVSdkMgr.getInstance().initVideoView(context, player, null);
        statusListener = new MyListener();
        PPTVSdkMgr.getInstance().setOnPlayerStatusListener(statusListener);
        List<Integer> list = PPTVSdkMgr.getInstance().getScaleTypeList();
        if (list != null && list.size() > 1)
            PPTVSdkMgr.getInstance().changeScaleType(PPTVSdkMgr.getInstance().getScaleTypeList().get(2));
    }

    @Override
    public int getCrrentDefinition() {
        return PPTVSdkMgr.getInstance().getCurrentFt();
    }

    @Override
    public List<Integer> getSupportDefinitions() {
        return PPTVSdkMgr.getInstance().getFtList();
    }

    @Override
    public void fastForward(int sec) {

    }

    @Override
    public void fastBackward(int sec) {

    }

    @Override
    public void changeDefinition(int definition) {
        PPTVSdkMgr.getInstance().changeFt(definition);
    }

    @Override
    public void playIndex(int index) {

    }

    @Override
    public void next() {

    }

    @Override
    public void previous() {

    }

    @Override
    public List<Integer> getVideoList() {
        return PPTVSdkMgr.getInstance().getFtList();
    }

    @Override
    public void appendDataSource(Object o) {

    }

    @Override
    public void appendDataSource(ArrayList t) {

    }

    @Override
    public void skipHeaderAndTail(boolean value) {
        PPTVSdkMgr.getInstance().autoSkip(value);
    }

    @Override
    public void setAutoNext(boolean value) {
        PPTVSdkMgr.getInstance().autoSkip(value);
    }

    @Override
    public List<Integer> getScaleTypeList() {
        return PPTVSdkMgr.getInstance().getScaleTypeList();
    }

    @Override
    public void changeScaleType(Integer type) {
        PPTVSdkMgr.getInstance().changeScaleType(type);
    }

    @Override
    public void setDisplay(SurfaceHolder sh) {

    }

    @Override
    public void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {

    }

    @Override
    public void setDataSource(FileDescriptor fd) throws IOException, IllegalArgumentException, IllegalStateException {

    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {

    }

    @Override
    public void setDataSource(PlayBean playBean) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        String url = playBean.getPptvUrl();
        try {
            url = URLEncoder.encode(url, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        urlParam = String.format("%s=%s",
                PPTVSdkParam.Player_Encodeurl, url);
    }

    @Override
    public String getDataSource() {
        return null;
    }

    @Override
    public void prepareAsync() throws IllegalStateException {

    }

    @Override
    public void start() throws IllegalStateException {
        if (mStatus == BasePlayerStatusListener.STATUS_PLAY_PAUSE) {
            PPTVSdkMgr.getInstance().resume();
            return;
        }
        try {
            PPTVSdkMgr.getInstance().play(context, urlParam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws IllegalStateException {
        PPTVSdkMgr.getInstance().stop(false);
    }

    @Override
    public void pause() throws IllegalStateException {
        PPTVSdkMgr.getInstance().pause();
    }

    @Override
    public void resume() throws IllegalStateException {
        PPTVSdkMgr.getInstance().resume();
    }

    @Override
    public void setScreenOnWhilePlaying(boolean screenOn) {

    }

    @Override
    public int getVideoWidth() {
        return 0;
    }

    @Override
    public int getVideoHeight() {
        return 0;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public void seekTo(long msec) throws IllegalStateException {
        PPTVSdkMgr.getInstance().seek((int) msec );
    }

    @Override
    public long getCurrentPosition() {
        return PPTVSdkMgr.getInstance().getRelTime();
    }

    @Override
    public long getDuration() {
        return PPTVSdkMgr.getInstance().getDuration();
    }

    @Override
    public void release() {
    }

    @Override
    public void reset() {

    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {

    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public void setLogEnabled(boolean enable) {

    }

    @Override
    public boolean isPlayable() {
        return false;
    }

    @Override
    public void setOnPreparedListener(OnPreparedListener listener) {
        this.onPreparedListener = listener;
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        this.onCompletionListener = listener;
    }

    @Override
    public void setOnBufferingUpdateListener(OnBufferingUpdateListener listener) {

    }

    @Override
    public void setOnSeekCompleteListener(OnSeekCompleteListener listener) {

    }

    @Override
    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener) {

    }

    @Override
    public void setOnErrorListener(OnErrorListener listener) {
        this.onErrorListener = listener;
    }

    @Override
    public void setOnInfoListener(OnInfoListener listener) {
        this.onInfoListener = listener;
    }

    @Override
    public void setAudioStreamType(int streamtype) {

    }

    @Override
    public void setKeepInBackground(boolean keepInBackground) {

    }

    @Override
    public int getVideoSarNum() {
        return 0;
    }

    @Override
    public int getVideoSarDen() {
        return 0;
    }

    @Override
    public void setWakeMode(Context context, int mode) {

    }

    @Override
    public void setLooping(boolean looping) {

    }

    @Override
    public boolean isLooping() {
        return false;
    }

    @Override
    public void setSurface(Surface surface) {

    }

    @Override
    public void skipAd() {
        PPTVSdkMgr.getInstance().skipAd();
    }

    @Override
    public void setDataSource(Context context, Uri uri, Map headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {

    }

    class MyListener implements BasePlayerStatusListener {

        @Override
        public void onAdCountDown(int i) {
            onInfoListener.onInfo(PPTVPlayer.this, IPlayer.MEDIA_INFO_AD_COUNTDOWN, i);
        }

        @Override
        public void onAdError(int i, int i1) {

        }

        @Override
        public void onAdFinished() {
            onInfoListener.onInfo(PPTVPlayer.this, IPlayer.MEDIA_INFO_AD_FINISH, 0);
        }

        @Override
        public void onAdLoading() {

        }

        @Override
        public void onAdStarted() {
            onInfoListener.onInfo(PPTVPlayer.this, IPlayer.MEDIA_INFO_AD_START, 0);
        }

        @Override
        public void onAdSizeChanged(int i, int i1) {

        }

        @Override
        public void onAdWebViewVisibleChanged(int i) {

        }

        @Override
        public void onBufferingUpdate(int i) {

        }

        @Override
        public void onBufferStart() {
            onInfoListener.onInfo(PPTVPlayer.this, IPlayer.MEDIA_INFO_BUFFERING_START, 0);

        }

        @Override
        public void onBufferEnd() {
            onInfoListener.onInfo(PPTVPlayer.this, IPlayer.MEDIA_INFO_BUFFERING_END, 0);
        }

        @Override
        public void onCompletion() {
            onCompletionListener.onCompletion(PPTVPlayer.this);
        }

        @Override
        public void onError(int i, int i1, int i2) {
            onErrorListener.onError(PPTVPlayer.this, i,i1,null);
        }

        @Override
        public void onLoading(boolean b) {

        }

        @Override
        public void onPrepared() {
            player.getScaleTypeList();
            player.changeScaleType(1);
            onPreparedListener.onPrepared(PPTVPlayer.this);
        }

        @Override
        public void onProgressUpdate(int i, int i1) {

        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onResolutionChanged(int i) {

        }

        @Override
        public void onStarted() {

        }

        @Override
        public void onStoped() {

        }

        @Override
        public void onSeekStartFromUser() {

        }

        @Override
        public void onSeekComplete(int i, int i1) {

        }

        @Override
        public void onStatus(int status) {
            Log.i("pptv_sdk", "onStatus: status=" + mStatus + ", new status=" + status);
            //TODO: for xiaomi: STATUS_BUFFER_END status after STATUS_PLAY_START
            if (status == BasePlayerStatusListener.STATUS_BUFFER_START || status == BasePlayerStatusListener.STATUS_BUFFER_END) {
                return;
            }
            mStatus = status;
            if (mStatus == BasePlayerStatusListener.STATUS_PLAY_START) {
            } else if (mStatus == BasePlayerStatusListener.STATUS_PLAY_PAUSE) {
            } else if (mStatus == BasePlayerStatusListener.STATUS_PLAY_FINISH ||
                    mStatus == BasePlayerStatusListener.STATUS_PLAY_STOP) {

            }
        }

        @Override
        public void onSizeChanged(int i, int i1) {

        }

    }
}
