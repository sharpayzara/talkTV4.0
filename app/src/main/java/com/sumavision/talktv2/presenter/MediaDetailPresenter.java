package com.sumavision.talktv2.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.MediaModel;
import com.sumavision.talktv2.model.PraiseModel;
import com.sumavision.talktv2.model.UserModel;
import com.sumavision.talktv2.model.entity.AppKeyResult;
import com.sumavision.talktv2.model.entity.MediaDetail;
import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.model.entity.PraiseData;
import com.sumavision.talktv2.model.entity.ResultData;
import com.sumavision.talktv2.model.impl.MediaModelImpl;
import com.sumavision.talktv2.model.impl.PraiseModelmpl;
import com.sumavision.talktv2.model.impl.UserModelImpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.IMediaDetailView;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.sumavision.talktv2.util.NetworkUtil;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * Created by sharpay on 16-6-17.
 */
public class MediaDetailPresenter extends BasePresenter<IMediaDetailView>{
    MediaModel model;
    UserModel userModel;
    private PraiseModel praiseModel;
    public MediaDetailPresenter(Context context, IMediaDetailView iView) {
        super(context, iView);
        model = new MediaModelImpl();
        praiseModel = new PraiseModelmpl();
        userModel = new UserModelImpl();
    }

//    public void judgeNetwork(){
////        if(NetworkUtil.isConnectedByState(context) && !NetworkUtil.isWIFIConnected(context)){
////            Toast.makeText(context,"你当前使用的是非WIFI网络，请注意流量!",Toast.LENGTH_LONG).show();
////        }
//    }
    public PraiseData loadPraise(String programId){
        return praiseModel.loadPraiseData(programId);
    }

    public void clickPraise(PraiseData praiseData){
        praiseModel.saveLocalData(praiseData);
        praiseModel.sendPraiseData(praiseData);
    }
    public void loadMediaDetail (String id) {
        model.loadMediaDetail(new CallBackListener<MediaDetail>() {
            @Override
            public void onSuccess(MediaDetail mediaDetail) {
                iView.loadDetailValue(mediaDetail);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("error", throwable.toString());
            }
        },id);
    }
    public void getMediaDetail(String vid){
        iView.showProgressBar();
        model.loadMediaDetail(new CallBackListener<MediaDetail>() {
            @Override
            public void onSuccess(MediaDetail mediaDetail) {
                iView.hideProgressBar();
                iView.fillDetailValue(mediaDetail);
            }

            @Override
            public void onFailure(Throwable throwable) {
                iView.hideProgressBar();
                if (!NetworkUtil.isConnectedByState(BaseApp.getContext())) {
                    iView.showWifiView();
                }else {
                    iView.showErrorView();
                }
                Log.e("error", throwable.toString());
            }
        },vid);
    }

    public void enterDetailLog(String id, Context context) {
        model.enterDetailLog(id, context);
    }

    public void pvLog(String id, Context context) {
        model.pvLog(id, context);
        sendIntegralLog("watch", id);
    }

    public PlayerHistoryBean getPlayHistory (String programId) {
        return model.getPlayHistory(programId);
    }

    public void insertPlayHistory (PlayerHistoryBean playerHistoryBean) {
        model.insertPlayHistory(playerHistoryBean);
    }

    public void delPlayHistory (String programId) {
        model.delPlayHistory(programId);
    }
    @Override
    public void release() {
        model.release();
        userModel.release();
        praiseModel.release();
    }

    public void loadAppKey(final SHARE_MEDIA plat){
        if(AppGlobalVars.appKeyResult != null && AppGlobalVars.appKeyResult.getData().size() > 2){
            iView.share3d(plat);
            return;
        }
        iView.showProgressBar();
        userModel.loadAppKey(new CallBackListener<AppKeyResult>() {
            @Override
            public void onSuccess(AppKeyResult appKey) {
                AppGlobalVars.appKeyResult = appKey;
                iView.hideProgressBar();
                iView.share3d(plat);
            }

            @Override
            public void onFailure(Throwable throwable) {
                iView.hideProgressBar();
                Toast.makeText(context,"请求失败,请重试！",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendIntegralLog(final String event, String idStr){
        if(AppGlobalVars.userInfo != null && !TextUtils.isEmpty(AppGlobalVars.userInfo.getUserId())){
            userModel.sendIntegralLog(new CallBackListener<ResultData>() {

                @Override
                public void onSuccess(ResultData resultData) {
                    if(resultData.getMsg() != null && resultData.getMsg().equals("success")){
                        if(event.equals("like")){
                            Toast.makeText(context,"点赞成功，获得积分+5",Toast.LENGTH_SHORT).show();
                        }else if(event.equals("share")){
                            Toast.makeText(context,"分享成功，获得积分+10",Toast.LENGTH_SHORT).show();
                        }
                        else if(event.equals("watch")) {
                            Toast.makeText(context,"播放成功，获得积分+10",Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {

                }
            },event,AppGlobalVars.userInfo.getUserId(),idStr);
        }
    }
}
