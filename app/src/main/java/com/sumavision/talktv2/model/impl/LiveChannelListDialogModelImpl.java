package com.sumavision.talktv2.model.impl;

import android.text.TextUtils;

import com.sumavision.talktv2.model.LiveChannelListDialogModel;
import com.sumavision.talktv2.model.entity.decor.LiveData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zjx on 2016/6/21.
 */
public class LiveChannelListDialogModelImpl implements LiveChannelListDialogModel{
    @Override
    public int getSelector(String channelId, HashMap<Integer, ArrayList<LiveData.ContentBean.TypeBean.ChannelBean>> channelDatas) {
        if(TextUtils.isEmpty(channelId))
            return  0;
        int count = channelDatas.size();
        for(int i=0; i<count; i++) {
            ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> channelBeens = channelDatas.get(i);
            int count1 = channelBeens.size();
            for(int j=0; j<count1; j++) {
                String id = channelBeens.get(j).getId();
                if (id.equals(channelId)) {
                    return i;
                }
            }
        }
        return 0;
    }

    @Override
    public void release() {

    }
}
