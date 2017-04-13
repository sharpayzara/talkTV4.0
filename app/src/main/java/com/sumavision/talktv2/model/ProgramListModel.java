package com.sumavision.talktv2.model;

import java.util.Map;

/**
 * Created by sharpay on 16-6-20.
 */
public interface ProgramListModel extends BaseModel {
    void getProgramListTopic(final String id, final CallBackListener listener);
    void getProgramListData(final String tid, final String cname,final Integer page,final Integer size,final CallBackListener listener);
    void getProgramSelectionData(final String cid, final CallBackListener listener);
    void getProgramListSelectionData(final Map<String,String> map, final CallBackListener listener);
}
