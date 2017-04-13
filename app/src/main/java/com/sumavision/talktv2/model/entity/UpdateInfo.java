package com.sumavision.talktv2.model.entity;

import java.util.List;

/**
 * Created by sharpay on 16-7-6.
 */
public class UpdateInfo {
    private String packageName;
    private String versionCode;
    private String versionName;
    private String downloadUrl;
    private String apkSize;
    private boolean isForceUpdate;
    private List<String> relaseList;
    public UpdateInfo(String packageName, String versionCode, String versionName, String downloadUrl, String apkSize,
                      boolean isForceUpdate, List<String> relaseList) {
        this.packageName = packageName;
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.downloadUrl = downloadUrl;
        this.apkSize = apkSize;
        this.isForceUpdate = isForceUpdate;
        this.relaseList = relaseList;
    }
    public String getPackageName() {
        return packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public String getVersionCode() {
        return versionCode;
    }
    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }
    public String getVersionName() {
        return versionName;
    }
    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
    public String getDownloadUrl() {
        return downloadUrl;
    }
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    public String getApkSize() {
        return apkSize;
    }
    public void setApkSize(String apkSize) {
        this.apkSize = apkSize;
    }
    public boolean getIsForceUpdate() {
        return isForceUpdate;
    }
    public void setIsForceUpdate(boolean isForceUpdate) {
        this.isForceUpdate = isForceUpdate;
    }
    public List<String> getRelaseList() {
        return relaseList;
    }
}
