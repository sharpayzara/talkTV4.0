package com.sumavision.talktv2.http;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by sharpay on 16-7-6.
 */
public interface DownloadRetrofit {
    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);
}
