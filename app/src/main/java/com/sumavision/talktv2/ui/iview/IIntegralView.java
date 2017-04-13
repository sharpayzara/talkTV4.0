package com.sumavision.talktv2.ui.iview;

import com.sumavision.talktv2.model.entity.UserIntegeInfoItem;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

/**
 *  desc  我的积分
 *  @author  yangjh
 *  created at  16-10-14 下午2:56
 */
public interface IIntegralView extends IBaseView {
    void enterIntegeteCenter(String url);
    void setUserIntegeInfo(UserIntegeInfoItem userIntegeInfoItem);
}
