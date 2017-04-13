package com.sumavision.talktv2.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.CollectCallBackListener;
import com.sumavision.talktv2.model.HomeModel;
import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.model.entity.SpecialListData;
import com.sumavision.talktv2.model.entity.decor.HomeRecommendData;
import com.sumavision.talktv2.model.entity.decor.HomeRecommendUpdateData;
import com.sumavision.talktv2.model.impl.HomeModelmpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.IHomeRecommendFragmentView;
import com.sumavision.talktv2.util.CommonUtil;
import com.sumavision.talktv2.util.NetworkUtil;

import java.util.ArrayList;

/**
 * Created by zhoutao on 16-6-27.
 */
public class HomeFragmentPresenter extends BasePresenter<IHomeRecommendFragmentView> {
    HomeModel model;

    Context mContext;
    private final int HIDE_HISTORY = 021456;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HIDE_HISTORY:
                    sendOrMoveHistoryHideMsg(false);
                    iView.hideHistory();
                    break;
            }
        }
    };

    public HomeFragmentPresenter(Context context, IHomeRecommendFragmentView iView) {
        super(context, iView);
        model = new HomeModelmpl();
        mContext = context;
    }

    @Override
    public void release() {
        model.release();
    }

    public void loadHomeRecommendData(final String nid){
        iView.showProgressBar();
        model.loadHomeRecommendData(new CallBackListener<HomeRecommendData>() {
            @Override
            public void onSuccess(HomeRecommendData homeRecommendData) {
                if (homeRecommendData.results.size() == 0) {
                    iView.showEmptyView();
                } else {
                    iView.showListView(homeRecommendData,nid);
                }
                iView.hideProgressBar();
            }

            @Override
            public void onFailure(Throwable throwable) {
                iView.showErrorView();
            }
        },nid);
    }
    public void loadHomeRecommendUpdateData(final String nid,final String v){
        iView.showProgressBar();
        model.loadHomeRecommendUpdateData(new CallBackListener<HomeRecommendUpdateData>() {
            @Override
            public void onSuccess(HomeRecommendUpdateData homeRecommendUpdateData) {
                iView.updateListView(homeRecommendUpdateData,nid,v);
                iView.hideProgressBar();
            }

            @Override
            public void onFailure(Throwable throwable) {
                iView.showErrorView();
                HomeRecommendData  homeData = (HomeRecommendData) BaseApp.getACache().getAsObject("recommendList");
                if (!CommonUtil.isNetworkConnected(mContext)){
                    iView.showListView(homeData,"rehp3t");
                }
            }
        },nid,v);
    }

    public void getHistoryData() {
        model.getPlayHistory(new CollectCallBackListener<PlayerHistoryBean>() {
            @Override
            public void liveSuccess(ArrayList<String> channelTypeList, ArrayList<PlayerHistoryBean> channelList) {
            }
            @Override
            public void vodSucceess(ArrayList<PlayerHistoryBean> historyList) {
                iView.showHistory(historyList.get(0));
            }
        });
    }

    public void sendOrMoveHistoryHideMsg(boolean send) {
        if(send)
            handler.sendEmptyMessageDelayed(HIDE_HISTORY, 4000L);
        else
            handler.removeMessages(HIDE_HISTORY);
    }
    public void loadSpecialListData(int page,int size){
        model.loadSpecialData(page, size, new CallBackListener<SpecialListData>() {
            @Override
            public void onSuccess(SpecialListData specialListData) {
                iView.hideProgressBar();
                iView.showSpecialView(specialListData);
            }

            @Override
            public void onFailure(Throwable throwable) {
                if (!NetworkUtil.isConnectedByState(BaseApp.getContext())) {
                    iView.showWifiView();
                } else {
                    iView.showErrorView();
                }
            }
        });
    }
}
