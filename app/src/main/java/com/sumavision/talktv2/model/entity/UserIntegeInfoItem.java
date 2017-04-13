package com.sumavision.talktv2.model.entity;

import com.sumavision.talktv2.model.entity.decor.BaseData;

/**
 * Created by zhoutao on 2016/10/9.
 */
public class UserIntegeInfoItem extends BaseData{

    /**
     * msg : success
     * obj : 10150
     */

    private String msg;
    private int obj;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getObj() {
        return obj;
    }

    public void setObj(int obj) {
        this.obj = obj;
    }
}
