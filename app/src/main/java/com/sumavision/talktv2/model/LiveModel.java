package com.sumavision.talktv2.model;


import android.content.Context;

import com.sumavision.talktv2.model.entity.decor.LiveData;

import java.util.ArrayList;

/**
 * 获取直播频道分类和列表的model
 * Created by zjx on 2016/6/3.
 */
public interface LiveModel extends BaseModel {
    void loadLiveListData(CallBackListener<LiveData> listener);

    void collectChannel(String channelId, ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> channelList,
                        ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> netChannelLis, int pos,
                        ArrayList<String> channelTypeList, CollectCallBackListener listener);

    void cancelCollect(String channelId, ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> coolectChannelDatas,
                       ArrayList<String> channelTypeList, CollectCallBackListener listener);

    void queryAllCollect(ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> netChannelLis,
                         ArrayList<String> channelTypeDatas, CollectCallBackListener listener);
    void favChangeForVideo(boolean fav, ArrayList<String> channelTypeNameDatas,
                           ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> channelDatasForCollect);
}
