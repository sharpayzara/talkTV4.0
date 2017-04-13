package com.sumavision.talktv2.videoplayer.player.impl;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.ViewGroup;

import com.sohuvideo.api.SohuPlayerError;
import com.sohuvideo.api.SohuPlayerItemBuilder;
import com.sohuvideo.api.SohuPlayerLoadFailure;
import com.sohuvideo.api.SohuPlayerMonitor;
import com.sohuvideo.api.SohuPlayerSetting;
import com.sohuvideo.api.SohuPlayerStatCallback;
import com.sohuvideo.api.SohuScreenView;
import com.sohuvideo.api.SohuVideoPlayer;
import com.sohuvideo.player.util.j;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.videoplayer.PlayBean;
import com.sumavision.talktv2.videoplayer.player.IPlayer;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/6/2.
 * <p/>
 * TODO 还有一个可以用的东西没拿过来，可以参考sdk文档
 */
public class SohuPlayer implements IPlayer<SohuPlayerItemBuilder> {

    Context context;
    ViewGroup playerHolder;
    SohuScreenView screenView;
    SohuVideoPlayer player;
    SohuPlayerSetting playerSetting;
    SohuPlayerStatCallback statCallback;
    MyMoniter monitor;

    OnPreparedListener onPreparedListener;
    OnCompletionListener onCompletionListener;
    OnErrorListener onErrorListener;
    OnInfoListener onInfoListener;

    public SohuPlayer(Context context, ViewGroup playerHolder) {
        this.playerHolder = playerHolder;
        this.context = context;
    }

    @Override
    public void init() {
        screenView = new SohuScreenView(context);
        try {
            Field mCurrentVideoViewField = screenView.getClass().getDeclaredField("mCurrentVideoView");
            mCurrentVideoViewField.setAccessible(true);
            com.sohuvideo.player.widget.VideoView vv = (com.sohuvideo.player.widget.VideoView) mCurrentVideoViewField.get(screenView);
            vv.setZOrderOnTop(false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        screenView.setBackgroundColor(context.getResources().getColor(R.color.black));
        player = new SohuVideoPlayer();
        player.setSohuScreenView(screenView);
        playerHolder.addView(screenView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        statCallback = new SohuPlayerStatCallback() {
            @Override
            public void onVV(SohuPlayerItemBuilder sohuPlayerItemBuilder) {

            }

            @Override
            public void onRealVV(SohuPlayerItemBuilder sohuPlayerItemBuilder, int i) {

            }

            @Override
            public void onEnd(SohuPlayerItemBuilder sohuPlayerItemBuilder, int i, boolean b) {

            }

            @Override
            public void onHeartBeat(SohuPlayerItemBuilder sohuPlayerItemBuilder, int i) {

            }
        };
        player.setSohuPlayerStatCallback(statCallback);

        monitor = new MyMoniter();
        player.setSohuPlayerMonitor(monitor);
        playerSetting = new SohuPlayerSetting();
        playerSetting.setNeedAutoNext(false);
    }

    @Override
    public int getCrrentDefinition() {
        return player.getCurrentDefinition();
    }

    @Override
    public List<Integer> getSupportDefinitions() {
        return player.getSupportDefinitions();
    }

    @Override
    public void fastForward(int sec) {

    }

    @Override
    public void fastBackward(int sec) {

    }

    @Override
    public void changeDefinition(int definition) {

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
    public List<SohuPlayerItemBuilder> getVideoList() {
        return null;
    }

    @Override
    public void appendDataSource(SohuPlayerItemBuilder o) {

    }

    @Override
    public void appendDataSource(ArrayList<SohuPlayerItemBuilder> t) {

    }

    @Override
    public void skipHeaderAndTail(boolean set) {
    }

    @Override
    public void setAutoNext(boolean value) {
    }

    @Override
    public List<Integer> getScaleTypeList() {
        return null;
    }

    @Override
    public void changeScaleType(Integer type) {

    }

    @Override
    public void setDisplay(SurfaceHolder sh) {

    }

    @Override
    public void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {

    }

    @Override
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {

    }

    @Override
    public void setDataSource(FileDescriptor fd) throws IOException, IllegalArgumentException, IllegalStateException {

    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        player.setDataSource(new SohuPlayerItemBuilder(null, 1000000568472L, 83063002, 2));
    }

    @Override
    public void setDataSource(PlayBean playBean) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        player.setDataSource(new SohuPlayerItemBuilder(null, playBean.getSohuAid(), playBean.getSohuVid(), playBean.getSohuSite()));
        Log.i("SohuPlayer", playBean.getSohuAid() + " "+playBean.getSohuVid() + " "+playBean.getSohuSite());
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
        player.play();
        Log.i("SohuPlayer", "start()");
    }

    @Override
    public void stop() throws IllegalStateException {
        // 保持视频播放位置
        player.stop(false);
    }

    @Override
    public void pause() throws IllegalStateException {
        player.pause();
    }

    @Override
    public void resume() throws IllegalStateException {
        player.play();
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
        // TODO 会不会出问题
        player.seekTo((int) msec*1000);
    }

    @Override
    public long getCurrentPosition() {
        return player.getCurrentPosition() / 1000;
    }

    @Override
    public long getDuration() {
        return player.getDuration() / 1000;
    }

    @Override
    public void release() {
        player.release();
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
    public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
        this.onPreparedListener = onPreparedListener;
    }

    @Override
    public void setOnErrorListener(OnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener onCompletionListener) {
        this.onCompletionListener = onCompletionListener;
    }

    @Override
    public void setOnInfoListener(OnInfoListener onInfoListener) {
        this.onInfoListener = onInfoListener;
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

    }

    class MyMoniter extends SohuPlayerMonitor {

        public void onPreparing() {
            j.c("SohuPlayerMonitor", "onPreparing");
        }

        public void onPrepared() {
            j.c("SohuPlayerMonitor", "onPrepared");
            onPreparedListener.onPrepared(SohuPlayer.this);
        }

        public void onPlay() {
            j.c("SohuPlayerMonitor", "onPlay");
        }

        public void onPause() {
            j.c("SohuPlayerMonitor", "onPause");
        }

        public void onStop() {
            j.c("SohuPlayerMonitor", "onStop");
        }

        public void onError(SohuPlayerError var1) {
            j.c("SohuPlayerMonitor", "onError:" + var1);
            onErrorListener.onError(SohuPlayer.this, 1, 1,var1.toString());
        }

        public void onStartLoading() {
            j.c("SohuPlayerMonitor", "onStartLoading");
        }

        public void onLoadSuccess() {
            j.c("SohuPlayerMonitor", "onLoadSuccess");
        }

        public void onLoadFail(SohuPlayerLoadFailure var1) {
            j.c("SohuPlayerMonitor", "onLoadFail:" + var1);
            onErrorListener.onError(SohuPlayer.this, 1, 1,var1.toString());
        }

        public void onBuffering(int var1) {
            j.c("SohuPlayerMonitor", "onBuffering:" + var1);
        }

        public void onComplete() {
            j.c("SohuPlayerMonitor", "onComplete");
            onCompletionListener.onCompletion(SohuPlayer.this);
        }

        public void onPausedAdvertShown() {
            j.c("SohuPlayerMonitor", "onPausedAdvertShown");
        }

        public void onProgressUpdated(int var1, int var2) {
            j.c("monitor", "onProgressUpdated:" + var1 + ",duration:" + var2);
        }

        public void onPlayItemChanged(SohuPlayerItemBuilder var1, int var2) {
            j.c("SohuPlayerMonitor", "onPlayItemChanged:[index][" + var2 + "]" + var1.toString());
        }

        public void onSkipHeader() {
            j.c("SohuPlayerMonitor", "onSkipHeader");
        }

        public void onSkipTail() {
            j.c("SohuPlayerMonitor", "onSkipTail");
        }

        public void onPreviousNextStateChange(boolean var1, boolean var2) {
            j.c("SohuPlayerMonitor", "onPreviousNextStateChange:previous=" + var1 + ",next:" + var2);
        }

        public void onDefinitionChanged() {
            j.c("SohuPlayerMonitor", "onDefinitionChanged");
        }

        public void onPlayOver(SohuPlayerItemBuilder var1) {
            j.c("SohuPlayerMonitor", "onPlayOver");
        }

        public void onDecodeChanged(boolean var1, int var2, int var3) {
            j.c("SohuPlayerMonitor", "onDecodeChanged isHardware:" + var1 + ", action:" + var2 + ",reason:" + var3);
        }

        public void onAppPlayStart() {
            j.c("SohuPlayerMonitor", "onAppPlayStart");
        }

        public void onAppPlayOver() {
            j.c("SohuPlayerMonitor", "onAppPlayOver");
        }

        public void onOadAllCompleted() {
            j.c("SohuPlayerMonitor", "onOadAllCompleted");
        }

    }

}
