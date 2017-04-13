package com.sumavision.talktv2.http;

import com.sumavision.talktv2.model.entity.HintData;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 *  desc  网络访问的接口
 *  @author  yangjh
 *  created at  16-5-23 上午11:35
 */
public interface SearchHintRetrofit {
     @GET("http://suggest.video.iqiyi.com/")
    Observable<HintData> getSearchData(@Query("if") String ifdata, @Query("rltnum") int pageCount, @Query("key") String key);
}
