package com.sumavision.talktv2.presenter;

import android.content.Context;

import com.jiongbull.jlog.JLog;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.DuibaModel;
import com.sumavision.talktv2.model.UserModel;
import com.sumavision.talktv2.model.WatchHistoryModel;
import com.sumavision.talktv2.model.entity.LoginAddressData;
import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.model.entity.UserInfo;
import com.sumavision.talktv2.model.entity.UserIntegeInfoItem;
import com.sumavision.talktv2.model.impl.DuibaModelImpl;
import com.sumavision.talktv2.model.impl.UserModelImpl;
import com.sumavision.talktv2.model.impl.WatchHistoryModelImpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.IUserView;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户中心界面的Presenter
 * Created by zjx on 2016/5/31.
 */
public class UserPresenter extends BasePresenter<IUserView> {
    UserModel userModel;
    DuibaModel duibaModel;
    WatchHistoryModel historyModel;
    public UserPresenter(Context context, IUserView iView) {
        super(context, iView);
        userModel = new UserModelImpl();
        duibaModel = new DuibaModelImpl();
        historyModel = new WatchHistoryModelImpl();
    }

    public void getHistoryData(){
        historyModel.getAll(new CallBackListener<List<PlayerHistoryBean>>() {
            @Override
            public void onSuccess(List<PlayerHistoryBean> beanList) {
                iView.showListData(beanList);
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }

    public UserInfo getUserInfo(){
        return userModel.getUserInfo();
    }
    @Override
    public void release() {
        userModel.release();
        duibaModel.release();
        historyModel.release();
    }
  /*  public void downloadPic (String cpId, String picUrl) {
        File file = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS), cpId + ".jpg");
        userModel.download(new CallBackListener() {
            @Override
            public void onSuccess(Object o) {
            }

            @Override
            public void onFailure(Throwable throwable) {
            }
        }, picUrl, file, null);
    }
*/
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

    public void getLoginAddress(String userId,String credits,String currentUrl){
        duibaModel.loadLoginAddress(new CallBackListener<LoginAddressData>() {
            @Override
            public void onSuccess(LoginAddressData loginAddressData) {
                iView.setLoginAddress(loginAddressData.getObj());
            }

            @Override
            public void onFailure(Throwable throwable) {
                JLog.e("request faild");
            }
        },userId,credits,currentUrl);
    }
}
