package com.sumavision.talktv2.model.impl;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.dao.RxDao;
import com.sumavision.talktv2.http.LogRetrofit;
import com.sumavision.talktv2.http.PayRetrofit;
import com.sumavision.talktv2.http.SumaClient;
import com.sumavision.talktv2.model.PayModel;
import com.sumavision.talktv2.model.PraiseModel;
import com.sumavision.talktv2.model.entity.PraiseData;
import com.sumavision.talktv2.model.entity.WXOrder;
import com.sumavision.talktv2.model.entity.decor.BaseData;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.sumavision.talktv2.util.AppUtil;
import com.sumavision.talktv2.util.CommonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

public class PraiseModelmpl implements PraiseModel{
    Subscription subscription;
    RxDao rxDao = new RxDao(BaseApp.getContext(),PraiseData.class);
    @Override
    public void saveLocalData(PraiseData praiseData) {
        if(praiseData.getId() != null){
           rxDao.update(praiseData);
        }else{
            rxDao.insert(praiseData);
        }
    }

    @Override
    public PraiseData loadPraiseData(String programId) {
        Map<String,String> map = new HashMap<>();
        map.put("programId",programId);
        List<PraiseData> list = rxDao.queryByCondition(map);
        if(list != null && list.size() > 0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public void sendPraiseData(final PraiseData praiseData) {
        subscription = SumaClient.subscribe(new Callable<Observable<BaseData>>() {
            @Override
            public Observable<BaseData> call() {
                String uid = "";
                if(AppGlobalVars.userInfo != null && AppGlobalVars.userInfo.getUserId() !=  null){
                    uid = AppGlobalVars.userInfo.getUserId();
                }
                return SumaClient.getRetrofitInstance(LogRetrofit.class, AppGlobalVars.logApiHost).favorite(uid, AppUtil.getDeviceInfo(BaseApp.getContext()),praiseData.getProgramId(),praiseData.getValid()?"ADD":"REMOVE");
            }
        }, new Action1<BaseData>() {
            @Override
            public void call(BaseData baseData) {
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
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