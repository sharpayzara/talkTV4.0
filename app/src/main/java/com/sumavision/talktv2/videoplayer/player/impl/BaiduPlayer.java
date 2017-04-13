/*package com.sumavision.talktv2.videoplayer.player.impl;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.ViewGroup;

import com.baidu.cyberplayer.core.BVideoView;
import com.sumavision.talktv2.model.entity.decor.PlayData;
import com.sumavision.talktv2.videoplayer.PlayBean;
import com.sumavision.talktv2.videoplayer.player.IPlayer;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

*//**
 * Created by Administrator on 2016/6/2.
 *//*
public class BaiduPlayer implements IPlayer<PlayData> {

    BVideoView player;
    BaiduListner baiduListner;
    ViewGroup playerHolder;
    Context context;

    OnPreparedListener onPreparedListener;
    OnCompletionListener onCompletionListener;
    OnErrorListener onErrorListener;
    OnInfoListener onInfoListener;
    Object lock = new Object();

    private boolean isFirst = true;

    public BaiduPlayer(Context context, ViewGroup playerHolder) {
        this.playerHolder = playerHolder;
        this.context = context;
    }

    @Override
    public void init() {
        player = new BVideoView(context.getApplicationContext());
        String AK = "qSN3scX2ct8hpaD2zd6VLxlT";
        String SK = "8Tn38lA9IgpmXqMo";
        BVideoView.setAKSK(AK, SK);
        playerHolder.addView(player, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        player.showCacheInfo(false);
        baiduListner = new BaiduListner();
        player.setOnPreparedListener(baiduListner);
        player.setOnErrorListener(baiduListner);
        player.setOnCompletionListener(baiduListner);
        player.setOnInfoListener(baiduListner);
        player.setOnPlayingBufferCacheListener(baiduListner);
    }

    @Override
    public int getCrrentDefinition() {
        return 0;
    }

    @Override
    public List<Integer> getSupportDefinitions() {
        return null;
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
    public List<PlayData> getVideoList() {
        return null;
    }

    @Override
    public void appendDataSource(PlayData o) {

    }

    @Override
    public void appendDataSource(ArrayList<PlayData> t) {

    }

    @Override
    public void skipHeaderAndTail(boolean value) {
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
    public void setDataSource(Context context, Uri uri, Map headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {

    }


    @Override
    public void setDataSource(FileDescriptor fd) throws IOException, IllegalArgumentException, IllegalStateException {

    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        player.setVideoPath(path);
    }

    @Override
    public void setDataSource(PlayBean playBean) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        String url = playBean.getUrl();
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
//        synchronized (lock) {
//            try {
//                if(!isFirst)
//                    lock.wait();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        isFirst = false;
        player.start();
    }

    @Override
    public void stop() throws IllegalStateException {
        player.stopPlayback();
    }

    @Override
    public void pause() throws IllegalStateException {
        player.pause();
    }

    @Override
    public void resume() throws IllegalStateException {
        player.resume();
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
        return player.isPlaying();
    }

    @Override
    public void seekTo(long msec) throws IllegalStateException {
        player.seekTo(msec);
    }

    @Override
    public long getCurrentPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return player.getDuration();
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

    class BaiduListner implements BVideoView.OnPreparedListener, BVideoView.OnCompletionListener, BVideoView.OnErrorListener, BVideoView.OnInfoListener, BVideoView.OnPlayingBufferCacheListener {

//        OnPreparedListener onPreparedListener;
//        OnCompletionListener onCompletionListener;
//        OnErrorListener onErrorListener;
//        OnInfoListener onInfoListener;

        @Override
        public void onPrepared() {
            onPreparedListener.onPrepared(BaiduPlayer.this);
        }

        @Override
        public void onCompletion() {
//            synchronized (lock) {
//                lock.notifyAll();
//            }
            onCompletionListener.onCompletion(BaiduPlayer.this);
        }



        @Override
        public boolean onInfo(int i, int i1) {
            return onInfoListener.onInfo(BaiduPlayer.this, i, i1);
        }

        @Override
        public boolean onError(int i, int i1) {
            onErrorListener.onError(BaiduPlayer.this, i, i1,null);
            return false;
        }

        @Override
        public void onPlayingBufferCache(int i) {
            onInfoListener.onInfo(BaiduPlayer.this, IPlayer.MEDIA_INFO_BUFFERING_PERCENT, i);
        }

//        @Override
//        public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
//            this.onPreparedListener = onPreparedListener;
//        }
//
//        @Override
//        public void setOnErrorListener(OnErrorListener onErrorListener) {
//            this.onErrorListener = onErrorListener;
//        }
//
//        @Override
//        public void setOnCompletionListener(OnCompletionListener onCompletionListener) {
//            this.onCompletionListener = onCompletionListener;
//        }
//
//        @Override
//        public void setOnInfoListener(OnInfoListener onInfoListener) {
//            this.onInfoListener = onInfoListener;
//        }
    }

    @Override
    public void setOnPreparedListener(OnPreparedListener listener) {
//        baiduListner.setOnPreparedListener(listener);
        onPreparedListener = listener;
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
//        baiduListner.setOnCompletionListener(listener);
        onCompletionListener = listener;
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
//        baiduListner.setOnErrorListener(listener);
        onErrorListener = listener;
    }

    @Override
    public void setOnInfoListener(OnInfoListener listener) {
//        baiduListner.setOnInfoListener(listener);
        onInfoListener = listener;
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
}*/
