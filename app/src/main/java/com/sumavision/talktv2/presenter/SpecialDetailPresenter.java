package com.sumavision.talktv2.presenter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.jiongbull.jlog.JLog;
import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.MediaModel;
import com.sumavision.talktv2.model.PlayerDetailModel;
import com.sumavision.talktv2.model.PraiseModel;
import com.sumavision.talktv2.model.SpecialModel;
import com.sumavision.talktv2.model.UserModel;
import com.sumavision.talktv2.model.entity.MediaDetail;
import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.model.entity.PraiseData;
import com.sumavision.talktv2.model.entity.ProgramDetail;
import com.sumavision.talktv2.model.entity.ResultData;
import com.sumavision.talktv2.model.entity.SeriesDetail;
import com.sumavision.talktv2.model.entity.SpecialContentList;
import com.sumavision.talktv2.model.entity.SpecialDetail;
import com.sumavision.talktv2.model.impl.MediaModelImpl;
import com.sumavision.talktv2.model.impl.PlayerDetailModelImpl;
import com.sumavision.talktv2.model.impl.PraiseModelmpl;
import com.sumavision.talktv2.model.impl.SpecialModelImpl;
import com.sumavision.talktv2.model.impl.UserModelImpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.IMediaDetailView;
import com.sumavision.talktv2.ui.iview.ISpecialDetailView;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.sumavision.talktv2.util.NetworkUtil;

/**
 * Created by sharpay on 16-6-17.
 */
public class SpecialDetailPresenter extends BasePresenter<ISpecialDetailView>{
    SpecialModel model;
    PlayerDetailModel programModel;
    MediaModel mediaModel;
    PraiseModel praiseModel;
    UserModel userModel;
    public SpecialDetailPresenter(Context context, ISpecialDetailView iView) {
        super(context, iView);
        model = new SpecialModelImpl();
        programModel = new PlayerDetailModelImpl();
        mediaModel = new MediaModelImpl();
        praiseModel = new PraiseModelmpl();
        userModel = new UserModelImpl();
    }

    public PraiseData loadPraise(String programId){
        return praiseModel.loadPraiseData(programId);
    }

    public void clickPraise(PraiseData praiseData){
        praiseModel.saveLocalData(praiseData);
        praiseModel.sendPraiseData(praiseData);
    }
//    public void judgeNetwork(){
////        if(NetworkUtil.isConnectedByState(context) && !NetworkUtil.isWIFIConnected(context)){
////            Toast.makeText(context,"你当前使用的是非WIFI网络，请注意流量!",Toast.LENGTH_LONG).show();
////        }
//    }

    public void loadSpecialDetail(String id){
        iView.showProgressBar();
        model.loadSpecialDetail(id, new CallBackListener<SpecialDetail>() {
            @Override
            public void onSuccess(SpecialDetail specialDetail) {
                iView.hideProgressBar();
                iView.showDetailView(specialDetail);
            }

            @Override
            public void onFailure(Throwable throwable) {
                if (!NetworkUtil.isConnectedByState(BaseApp.getContext())) {
                    iView.showWifiView();
                }else {
                    iView.showErrorView();
                }
                JLog.d("talkTV4.0", "访问异常");
            }
        });
    }

    public void loadProgramDetail(String id){  //获取播放路径
        programModel.getDetailData(id, "", "", new CallBackListener<ProgramDetail>() {
            @Override
            public void onSuccess(ProgramDetail programDetail) {
                iView.fillProgramDetail(programDetail);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(context,"对不起，节目加载失败",Toast.LENGTH_SHORT).show();
                Log.e("error", throwable.toString());
            }
        });
    }

    public void getSeriesListValue(String id, String cpid, int page, int size, final int flag){  //flag=0 代表横向，flag=1 代表竖向
        programModel.getSeriesListData(id, cpid, page ,size, new CallBackListener<SeriesDetail>() {
            @Override
            public void onSuccess(SeriesDetail seriesDetail) {
                iView.fillSeriesVListValue(seriesDetail);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("error", throwable.toString());
            }
        });
    }

    public void loadSpecialContentList(String id,int page,int size){
        model.loadSpecialContentList(id,page,size, new CallBackListener<SpecialContentList>() {
            @Override
            public void onSuccess(SpecialContentList specialContentList) {
                iView.hideProgressBar();
                iView.fillSpecialList(specialContentList);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(context,"列表数据加载失败",Toast.LENGTH_SHORT).show();
                iView.hideProgressBar();
            }
        });
    }

    public void insertPlayHistory (PlayerHistoryBean playerHistoryBean) {
        mediaModel.insertPlayHistory(playerHistoryBean);
    }

    public void delPlayHistory (String programId) {
        mediaModel.delPlayHistory(programId);
    }

    public PlayerHistoryBean getPlayHistory (String programId) {
        return mediaModel.getPlayHistory(programId);
    }

    public void enterDetailLog(String id, Context context) {
        model.enterDetailLog(id, context);
    }

    public void pvLog(String id, Context context) {
        model.pvLog(id, context);
        sendIntegralLog("watch", id);
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
        programModel.release();
        mediaModel.release();
        praiseModel.release();
        userModel.release();
    }
}
