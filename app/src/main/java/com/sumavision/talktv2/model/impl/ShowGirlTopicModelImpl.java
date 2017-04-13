package com.sumavision.talktv2.model.impl;

import android.content.Context;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.dao.RxDao;
import com.sumavision.talktv2.http.LogRetrofit;
import com.sumavision.talktv2.http.MediaRetrofit;
import com.sumavision.talktv2.http.ShowGirlRetrofit;
import com.sumavision.talktv2.http.SumaClient;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.MediaModel;
import com.sumavision.talktv2.model.ShowGirlModel;
import com.sumavision.talktv2.model.entity.MediaDetail;
import com.sumavision.talktv2.model.entity.MediaList;
import com.sumavision.talktv2.model.entity.ShowGirlList;
import com.sumavision.talktv2.model.entity.ShowGirlTopic;
import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.model.entity.decor.BaseData;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.sumavision.talktv2.util.AppUtil;

import java.util.HashMap;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by sharpay on 16-6-30.
 */
public class ShowGirlTopicModelImpl implements ShowGirlModel {
    Subscription subscription;
    
    @Override
    public void release() {
        if(subscription != null){
            subscription.unsubscribe();
        }
    }

    @Override
    public void getShowGirlTopicData(final CallBackListener listener) {
        subscription = SumaClient.subscribe(new Callable<Observable<ShowGirlTopic>>() {
            @Override
            public Observable<ShowGirlTopic> call() {
                return SumaClient.getRetrofitInstance(ShowGirlRetrofit.class, AppGlobalVars.showGirlHost).getShowGirlTopicData();
            }
        }, new Action1<ShowGirlTopic>() {
            @Override
            public void call(ShowGirlTopic showGirlTopic) {
                listener.onSuccess(showGirlTopic);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },ShowGirlTopic.class);
    }

    public void getShowGirlListData(final CallBackListener listener,final int partId,final int start, final int offset) {
        subscription = SumaClient.subscribe(new Callable<Observable<ShowGirlList>>() {
            @Override
            public Observable<ShowGirlList> call() {
                return SumaClient.getRetrofitInstance(ShowGirlRetrofit.class, AppGlobalVars.showGirlHost).getShowGirlListData(partId,start,offset);
            }
        }, new Action1<ShowGirlList>() {
            @Override
            public void call(ShowGirlList showGirlList) {
                listener.onSuccess(showGirlList);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },ShowGirlList.class);
    }
}
