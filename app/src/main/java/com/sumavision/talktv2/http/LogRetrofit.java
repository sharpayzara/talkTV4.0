package com.sumavision.talktv2.http;

import com.sumavision.talktv2.model.entity.Base;
import com.sumavision.talktv2.model.entity.PreferenceBean;
import com.sumavision.talktv2.model.entity.ResultData;
import com.sumavision.talktv2.model.entity.decor.BaseData;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * log统计的retrofit
 * Created by zjx on 2016/7/19.
 */
public interface LogRetrofit {


    @GET("log/pv/")
    Observable<BaseData> enterDetailLog(@Query("id") String id, @Query("did") String did);

    @GET("log/vod/")
    Observable<BaseData> pvLog(@Query("id") String id, @Query("did") String did);

    @GET("log/live/")
    Observable<BaseData> livePlayLog(@Query("id") String id, @Query("did") String did, @Query("mid") String mid);

    @GET("log/info/")
    Observable<PreferenceBean> preferenceLog(@Query("sex") String sex, @Query("des") String  des, @Query("did") String did);

    @GET("play/live/")
    Observable<BaseData> livePlayErrorLog(@Query("cid") String channelId, @Query("mid") int mid, @Query("ftype") String errorType);

    @GET("play/vod/")
    Observable<BaseData> vodPlayErrorLog(@Query("sid") String channelId, @Query("mid") String mid, @Query("ftype") String errorType);


    @GET("log/msg/")
    Observable<PreferenceBean> baiduPushUpload(@Query("cid") String cid, @Query("uid") String  uid, @Query("did") String did, @Query("aid") String aid);


    //有奖反馈
    @GET("feedback/")
    Observable<ResultData> feedBack(@Query("problem") String problemStr, @Query("suggest") String suggest, @Query("contact") String contact, @Query("userId") String userId);


    @GET("favorite/")
    Observable<BaseData> favorite(@Query("uid") String uid, @Query("did") String  did, @Query("sid") String sid, @Query("evt") String evt);
}
