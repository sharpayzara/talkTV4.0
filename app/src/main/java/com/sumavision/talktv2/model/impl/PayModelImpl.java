package com.sumavision.talktv2.model.impl;
import com.sumavision.talktv2.http.PayRetrofit;
import com.sumavision.talktv2.http.SumaClient;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.PayModel;
import com.sumavision.talktv2.model.entity.WXOrder;
import com.sumavision.talktv2.util.AppGlobalVars;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by sharpay on 16-9-2.
 */
public class PayModelImpl implements PayModel {
    Subscription subscription;

    @Override
    public void getWXOrder(final CallBackListener<WXOrder> listener) {
        subscription = SumaClient.subscribe(new Callable<Observable<WXOrder>>() {
            @Override
            public Observable<WXOrder> call() {
                return SumaClient.getRetrofitInstance(PayRetrofit.class, AppGlobalVars.pay_order).getWXOrder();
            }
        }, new Action1<WXOrder>() {
            @Override
            public void call(WXOrder WXOrder) {
                listener.onSuccess(WXOrder);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },WXOrder.class);
    }

    @Override
    public void release() {
        if(subscription != null){
            subscription.unsubscribe();
        }
    }
}
