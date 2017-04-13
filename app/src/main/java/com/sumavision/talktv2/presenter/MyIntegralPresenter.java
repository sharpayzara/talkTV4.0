package com.sumavision.talktv2.presenter;

import android.content.Context;

import com.jiongbull.jlog.JLog;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.DuibaModel;
import com.sumavision.talktv2.model.UserModel;
import com.sumavision.talktv2.model.entity.LoginAddressData;
import com.sumavision.talktv2.model.entity.UserIntegeInfoItem;
import com.sumavision.talktv2.model.impl.DuibaModelImpl;
import com.sumavision.talktv2.model.impl.UserModelImpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.IIntegralView;

/**
 * Created by sharpay on 2016/6/6.
 */
public class MyIntegralPresenter extends BasePresenter<IIntegralView> {
    UserModel userModel;
    DuibaModel duibaModel;
    public MyIntegralPresenter(Context context, IIntegralView iView) {
        super(context, iView);
        userModel = new UserModelImpl();
        duibaModel = new DuibaModelImpl();
    }

    public void getLoginAddress(String userId,String credits,String currentUrl){
        duibaModel.loadLoginAddress(new CallBackListener<LoginAddressData>() {
            @Override
            public void onSuccess(LoginAddressData loginAddressData) {
                iView.enterIntegeteCenter(loginAddressData.getObj());
            }

            @Override
            public void onFailure(Throwable throwable) {
                JLog.e("request faild");
            }
        },userId,credits,currentUrl);
    }
    @Override
    public void release() {
        userModel.release();
        duibaModel.release();
    }
    public void getUserIntegtion(String userId){
        duibaModel.getUserIntegtion(new CallBackListener<UserIntegeInfoItem>() {
            @Override
            public void onSuccess(UserIntegeInfoItem userIntegeInfoItem) {
                iView.setUserIntegeInfo(userIntegeInfoItem);
            }

            @Override
            public void onFailure(Throwable throwable) {
                JLog.e("request faild");
            }
        },userId);
    }

}
