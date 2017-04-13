package com.sumavision.talktv2.model.entity.decor;

import java.io.Serializable;

/**
 * Created by zhoutao on 2016/6/22.
 */
public class CachingDataItem implements Serializable {
    public int id;//节目id
    public String title;//节目名
    public String img;//节目图片地址
    public String info;//节目描述信息
    public boolean isCheck;//是否被选中

    public CachingDataItem(int id, String title, String img,String info,boolean isCheck){
        this.id = id;
        this.title = title;
        this.img=img;
        this.info = info;
        this.isCheck = isCheck;
    }
}
