package com.sumavision.talktv2.model.entity;

import com.sumavision.talktv2.model.entity.decor.BaseData;

import java.util.List;

/**
 * Created by sharpay on 16-6-20.
 */
public class SeriesDetail extends BaseData{

    private int total;
    private List<SourceBean> source;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<SourceBean> getSource() {
        return source;
    }

    public void setSource(List<SourceBean> source) {
        this.source = source;
    }

    public static class SourceBean {
        private String id;
        private String epi;
        private String name;
        private String playUrl;
        private String videoType;
        private String count;
        private String picUrl;
        private boolean isdownload; // 标记是否在缓存popup window中选中
        private boolean isCached; // 标记是否已经加入了缓存

        private String localPath; // 缓存所在位置，用于播放

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
        public String getEpi() {
            return epi;
        }

        public void setEpi(String epi) {
            this.epi = epi;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPlayUrl() {
            return playUrl;
        }

        public void setPlayUrl(String playUrl) {
            this.playUrl = playUrl;
        }

        public String getVideoType() {
            return videoType;
        }

        public void setVideoType(String videoType) {
            this.videoType = videoType;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public boolean isdownload() {
            return isdownload;
        }

        public void setIsdownload(boolean isdownload) {
            this.isdownload = isdownload;
        }

        public String getPicUrl() {
            return picUrl;
        }

        public void setPicUrl(String picUrl) {
            this.picUrl = picUrl;
        }

        public boolean isCached() {
            return isCached;
        }

        public void setCached(boolean cached) {
            isCached = cached;
        }

        public String getLocalPath() {
            return localPath;
        }

        public void setLocalPath(String localPath) {
            this.localPath = localPath;
        }
    }
}
