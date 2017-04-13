package com.sumavision.talktv2.model.impl;

import android.content.Context;
import android.text.TextUtils;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.common.ACache;
import com.sumavision.talktv2.http.LogRetrofit;
import com.sumavision.talktv2.http.SumaClient;
import com.sumavision.talktv2.model.entity.decor.BaseData;
import com.sumavision.talktv2.mycrack.CrackResult;
import com.sumavision.talktv2.util.AppGlobalVars;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by zjx on 2016/6/28.
 */
public class VodPlayerModelImpl {

    public int getDefaultDefintion (Context context, CrackResult result,
                                    ArrayList<String> urls, ArrayList<String> denfintions) {
        urls.clear();
        denfintions.clear();
        int defaultIndex = 1;
        try{
            if (BaseApp.getACache().getAsObject("currentSelect") != null) {
                defaultIndex = (int) ACache.get(context).getAsObject("currentSelect") + 1;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        int currDefintion = -1;
        if(!TextUtils.isEmpty(result.standUrl)){
            urls.add(result.standUrl);
            denfintions.add("标清");
        }
        if(!TextUtils.isEmpty(result.highUrl)) {
            urls.add(result.highUrl);
            denfintions.add("高清");
        }
        if(!TextUtils.isEmpty(result.superUrl)) {
            urls.add(result.superUrl);
            denfintions.add("超清");
        }
        String defaultDefintion = "";
        if(defaultIndex ==1)
            defaultDefintion = "标清";
        else if(defaultIndex == 2)
            defaultDefintion = "高清";
        else if(defaultIndex == 3)
            defaultDefintion = "超清";
        for(int i=0; i<denfintions.size(); i++) {
            if(defaultDefintion.equals(denfintions.get(i)))
                currDefintion = i;
        }
        return  currDefintion;
    }

    public int getPPTVDefintion(Context context, ArrayList<String> urls, ArrayList<String> denfintions, List<Integer> defintionInt) {
        int defaultIndex = 1;
        try{
            if (BaseApp.getACache().getAsObject("currentSelect") != null) {
                defaultIndex = (int) ACache.get(context).getAsObject("currentSelect") + 1;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        for(int i : defintionInt) {
            if(i == 0) {
                denfintions.add("标清");
                urls.add(String.valueOf(i));
            }
            else if(i == 1) {
                denfintions.add("高清");
                urls.add(String.valueOf(i));
            }
            else if(i == 2) {
                denfintions.add("超清");
                urls.add(String.valueOf(i));
            }
        }

        int currDefintion = -1;
        String defaultDefintion = "";
        if(defaultIndex ==1)
            defaultDefintion = "标清";
        else if(defaultIndex == 2)
            defaultDefintion = "高清";
        else if(defaultIndex == 3)
            defaultDefintion = "超清";
        for(int i=0; i<denfintions.size(); i++) {
            if(defaultDefintion.equals(denfintions.get(i)))
                currDefintion = i;
        }
        return currDefintion;
    }

    public void playErrorLog(final String channelId, final String sourceId, final String errorType) {
        Subscription subscription = SumaClient.subscribe(new Callable<Observable<BaseData>>() {
            @Override
            public Observable<BaseData> call() throws Exception {
                return SumaClient.getRetrofitInstance(LogRetrofit.class, AppGlobalVars.logApiHost).vodPlayErrorLog(channelId, sourceId, errorType);
            }
        }, new Action1<BaseData>() {
            @Override
            public void call(BaseData baseData) {

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {

            }
        }, BaseData.class);

    }
}
