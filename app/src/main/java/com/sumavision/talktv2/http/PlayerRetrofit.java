package com.sumavision.talktv2.http;

import com.sumavision.talktv2.model.entity.CpDataNet;
import com.sumavision.talktv2.model.entity.DetailRecomendData;
import com.sumavision.talktv2.model.entity.ProgramDetail;
import com.sumavision.talktv2.model.entity.ProgramListData;
import com.sumavision.talktv2.model.entity.ProgramListTopic;
import com.sumavision.talktv2.model.entity.ProgramSelection;
import com.sumavision.talktv2.model.entity.SeriesDetail;
import com.sumavision.talktv2.model.entity.YsqBean;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 *  desc  网络访问的接口
 *  @author  yangjh
 *  created at  16-5-23 上午11:35
 */
public interface PlayerRetrofit {

    //获取节目详情
    @GET("series/info")
    Observable<ProgramDetail> getDetailData(@Header("Cache-Control") String cacheControl, @Query("id") String id, @Query("mid") String mid, @Query("cpid") String cpId);

    //获取电视剧选集数据
    @GET("series/movie")
    Observable<SeriesDetail> getSeriesGridData(@Header("Cache-Control") String cacheControl, @Query("id") String id, @Query("cpid") String cpid, @Query("tab") String tab);

    //获取综艺选集数据
    @GET("series/movie")
    Observable<SeriesDetail> getSeriesSizeData(@Header("Cache-Control") String cacheControl, @Query("id") String id, @Query("cpid") String cpid, @Query("page") int page, @Query("size") int size);

    @GET("series/related ")
    Observable<DetailRecomendData> getRecommendData(@Header("Cache-Control") String cacheControl, @Query("id") String id, @Query("cname") String cname);

    //获取节目topic]
    @GET("nav/topic")
    Observable<ProgramListTopic> getProgramListTopic(@Header("Cache-Control") String cacheControl, @Query("cid") String id);

    //获取节目列表数据
    @GET("series/topic/list")
    Observable<ProgramListData> getProgramListData(@Query("tid") String tid, @Query("cname") String cname, @Query("page") Integer page, @Query("size") Integer size);

    //获取节目筛选数据
    @GET("nav/fl")
    Observable<ProgramSelection> getProgramSelectionData(@Header("Cache-Control") String cacheControl, @Query("cid") String cid);


    //获取筛选节目列表数据
    @GET("series/filter/list")
    Observable<ProgramListData> getProgramListSelectionData(@QueryMap Map<String, String> options);

    //获取影视圈列表数据
    @GET("media/views")
    Observable<YsqBean> getYsqData(@Header("Cache-Control") String cacheControl, @Query("page") Integer page, @Query("size") Integer size);

    //获取总Cp数据   http://172.16.40.87:7180/nav/cp/
    @GET("nav/cp/")
    Observable<CpDataNet> getCpData();

}
