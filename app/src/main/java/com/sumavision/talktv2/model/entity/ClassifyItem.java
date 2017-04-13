package com.sumavision.talktv2.model.entity;

import com.sumavision.talktv2.model.entity.decor.BaseData;

/**
 * Created by zhoutao on 2016/5/27.
 */
public class ClassifyItem extends BaseData{
    public int isFixed;//是否固定
    public String name;//分类名
    public String navId;//id
    public String picture;
    public String type;

    public ClassifyItem(int isFixed,String name,String navId,String picture,String type){
        this.isFixed = isFixed;
        this.name=name;
        this.navId = navId;
        this.picture = picture;
        this.type = type;
    }
}
