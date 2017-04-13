package com.sumavision.talktv2.model.entity;

import com.sumavision.talktv2.videoplayer.PlayBean;

/**
 * Created by sharpay on 16-6-16.
 */
public class MediaRecommand {
    private String code;
    private String name;
    private int duration;
    private String playCount;
   // private String url = "http://www.iqiyi.com/v_19rrlmbbi0.html#vfrm=2-3-0-1&curid=496938900_1e18fbf4d326aa8614b524b66baa9319";
    private int playerType;
    private String picUrl;
    private PlayBean playBean;
    private int sdkType;
    private int videoType;

    public int getSdkType() {
        return sdkType;
    }

    public void setSdkType(int sdkType) {
        this.sdkType = sdkType;
    }

    public int getVideoType() {
        return videoType;
    }

    public void setVideoType(int videoType) {
        this.videoType = videoType;
    }

    public String getPlayCount() {
        return playCount;
    }

    public void setPlayCount(String playCount) {
        this.playCount = playCount;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public int getPlayerType() {
        return playerType;
    }

    public void setPlayerType(int playerType) {
        this.playerType = playerType;
    }

    public PlayBean getPlayBean() {
        return playBean;
    }

    public void setPlayBean(PlayBean playBean) {
        this.playBean = playBean;
    }


}
