package com.sumavision.talktv2.model.impl;

import android.content.Context;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.dao.RxDao;
import com.sumavision.talktv2.dao.ormlite.DbCallBack;
import com.sumavision.talktv2.http.LogRetrofit;
import com.sumavision.talktv2.http.SumaClient;
import com.sumavision.talktv2.http.LiveRetrofit;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.entity.LiveCollectBean;
import com.sumavision.talktv2.model.entity.decor.BaseData;
import com.sumavision.talktv2.model.entity.decor.LiveData;
import com.sumavision.talktv2.model.entity.decor.LiveDetailData;
import com.sumavision.talktv2.model.entity.decor.LiveRquestBean;
import com.sumavision.talktv2.ui.iview.ILivePlayerVideoView;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.sumavision.talktv2.util.AppUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by zjx on 2016/6/22.
 */
public class LivePlayerVideoViewModelImpl {

    Subscription subscription;
    private RxDao collectUtil = new RxDao(BaseApp.getContext(), LiveCollectBean.class);

    /**
     * 获取节目单列表
     * @param chnnelId
     * @param listener
     */
    public void getLiveDetailData(final String chnnelId, final CallBackListener<LiveDetailData> listener) {
        subscription = SumaClient.subscribe(new Callable<Observable<LiveDetailData>>() {
            @Override
            public Observable<LiveDetailData> call() throws Exception {
                LiveRquestBean client = new LiveRquestBean("1", chnnelId);
                return SumaClient.getRetrofitInstance(LiveRetrofit.class, AppGlobalVars.liveApiHost).getLiveDetailData(client);
            }
        }, new Action1<LiveDetailData>() {
            @Override
            public void call(LiveDetailData liveDetailData) {
                listener.onSuccess(liveDetailData);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        }, LiveDetailData.class);
    }

    public void isFav(String channelId, final ILivePlayerVideoView iView) {
        collectUtil.subscribe();
        HashMap<String, String> map = new HashMap();
        map.put("channelId", channelId);
        collectUtil.queryByConditionSync(map, new DbCallBack<ArrayList<LiveCollectBean>>() {
            @Override
            public void onComplete(ArrayList<LiveCollectBean> data) {
                if(data.size()>0)
                    iView.isFav(true);
                else
                    iView.isFav(false);
            }
        });
    }

    /**
     * 收藏channel
     * @param collectDatas
     * @param channelBean
     */
    public void favChannel (ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> collectDatas,
                            LiveData.ContentBean.TypeBean.ChannelBean channelBean) {
        LiveCollectBean liveCollectBean = new LiveCollectBean();
        liveCollectBean.setChannelId(channelBean.getId());
        collectUtil.insert(liveCollectBean);
        LiveData.ContentBean.TypeBean.ChannelBean bean = null;
        try {
            bean = (LiveData.ContentBean.TypeBean.ChannelBean) channelBean.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        bean.setChannelType("收藏");
        collectDatas.add(bean);
    }

    /**
     * 取消收藏
     * @param collectDatas
     * @param channelId
     */
    public void cancelFav (ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> collectDatas, String channelId){
        LiveCollectBean liveCollectBean = new LiveCollectBean();
        liveCollectBean.setChannelId(channelId);
        collectUtil.deleteByColumnName("channelId", channelId);
        int count = collectDatas.size();
        int index = 0;
        for(int i=0; i<count; i++) {
            if(channelId.equals(collectDatas.get(i).getId())){
                index = i;
                break;
            }
        }
        collectDatas.remove(index);
    }

    public int getCurrType(ArrayList<String> types, String type) {
        if("收藏".equals(type))
            return -1;
        int j = 0;
        for(int i=0; i<types.size(); i++) {
            if(type.equals(types.get(i))) {
                j = i;
                break;
            }
        }
        return j;
    }

    public void playErrorLog(final String channelId, final int sourceId, final String errorType) {
        subscription = SumaClient.subscribe(new Callable<Observable<BaseData>>() {
            @Override
            public Observable<BaseData> call() throws Exception {
                return SumaClient.getRetrofitInstance(LogRetrofit.class, AppGlobalVars.logApiHost).livePlayErrorLog(channelId, sourceId, errorType);
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

    public void livePlayLog(final String channelId, final String mid, final Context context) {
        subscription = SumaClient.subscribe(new Callable<Observable<BaseData>>() {
            @Override
            public Observable<BaseData> call() throws Exception {
                return SumaClient.getRetrofitInstance(LogRetrofit.class, AppGlobalVars.logApiHost).livePlayLog(channelId, AppUtil.getDeviceInfo(context), mid);
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
