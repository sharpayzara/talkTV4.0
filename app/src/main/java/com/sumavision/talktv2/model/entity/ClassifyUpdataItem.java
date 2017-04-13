package com.sumavision.talktv2.model.entity;

import com.sumavision.talktv2.model.entity.decor.BaseData;

/**
 * 这是具体更新分类的javabean
 * Created by zhoutao on 2016/5/27.
 */
public class ClassifyUpdataItem extends BaseData {
    public int isFixed;//是否固定
    public String name;//分类名
    public String navId;//id
    public String picture;//分类图片地址
    public String type;//分类图片地址
    public int status;//本次更新是增加还是删除操作
}
