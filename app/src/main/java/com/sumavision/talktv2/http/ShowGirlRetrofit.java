package com.sumavision.talktv2.http;

import com.sumavision.talktv2.model.entity.MediaDetail;
import com.sumavision.talktv2.model.entity.MediaList;
import com.sumavision.talktv2.model.entity.MediaTopic;
import com.sumavision.talktv2.model.entity.ShowGirlList;
import com.sumavision.talktv2.model.entity.ShowGirlTopic;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 *  desc  网络访问的接口
 *  @author  yangjh
 *  created at  16-5-23 上午11:35
 */
public interface ShowGirlRetrofit {

    //获取星秀topic
    @GET("/CDN/output/M/1/I/10002031/P/platform-2_a-1_c-70327/json.js")
    Observable<ShowGirlTopic> getShowGirlTopicData();

    //获取星秀列表
    @GET("/CDN/output/M/1/I/10002032/P/partId-{partId}_start-{start}_offset-{offset}_platform-2_a-1_c-100/json.js")
    Observable<ShowGirlList> getShowGirlListData(@Path("partId") int partId, @Path("start") int start, @Path("offset") int offset);

}
