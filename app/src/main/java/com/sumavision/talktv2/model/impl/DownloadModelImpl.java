package com.sumavision.talktv2.model.impl;

import com.sumavision.talktv2.http.HomeRetrofit;
import com.sumavision.talktv2.http.LiveRetrofit;
import com.sumavision.talktv2.http.SumaClient;
import com.sumavision.talktv2.model.entity.UpdateBean;
import com.sumavision.talktv2.model.entity.decor.LiveData;
import com.sumavision.talktv2.model.entity.decor.LiveRquestBean;
import com.sumavision.talktv2.ui.listener.DownloadProgressListener;
import com.sumavision.talktv2.http.DownLoadClient;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.DownloadModel;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.sumavision.talktv2.util.StringUtil;

import java.io.File;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by sharpay on 16-7-6.
 */
public class DownloadModelImpl implements DownloadModel{
    Subscription subscription;
    @Override
    public void download(final CallBackListener listener, final String url, final File file, DownloadProgressListener progressListener) {
        String baseUrl = StringUtil.getHostName(url);
        subscription = new DownLoadClient(baseUrl,progressListener).downloadFile(url, file, new Subscriber() {
            @Override
            public void onCompleted() {
                listener.onSuccess(null);
            }

            @Override
            public void onError(Throwable e) {
                listener.onFailure(e);
            }

            @Override
            public void onNext(Object o) {

            }
        });
    }

    @Override
    public void updteData(final CallBackListener listener) {
            subscription = SumaClient.subscribe(new Callable<Observable<UpdateBean>>() {
                @Override
                public Observable<UpdateBean> call() throws Exception {
                    return SumaClient.getRetrofitInstance(HomeRetrofit.class).getUpdateData();
                }
            }, new Action1<UpdateBean>() {
                @Override
                public void call(UpdateBean updateBean) {
                    listener.onSuccess(updateBean);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    listener.onFailure(throwable);
                }
            }, UpdateBean.class);

    }

    @Override
    public void release() {
        if(subscription != null){
            subscription.unsubscribe();
        }
    }

}
