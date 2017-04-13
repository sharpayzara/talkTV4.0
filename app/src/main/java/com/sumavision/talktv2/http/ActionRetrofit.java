package com.sumavision.talktv2.http;

import com.sumavision.talktv2.model.entity.ActionUrl;

import retrofit2.http.GET;
import retrofit2.http.Header;
import rx.Observable;

/**
 *  desc  网络访问的接口
 *  @author  yangjh
 *  created at  16-5-23 上午11:35
 */
public interface ActionRetrofit {
     @GET("mepg-api/nav/action/")
    Observable<ActionUrl> getActionUrlData(@Header("Cache-Control") String cacheControl);
}
