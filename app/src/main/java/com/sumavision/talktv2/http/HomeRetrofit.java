package com.sumavision.talktv2.http;

import com.sumavision.talktv2.model.entity.ADInfoItem;
import com.sumavision.talktv2.model.entity.ScreenBean;
import com.sumavision.talktv2.model.entity.SpecialContentList;
import com.sumavision.talktv2.model.entity.SpecialDetail;
import com.sumavision.talktv2.model.entity.SpecialListData;
import com.sumavision.talktv2.model.entity.UpdateBean;
import com.sumavision.talktv2.model.entity.VersionData;
import com.sumavision.talktv2.model.entity.decor.BaseData;
import com.sumavision.talktv2.model.entity.decor.ClassifyData;
import com.sumavision.talktv2.model.entity.decor.ClassifyUpdataData;
import com.sumavision.talktv2.model.entity.decor.HomeRecommendData;
import com.sumavision.talktv2.model.entity.decor.HomeRecommendUpdateData;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 *  desc  网络访问的接口
 *  @author  yangjh
 *  created at  16-5-23 上午11:35
 */
public interface HomeRetrofit {


    /* //当本地数据存在的时候，
     @POST("/test/rest1.php")
     Observable<ClassifyUpdataData> getClassifyUpdateData(@Field("tag") String tag);*/
    //当本地数据存在的时候，更新分类
    @GET("nav/update/")
    Observable<ClassifyUpdataData> getClassifyUpdateData(@Header("Cache-Control") String cacheControl);


    //第一次请求服务器,获取全量分类
    @GET("nav/")
    Observable<ClassifyData> getClassifyData();

    //请求推荐列表全量接口
    @GET("nav/rec")
    Observable<HomeRecommendData> getHomeRecommendData(@Query("nid") String nid);
    //请求推荐列表更新接口
    @GET("nav/rec/update/")
    Observable<HomeRecommendUpdateData> getHomeRecommendUpdateData(@Query("nid") String nid, @Query("v") String v);

    //http://tvfan.cn/clientProcess.do
    //请服务器版本信息
    //"method":"versionLatest","jsession":"7125141c-0a2d-48e9-bc7e-6b2df6bd3256","client":1,"vid":"3.1.6.0","version":"2.2"
    @POST("/clientProcess.do")
    Observable<VersionData> getVersionData();

    @GET("nav/openview/")
    Observable<ScreenBean> loadScreenData();

    @GET("nav/upgrade/")
    Observable<UpdateBean> getUpdateData();

    //获取广告信息
    @GET("ad")
    Observable<ADInfoItem> getADInfo(@Query("type") String type);

    @GET("special/list")
    //加载专题列表接口
    Observable<SpecialListData> loadSpecialListData(@Query("page") int page, @Query("size") int size);

    @GET("special")
    //加载专题详情接口
    Observable<SpecialDetail> loadSpecialDetailData(@Query("id") String id);

    @GET("special/content/list")
        //加载专题详情列表接口
    Observable<SpecialContentList> loadSpecialContentList(@Query("id") String id,@Query("page") int page, @Query("size") int size);
/*

    @GET("")
    Observable<BaseData> sendScanData(@Query("id") String name);
*/


    @GET
    Observable<BaseData> sendScanData(@Url String url,@Query("id") String name);
}