package com.sumavision.talktv2.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.jiongbull.jlog.JLog;
import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.PlayerDetailModel;
import com.sumavision.talktv2.model.PraiseModel;
import com.sumavision.talktv2.model.SpecialModel;
import com.sumavision.talktv2.model.UserModel;
import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.model.entity.PraiseData;
import com.sumavision.talktv2.model.entity.ProgramDetail;
import com.sumavision.talktv2.model.entity.ResultData;
import com.sumavision.talktv2.model.entity.SpecialContentList;
import com.sumavision.talktv2.model.entity.SpecialDetail;
import com.sumavision.talktv2.model.entity.SpecialListData;
import com.sumavision.talktv2.model.impl.PlayerDetailModelImpl;
import com.sumavision.talktv2.model.impl.PraiseModelmpl;
import com.sumavision.talktv2.model.impl.SpecialModelImpl;
import com.sumavision.talktv2.model.impl.UserModelImpl;
import com.sumavision.talktv2.model.impl.WatchHistoryModelImpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.ISpecialView;
import com.sumavision.talktv2.ui.iview.ISplashView;
import com.sumavision.talktv2.ui.iview.base.IBaseView;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.sumavision.talktv2.util.NetworkUtil;

import java.util.ArrayList;

/**
 * Created by sharpay on 2016/6/6.
 */
public class SpecialPresenter extends BasePresenter<ISpecialView> {
    SpecialModel model;
    UserModel userModel;
    PraiseModel praiseModel;
    public SpecialPresenter(Context context, ISpecialView iView) {
        super(context, iView);
        model = new SpecialModelImpl();
        userModel = new UserModelImpl();
        praiseModel = new PraiseModelmpl();
    }

    public void clickPraise(PraiseData praiseData){
        praiseModel.saveLocalData(praiseData);
        praiseModel.sendPraiseData(praiseData);
    }

    public PraiseData loadPraise(String programId){
        return praiseModel.loadPraiseData(programId);
    }

    public void loadSpecialDetail(String id){
        iView.showProgressBar();
        model.loadSpecialDetail(id, new CallBackListener<SpecialDetail>() {
            @Override
            public void onSuccess(SpecialDetail specialDetail) {
                iView.showDetailView(specialDetail);
            }

            @Override
            public void onFailure(Throwable throwable) {
                JLog.d("talkTV4.0", "访问异常");
            }
        });
    }

    public void loadSpecialContentList(String id,int page,int size){
        iView.showProgressBar();
        model.loadSpecialContentList(id,page,size, new CallBackListener<SpecialContentList>() {
            @Override
            public void onSuccess(SpecialContentList specialContentList) {
                iView.hideProgressBar();
                iView.fillSpecialList(specialContentList);
            }

            @Override
            public void onFailure(Throwable throwable) {
                if (!NetworkUtil.isConnectedByState(BaseApp.getContext())) {
                    iView.showWifiView();
                }else {
                    iView.showErrorView();
                }
                JLog.d("talkTV4.0", "访问异常");
                Toast.makeText(context,"列表数据加载失败",Toast.LENGTH_SHORT).show();
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
    @Override
    public void release() {
        model.release();
        userModel.release();
        praiseModel.release();
    }
}
