package com.sumavision.talktv2.http;

import com.sumavision.talktv2.model.entity.decor.LiveData;
import com.sumavision.talktv2.model.entity.decor.LiveDetailData;
import com.sumavision.talktv2.model.entity.decor.LiveRquestBean;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * 直播相关的Retrofit
 * Created by zjx on 2016/6/3.
 */
public interface LiveRetrofit {

    //获取直播type和list数据
    @POST("tms/v40/live/program!channelListation.do")
    Observable<LiveData> getLiveData(@Body LiveRquestBean client);

    @POST("tms/v40/live/program!channelDetailation.do")
    Observable<LiveDetailData> getLiveDetailData(@Body LiveRquestBean client);
}
