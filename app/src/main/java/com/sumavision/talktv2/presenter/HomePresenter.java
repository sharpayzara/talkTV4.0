package com.sumavision.talktv2.presenter;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import com.jiongbull.jlog.JLog;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.DuibaModel;
import com.sumavision.talktv2.model.HomeModel;
import com.sumavision.talktv2.model.UserModel;
import com.sumavision.talktv2.model.entity.ADInfoItem;
import com.sumavision.talktv2.model.entity.AppKeyResult;
import com.sumavision.talktv2.model.entity.LoginAddressData;
import com.sumavision.talktv2.model.entity.ResultData;
import com.sumavision.talktv2.model.entity.ScreenBean;
import com.sumavision.talktv2.model.entity.UserIntegeInfoItem;
import com.sumavision.talktv2.model.entity.decor.BaseData;
import com.sumavision.talktv2.model.impl.DuibaModelImpl;
import com.sumavision.talktv2.model.impl.HomeModelmpl;
import com.sumavision.talktv2.model.impl.UserModelImpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.IHomeView;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.sumavision.talktv2.util.NetworkUtil;
import com.sumavision.talktv2.util.UpdateUtil;

import java.io.File;

/**
 * Created by zhoutao on 2016/6/6.
 */
public class HomePresenter extends BasePresenter<IHomeView> {
    Context mContext;
    HomeModel model;
    UserModel userModel;
    DuibaModel duibaModel;
    public HomePresenter(Context context, IHomeView iView) {
        super(context, iView);
        mContext = context;
        model = new HomeModelmpl();
        userModel = new UserModelImpl();
        duibaModel = new DuibaModelImpl();
    }
    public void update(){
        new UpdateUtil().checkUpdate(mContext,false);
    }
    public void judgeNetwork(Context context){
        if(!NetworkUtil.isConnectedByState(context)){
            Toast.makeText(mContext,"未链接到网络，请检查网络设置",Toast.LENGTH_LONG).show();
        }
    }
    public void loadADInfo(Context context,String type){
        model.loadADInfo(type, new CallBackListener<ADInfoItem>() {
            @Override
            public void onSuccess(ADInfoItem adInfoItem) {
                iView.setADInfo(adInfoItem);
            }

            @Override
            public void onFailure(Throwable throwable) {
                JLog.e(throwable.toString());
            }
        });
    }

    public void loadScreenData(){
        model.loadScreenData(new CallBackListener<ScreenBean>() {
            @Override
            public void onSuccess(ScreenBean screenBean) {
                String pic = screenBean.getPicurl();
                String suffix = pic.substring(pic.lastIndexOf("."));
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), ".dowloading"+suffix);
                model.download(pic,file);
            }
            @Override
            public void onFailure(Throwable throwable) {
                JLog.e(throwable.toString());
            }
        });
    }


    public void loadAppKey(){
        if(AppGlobalVars.appKeyResult != null && AppGlobalVars.appKeyResult.getData().size() > 2){
            return;
        }
        userModel.loadAppKey(new CallBackListener<AppKeyResult>() {
            @Override
            public void onSuccess(AppKeyResult appKey) {
                AppGlobalVars.appKeyResult = appKey;
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }

    /**
     *  desc  每天进入一次app的积分
     *  @author  yangjh
     *  created at  16-10-12 下午2:02
     */
    public void sendOpenAppLog(){
        if(AppGlobalVars.userInfo != null && !TextUtils.isEmpty(AppGlobalVars.userInfo.getUserId())){
            userModel.sendIntegralLog(new CallBackListener<ResultData>() {

                @Override
                public void onSuccess(ResultData resultData) {
                    if(resultData.getMsg() != null && resultData.getMsg().equals("success")){
                        Toast.makeText(mContext,"欢迎回到电视粉，获得积分+10",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {

                }
            },"login",AppGlobalVars.userInfo.getUserId(),"");
        }
    }

    /**
     * 获取用户积分
     * @param userId
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

    /**
     * 获取兑吧登陆地址
     * @param userId
     * @param credits
     * @param currentUrl 兑吧具体活动页面的地址
     */
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

    public void sendScanData(String urlStr){
        model.sendScanData(urlStr,new CallBackListener<BaseData>() {
            @Override
            public void onSuccess(BaseData baseData) {
            }

            @Override
            public void onFailure(Throwable throwable) {
            }
        });
    }
    @Override
    public void release() {
        model.release();
        userModel.release();
        duibaModel.release();
    }
}
