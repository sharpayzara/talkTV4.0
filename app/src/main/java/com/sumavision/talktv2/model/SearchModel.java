package com.sumavision.talktv2.model;

/**
 * Created by sharpay on 16-5-26.
 */
public interface SearchModel extends BaseModel{
    void loadSearchListInfo(CallBackListener listener,String q,int page);
    void loadSearchHotData(CallBackListener listener);
    void loadHintData(CallBackListener listener, String msg);
}
