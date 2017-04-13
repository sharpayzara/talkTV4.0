package com.sumavision.talktv2.presenter;

import android.content.Context;
import android.util.Log;

import com.jiongbull.jlog.JLog;
import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.HomeModel;
import com.sumavision.talktv2.model.entity.ADInfoItem;
import com.sumavision.talktv2.model.entity.decor.ClassifyData;
import com.sumavision.talktv2.model.entity.decor.ClassifyUpdataData;
import com.sumavision.talktv2.model.impl.HomeModelmpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.IRecommendView;
import com.sumavision.talktv2.util.NetworkUtil;

public class RecommendPresenter extends BasePresenter<IRecommendView> {
    HomeModel homeModel;
    Context mContext ;
    public RecommendPresenter(Context context, IRecommendView iView) {
        super(context, iView);
        homeModel = new HomeModelmpl();
        mContext = context;
    }

    @Override
    public void release() {
        homeModel.release();
    }
    public void getClassifyData(){
        iView.showProgressBar();
        homeModel.loadClassifys(new CallBackListener<ClassifyData>() {

            @Override
            public void onSuccess(ClassifyData classifyData) {
                iView.hideProgressBar();
                if(classifyData == null){
                    //说明服务器没有分类的全量数据
                    iView.showClassifyView(null);
                    JLog.d("talkTV4.0", "当前未获取到全量数据");
                }else{
                    iView.showClassifyView(classifyData.results);
                }

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
    public void getClassifyUpdataData(){
        iView.showProgressBar();
        homeModel.loadClassifyUpdatas(new CallBackListener<ClassifyUpdataData>() {
            @Override
            public void onSuccess(ClassifyUpdataData classifyUpdataData) {
                iView.hideProgressBar();
                if(classifyUpdataData == null || classifyUpdataData.isCacheSource()){
                    // 说明服务器没有更新
                    //这里需要去走本地获取分类列表数据
                    iView.updataClassifyView(null);
                    Log.d("talkTV4.0", "当前未获取到分类更新数据");
                }else{
                    //说明服务器有更新
                    iView.updataClassifyView(classifyUpdataData.results);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                iView.hideProgressBar();
                iView.updataClassifyView(null);
                JLog.e(throwable.toString());
            }
        });
    }
    public void loadADInfo(Context context,String type){
        homeModel.loadADInfo(type, new CallBackListener<ADInfoItem>() {
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

}

