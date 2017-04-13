package com.sumavision.talktv2.ui.iview;

import com.sumavision.talktv2.model.entity.ADInfoItem;
import com.sumavision.talktv2.model.entity.UserIntegeInfoItem;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

/**
 * Created by zjx on 2016/5/31.
 */
public interface IHomeView extends IBaseView {
    void setADInfo(ADInfoItem adInfo);
    void setUserIntegeInfo(UserIntegeInfoItem userIntegeInfoItem);
    void setLoginAddress(String intege);
}
