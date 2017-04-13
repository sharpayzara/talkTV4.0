package com.sumavision.talktv2.model.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 长视频点播
 * Created by zjx on 2016/6/29.
 */
@DatabaseTable(tableName = "playhistory")
public class PlayerHistoryBean {
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField
    private String programId;
    @DatabaseField
    private String programName;
    @DatabaseField
    private String picUrl;
    @DatabaseField
    private String cpId;
    @DatabaseField
    private int playPos;
    @DatabaseField
    private String mid;
    @DatabaseField
    private String programType;//电影、、、电视剧
    @DatabaseField
    private int sdkType;//1--搜狐 2--pptv
    @DatabaseField
    private int videoType;//1--破解   2--sdk
    @DatabaseField
    private int mediaType;//1--长视频、、、2--短视频    3--专题详情页
    @DatabaseField
    private String epi;

    @DatabaseField
    private String specialId;

    public String getEpi() {
        return epi;
    }

    public void setEpi(String epi) {
        this.epi = epi;
    }

    public boolean isChecked;
    public boolean isOpened;

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

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public String getProgramType() {
        return programType;
    }

    public void setProgramType(String programType) {
        this.programType = programType;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public long getPointTime() {
        return pointTime;
    }

    public void setPointTime(long pointTime) {
        this.pointTime = pointTime;
    }

    public String getCpId() {
        return cpId;
    }

    public void setCpId(String cpId) {
        this.cpId = cpId;
    }

    public int getPlayPos() {
        return playPos;
    }

    public void setPlayPos(int playPos) {
        this.playPos = playPos;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getSpecialId() {
        return specialId;
    }

    public void setSpecialId(String specialId) {
        this.specialId = specialId;
    }

    @DatabaseField
    private long pointTime;

}
