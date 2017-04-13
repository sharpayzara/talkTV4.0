package com.sumavision.talktv2.model.entity;

import com.j256.ormlite.field.DatabaseField;
import com.sumavision.talktv2.model.entity.decor.BaseData;

/**
 * Created by sharpay on 16-7-11.
 */
public class ActionUrl extends BaseData{
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField
    private String global_so;
    @DatabaseField
    private String liveapi;
    @DatabaseField
    private String upgc_api;
    @DatabaseField
    private String mepg_api;
    @DatabaseField
    private String usercenter;
    @DatabaseField
    private String mepg_log;
    @DatabaseField
    private String duiba;
    @DatabaseField
    private String share_api;

    public String getShare_api() {
        return share_api;
    }

    public void setShare_api(String share_api) {
        this.share_api = share_api;
    }

    public String getMepg_log() {
        return mepg_log;
    }

    public void setMepg_log(String mepg_log) {
        this.mepg_log = mepg_log;
    }
    public String getDuiba() {
        return duiba;
    }

    public void setDuiba(String duiba) {
        this.duiba = duiba;
    }

    public String getGlobal_so() {
        return global_so;
    }

    public void setGlobal_so(String global_so) {
        this.global_so = global_so;
    }

    public String getLiveapi() {
        return liveapi;
    }

    public void setLiveapi(String liveapi) {
        this.liveapi = liveapi;
    }

    public String getUpgc_api() {
        return upgc_api;
    }

    public void setUpgc_api(String upgc_api) {
        this.upgc_api = upgc_api;
    }

    public String getMepg_api() {
        return mepg_api;
    }

    public void setMepg_api(String mepg_api) {
        this.mepg_api = mepg_api;
    }

    public String getUsercenter() {
        return usercenter;
    }

    public void setUsercenter(String usercenter) {
        this.usercenter = usercenter;
    }
}
