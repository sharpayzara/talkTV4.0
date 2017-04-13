package com.sumavision.talktv2.model.impl;

import com.sumavision.talktv2.http.PlayerRetrofit;
import com.sumavision.talktv2.http.SumaClient;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.YsqModel;
import com.sumavision.talktv2.model.entity.YsqBean;
import java.util.concurrent.Callable;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;


public class YsqModelmpl implements YsqModel{
    Subscription subscription;

    @Override
    public void loadYsqData(final CallBackListener listener, final int page, final int size) {
        subscription = SumaClient.subscribe(new Callable<Observable<YsqBean>>() {
            @Override
            public Observable<YsqBean> call() {
                return SumaClient.getRetrofitInstance(PlayerRetrofit.class).getYsqData(SumaClient.getCacheControl(),page,size);
            }
        }, new Action1<YsqBean>() {
            @Override
            public void call(YsqBean ysqBean) {
                listener.onSuccess(ysqBean);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },YsqBean.class);
    }
    @Override
    public void release() {
        if(subscription != null){
            subscription.unsubscribe();
        }
    }
}
