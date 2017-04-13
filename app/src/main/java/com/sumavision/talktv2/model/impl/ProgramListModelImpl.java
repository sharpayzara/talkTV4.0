package com.sumavision.talktv2.model.impl;
import com.sumavision.talktv2.http.PlayerRetrofit;
import com.sumavision.talktv2.http.SumaClient;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.ProgramListModel;
import com.sumavision.talktv2.model.entity.ProgramListData;
import com.sumavision.talktv2.model.entity.ProgramListTopic;
import com.sumavision.talktv2.model.entity.ProgramSelection;

import java.util.Map;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by sharpay on 16-6-20.
 */
public class ProgramListModelImpl implements ProgramListModel {
    private Subscription subscription;
    @Override

    public void release() {
        if(subscription != null){
            subscription.unsubscribe();
        }
    }

    @Override
    public void getProgramListTopic(final String id, final CallBackListener listener) {
        subscription = SumaClient.subscribe(new Callable<Observable<ProgramListTopic>>() {
            @Override
            public Observable<ProgramListTopic> call() {
                return SumaClient.getRetrofitInstance(PlayerRetrofit.class).getProgramListTopic(SumaClient.getCacheControl(),id);
            }
        }, new Action1<ProgramListTopic>() {
            @Override
            public void call(ProgramListTopic programListTopic) {
                listener.onSuccess(programListTopic);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },ProgramListTopic.class);
    }

    @Override
    public void getProgramListData(final String tid,final String cname,final Integer page,final Integer size, final CallBackListener listener) {
        subscription = SumaClient.subscribe(new Callable<Observable<ProgramListData>>() {
            @Override
            public Observable<ProgramListData> call() {
                return SumaClient.getRetrofitInstance(PlayerRetrofit.class).getProgramListData(tid,cname,page,size);
            }
        }, new Action1<ProgramListData>() {
            @Override
            public void call(ProgramListData programListData) {
                listener.onSuccess(programListData);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },ProgramListData.class);
    }

    @Override
    public void getProgramSelectionData(final String tid, final CallBackListener listener) {
        subscription = SumaClient.subscribe(new Callable<Observable<ProgramSelection>>() {
            @Override
            public Observable<ProgramSelection> call() {
                return SumaClient.getRetrofitInstance(PlayerRetrofit.class).getProgramSelectionData(SumaClient.getCacheControl(),tid);
            }
        }, new Action1<ProgramSelection>() {
            @Override
            public void call(ProgramSelection programListData) {
                listener.onSuccess(programListData);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },ProgramSelection.class);
    }

    @Override
    public void getProgramListSelectionData(final Map<String, String> map, final CallBackListener listener) {
        subscription = SumaClient.subscribe(new Callable<Observable<ProgramListData>>() {
            @Override
            public Observable<ProgramListData> call() {
                return SumaClient.getRetrofitInstance(PlayerRetrofit.class).getProgramListSelectionData(map);
            }
        }, new Action1<ProgramListData>() {
            @Override
            public void call(ProgramListData programListData) {
                listener.onSuccess(programListData);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },ProgramListData.class);
    }
}


