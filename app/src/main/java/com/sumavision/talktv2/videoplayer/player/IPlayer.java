package com.sumavision.talktv2.videoplayer.player;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.sumavision.talktv2.videoplayer.PlayBean;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Zhangyisu on 2016/6/1.
 */
public interface IPlayer<T> {
    /*
     * Do not change these values without updating their counterparts in native
     */
    int MEDIA_INFO_UNKNOWN = 1;
    int MEDIA_INFO_STARTED_AS_NEXT = 2;
    int MEDIA_INFO_VIDEO_RENDERING_START = 3;
    int MEDIA_INFO_VIDEO_TRACK_LAGGING = 700;
    int MEDIA_INFO_BUFFERING_START = 701;
    int MEDIA_INFO_BUFFERING_END = 702;
    int MEDIA_INFO_NETWORK_BANDWIDTH = 703;
    int MEDIA_INFO_BUFFERING_PERCENT = 704;
    int MEDIA_INFO_BAD_INTERLEAVING = 800;
    int MEDIA_INFO_NOT_SEEKABLE = 801;
    int MEDIA_INFO_METADATA_UPDATE = 802;
    int MEDIA_INFO_TIMED_TEXT_ERROR = 900;
    int MEDIA_INFO_UNSUPPORTED_SUBTITLE = 901;
    int MEDIA_INFO_SUBTITLE_TIMED_OUT = 902;

    int MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001;
    int MEDIA_INFO_AUDIO_RENDERING_START = 10002;

    int MEDIA_INFO_AD_START = 20001;
    int MEDIA_INFO_AD_FINISH = 20002;
    int MEDIA_INFO_AD_COUNTDOWN = 20003;

    int MEDIA_ERROR_UNKNOWN = 1;
    int MEDIA_ERROR_SERVER_DIED = 100;
    int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;
    int MEDIA_ERROR_IO = -1004;
    int MEDIA_ERROR_MALFORMED = -1007;
    int MEDIA_ERROR_UNSUPPORTED = -1010;
    int MEDIA_ERROR_TIMED_OUT = -110;

    //========================= add by zys
    void init();

    int getCrrentDefinition();

    List<Integer> getSupportDefinitions();

    void fastForward(int sec);

    void fastBackward(int sec);

    void changeDefinition(int definition);

    void playIndex(int index);

    void next();

    void previous();

    List<T> getVideoList();

    void appendDataSource(T t);

    void appendDataSource(ArrayList<T> t);

    void skipHeaderAndTail(boolean value);

    void setAutoNext(boolean value);

    List<Integer> getScaleTypeList();

    void changeScaleType (Integer type);

    //========================= added by zys


    void setDisplay(SurfaceHolder sh);

    void setDataSource(Context context, Uri uri)
            throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    void setDataSource(Context context, Uri uri, Map<String, String> headers)
            throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;

    void setDataSource(FileDescriptor fd)
            throws IOException, IllegalArgumentException, IllegalStateException;

    void setDataSource(String path)
            throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;

    void setDataSource(PlayBean playBean)
            throws IOException, IllegalArgumentException, SecurityException, IllegalStateException;

    String getDataSource();

    void prepareAsync() throws IllegalStateException;

    void start() throws IllegalStateException;

    void stop() throws IllegalStateException;

    void pause() throws IllegalStateException;

    void resume() throws IllegalStateException;

    void setScreenOnWhilePlaying(boolean screenOn);

    int getVideoWidth();

    int getVideoHeight();

    boolean isPlaying();

    void seekTo(long msec) throws IllegalStateException;

    // 单位是秒
    long getCurrentPosition();

    // 单位是秒
    long getDuration();

    void release();

    void reset();

    void setVolume(float leftVolume, float rightVolume);

    int getAudioSessionId();

    // TODO
//    MediaInfo getMediaInfo();

    @SuppressWarnings("EmptyMethod")
    @Deprecated
    void setLogEnabled(boolean enable);

    @Deprecated
    boolean isPlayable();

    void setOnPreparedListener(OnPreparedListener listener);

    void setOnCompletionListener(OnCompletionListener listener);

    void setOnBufferingUpdateListener(
            OnBufferingUpdateListener listener);

    void setOnSeekCompleteListener(
            OnSeekCompleteListener listener);

    void setOnVideoSizeChangedListener(
            OnVideoSizeChangedListener listener);

    void setOnErrorListener(OnErrorListener listener);

    void setOnInfoListener(OnInfoListener listener);

    /*--------------------
     * Listeners
     */
    interface OnPreparedListener {
        void onPrepared(IPlayer mp);
    }

    interface OnCompletionListener {
        void onCompletion(IPlayer mp);
    }

    interface OnBufferingUpdateListener {
        void onBufferingUpdate(IPlayer mp, int percent);
    }

    interface OnSeekCompleteListener {
        void onSeekComplete(IPlayer mp);
    }

    interface OnVideoSizeChangedListener {
        void onVideoSizeChanged(IPlayer mp, int width, int height,
                                int sar_num, int sar_den);
    }

    interface OnErrorListener {
        void onError(IPlayer mp, int what, int extra,String var);
    }

    interface OnInfoListener {
        boolean onInfo(IPlayer mp, int what, int extra);
    }

    /*--------------------
     * Optional
     */
    void setAudioStreamType(int streamtype);

    @Deprecated
    void setKeepInBackground(boolean keepInBackground);

    int getVideoSarNum();

    int getVideoSarDen();

    @Deprecated
    void setWakeMode(Context context, int mode);

    void setLooping(boolean looping);

    boolean isLooping();

    /*--------------------
     * AndroidMediaPlayer: JELLY_BEAN
     */
//    ITrackInfo[] getTrackInfo();

    /*--------------------
     * AndroidMediaPlayer: ICE_CREAM_SANDWICH:
     */
    void setSurface(Surface surface);

    /*--------------------
     * AndroidMediaPlayer: M:
     * TODO
     */
//    void setDataSource(IMediaDataSource mediaDataSource);

    void skipAd();

}
