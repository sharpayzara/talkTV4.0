package com.sumavision.talktv2.videoplayer;

/**
 * Created by zhangyisu on 2016/6/14.
 *
 * 播放信息相关bean
 */
public class PlayBean {

    String programId;
    String mId = "";

    String url; // 破解地址
    String playPath; // 直接播放地址

    // 搜狐相关
    long sohuAid;
    long sohuVid;
    int sohuSite;

    // PPTV相关
    String pptvSid;
    String pptvVid;
    String pptvFt;
    String pptvPlaytype;
    String pptvUrl;

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getPptvUrl() {
        return pptvUrl;
    }

    public void setPptvUrl(String pptvUrl) {
        this.pptvUrl = pptvUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPlayPath() {
        return playPath;
    }

    public void setPlayPath(String playPath) {
        this.playPath = playPath;
    }

    public long getSohuAid() {
        return sohuAid;
    }

    public void setSohuAid(long sohuAid) {
        this.sohuAid = sohuAid;
    }

    public long getSohuVid() {
        return sohuVid;
    }

    public void setSohuVid(long sohuVid) {
        this.sohuVid = sohuVid;
    }

    public int getSohuSite() {
        return sohuSite;
    }

    public void setSohuSite(int sohuSite) {
        this.sohuSite = sohuSite;
    }

    public String getPptvSid() {
        return pptvSid;
    }

    public void setPptvSid(String pptvSid) {
        this.pptvSid = pptvSid;
    }

    public String getPptvVid() {
        return pptvVid;
    }

    public void setPptvVid(String pptvVid) {
        this.pptvVid = pptvVid;
    }

    public String getPptvFt() {
        return pptvFt;
    }

    public void setPptvFt(String pptvFt) {
        this.pptvFt = pptvFt;
    }

    public String getPptvPlaytype() {
        return pptvPlaytype;
    }

    public void setPptvPlaytype(String pptvPlaytype) {
        this.pptvPlaytype = pptvPlaytype;
    }


}
