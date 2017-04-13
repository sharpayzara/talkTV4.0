package com.sumavision.talktv2.http;

import com.sumavision.talktv2.model.entity.MediaDetail;
import com.sumavision.talktv2.model.entity.MediaList;
import com.sumavision.talktv2.model.entity.MediaTopic;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import rx.Observable;

/**
 *  desc  网络访问的接口
 *  @author  yangjh
 *  created at  16-5-23 上午11:35
 */
public interface MediaRetrofit {

    //获取自媒体topic
    @GET("nav")
    Observable<MediaTopic> getMeidaTopicData(@Header("Cache-Control") String cacheControl);

    //获取自媒体列表
    @GET("nav/list")
    Observable<MediaList> getMeidaListData(@Header("Cache-Control") String cacheControl, @Query("page") Integer page, @Query("size") Integer size, @Query("txt") String txt);

    //获取自媒体详情
    @GET("video/info")
    Observable<MediaDetail> getMeidaDetailData(@Query("vid") String vid);

}
