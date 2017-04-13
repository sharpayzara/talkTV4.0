package com.sumavision.talktv2.model;

import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import java.util.List;

/**
 *  desc  历史记录查看
 *  @author  yangjh
 *  created at  16-12-8 上午3:25
 */

public interface WatchHistoryModel<T> extends BaseModel{
    PlayerHistoryBean getPlayHistory (String programId) ;
    void delPlayHistory (Integer programId);
    void getAll(CallBackListener<List<PlayerHistoryBean>> callback);
}
