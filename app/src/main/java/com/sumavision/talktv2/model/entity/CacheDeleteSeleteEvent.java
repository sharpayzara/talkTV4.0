package com.sumavision.talktv2.model.entity;

/**
 * Created by zhangyisu on 2016/7/11.
 */
public class CacheDeleteSeleteEvent {
    public int deleteCount;
    public boolean selectAll;
    public CacheDeleteSeleteEvent(int size, boolean b) {
        this.deleteCount = size;
        this.selectAll = b;
    }
}
