package com.sumavision.talktv2.model;

import com.sumavision.talktv2.model.entity.decor.LiveDetailData;

/**
 * Created by zjx on 2016/6/14.
 */
public interface LiveDetailModel extends BaseModel{
    void getLiveDetailData(String chnnelId, CallBackListener<LiveDetailData> listener);

    void favChannel (String channelId);

    void cancelChannel (String channelId);
}
