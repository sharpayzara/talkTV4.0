package com.sumavision.talktv2.model.entity;

import com.sumavision.talktv2.model.entity.decor.BaseData;

import java.util.List;

/**
 * Created by sharpay on 16-9-7.
 */
public class SpecialContentList extends BaseData{
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
        private String brtxt;
        private String cate;
        private String desc;
        private String id;
        private String name;
        private String picture;
        private String playCount;
        private String release;
        private int position;

        public String getBrtxt() {
            return brtxt;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public void setBrtxt(String brtxt) {
            this.brtxt = brtxt;
        }

        public String getCate() {
            return cate;
        }

        public void setCate(String cate) {
            this.cate = cate;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        public String getPlayCount() {
            return playCount;
        }

        public void setPlayCount(String playCount) {
            this.playCount = playCount;
        }

        public String getRelease() {
            return release;
        }

        public void setRelease(String release) {
            this.release = release;
        }
    }
}
