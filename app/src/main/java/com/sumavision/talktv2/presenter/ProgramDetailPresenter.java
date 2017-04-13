package com.sumavision.talktv2.presenter;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.PlayerDetailModel;
import com.sumavision.talktv2.model.PraiseModel;
import com.sumavision.talktv2.model.UserModel;
import com.sumavision.talktv2.model.entity.CpDataNet;
import com.sumavision.talktv2.model.entity.DetailRecomendData;
import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.model.entity.PraiseData;
import com.sumavision.talktv2.model.entity.ProgramDetail;
import com.sumavision.talktv2.model.entity.ResultData;
import com.sumavision.talktv2.model.entity.SeriesDetail;
import com.sumavision.talktv2.model.impl.PlayerDetailModelImpl;
import com.sumavision.talktv2.model.impl.PraiseModelmpl;
import com.sumavision.talktv2.model.impl.UserModelImpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.IProgranDetailView;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.sumavision.talktv2.util.NetworkUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by sharpay on 16-6-12.
 */
public class ProgramDetailPresenter extends BasePresenter<IProgranDetailView>{
    private PlayerDetailModel model;
    private PraiseModel praiseModel;
    UserModel userModel;
    public ProgramDetailPresenter(Context context, IProgranDetailView iView) {
        super(context, iView);
        model = new PlayerDetailModelImpl();
        praiseModel = new PraiseModelmpl();
        userModel = new UserModelImpl();
    }

    @Override
    public void release() {
        model.release();
        praiseModel.release();
        userModel.release();
    }

    public void loadDetailData(String id,String mid, String cpId){
        iView.showProgressBar();
        model.getDetailData(id, mid, cpId, new CallBackListener<ProgramDetail>() {
            @Override
            public void onSuccess(ProgramDetail programDetail) {
                iView.fillDetailValue(programDetail);
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
        });
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

    public void getSeriesGridValue(String id,String cpid,String tab,CallBackListener<SeriesDetail> callBackListener){
        model.getSeriesGridData(id, cpid, tab, callBackListener);
    }

    public void getSeriesListValue(String id, String cpid, int page, int size,CallBackListener<SeriesDetail> callBackListener){
        model.getSeriesListData(id, cpid, page ,size,callBackListener);
    }

    public void getSeriesListValue(String id, String cpid, int page, int size, final int flag){  //flag=0 代表横向，flag=1 代表竖向
        model.getSeriesListData(id, cpid, page ,size, new CallBackListener<SeriesDetail>() {
            @Override
            public void onSuccess(SeriesDetail seriesDetail) {
                iView.hideProgressBar();
                iView.fillSeriesVListValue(seriesDetail);
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
        });
    }

    public void changeCp(String id,String mid, String cpId){
        model.getDetailData(id, "", cpId, new CallBackListener<ProgramDetail>() {
            @Override
            public void onSuccess(ProgramDetail o) {
                iView.changeCp(o);
            }

            @Override
            public void onFailure(Throwable throwable) {
                if (!NetworkUtil.isConnectedByState(BaseApp.getContext())) {
                    iView.showWifiView();
                }else {
                    iView.showErrorView();
                }
            }
        });
    }

    public void getRecommendData (String id, String cname) {
        model.getRecommendData(id, cname, new CallBackListener<DetailRecomendData>() {
            @Override
            public void onSuccess(DetailRecomendData o) {
                iView.fillRecommendData(o);
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }

    public void getCpData () {
        model.getCpData(new CallBackListener<CpDataNet>() {
            @Override
            public void onSuccess(CpDataNet cpDataNet) {
                iView.fillCpData(cpDataNet);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("error", throwable.toString());
            }
        });
    }

    public void downloadPic (String cpId, String picUrl) {
        File dirFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/.tvfanfile");
        File file = null;
        if(!dirFile.exists()){
            dirFile.mkdirs();
        }
        file = new File(dirFile, cpId+".jpg");
        model.download(new CallBackListener() {
            @Override
            public void onSuccess(Object o) {
            }

            @Override
            public void onFailure(Throwable throwable) {
            }
        }, picUrl, file, null);
    }

    public void enterDetailLog(String programId, Context context) {
        model.enterDedailLog(programId, context);
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

    public int getEpiPos (String epi, List<SeriesDetail.SourceBean> sourceBeens) {
        return model.getEpiPos(epi, sourceBeens);
    }
    public void sendIntegralLog(final String event, String idStr){
        if(AppGlobalVars.userInfo != null && !TextUtils.isEmpty(AppGlobalVars.userInfo.getUserId())){
            userModel.sendIntegralLog(new CallBackListener<ResultData>() {

                @Override
                public void onSuccess(ResultData resultData) {
                    if (resultData.getMsg() != null && resultData.getMsg().equals("success")) {
                        if (event.equals("like")) {
                            Toast.makeText(context, "点赞成功，获得积分+5", Toast.LENGTH_SHORT).show();
                        } else if (event.equals("share")) {
                            Toast.makeText(context, "分享成功，获得积分+10", Toast.LENGTH_SHORT).show();
                        } else if (event.equals("watch")) {
                            Toast.makeText(context, "播放，获得积分+10", Toast.LENGTH_SHORT).show();
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
