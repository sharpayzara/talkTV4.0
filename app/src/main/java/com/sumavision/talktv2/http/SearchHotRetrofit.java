package com.sumavision.talktv2.http;

import com.sumavision.talktv2.model.entity.decor.SearchData;

import retrofit2.http.GET;
import retrofit2.http.Header;
import rx.Observable;

/**
 *  desc  网络访问的接口
 *  @author  yangjh
 *  created at  16-5-23 上午11:35
 */
public interface SearchHotRetrofit {
     @GET("nav/hot/")
    Observable<SearchData> getSearchData(@Header("Cache-Control") String cacheControl);
}
