package com.sumavision.talktv2.model.entity;

/**
 * Created by zhangyisu on 2016/7/11.
 */
public class ShowEditButtonEvent {
    public int item;
    public boolean show;

    public ShowEditButtonEvent(int item, boolean show) {
        this.item = item;
        this.show = show;
    }
}
