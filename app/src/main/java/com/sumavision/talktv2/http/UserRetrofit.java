package com.sumavision.talktv2.http;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.model.entity.AccountData;
import com.sumavision.talktv2.model.entity.AppKeyResult;
import com.sumavision.talktv2.model.entity.LoginAddressData;
import com.sumavision.talktv2.model.entity.LoginResult;
import com.sumavision.talktv2.model.entity.ResultData;
import com.sumavision.talktv2.model.entity.UserIntegeInfoItem;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 *  desc  网络访问的接口
 *  @author  yangjh
 *  created at  16-5-23 上午11:35
 */
public interface UserRetrofit {

    //官方登陆
    @POST("account/login")
    Observable<LoginResult> loginIn(@Query("account") String account, @Query("password") String password, @Query("atype") String atype);

    //三方登陆
    @POST("account/3rdparty")
    Observable<LoginResult> loginIn3rd(@Query("thpId") String openId, @Query("token") String accessToken, @Query("expires") String expires, @Query("by") String by);

    //保存用户信息详情
    @POST("account/userinfo/")
    Observable<ResultData> updateUserInfo(@Query("userId") String userId, @Query("nickName") String nickName, @Query("sex") String sex, @Query("birthday") String birthday
            , @Query("city") String city, @Query("province") String province, @Query("tag") String tag);

    //获取用户信息
    @GET("account/userinfo")
    Observable<AccountData> loadUserInfo(@Query("userId") String userId, @Query("token") String token);

    //获取用户积分
    @GET("credits")
    Observable<UserIntegeInfoItem> loadUserIntege(@Query("userId") String userId);
    //退出登陆
    @POST("account/logout")
    Observable<ResultData> loginOut(@Query("userId") String userId, @Query("by") String by);


    @Multipart
    @POST("account/profile")
    Observable<ResultData> uploadImg(@PartMap Map<String,RequestBody> params);


    //获取appkey
    @POST("app/keys")
    Observable<AppKeyResult> loadAppkey();

    //加积分
    @GET("credits/{event}/{userId}")
    Observable<ResultData> sendOpenAppLog(@Path("event") String event,@Path("userId") String userId, @Query("src") String src);

    @GET("autologin?")
    Observable<LoginAddressData> getLoginAddress(@Query("uid") String uid, @Query("credits") String credits, @Query("redirect") String currentUrl);
}
