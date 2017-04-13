package com.sumavision.talktv2.model.impl;

import com.jiongbull.jlog.JLog;
import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.dao.RxDao;
import com.sumavision.talktv2.dao.ormlite.DbCallBack;
import com.sumavision.talktv2.http.ActionRetrofit;
import com.sumavision.talktv2.http.SumaClient;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.SplashModel;
import com.sumavision.talktv2.model.entity.ActionUrl;
import com.sumavision.talktv2.util.AppGlobalVars;

import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by sharpay on 16-7-11.
 */
public class SplashModelImpl implements SplashModel {
    Subscription subscription;
    RxDao rxDao = new RxDao(BaseApp.getContext(), ActionUrl.class, false);
    @Override
    public void release() {
        if(subscription != null){
            subscription.unsubscribe();
        }
    }

    @Override
    public void loadActionData(final CallBackListener listener) {
        subscription = SumaClient.subscribe(new Callable<Observable<ActionUrl>>() {
            @Override
            public Observable<ActionUrl> call() {
                //网络请求
                return SumaClient.getRetrofitInstance(ActionRetrofit.class, AppGlobalVars.actionHost).getActionUrlData(SumaClient.getCacheControl());
            }
        }, new Action1<ActionUrl>() {
            @Override
            public void call(final ActionUrl actionUrl) {
                rxDao.subscribe();
                rxDao.queryForAllSync(new DbCallBack<List<ActionUrl>>() {
                    @Override
                    public void onComplete(List<ActionUrl> data) {
                        if(data != null && data.size() > 0 ){
                            ActionUrl temp= data.get(0);
                            temp.setGlobal_so(actionUrl.getGlobal_so());
                            temp.setLiveapi(actionUrl.getLiveapi());
                            temp.setMepg_api(actionUrl.getMepg_api());
                            temp.setUpgc_api(actionUrl.getUpgc_api());
                            temp.setMepg_log(actionUrl.getMepg_log());
                            temp.setUsercenter(actionUrl.getUsercenter());
                            temp.setDuiba(actionUrl.getDuiba());
                            temp.setShare_api(actionUrl.getShare_api());
                          //  rxDao.update(temp);
                            rxDao.unsubscribe();
                        }else{
                            ActionUrl temp = new ActionUrl();
                            temp.setGlobal_so(actionUrl.getGlobal_so());
                            temp.setLiveapi(actionUrl.getLiveapi());
                            temp.setMepg_api(actionUrl.getMepg_api());
                            temp.setUpgc_api(actionUrl.getUpgc_api());
                            temp.setMepg_log(actionUrl.getMepg_log());
                            temp.setUsercenter(actionUrl.getUsercenter());
                            temp.setDuiba(actionUrl.getDuiba());
                            temp.setShare_api(actionUrl.getShare_api());
                            rxDao.insert(temp);
                            rxDao.unsubscribe();
                        }
                    }
                });
                AppGlobalVars.globalSoHost = actionUrl.getGlobal_so();
                AppGlobalVars.mepgApiHost = actionUrl.getMepg_api();
                AppGlobalVars.upgcApiHost= actionUrl.getUpgc_api();
                AppGlobalVars.liveApiHost = actionUrl.getLiveapi();
                AppGlobalVars.logApiHost = actionUrl.getMepg_log();
                AppGlobalVars.userCenterApiHost = actionUrl.getUsercenter();
                AppGlobalVars.duibaHost = actionUrl.getDuiba();
                AppGlobalVars.shareApiHost = actionUrl.getShare_api();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                JLog.e(throwable.toString());
            }
        },ActionUrl.class);
    }
}
