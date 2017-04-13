package com.sumavision.talktv2.model;


import android.content.Context;

import com.sumavision.talktv2.model.entity.MediaDetail;
import com.sumavision.talktv2.model.entity.MediaList;
import com.sumavision.talktv2.model.entity.MediaTopic;
import com.sumavision.talktv2.model.entity.PlayerHistoryBean;

/**
 * 获取直播频道分类和列表的model
 * Created by zjx on 2016/6/3.
 */
public interface MediaModel extends BaseModel {
    void loadMediaTopic(CallBackListener<MediaTopic> listener);

    void loadMediaList(CallBackListener<MediaList> listener,Integer page,Integer size,String txt);

    void loadMediaDetail(CallBackListener<MediaDetail> listener, String vid);
    PlayerHistoryBean getPlayHistory (String programId);
    void insertPlayHistory (PlayerHistoryBean playerHistoryBean);
    void delPlayHistory (String programId);
    void enterDetailLog(String id, Context context);
    void pvLog(String id,Context context);
}
