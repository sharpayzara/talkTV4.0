package com.sumavision.talktv2.model.entity.decor;

/**
 * Created by zjx on 2016/6/6.
 */
public class LiveRquestBean {
    public String client;
    public String channelId;

    public LiveRquestBean(String client, String channelId) {
        this.client = client;
        this.channelId = channelId;
    }
}
