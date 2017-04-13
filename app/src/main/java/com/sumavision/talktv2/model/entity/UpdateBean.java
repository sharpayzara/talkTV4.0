package com.sumavision.talktv2.model.entity;

import com.sumavision.talktv2.model.entity.decor.BaseData;

import java.util.List;

/**
 * Created by sharpay on 16-7-31.
 */
public class UpdateBean extends BaseData{

    private String apksize;
    private String downloadUrl;
    private String forceupdate;  //0 代表false； 1代表true
    private int versionCode;
    private String versionName;
    private List<String> release;

    public String getApksize() {
        return apksize;
    }

    public void setApksize(String apksize) {
        this.apksize = apksize;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getForceupdate() {
        return forceupdate;
    }

    public void setForceupdate(String forceupdate) {
        this.forceupdate = forceupdate;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public List<String> getRelease() {
        return release;
    }

    public void setRelease(List<String> release) {
        this.release = release;
    }
}
