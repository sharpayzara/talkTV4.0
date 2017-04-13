package com.sumavision.talktv2.model;

import com.sumavision.talktv2.model.entity.decor.LiveData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zjx on 2016/6/21.
 */
public interface LiveChannelListDialogModel extends BaseModel {
    int getSelector(String channelId, HashMap<Integer, ArrayList<LiveData.ContentBean.TypeBean.ChannelBean>> channelDatas);
}
