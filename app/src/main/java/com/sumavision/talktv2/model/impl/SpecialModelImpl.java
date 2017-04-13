package com.sumavision.talktv2.model.impl;

import android.content.Context;

import com.sumavision.talktv2.http.HomeRetrofit;
import com.sumavision.talktv2.http.LogRetrofit;
import com.sumavision.talktv2.http.SumaClient;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.SpecialModel;
import com.sumavision.talktv2.model.entity.SpecialContentList;
import com.sumavision.talktv2.model.entity.SpecialDetail;
import com.sumavision.talktv2.model.entity.decor.BaseData;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.sumavision.talktv2.util.AppUtil;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by sharpay on 16-8-23.
 */
public class SpecialModelImpl implements SpecialModel {
    Subscription subscription;

    @Override
    public void release() {
        if(subscription != null){
            subscription.unsubscribe();
        }
    }

    @Override
    public void loadSpecialDetail(final String id,final CallBackListener listener) {
            subscription = SumaClient.subscribe(new Callable<Observable<SpecialDetail>>() {
                @Override
                public Observable<SpecialDetail> call() {
                    //网络请求
                    return SumaClient.getRetrofitInstance(HomeRetrofit.class).loadSpecialDetailData(id);
                }
            }, new Action1<SpecialDetail>() {
                @Override
                public void call(SpecialDetail specialDetail ) {
                    listener.onSuccess(specialDetail);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    listener.onFailure(throwable);
                }
            },SpecialDetail.class);
        }

    @Override
    public void loadSpecialContentList(final String id,final int page,final int size,final CallBackListener listener) {
        subscription = SumaClient.subscribe(new Callable<Observable<SpecialContentList>>() {
            @Override
            public Observable<SpecialContentList> call() {
                //网络请求
                return SumaClient.getRetrofitInstance(HomeRetrofit.class).loadSpecialContentList(id,page,size);
            }
        }, new Action1<SpecialContentList>() {
            @Override
            public void call(SpecialContentList SpecialContentList ) {
                listener.onSuccess(SpecialContentList);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },SpecialContentList.class);
    }


    @Override
    public void enterDetailLog(final String id, final Context context) {
        subscription = SumaClient.subscribe(new Callable<Observable<BaseData>>() {
            @Override
            public Observable< BaseData> call() throws Exception {
                return SumaClient.getRetrofitInstance(LogRetrofit.class, AppGlobalVars.logApiHost).enterDetailLog(id, AppUtil.getDeviceInfo(context));
            }
        }, new Action1<BaseData>() {
            @Override
            public void call(BaseData baseData) {
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
            }
        }, BaseData.class);
    }

    @Override
    public void pvLog(final String id, final Context context) {
        subscription = SumaClient.subscribe(new Callable<Observable<BaseData>>() {
            @Override
            public Observable<BaseData> call() throws Exception {
                return SumaClient.getRetrofitInstance(LogRetrofit.class, AppGlobalVars.logApiHost).pvLog(id, AppUtil.getDeviceInfo(context));
            }
        }, new Action1<BaseData>() {
            @Override
            public void call(BaseData baseData) {
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
            }
        }, BaseData.class);
    }
}
