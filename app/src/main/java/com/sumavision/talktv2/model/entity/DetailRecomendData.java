package com.sumavision.talktv2.model.entity;

import com.sumavision.talktv2.model.entity.decor.BaseData;

import java.util.List;

/**
 * Created by zjx on 2016/7/6.
 */
public class DetailRecomendData extends BaseData {

    /**
     * id : se4ehuso
     * name : 十万个冷笑话
     * picurl : /opt/resource/photo/medium/tseries/2016/0427/en6y0cjcth73h.jpg
     */

    private List<ItemsBean> items;
    private String style;

    public List<ItemsBean> getItems() {
        return items;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setItems(List<ItemsBean> items) {
        this.items = items;
    }

    public static class ItemsBean {
        private String id;
        private String name;
        private String picurl;
        private String prompt;

        public String getPrompt() {
            return prompt;
        }

        public void setPrompt(String prompt) {
            this.prompt = prompt;
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

        public String getPicurl() {
            return picurl;
        }

        public void setPicurl(String picurl) {
            this.picurl = picurl;
        }
    }
}
