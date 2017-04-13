package com.sumavision.talktv2.http;

import com.sumavision.talktv2.model.entity.decor.SearchInfoData;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import rx.Observable;

/**
 *  desc  网络访问的接口
 *  @author  yangjh
 *  created at  16-5-23 上午11:35
 */
public interface  SearchRetrofit {
     @GET("so/")
    Observable<SearchInfoData> getSearchListInfoData(@Header("Cache-Control") String cacheControl, @Query("q") String q,@Query("page") int page);
}
