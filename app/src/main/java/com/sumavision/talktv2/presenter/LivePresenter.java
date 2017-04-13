package com.sumavision.talktv2.presenter;

import android.content.Context;
import android.util.Log;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.CollectCallBackListener;
import com.sumavision.talktv2.model.LiveModel;
import com.sumavision.talktv2.model.entity.decor.LiveData;
import com.sumavision.talktv2.model.impl.LiveModelmpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.ILiveView;
import com.sumavision.talktv2.util.NetworkUtil;

import java.util.ArrayList;

/**
 * Created by zjx on 2016/5/31.
 */
public class LivePresenter extends BasePresenter<ILiveView> {

    private LiveModel liveModel;
    private Context context;
    public LivePresenter(Context context, ILiveView iView) {
        super(context, iView);
        liveModel = new LiveModelmpl();
        this.context = context;
    }

    /**
     *获取直播type和list数据
     */
    public void getLiveData () {
        iView.showProgressBar();
        liveModel.loadLiveListData(new CallBackListener<LiveData>() {
            @Override
            public void onSuccess(LiveData liveData) {
                iView.hideProgressBar();
                if(liveData != null && "success".equals(liveData.getMsg())){
                    if(liveData.getContent() == null){
                        iView.showEmptyView();
                    }
                    iView.getLiveData(liveData.getContent());
                    iView.hideProgressBar();
                }else{
                    iView.showErrorView();
                }
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

    /**
     * 收藏频道
     */
    public void collectChannel (String channelId, ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> collectChannelList,
                                ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> netChannelList, int pos,  ArrayList<String> channelTypeList) {

        liveModel.collectChannel(channelId, collectChannelList, netChannelList, pos, channelTypeList, new CollectCallBackListener<LiveData.ContentBean.TypeBean.ChannelBean>() {
            @Override
            public void liveSuccess(ArrayList<String> channelTypeList, ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> channelList) {
                iView.collectSuccess(channelTypeList, channelList);
            }

            @Override
            public void vodSucceess(ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> historyList) {

            }
        });
    }

    public void cacelFav (String channelId, ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> collectChannelDatas,
                          ArrayList<String> channelTypeList) {
        liveModel.cancelCollect(channelId, collectChannelDatas, channelTypeList, new CollectCallBackListener<LiveData.ContentBean.TypeBean.ChannelBean>() {
            @Override
            public void liveSuccess(ArrayList<String> channelTypeList, ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> channelList) {
                iView.cancelCollect();
            }

            @Override
            public void vodSucceess(ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> historyList) {

            }
        });
    }

    public void queryAllCollect(ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> netChannelDatas, ArrayList<String> channelTypeList) {
       liveModel.queryAllCollect(netChannelDatas, channelTypeList, new CollectCallBackListener<LiveData.ContentBean.TypeBean.ChannelBean>() {
            @Override
            public void liveSuccess(ArrayList<String> channelTypeList, ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> channelList) {
                iView.queryAllCollect(channelTypeList, channelList);
            }

            @Override
            public void vodSucceess(ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> historyList) {

            }
        });
    }

    public void favChangeForVideo(boolean fav, ArrayList<String> channelTypeNameDatas,
                                  ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> channelDatasForCollect) {
        liveModel.favChangeForVideo(fav, channelTypeNameDatas, channelDatasForCollect);
    }

    public int getPos(String channelId, ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> channelDatas) {
        int count = channelDatas.size();
        for(int i=0; i<count; i++) {
            if(channelId.equals(channelDatas.get(i).getId()))
                return i;
        }
        return 0;
    }

    @Override
    public void release() {
        liveModel.release();
    }
}
