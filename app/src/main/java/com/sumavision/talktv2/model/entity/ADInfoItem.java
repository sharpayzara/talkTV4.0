package com.sumavision.talktv2.model.entity;

import com.sumavision.talktv2.model.entity.decor.BaseData;

/**
 * Created by zhoutao on 2016/9/28.
 */
public class ADInfoItem extends BaseData{
    /**
     * adurl : http://1.tvfan.cn广告链接地址
     * picurl : http://www.tvfan.cn/resource/photo/tv/2016/09/29/10510837667.png广告图片地址
     */

    private ObjBean obj;

    public ObjBean getObj() {
        return obj;
    }

    public void setObj(ObjBean obj) {
        this.obj = obj;
    }

    public static class ObjBean {
        private String adurl;
        private String picurl;
        private String type;

        public String getAdurl() {
            return adurl;
        }

        public void setAdurl(String adurl) {
            this.adurl = adurl;
        }
        public String getStyle() {
            return type;
        }

        public void setStyle(String type) {
            this.type = type;
        }

        public String getPicurl() {
            return picurl;
        }

        public void setPicurl(String picurl) {
            this.picurl = picurl;
        }
    }
}
