package com.sumavision.talktv2.presenter;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.jiongbull.jlog.JLog;
import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.HomeModel;
import com.sumavision.talktv2.model.PreferenceModel;
import com.sumavision.talktv2.model.UserModel;
import com.sumavision.talktv2.model.entity.LoginResult;
import com.sumavision.talktv2.model.entity.PreferenceBean;
import com.sumavision.talktv2.model.entity.ResultData;
import com.sumavision.talktv2.model.entity.ScreenBean;
import com.sumavision.talktv2.model.entity.UserInfo;
import com.sumavision.talktv2.model.impl.HomeModelmpl;
import com.sumavision.talktv2.model.impl.PreferenceModelImpl;
import com.sumavision.talktv2.model.impl.UserModelImpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.ILoginView;
import com.sumavision.talktv2.ui.iview.IUserInfoView;
import com.sumavision.talktv2.ui.iview.base.IBaseView;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.NetworkUtil;
import com.sumavision.talktv2.util.UpdateUtil;

import java.io.File;

/**
 * Created by sharpay on 2016/8/23.
 */
public class UserInfoPresenter extends BasePresenter<IUserInfoView> {
    Context mContext;
    UserModel userModel;
    PreferenceModel preferenceModel;
    public UserInfoPresenter(Context context, IUserInfoView iView) {
        super(context, iView);
        userModel =  new UserModelImpl();
        preferenceModel = new PreferenceModelImpl();
    }

    @Override
    public void release() {
        userModel.release();
    }

    public void uploadImg(File file){
        userModel.uploadImg(file, new CallBackListener<ResultData>() {
            @Override
            public void onSuccess(ResultData resultData) {
                if(resultData.getMsg().equals("success")){
                    AppGlobalVars.userInfo.setImageUrl(resultData.getObj());
                    userModel.saveUserInfo();
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(context,"头像上传失败,请重试！",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void saveAndUpdateUserInfo(final UserInfo tempUserInfo, final boolean isExit){
        userModel.saveAndUpdateUserInfo(new CallBackListener<ResultData>() {
            @Override
            public void onSuccess(ResultData resultData) {
                if(isExit){
                    iView.loginOut();
                }else{
                    Toast.makeText(context,"用户信息保存成功",Toast.LENGTH_SHORT).show();
                    BusProvider.getInstance().post("CHANGENAME",AppGlobalVars.userInfo.getNickName());
                    release();
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                if(isExit){
                    iView.loginOut();
                    return;
                }
                AppGlobalVars.userInfo = tempUserInfo;
                if (!NetworkUtil.isConnectedByState(BaseApp.getContext())) {
                    Toast.makeText(context,"保存失败,未链接到网络！",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context,"保存失败,请稍后再试",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void savePreferenceStr(PreferenceBean bean){
        preferenceModel.savePreferenceStr(bean);
    }

    public UserInfo getUserInfo() {
        return userModel.getUserInfo();
    }

    public boolean logoutUser() {
        preferenceModel.clearPreferenceStr();
        return userModel.logoutUser();
    }

    public PreferenceBean loadPreferenceData(){
        return preferenceModel.loadPreferenceStr();
    }
}
