package com.sumavision.talktv2.model;

import com.sumavision.talktv2.model.entity.PreferenceBean;

/**
 * Created by sharpay on 16-7-20.
 */
public interface PreferenceModel extends BaseModel{
    PreferenceBean loadPreferenceStr();
    void savePreferenceStr(PreferenceBean bean);
    void sendPreferenceStr(PreferenceBean bean);
    void sendFeedBack(String problem,String suggest,String contact);
    void clearPreferenceStr();
}
