package com.sumavision.talktv2.model.entity;

import com.sumavision.talktv2.model.entity.decor.BaseData;

import java.util.List;

/**
 * Created by sharpay on 16-6-30.
 */
public class MediaList extends BaseData{

    private int total;
    private List<ItemsBean> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ItemsBean> getItems() {
        return items;
    }

    public void setItems(List<ItemsBean> items) {
        this.items = items;
    }

    public static class ItemsBean {
        private String code;
        private int duration;
        private String name;
        private String playCount;
        private String playUrl;
        private int sdkType;
        private int videoType;
        private String picurl;

        public String getPicUrl() {
            return picurl;
        }

        public void setPicUrl(String picUrl) {
            this.picurl = picUrl;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPlayCount() {
            return playCount;
        }

        public void setPlayCount(String playCount) {
            this.playCount = playCount;
        }

        public String getPlayUrl() {
            return playUrl;
        }

        public void setPlayUrl(String playUrl) {
            this.playUrl = playUrl;
        }

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
    }
}
