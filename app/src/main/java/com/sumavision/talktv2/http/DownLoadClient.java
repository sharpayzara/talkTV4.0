package com.sumavision.talktv2.http;

import android.support.annotation.NonNull;

import com.jiongbull.jlog.JLog;
import com.sumavision.talktv2.http.interceptor.DownloadProgressInterceptor;
import com.sumavision.talktv2.ui.listener.DownloadProgressListener;
import com.sumavision.talktv2.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 *  desc 获取retrofit
 *  @author  yangjh
 *  created at  16-5-23 上午10:50
 */
public class DownLoadClient{
    private static final String TAG = "DownloadAPI";
    private static final int DEFAULT_TIMEOUT = 15;
    public  Retrofit retrofit;
    public DownLoadClient(String url, DownloadProgressListener listener) {
        DownloadProgressInterceptor interceptor = new DownloadProgressInterceptor(listener);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }
    public Subscription downloadFile( @NonNull String url, final File file, Subscriber subscriber) {
        JLog.e("downloadurl: " + url);
        Subscription subscription = null;
        try {
            subscription =retrofit.create(DownloadRetrofit.class).download(url)
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .map(new Func1<ResponseBody, InputStream>() {
                        @Override
                        public InputStream call(ResponseBody responseBody) {
                            return responseBody.byteStream();
                        }
                    })
                    .observeOn(Schedulers.computation())
                    .doOnNext(new Action1<InputStream>() {
                        @Override
                        public void call(InputStream inputStream) {
                            try {
                                FileUtil.writeFile(inputStream, file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subscription;
    }
}
