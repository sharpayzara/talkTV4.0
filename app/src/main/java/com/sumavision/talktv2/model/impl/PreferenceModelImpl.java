package com.sumavision.talktv2.model.impl;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.dao.RxDao;
import com.sumavision.talktv2.http.LogRetrofit;
import com.sumavision.talktv2.http.SumaClient;
import com.sumavision.talktv2.model.PreferenceModel;
import com.sumavision.talktv2.model.entity.PreferenceBean;
import com.sumavision.talktv2.model.entity.ProgramListData;
import com.sumavision.talktv2.model.entity.ResultData;
import com.sumavision.talktv2.model.entity.UserInfo;
import com.sumavision.talktv2.util.AppGlobalVars;

import org.feezu.liuli.timeselector.Utils.TextUtil;
import org.w3c.dom.Text;

import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by sharpay on 16-6-30.
 */
public class PreferenceModelImpl implements PreferenceModel {
    RxDao rxDao = new RxDao(BaseApp.getContext(), PreferenceBean.class);
    Subscription subscription;
    PreferenceBean bean ;
    @Override
    public PreferenceBean loadPreferenceStr() {
        List list = rxDao.queryForAll();
        if(list.size() > 0){
            return bean = (PreferenceBean) list.get(0);
        }else{
            return null;
        }

    }

    @Override
    public void clearPreferenceStr(){
        rxDao.clearTableData();
    }

    @Override
    public void savePreferenceStr(PreferenceBean bean) {
        rxDao.clearTableData();
        rxDao.insert(bean);
    }

    @Override
    public void sendPreferenceStr(final PreferenceBean bean) {
        if(bean != null){
            subscription = SumaClient.subscribe(new Callable<Observable<PreferenceBean>>() {
                @Override
                public Observable<PreferenceBean> call() {
                    return SumaClient.getRetrofitInstance(LogRetrofit.class, AppGlobalVars.logApiHost).preferenceLog(bean.getRole(), bean.getPreference(), bean.getDid());
                }
            }, new Action1<PreferenceBean>() {
                @Override
                public void call(PreferenceBean preferenceBean) {
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                }
            }, ProgramListData.class);
        }
    }

    @Override
    public void sendFeedBack(final String problem, final String suggest, final String contact) {
        subscription = SumaClient.subscribe(new Callable<Observable<ResultData>>() {
            @Override
            public Observable<ResultData> call() {
                if(AppGlobalVars.userInfo != null && !TextUtil.isEmpty(AppGlobalVars.userInfo.getUserId())){
                    return SumaClient.getRetrofitInstance(LogRetrofit.class, AppGlobalVars.logApiHost).feedBack(problem,suggest,contact,AppGlobalVars.userInfo.getUserId());
                }else{
                    return SumaClient.getRetrofitInstance(LogRetrofit.class, AppGlobalVars.logApiHost).feedBack(problem,suggest,contact,"");
                }
            }
        }, new Action1<ResultData>() {
            @Override
            public void call(ResultData resultData) {
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
            }
        }, ResultData.class);
    }

    @Override
    public void release() {
        if(subscription != null){
            subscription.unsubscribe();
        }
    }
}
