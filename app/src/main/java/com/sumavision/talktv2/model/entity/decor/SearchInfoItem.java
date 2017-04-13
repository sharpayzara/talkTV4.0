package com.sumavision.talktv2.model.entity.decor;

import java.io.Serializable;

/**
 * Created by zhoutao on 2016/6/26.
 */
public class SearchInfoItem extends BaseData implements Serializable {
    public String code;//节目id
    public String name;//节目名
    public float score;//评分
    public String actor;//演员
    public String picUrl;//图片地址
    public String cate;//类型
    public String videoType;//类型
    public String sdkType;//类型
    public String type;//类型
    public String year;//类型
}
