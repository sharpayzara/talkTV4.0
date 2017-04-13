package com.sumavision.talktv2.model;

import com.sumavision.talktv2.model.entity.decor.LiveData;

import java.util.ArrayList;

/**
 * Created by zjx on 2016/6/14.
 */
public interface CollectCallBackListener<T> {
    void liveSuccess(ArrayList<String> channelTypeList, ArrayList<T> channelList);

    void vodSucceess(ArrayList<T> historyList);
}
