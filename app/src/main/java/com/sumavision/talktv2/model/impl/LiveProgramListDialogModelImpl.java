package com.sumavision.talktv2.model.impl;

import com.sumavision.talktv2.model.LiveProgramListDialogModel;
import rx.Subscription;

/**
 * Created by zjx on 2016/6/21.
 */
public class LiveProgramListDialogModelImpl implements LiveProgramListDialogModel {
    Subscription subscription;

    /**
     * 获取节目单列表
     * @param chnnelId
     * @param listener
     *//*
    @Override
    public void getLiveDetailData(final String chnnelId, final CallBackListener<LiveDetailData> listener) {
        subscription = LiveClient.subscribe(new Callable<Observable<LiveDetailData>>() {
            @Override
            public Observable<LiveDetailData> call() throws Exception {
                LiveRquestBean client = new LiveRquestBean("1", chnnelId);
                return LiveClient.getRetrofitInstance(LiveRetrofit.class).getLiveDetailData(client);
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
    }*/
}
