package com.sumavision.talktv2.model.impl;

import com.sumavision.talktv2.http.SumaHttpsClient;
import com.sumavision.talktv2.http.UserRetrofit;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.DuibaModel;
import com.sumavision.talktv2.model.entity.LoginAddressData;
import com.sumavision.talktv2.model.entity.ResultData;
import com.sumavision.talktv2.model.entity.UserIntegeInfoItem;
import com.sumavision.talktv2.util.AppGlobalVars;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by sharpay on 2016/9/6.
 */
public class DuibaModelImpl implements DuibaModel{
    Subscription subscription;
    @Override
    public void release() {
        if(subscription != null){
            subscription.unsubscribe();
        }
    }
    @Override
    public void getUserIntegtion(final CallBackListener<UserIntegeInfoItem> listener, String userId) {
        subscription = SumaHttpsClient.subscribe(new Callable<Observable<UserIntegeInfoItem>>() {
            @Override
            public Observable<UserIntegeInfoItem> call() {
                //网络请求
                return SumaHttpsClient.getRetrofitInstance(UserRetrofit.class).loadUserIntege(AppGlobalVars.userInfo.getUserId());
            }
        }, new Action1<UserIntegeInfoItem>() {
            @Override
            public void call(UserIntegeInfoItem userIntegeInfoItem) {
                listener.onSuccess(userIntegeInfoItem);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },ResultData.class);
    }
    @Override
    public void loadLoginAddress(final CallBackListener listener, final String userId, final String credits,final String currentUrl) {
        subscription = SumaHttpsClient.subscribe(new Callable<Observable<LoginAddressData>>() {
            @Override
            public Observable<LoginAddressData> call() {
                //网络请求
                return SumaHttpsClient.getRetrofitNewInstance(UserRetrofit.class,AppGlobalVars.duibaHost).getLoginAddress(userId,credits,currentUrl);
            }
        }, new Action1<LoginAddressData>() {
            @Override
            public void call(LoginAddressData url) {
                listener.onSuccess(url);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },LoginAddressData.class);
    }



}
