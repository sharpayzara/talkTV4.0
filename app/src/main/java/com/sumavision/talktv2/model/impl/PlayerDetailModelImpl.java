package com.sumavision.talktv2.model.impl;
import android.content.Context;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.dao.RxDao;
import com.sumavision.talktv2.dao.ormlite.DbCallBack;
import com.sumavision.talktv2.http.DownLoadClient;
import com.sumavision.talktv2.http.LogRetrofit;
import com.sumavision.talktv2.http.PlayerRetrofit;
import com.sumavision.talktv2.http.SumaClient;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.PlayerDetailModel;
import com.sumavision.talktv2.model.entity.CpDataNet;
import com.sumavision.talktv2.model.entity.DetailRecomendData;
import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.model.entity.ProgramDetail;
import com.sumavision.talktv2.model.entity.SeriesDetail;
import com.sumavision.talktv2.model.entity.decor.BaseData;
import com.sumavision.talktv2.ui.listener.DownloadProgressListener;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.sumavision.talktv2.util.AppUtil;
import com.sumavision.talktv2.util.StringUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by sharpay on 16-6-20.
 */
public class PlayerDetailModelImpl implements PlayerDetailModel {
    private Subscription subscription;

    @Override
    public void getDetailData(final String id, final String mid, final String cpId, final CallBackListener listener) {
        subscription = SumaClient.subscribe(new Callable<Observable<ProgramDetail>>() {
            @Override
            public Observable<ProgramDetail> call() {
                return SumaClient.getRetrofitInstance(PlayerRetrofit.class).getDetailData(SumaClient.getCacheControl(),id,mid,cpId);
            }
        }, new Action1<ProgramDetail>() {
            @Override
            public void call(ProgramDetail programDetail) {
                listener.onSuccess(programDetail);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },ProgramDetail.class);
    }

    @Override
    public void getSeriesGridData(final String id, final String cpid, final String tab, final CallBackListener listener ) {
        subscription = SumaClient.subscribe(new Callable<Observable<SeriesDetail>>() {
            @Override
            public Observable<SeriesDetail> call() {
                return SumaClient.getRetrofitInstance(PlayerRetrofit.class).getSeriesGridData(SumaClient.getCacheControl(),id,cpid,tab);
            }
        }, new Action1<SeriesDetail>() {
            @Override
            public void call(SeriesDetail seriesDetail) {
                listener.onSuccess(seriesDetail);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },SeriesDetail.class);
    }

    @Override
    public void getSeriesListData(final String id, final String cpid, final int page, final int size, final CallBackListener listener) {
        subscription = SumaClient.subscribe(new Callable<Observable<SeriesDetail>>() {
            @Override
            public Observable<SeriesDetail> call() {
                return SumaClient.getRetrofitInstance(PlayerRetrofit.class).getSeriesSizeData(SumaClient.getCacheControl(),id,cpid,page,size);
            }
        }, new Action1<SeriesDetail>() {
            @Override
            public void call(SeriesDetail seriesDetail) {
                listener.onSuccess(seriesDetail);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },SeriesDetail.class);
    }

    @Override
    public void getRecommendData(final String id, final String cname, final CallBackListener listener) {
        subscription = SumaClient.subscribe(new Callable<Observable<DetailRecomendData>>() {
            @Override
            public Observable<DetailRecomendData> call() throws Exception {
                return SumaClient.getRetrofitInstance(PlayerRetrofit.class).getRecommendData(SumaClient.getCacheControl(),id, cname);
            }
        }, new Action1<DetailRecomendData>() {
            @Override
            public void call(DetailRecomendData detailRecomendData) {
                listener.onSuccess(detailRecomendData);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },DetailRecomendData.class);
    }

    @Override
    public void getCpData(final CallBackListener listener) {
        subscription = SumaClient.subscribe(new Callable<Observable<CpDataNet>>() {
            @Override
            public Observable<CpDataNet> call() throws Exception {
                return SumaClient.getRetrofitInstance(PlayerRetrofit.class).getCpData();
            }
        }, new Action1<CpDataNet>() {
            @Override
            public void call(CpDataNet cpDataNet) {
                listener.onSuccess(cpDataNet);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        }, CpDataNet.class);
    }

    @Override
    public void download(final CallBackListener listener, String url, File file, DownloadProgressListener progressListener) {
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
    public void enterDedailLog(final String id, final Context context) {
        subscription = SumaClient.subscribe(new Callable<Observable<BaseData>>() {
            @Override
            public Observable<BaseData> call() throws Exception {
                return SumaClient.getRetrofitInstance(LogRetrofit.class, AppGlobalVars.logApiHost).enterDetailLog(id, AppUtil.getDeviceInfo(context));
            }
        }, new Action1<BaseData>() {
            @Override
            public void call(BaseData baseData) {
                int i = 1;
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                int i = 1;
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
    public int getEpiPos(String epi, List<SeriesDetail.SourceBean> sourceBeens) {
        int count = sourceBeens.size();
        for(int i=0; i<count; i++) {
            if(epi.equals(sourceBeens.get(i).getEpi()))
                return i;
        }
        return 0;
    }

    @Override
    public PlayerHistoryBean getPlayHistory (String programId) {
        return (PlayerHistoryBean) rxDao.queryForFirst("programId", programId);
    }

    @Override
    public void insertPlayHistory (final PlayerHistoryBean playerHistoryBean) {
        rxDao.subscribe();
        rxDao.queryForAllSync(new DbCallBack<List<PlayerHistoryBean>>() {
            @Override
            public void onComplete(List<PlayerHistoryBean> datas) {
                if(datas != null && datas.size()>=50) {
                    rxDao.deleteByColumnNameSync("programId", datas.get(0).getProgramId(), new DbCallBack() {
                        @Override
                        public void onComplete(Object data) {

                        }
                    });
                }
            }
        });

        HashMap<String, String> map = new HashMap<>();
        map.put("programId", playerHistoryBean.getProgramId());
        rxDao.queryByConditionSync(map, new DbCallBack<List<PlayerHistoryBean>>() {
            @Override
            public void onComplete(List<PlayerHistoryBean> datas) {
                if(datas != null && datas.size()>0) {
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

    RxDao rxDao = new RxDao(BaseApp.getContext(), PlayerHistoryBean.class);
    @Override
    public void delPlayHistory (String programId) {
        HashMap<String, String> map = new HashMap<>();
        map.put("programId", programId);
        rxDao.deleteByColumnName(map);
    }



    @Override
    public void release() {
        if(subscription != null){
            subscription.unsubscribe();
        }
    }
}
