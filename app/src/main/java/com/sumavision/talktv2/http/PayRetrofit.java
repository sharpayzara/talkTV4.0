package com.sumavision.talktv2.http;

import com.sumavision.talktv2.model.entity.MediaDetail;
import com.sumavision.talktv2.model.entity.MediaList;
import com.sumavision.talktv2.model.entity.MediaTopic;
import com.sumavision.talktv2.model.entity.WXOrder;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import rx.Observable;

/**
 *  desc  网络访问的接口
 *  @author  yangjh
 *  created at  16-5-23 上午11:35
 */
public interface PayRetrofit {

    //获取自媒体topic
    @GET("/pub_v2/app/app_pay.php?plat=android")
    Observable<WXOrder> getWXOrder();
}
