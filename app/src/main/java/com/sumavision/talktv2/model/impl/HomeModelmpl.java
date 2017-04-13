package com.sumavision.talktv2.model.impl;

import android.os.Environment;
import android.text.TextUtils;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.dao.RxDao;
import com.sumavision.talktv2.http.DownLoadClient;
import com.sumavision.talktv2.http.HomeRetrofit;
import com.sumavision.talktv2.http.SumaClient;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.CollectCallBackListener;
import com.sumavision.talktv2.model.HomeModel;
import com.sumavision.talktv2.model.entity.ADInfoItem;
import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.model.entity.ScreenBean;
import com.sumavision.talktv2.model.entity.SpecialListData;
import com.sumavision.talktv2.model.entity.VersionData;
import com.sumavision.talktv2.model.entity.decor.BaseData;
import com.sumavision.talktv2.model.entity.decor.ClassifyData;
import com.sumavision.talktv2.model.entity.decor.ClassifyUpdataData;
import com.sumavision.talktv2.model.entity.decor.HomeRecommendData;
import com.sumavision.talktv2.model.entity.decor.HomeRecommendUpdateData;
import com.sumavision.talktv2.util.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by zhoutao on 16-5-26.
 */
public class HomeModelmpl implements HomeModel {
    Subscription subscription;
    @Override
    public void loadClassifys(final CallBackListener listener) {
        subscription = SumaClient.subscribe(new Callable<Observable<ClassifyData>>() {
            @Override
            public Observable<ClassifyData> call() {
                //网络请求
                return SumaClient.getRetrofitInstance(HomeRetrofit.class).getClassifyData();
            }
        }, new Action1<ClassifyData>() {
            @Override
            public void call(ClassifyData classifyData) {
                listener.onSuccess(classifyData);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {

            }
        },ClassifyData.class);
    }

    @Override
    public void loadClassifyUpdatas(final CallBackListener listener){
        subscription = SumaClient.subscribe(new Callable<Observable<ClassifyUpdataData>>() {
            @Override
            public Observable<ClassifyUpdataData> call() throws Exception {
                return SumaClient.getRetrofitInstance(HomeRetrofit.class). getClassifyUpdateData(SumaClient.getCacheControl());
            }
        }, new Action1<ClassifyUpdataData>() {
            @Override
            public void call(ClassifyUpdataData classifyUpdataData) {
                listener.onSuccess(classifyUpdataData);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },ClassifyUpdataData.class);
    }

    @Override
    public void getUpdateInfo(final CallBackListener listener) {
        subscription = SumaClient.subscribe(new Callable<Observable<VersionData>>() {
            @Override
            public Observable<VersionData> call() throws Exception {
                return SumaClient.getRetrofitInstance(HomeRetrofit.class).getVersionData();
            }
        }, new Action1<VersionData>() {
            @Override
            public void call(VersionData versionData) {
                listener.onSuccess(versionData);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },ClassifyUpdataData.class);
    }

    @Override
    public void loadHomeRecommendData(final CallBackListener listener, final String nid) {
        subscription = SumaClient.subscribe(new Callable<Observable<HomeRecommendData>>() {
            @Override
            public Observable<HomeRecommendData> call() throws Exception {
                return SumaClient.getRetrofitInstance(HomeRetrofit.class). getHomeRecommendData(nid);
            }
        }, new Action1<HomeRecommendData>() {
            @Override
            public void call(HomeRecommendData homeRecommendData) {
                listener.onSuccess(homeRecommendData);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },HomeRecommendData.class);
    }

    @Override
    public void loadHomeRecommendUpdateData(final CallBackListener listener,final String nid,final String v) {
        subscription = SumaClient.subscribe(new Callable<Observable<HomeRecommendUpdateData>>() {
            @Override
            public Observable<HomeRecommendUpdateData> call() throws Exception {
                return SumaClient.getRetrofitInstance(HomeRetrofit.class). getHomeRecommendUpdateData(nid,v);
            }
        }, new Action1<HomeRecommendUpdateData>() {
            @Override
            public void call(HomeRecommendUpdateData homeRecommendData) {
                listener.onSuccess(homeRecommendData);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },HomeRecommendUpdateData.class);
    }

    @Override
    public void release() {
        if(subscription != null){
            subscription.unsubscribe();
        }
    }

    @Override
    public void download(String url, final File file) {
        String baseUrl = StringUtil.getHostName(url);
        subscription = new DownLoadClient(baseUrl,null).downloadFile(url, file, new Subscriber() {
            @Override
            public void onCompleted() {
                final File newFile= new File(Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_DOWNLOADS), ".screensaver.png");
                file.renameTo(newFile);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {

            }
        });
    }

    @Override
    public void loadScreenData(final CallBackListener listener){
        subscription = SumaClient.subscribe(new Callable<Observable<ScreenBean>>() {
            @Override
            public Observable<ScreenBean> call() throws Exception {
                return SumaClient.getRetrofitInstance(HomeRetrofit.class).loadScreenData();
            }
        }, new Action1<ScreenBean>() {
            @Override
            public void call(ScreenBean screenBean) {
                listener.onSuccess(screenBean);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },ClassifyUpdataData.class);
    }

    @Override
    public void getPlayHistory(final CollectCallBackListener listener) {
        RxDao rxDao = new RxDao(BaseApp.getContext(), PlayerHistoryBean.class, false);
        rxDao.unsubscribe();

        rxDao.queryForAllObservable()
                .subscribe(new Action1<ArrayList<PlayerHistoryBean>>() {
                    @Override
                    public void call(ArrayList<PlayerHistoryBean> data) {
                        if(data.size()<=0)
                            return;
                        PlayerHistoryBean playerHistoryBean = null;
                        for (PlayerHistoryBean bean : data) {
                            if(bean.getMediaType() == 1) {
                                playerHistoryBean = bean;
                                break;
                            }
                        }
                        data.clear();
                        String historyContent = "";
                        if(playerHistoryBean != null) {
                            StringBuilder builder = new StringBuilder("点击继续播放  ");
                            builder.append(playerHistoryBean.getProgramName());
                            if("1".equals(playerHistoryBean.getProgramType())) {
                            }
                            else if("2".equals(playerHistoryBean.getProgramType()) && !TextUtils.isEmpty(playerHistoryBean.getEpi())) {
                                builder.append("第"+playerHistoryBean.getEpi()+"集");
                            }
                            else if("3".equals(playerHistoryBean.getProgramType()) && !TextUtils.isEmpty(playerHistoryBean.getEpi())) {
                                builder.append("第"+playerHistoryBean.getEpi()+"期");
                            }
                            historyContent = builder.toString();
                        }
                        playerHistoryBean.setProgramName(historyContent);
                        data.add(playerHistoryBean);
                        listener.vodSucceess(data);
                    }
                });
    }

    @Override
    public void loadSpecialData(final int page, final int size, final CallBackListener<SpecialListData> listener) {
        subscription = SumaClient.subscribe(new Callable<Observable<SpecialListData>>() {
            @Override
            public Observable<SpecialListData> call() throws Exception {
                return SumaClient.getRetrofitInstance(HomeRetrofit.class).loadSpecialListData(page,size);
            }
        }, new Action1<SpecialListData>() {
            @Override
            public void call(SpecialListData specialListData) {
                listener.onSuccess(specialListData);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },SpecialListData.class);
    }

    @Override
    public void loadADInfo(final String type, final CallBackListener<ADInfoItem> listener) {
        subscription = SumaClient.subscribe(new Callable<Observable<ADInfoItem>>() {
            @Override
            public Observable<ADInfoItem> call() throws Exception {
                return SumaClient.getRetrofitInstance(HomeRetrofit.class).getADInfo(type);
            }
        }, new Action1<ADInfoItem>() {
            @Override
            public void call(ADInfoItem adInfoItem) {
                listener.onSuccess(adInfoItem);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },SpecialListData.class);
    }

    @Override
    public void sendScanData(String urlStr,CallBackListener<BaseData> listener) {
        subscription = SumaClient.subscribe(new Callable<Observable<BaseData>>() {
            @Override
            public Observable<BaseData> call() throws Exception {
                return SumaClient.getRetrofitInstance(HomeRetrofit.class).sendScanData(urlStr,"TVFAN");
            }
        }, new Action1<BaseData>() {
            @Override
            public void call(BaseData baseData) {
                listener.onSuccess(baseData);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },BaseData.class);
    }
}
