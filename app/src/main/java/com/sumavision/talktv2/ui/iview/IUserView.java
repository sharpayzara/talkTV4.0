package com.sumavision.talktv2.ui.iview;

import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.model.entity.UserIntegeInfoItem;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

import java.util.List;

/**
 * 用户中心界面的IView
 * Created by zjx on 2016/5/31.
 */
public interface IUserView extends IBaseView {
    void setUserIntegeInfo(UserIntegeInfoItem userIntegeInfoItem);
    void setLoginAddress(String url);
    void showListData(List<PlayerHistoryBean> beanList);
}
