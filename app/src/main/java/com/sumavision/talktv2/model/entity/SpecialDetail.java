package com.sumavision.talktv2.model.entity;

import com.sumavision.talktv2.model.entity.decor.BaseData;

/**
 * Created by sharpay on 16-9-7.
 */
public class SpecialDetail extends BaseData{

    private String brtxt;
    private String desc;
    private String id;
    private String name;
    private String picture2;
    private String style;

    public String getBrtxt() {
        return brtxt;
    }

    public void setBrtxt(String brtxt) {
        this.brtxt = brtxt;
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

    public String getPicture2() {
        return picture2;
    }

    public void setPicture2(String picture2) {
        this.picture2 = picture2;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
