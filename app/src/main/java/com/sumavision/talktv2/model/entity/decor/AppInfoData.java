package com.sumavision.talktv2.model.entity.decor;


/**
 * 后台分类数据模型
 * Created by zhoutao on 2016/5/27.
 */
public class AppInfoData extends BaseData{
    public String packageName;//节目名
    public int version;//节目图片地址

    public AppInfoData(){}
    public AppInfoData(String packageName, int version){
        this.packageName = packageName;
        this.version = version;
    }
}
