package com.sumavision.talktv2.model.entity.decor;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * 后台分类数据模型
 * Created by zhoutao on 2016/5/27.
 */
public class SearchInfoData extends BaseData{
    @SerializedName("items")
    public ArrayList<SearchInfoItem> results;
    public int total;
}
