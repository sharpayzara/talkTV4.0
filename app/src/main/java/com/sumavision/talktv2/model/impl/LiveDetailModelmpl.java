package com.sumavision.talktv2.model.impl;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.dao.RxDao;
import com.sumavision.talktv2.http.LiveRetrofit;
import com.sumavision.talktv2.http.SumaClient;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.LiveDetailModel;
import com.sumavision.talktv2.model.entity.LiveCollectBean;
import com.sumavision.talktv2.model.entity.decor.LiveDetailData;
import com.sumavision.talktv2.model.entity.decor.LiveRquestBean;
import com.sumavision.talktv2.util.AppGlobalVars;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * 直播详情界面的model
 * Created by zjx on 2016/6/14.
 */
public class LiveDetailModelmpl implements LiveDetailModel {
    Subscription subscription;
    private RxDao collectUtil = new RxDao(BaseApp.getContext(), LiveCollectBean.class);

    /**
     * 获取直播频道详情
     * @param chnnelId
     * @param listener
     */
    @Override
    public void getLiveDetailData(final String chnnelId, final CallBackListener<LiveDetailData> listener) {
        subscription = SumaClient.subscribe(new Callable<Observable<LiveDetailData>>() {
            @Override
            public Observable<LiveDetailData> call() throws Exception {
                LiveRquestBean client = new LiveRquestBean("1", chnnelId);
                return SumaClient.getRetrofitInstance(LiveRetrofit.class, AppGlobalVars.liveApiHost).getLiveDetailData(client);
            }
        }, new Action1<LiveDetailData>() {
            @Override
            public void call(LiveDetailData liveDetailData) {
                listener.onSuccess(liveDetailData);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        }, LiveDetailData.class);
    }

    @Override
    public void favChannel(String channelId) {
        LiveCollectBean liveCollectBean = new LiveCollectBean();
        liveCollectBean.setChannelId(channelId);
        collectUtil.insert(liveCollectBean);
    }

    @Override
    public void cancelChannel(String channelId) {
        LiveCollectBean liveCollectBean = new LiveCollectBean();
        liveCollectBean.setChannelId(channelId);
        collectUtil.deleteByColumnName("channelId", channelId);
    }

    @Override
    public void release() {

    }
}
