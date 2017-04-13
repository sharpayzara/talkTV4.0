package com.sumavision.talktv2.model.entity.decor;

import com.google.gson.annotations.SerializedName;
import com.sumavision.talktv2.model.entity.HomeRecommendUpdate;

import java.util.List;

/**
 * 通用(Android ,ios,前端，拓展资源，休息视频)数据模型
 *
 */
public class HomeRecommendUpdateData extends BaseData {
   @SerializedName("items")
    public List<HomeRecommendUpdate> results;
    public String version;
    public int updated;
    @Override
    public String toString() {
        return "HomeRecommendData{" +
                "results=" + results +
                '}';
    }
}
