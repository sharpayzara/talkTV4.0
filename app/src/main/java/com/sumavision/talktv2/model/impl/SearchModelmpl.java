package com.sumavision.talktv2.model.impl;

import com.sumavision.talktv2.http.SearchHintRetrofit;
import com.sumavision.talktv2.http.SearchHotRetrofit;
import com.sumavision.talktv2.http.SearchRetrofit;
import com.sumavision.talktv2.http.SumaClient;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.SearchModel;
import com.sumavision.talktv2.model.entity.HintData;
import com.sumavision.talktv2.model.entity.decor.SearchData;
import com.sumavision.talktv2.model.entity.decor.SearchInfoData;
import com.sumavision.talktv2.util.AppGlobalVars;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by zhoutao on 16-5-26.
 */
public class SearchModelmpl implements SearchModel {
    Subscription subscription;

    @Override
    public void release() {
        if(subscription != null){
            subscription.unsubscribe();
        }
    }

    @Override
    public void loadSearchListInfo(final CallBackListener listener,final String q,final int page) {
        subscription = SumaClient.subscribe(new Callable<Observable<SearchInfoData>>() {
            @Override
            public Observable<SearchInfoData> call() {
                //网络请求
                return SumaClient.getRetrofitInstance(SearchRetrofit.class, AppGlobalVars.globalSoHost).getSearchListInfoData(SumaClient.getCacheControl(),q,page);
            }
        }, new Action1<SearchInfoData>() {
            @Override
            public void call(SearchInfoData searchInfoData ) {
                listener.onSuccess(searchInfoData);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },SearchInfoData.class);
    }

    @Override
    public void loadSearchHotData(final CallBackListener listener) {
        subscription = SumaClient.subscribe(new Callable<Observable<SearchData>>() {
            @Override
            public Observable<SearchData> call() {
                //网络请求
                return SumaClient.getRetrofitInstance(SearchHotRetrofit.class).getSearchData(SumaClient.getCacheControl());
            }
        }, new Action1<SearchData>() {
            @Override
            public void call(SearchData searchData) {
                listener.onSuccess(searchData);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },SearchData.class);
    }

    @Override
    public void loadHintData(final CallBackListener listener, final String msg) {
        subscription = SumaClient.subscribe(new Callable<Observable<HintData>>() {
            @Override
            public Observable<HintData> call() {
                //网络请求
                return SumaClient.getRetrofitInstance(SearchHintRetrofit.class,"http://suggest.video.iqiyi.com").getSearchData("mobile",10,msg);
            }
        }, new Action1<HintData>() {
            @Override
            public void call(HintData hintData) {
                listener.onSuccess(hintData);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },HintData.class);
    }
}
