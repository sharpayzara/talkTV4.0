package com.sumavision.talktv2.videoplayer.player.impl;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.sumavision.talktv2.model.entity.decor.PlayData;
import com.sumavision.talktv2.videoplayer.PlayBean;
import com.sumavision.talktv2.videoplayer.player.IPlayer;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangyisu on 2016/7/26.
 */
public class SystemPlayer implements IPlayer<PlayData> {


    private final ViewGroup playerHolder;
    Context context;
    VideoView player;
    private OnPreparedListener onPreParedListener;
    private OnCompletionListener onCompletionListener;
    private OnErrorListener onErrorListener;
    private OnInfoListener onInfoListener;
    private SystemPlayerListener systemPlayerListener;

    public SystemPlayer(Context context, ViewGroup playerHolder) {
        this.playerHolder = playerHolder;
        this.context = context;
    }

    @Override
    public void init() {
        player = new VideoView(context);
        playerHolder.addView(player, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        player.showCacheInfo(false);
        systemPlayerListener = new SystemPlayerListener();
        player.setOnPreparedListener(systemPlayerListener);
        player.setOnErrorListener(systemPlayerListener);
        player.setOnCompletionListener(systemPlayerListener);
//        player.setOnInfoListener(systemPlayerListener);
    }

    @Override
    public int getCrrentDefinition() {
        return player.getCurrentPosition();
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
        player.seekTo((int) msec);
    }

    @Override
    public long getCurrentPosition() {
        return 0;
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

    @Override
    public void setOnPreparedListener(OnPreparedListener listener) {
        this.onPreParedListener = listener;
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

    }

    private class SystemPlayerListener implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnInfoListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            onCompletionListener.onCompletion(SystemPlayer.this);
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            onErrorListener.onError(SystemPlayer.this, what, extra, "");
            return false;
        }

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            onInfoListener.onInfo(SystemPlayer.this, what, extra);
            return false;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            onPreParedListener.onPrepared(SystemPlayer.this);
        }
    }
}
