package com.sumavision.talktv2.model;

/**
 * Created by sharpay on 16-5-26.
 */
public interface YsqModel extends BaseModel{
    void loadYsqData(CallBackListener listener, int page,int size);
}
