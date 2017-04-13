package com.sumavision.talktv2.model.entity;

import com.sumavision.talktv2.videoplayer.PlayBean;
import com.sumavision.talktv2.videoplayer.view.VodPlayerVideoView;

/**
 * Created by sharpay on 16-7-12.
 */
public class HorVideoData {
    private VodPlayerVideoView videoView;
    private boolean isLandscape;
    private PlayBean currBean;
    private long breakTime;
    public VodPlayerVideoView getVideoView() {
        return videoView;
    }

    public long getBreakTime() {
        return breakTime;
    }

    public void setBreakTime(long breakTime) {
        this.breakTime = breakTime;
    }

    public void setVideoView(VodPlayerVideoView videoView) {
        this.videoView = videoView;
    }

    public boolean isLandscape() {
        return isLandscape;
    }

    public void setLandscape(boolean landscape) {
        isLandscape = landscape;
    }

    public PlayBean getCurrBean() {
        return currBean;
    }

    public void setCurrBean(PlayBean currBean) {
        this.currBean = currBean;
    }
}
