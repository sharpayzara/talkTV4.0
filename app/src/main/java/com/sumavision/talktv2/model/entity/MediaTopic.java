package com.sumavision.talktv2.model.entity;

import com.sumavision.talktv2.model.entity.decor.BaseData;

import java.util.List;

/**
 * Created by sharpay on 16-6-30.
 */
public class MediaTopic extends BaseData{

    private List<ItemsBean> items;

    public List<ItemsBean> getItems() {
        return items;
    }

    public void setItems(List<ItemsBean> items) {
        this.items = items;
    }

    public static class ItemsBean {
        private String navCode;
        private String navName;
        private String value;

        public String getNavCode() {
            return navCode;
        }

        public void setNavCode(String navCode) {
            this.navCode = navCode;
        }

        public String getNavName() {
            return navName;
        }

        public void setNavName(String navName) {
            this.navName = navName;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
