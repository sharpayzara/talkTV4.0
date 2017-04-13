package com.sumavision.talktv2.model.entity;

import com.sumavision.talktv2.model.entity.decor.BaseData;

import java.util.ArrayList;

/**
 * Created by zhoutao on 2016/5/27.
 */
public class HomeRecommendUpdate extends BaseData {

    public String action;
    public String card_id;
    public String card_name;
    public String card_type;
    public int status;
    public String card_style;
    public int hasChange;
    public int hasMore;
    public ArrayList<HomeRecommendItem> items;
    public String style;
    public HomeRecommendUpdate(){

    }
}
