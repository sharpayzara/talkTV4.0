package com.sumavision.talktv2.videoplayer.player.impl;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.ViewGroup;

import com.sumavision.talktv2.model.entity.decor.PlayData;
import com.sumavision.talktv2.ui.widget.media.IjkVideoView;
import com.sumavision.talktv2.videoplayer.PlayBean;
import com.sumavision.talktv2.videoplayer.player.IPlayer;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 *  desc  ijk播放器
 *  @author  yangjh
 *  created at  16-11-10 下午2:16 
 */
public class IJKPlayer implements IPlayer<PlayData> {
    IjkVideoView player;
    ViewGroup playerHolder;
    Context context;

    OnPreparedListener onPreparedListener;
    OnCompletionListener onCompletionListener;
    OnErrorListener onErrorListener;
    OnInfoListener onInfoListener;
    IJKListener ijkListner;
    public IJKPlayer(Context context, ViewGroup playerHolder) {
        this.playerHolder = playerHolder;
        this.context = context;
    }
    @Override
    public void init() {
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        player = new IjkVideoView(context);
        playerHolder.addView(player, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ijkListner = new IJKListener();
        player.setOnPreparedListener(ijkListner);
        player.setOnErrorListener(ijkListner);
        player.setOnCompletionListener(ijkListner);
        player.setOnInfoListener(ijkListner);
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
    public void appendDataSource(PlayData playData) {

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
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {

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
        player.seekTo((int)msec*1000);
    }

    @Override
    public long getCurrentPosition() {
        Log.e("getCurrentPosition",player.getCurrentPosition()/1000+"");
        return player.getCurrentPosition()/1000;
    }

    @Override
    public long getDuration() {
        Log.e("getDuration",player.getDuration()/1000+"");
            return player.getDuration()/1000;
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
        onPreparedListener = listener;
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
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
        onErrorListener = listener;
    }

    @Override
    public void setOnInfoListener(OnInfoListener listener) {
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

    private class IJKListener implements IMediaPlayer.OnPreparedListener,IMediaPlayer.OnErrorListener,IMediaPlayer.OnCompletionListener,IMediaPlayer.OnInfoListener{
        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            onPreparedListener.onPrepared(IJKPlayer.this);
        }

        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            onCompletionListener.onCompletion(IJKPlayer.this);
        }

        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
            if(i == -10000)
                return false;
            onErrorListener.onError(IJKPlayer.this, i, i1,null);
            return false;
        }

        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
            return onInfoListener.onInfo(IJKPlayer.this, i, i1);
        }
    }
}
