package com.sumavision.talktv2.videoplayer;

/**
 * Created by zhangyisu on 2016/6/12.
 */
public interface IGesture {
    void onVolumeSlide(float percent);
    void onBrightnessSlide(float percent);
    int videoHeight();
    int videoWidth();
}
