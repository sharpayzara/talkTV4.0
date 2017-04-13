package com.sumavision.talktv2.model;

import android.content.Context;

/**
 * Created by sharpay on 16-9-7.
 */
public interface SpecialModel extends BaseModel{
    void loadSpecialDetail(String id,CallBackListener listener);
    void loadSpecialContentList(String id,int page,int size,CallBackListener listener);

    void enterDetailLog(String id, Context context);
    void pvLog(String id,Context context);
}
