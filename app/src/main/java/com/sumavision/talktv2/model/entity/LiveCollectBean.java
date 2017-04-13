package com.sumavision.talktv2.model.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 直播收藏的
 * Created by zjx on 2016/6/13.
 */
@DatabaseTable(tableName = "live_collect")
public class LiveCollectBean {
    @DatabaseField
    private String channelId;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

}
