package com.sumavision.talktv2.model.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by sharpay on 16-6-26.
 */
@DatabaseTable(tableName = "collect_bean")
public class CollectBean {
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField
    private String sid;
    @DatabaseField
    private String cpid;
    @DatabaseField
    private VideoType videoType;
    @DatabaseField
    private String name;
    @DatabaseField
    private String actor;
    @DatabaseField
    private String info;
    @DatabaseField
    private String picurl;
    @DatabaseField
    private String release;
    @DatabaseField
    private String src;
    @DatabaseField
    private String subType;
    @DatabaseField
    private String tabStyle;
    @DatabaseField
    private String total;
    @DatabaseField
    private String zone;
    @DatabaseField
    private String playCount;
    @DatabaseField
    private String director;
    @DatabaseField
    private String score;
    @DatabaseField
    private String newestSelection;
    @DatabaseField
    private String isdownload;
    @DatabaseField
    private String currType;
    public boolean isOpened;
    public boolean isChecked;
    @DatabaseField
    public String vid;
    @DatabaseField
    public int sdkType;
    @DatabaseField
    public int mediaVideoType;
    @DatabaseField
    private String programIdStr;

    public String getProgramIdStr() {
        return programIdStr;
    }

    public void setProgramIdStr(String programIdStr) {
        this.programIdStr = programIdStr;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public VideoType getVideoType() {
        return videoType;
    }

    public void setVideoType(VideoType videoType) {
        this.videoType = videoType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getTabStyle() {
        return tabStyle;
    }

    public void setTabStyle(String tabStyle) {
        this.tabStyle = tabStyle;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getPlayCount() {
        return playCount;
    }

    public void setPlayCount(String playCount) {
        this.playCount = playCount;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getIsdownload() {
        return isdownload;
    }

    public void setIsdownload(String isdownload) {
        this.isdownload = isdownload;
    }

    public String getNewestSelection() {
        return newestSelection;
    }

    public void setNewestSelection(String newestSelection) {
        this.newestSelection = newestSelection;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public String getCpid() {
        return cpid;
    }

    public void setCpid(String cpid) {
        this.cpid = cpid;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getCurrType() {
        return currType;
    }

    public String getVid() {
        return vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public int getSdkType() {
        return sdkType;
    }

    public void setSdkType(int sdkType) {
        this.sdkType = sdkType;
    }

    public int getMediaVideoType() {
        return mediaVideoType;
    }

    public void setMediaVideoType(int mediaVideoType) {
        this.mediaVideoType = mediaVideoType;
    }

    public void setCurrType(String currType) {
        this.currType = currType;
    }
}
