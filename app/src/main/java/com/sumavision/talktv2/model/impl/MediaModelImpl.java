package com.sumavision.talktv2.model.impl;

import android.content.Context;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.dao.RxDao;
import com.sumavision.talktv2.dao.ormlite.DbCallBack;
import com.sumavision.talktv2.http.LogRetrofit;
import com.sumavision.talktv2.http.SumaClient;
import com.sumavision.talktv2.http.MediaRetrofit;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.MediaModel;
import com.sumavision.talktv2.model.entity.MediaDetail;
import com.sumavision.talktv2.model.entity.MediaList;
import com.sumavision.talktv2.model.entity.MediaTopic;
import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.model.entity.decor.BaseData;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.sumavision.talktv2.util.AppUtil;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by sharpay on 16-6-30.
 */
public class MediaModelImpl implements MediaModel {
    Subscription subscription;
    @Override
    public void loadMediaTopic(final CallBackListener<MediaTopic> listener) {
        subscription = SumaClient.subscribe(new Callable<Observable<MediaTopic>>() {
            @Override
            public Observable<MediaTopic> call() {
                return SumaClient.getRetrofitInstance(MediaRetrofit.class, AppGlobalVars.upgcApiHost).getMeidaTopicData(SumaClient.getCacheControl());
            }
        }, new Action1<MediaTopic>() {
            @Override
            public void call(MediaTopic mediaTopic) {
                listener.onSuccess(mediaTopic);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },MediaTopic.class);
    }


    @Override
    public void loadMediaList(final CallBackListener<MediaList> listener, final Integer page, final Integer size, final String txt){
        subscription = SumaClient.subscribe(new Callable<Observable<MediaList>>() {
            @Override
            public Observable<MediaList> call() {
                return SumaClient.getRetrofitInstance(MediaRetrofit.class, AppGlobalVars.upgcApiHost).getMeidaListData(SumaClient.getCacheControl(),page,size,txt);
            }
        }, new Action1<MediaList>() {
            @Override
            public void call(MediaList mediaTopic) {
                listener.onSuccess(mediaTopic);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },MediaList.class);
    }

    @Override
    public void loadMediaDetail(final CallBackListener<MediaDetail> listener, final String vid) {
        subscription = SumaClient.subscribe(new Callable<Observable<MediaDetail>>() {
            @Override
            public Observable<MediaDetail> call() {
                return SumaClient.getRetrofitInstance(MediaRetrofit.class, AppGlobalVars.upgcApiHost).getMeidaDetailData(vid);
            }
        }, new Action1<MediaDetail>() {
            @Override
            public void call(MediaDetail mdiaDetail) {
                listener.onSuccess(mdiaDetail);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },MediaDetail.class);
    }

    @Override
    public PlayerHistoryBean getPlayHistory (String programId) {
        rxDao.subscribe();
        PlayerHistoryBean playerHistoryBean = (PlayerHistoryBean) rxDao.queryForFirst("programId", programId);
        rxDao.unsubscribe();
        return playerHistoryBean;
    }

    RxDao rxDao = new RxDao(BaseApp.getContext(), PlayerHistoryBean.class);
    @Override
    public void insertPlayHistory (final PlayerHistoryBean playerHistoryBean) {
        rxDao.subscribe();
        rxDao.queryForAllSync(new DbCallBack<List<PlayerHistoryBean>>() {
            @Override
            public void onComplete(List<PlayerHistoryBean> datas) {
                if(datas.size()>=50) {
                    rxDao.deleteByColumnNameSync("programId", datas.get(0).getProgramId(), new DbCallBack() {
                        @Override
                        public void onComplete(Object data) {

                        }
                    });
                }
            }
        });

        HashMap<String, String> map = new HashMap();
        map.put("programId", playerHistoryBean.getProgramId());
        rxDao.queryByConditionSync(map, new DbCallBack<List<PlayerHistoryBean>>() {
            @Override
            public void onComplete(List<PlayerHistoryBean> datas) {
                if(datas.size()>0) {
                    rxDao.deleteByColumnNameSync("programId", datas.get(0).getProgramId(), new DbCallBack<Boolean>() {
                        @Override
                        public void onComplete(Boolean data) {
                            if(data) {
                                rxDao.insertSync(playerHistoryBean, new DbCallBack() {
                                    @Override
                                    public void onComplete(Object data) {
                                        rxDao.unsubscribe();
                                    }
                                });
                            }
                        }
                    });
                }
                else {
                    rxDao.insertSync(playerHistoryBean, new DbCallBack() {
                        @Override
                        public void onComplete(Object data) {
                            rxDao.unsubscribe();
                        }
                    });
                }
            }
        });

    }

    @Override
    public void delPlayHistory (String programId) {
        rxDao.subscribe();
        HashMap<String, String> map = new HashMap<>();
        map.put("programId", programId);
        rxDao.deleteByColumnName(map);
        rxDao.unsubscribe();
    }

    @Override
    public void enterDetailLog(final String id, final Context context) {
        subscription = SumaClient.subscribe(new Callable<Observable< BaseData>>() {
            @Override
            public Observable< BaseData> call() throws Exception {
                return SumaClient.getRetrofitInstance(LogRetrofit.class, AppGlobalVars.logApiHost).enterDetailLog(id, AppUtil.getDeviceInfo(context));
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

    @Override
    public void pvLog(final String id, final Context context) {
        subscription = SumaClient.subscribe(new Callable<Observable<BaseData>>() {
            @Override
            public Observable<BaseData> call() throws Exception {
                return SumaClient.getRetrofitInstance(LogRetrofit.class, AppGlobalVars.logApiHost).pvLog(id, AppUtil.getDeviceInfo(context));
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

    @Override
    public void release() {
        if(subscription != null){
            subscription.unsubscribe();
        }
    }
}
