package com.sumavision.talktv2.model.impl;

import android.content.Context;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.dao.RxDao;
import com.sumavision.talktv2.dao.ormlite.DbCallBack;
import com.sumavision.talktv2.http.LogRetrofit;
import com.sumavision.talktv2.http.SumaClient;
import com.sumavision.talktv2.http.LiveRetrofit;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.CollectCallBackListener;
import com.sumavision.talktv2.model.LiveModel;
import com.sumavision.talktv2.model.entity.LiveCollectBean;
import com.sumavision.talktv2.model.entity.decor.BaseData;
import com.sumavision.talktv2.model.entity.decor.LiveData;
import com.sumavision.talktv2.model.entity.decor.LiveRquestBean;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.sumavision.talktv2.util.AppUtil;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 *  获取直播type和list数据
 * Created by zjx on 2016/6/3.
 */
public class LiveModelmpl implements LiveModel {
    private Subscription subscription;
    private RxDao collectUtil = new RxDao(BaseApp.getContext(), LiveCollectBean.class);
    @Override
    public void loadLiveListData(final CallBackListener listener) {
        subscription = SumaClient.subscribe(new Callable<Observable<LiveData>>() {
            @Override
            public Observable<LiveData> call() throws Exception {
                LiveRquestBean client = new LiveRquestBean("1", "");
                if (AppGlobalVars.liveApiHost == null) {
                    SumaClient.setAppGlobalHost();
                }
                return SumaClient.getRetrofitInstance(LiveRetrofit.class, AppGlobalVars.liveApiHost).getLiveData(client);
            }
        }, new Action1<LiveData>() {
            @Override
            public void call(LiveData liveData) {
                listener.onSuccess(liveData);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        }, LiveData.class);
    }

    @Override
    public void release() {
        if(subscription != null){
            subscription.unsubscribe();
        }
    }

    /**
     *收藏直播频道
     * @param channelId
     */
    @Override
    public void collectChannel(String channelId, final ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> collectChannelList,
                               final ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> netChannelList, final int pos,
                               final ArrayList<String> channelTypeList, final CollectCallBackListener listener) {
        LiveCollectBean liveCollectBean = new LiveCollectBean();
        liveCollectBean.setChannelId(channelId);
        collectUtil.subscribe();
        collectUtil.insertSync(liveCollectBean, new DbCallBack<Boolean>() {
            @Override
            public void onComplete(Boolean data) {
                if(data) {
                    int count  = pos - collectChannelList.size();
                    LiveData.ContentBean.TypeBean.ChannelBean channelBean = null;
                    try {
                        channelBean = (LiveData.ContentBean.TypeBean.ChannelBean) netChannelList.get(count).clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    channelBean.setChannelType("收藏");
                    collectChannelList.add(channelBean);
                    ArrayList<String> typeList = new ArrayList<>();
                    if(!"收藏".equals(channelTypeList.get(0))) {
                        typeList.add("收藏");
                        typeList.addAll(channelTypeList);
                        listener.liveSuccess(typeList, collectChannelList);
                    }
                    else {
                        listener.liveSuccess(channelTypeList, collectChannelList);
                    }
                }
            }
        });


    }

    /**
     *
     * @param channelId
     * @param collectChannelDatas
     * @param channelTypeList
     */
    @Override
    public void cancelCollect(final String channelId, final ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> collectChannelDatas,
                              final ArrayList<String> channelTypeList, final CollectCallBackListener listener) {
        collectUtil.subscribe();
        collectUtil.deleteByColumnNameSync("channelId", channelId, new DbCallBack<Boolean>() {
            @Override
            public void onComplete(Boolean data) {
                if(data) {
                    int count = collectChannelDatas.size();
                    for (int i=0; i<count; i++) {
                        String id = collectChannelDatas.get(i).getId();
                        if(id.equals(channelId)) {
                            collectChannelDatas.remove(i);
                            break;
                        }
                    }
                    if(collectChannelDatas.size()==0)
                        channelTypeList.remove(0);

                    listener.liveSuccess(null, null);
                }

            }
        });
    }

    /**
     * 获取全部收藏节目
     * @param listener
     */
    @Override
    public void queryAllCollect(final ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> netChannelDatas,
                                final ArrayList<String> channelTypeDatasForNet, final CollectCallBackListener listener) {
        collectUtil.subscribe();
        collectUtil.queryForAllSync(new DbCallBack<ArrayList<LiveCollectBean>>() {
            @Override
            public void onComplete(ArrayList<LiveCollectBean> data) {
                ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> collectChannelDatas = new ArrayList<>();
                ArrayList<String> ids = new ArrayList<>();

                for(LiveCollectBean liveCollectBean : data) {
                    ids.add(liveCollectBean.getChannelId());
                }

                A:for(String id : ids) {
                    for(LiveData.ContentBean.TypeBean.ChannelBean channelBean : netChannelDatas) {
                        String channelId = channelBean.getId();
                        if(id.equals(channelId)) {
                            LiveData.ContentBean.TypeBean.ChannelBean channelData = null;
                            try {
                                channelData = (LiveData.ContentBean.TypeBean.ChannelBean) channelBean.clone();
                                channelData.setChannelType("收藏");
                                collectChannelDatas.add(channelData);
                                continue A;
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
                ArrayList<String> channelType = new ArrayList<>();
                if( channelTypeDatasForNet.size()>0 && "收藏".equals(channelTypeDatasForNet.get(0)))
                    channelTypeDatasForNet.remove(0);
                if(ids.size()>0 )
                    channelType.add("收藏");
                channelType.addAll(channelTypeDatasForNet);
                listener.liveSuccess(channelType, collectChannelDatas);
            }
        });

    }

    @Override
    public void favChangeForVideo(boolean fav, ArrayList<String> channelTypeNameDatas,
                                  ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> channelDatasForCollect) {
        if(fav) {
            if(channelDatasForCollect.size()==1 && !"收藏".equals(channelTypeNameDatas.get(0))) {
                channelTypeNameDatas.add(0, "收藏");
            }
        }
        else {
            if(channelDatasForCollect.size()==0 && "收藏".equals(channelTypeNameDatas.get(0))) {
                channelTypeNameDatas.remove(0);
            }
        }

    }


}
