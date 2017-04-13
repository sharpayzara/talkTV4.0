package com.sumavision.talktv2.http.interceptor;

import android.text.TextUtils;

import com.jiongbull.jlog.JLog;
import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.dao.RxDao;
import com.sumavision.talktv2.dao.ormlite.DbCallBack;
import com.sumavision.talktv2.model.entity.HttpCache;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.sumavision.talktv2.util.NetworkUtil;
import com.sumavision.talktv2.util.UserAgentUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 云端响应头拦截器，用来配置缓存策略
 * Dangerous interceptor that rewrites the server's cache-control header.
 */

public class HttpCacheInterceptor implements Interceptor {
    private static RxDao httpCacheDao = new RxDao(BaseApp.getContext(), HttpCache.class,false);
    private static Map<String,String> map = new HashMap<String,String>();
    //设缓存有效期为两天
    public static final long CACHE_STALE_SEC = 60 * 60 * 24 * 2;
    //查询缓存的Cache-Control设置，为if-only-cache时只查询缓存而不会请求服务器，max-stale可以配合设置缓存失效时间
    public static final String CACHE_CONTROL_CACHE = "only-if-cached, max-stale=" + CACHE_STALE_SEC;
    //查询网络的Cache-Control设置，头部Cache-Control设为max-age=0时则不会使用缓存而请求服务器
    public static final String CACHE_CONTROL_NETWORK = "max-age=0";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!NetworkUtil.isConnectedByState(BaseApp.getContext())) {
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE).build();
            JLog.e("no network");
        }else{
            request =  setHeaderCache(request.url().toString(),request);
        }
        JLog.e(String.format("Sending request %s by %s%n%s",
                request.url(), request.method(), request.headers()));
        Response originalResponse = chain.proceed(request);

        if(originalResponse.code() == 504){
            delHeaderCache(request.url().toString());
        }
        if (NetworkUtil.isConnectedByState(BaseApp.getContext())) {
            //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
            if(originalResponse.code() == 200 ){
                saveHeaderCache(request.url().toString(),originalResponse.header("Last-Modified"),originalResponse.header("ETag"));
            }
            String cacheControl = request.cacheControl().toString();
            return originalResponse.newBuilder()
                    .header("Cache-Control", cacheControl)
                    .removeHeader("Pragma").build();
        } else {
            return originalResponse.newBuilder().header("Cache-Control",
                    "public, only-if-cached," + CACHE_STALE_SEC)
                    .removeHeader("Pragma").build();
        }

    }

    private Request setHeaderCache(final String urlStr,Request request) {
        map.clear();
        if(urlStr.contains(AppGlobalVars.mepgApiHost+"nav/update")){
            map.put("urlStr",urlStr);
            if(httpCacheDao.queryByCondition(map).size() == 0 ){
                map.put("urlStr",AppGlobalVars.mepgApiHost + "nav/");
            }
        }else{
            map.put("urlStr",urlStr);
        }
        List<HttpCache> list = httpCacheDao.queryByCondition(map);
        Request.Builder requestBuilder = request.newBuilder();
        requestBuilder.header("user-agent", UserAgentUtils.getUserAgent());
        if(list.size() > 0){
            HttpCache cache = list.get(0);
            requestBuilder.header("If-Modified-Since",cache.getIfModifiedSince()+"");
            requestBuilder.header("If-None-Match",cache.getIfNoneMatch()+"");
            return requestBuilder.build();
        }else{
            requestBuilder.header("If-Modified-Since","");
            requestBuilder.header("If-None-Match","");
            return requestBuilder.build();
        }
    }

    private void saveHeaderCache(final String urlStr, final String ifModifiedSince, final String ifNoneMatch) {
        if(TextUtils.isEmpty(ifModifiedSince)){
            return;
        }
        Observable.just("").subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io()).subscribe(
                new Action1<String>() {
                    @Override
                    public void call(String s) {
                        map.clear();
                        map.put("urlStr",urlStr);
                        httpCacheDao.subscribe();
                        httpCacheDao.queryByConditionSync(map, new DbCallBack<List<HttpCache>>() {

                            @Override
                            public void onComplete(List<HttpCache> data) {
                                    if(data != null && data.size() > 0){
                                        HttpCache cache = data.get(0);
                                        cache.setIfNoneMatch(ifNoneMatch);
                                        cache.setIfModifiedSince(ifModifiedSince);
                                        httpCacheDao.updateSync(cache, new DbCallBack<Boolean>() {
                                            @Override
                                            public void onComplete(Boolean data) {
                                                httpCacheDao.unsubscribe();
                                            }
                                        });
                                    }else{
                                        HttpCache cache = new HttpCache();
                                        cache.setUrlStr(urlStr);
                                        cache.setIfModifiedSince(ifModifiedSince);
                                        cache.setIfNoneMatch(ifNoneMatch);

                                        httpCacheDao.insertSync(cache, new DbCallBack() {
                                            @Override
                                            public void onComplete(Object data) {
                                                httpCacheDao.unsubscribe();
                                            }
                                        });
                                    }
                                }
                        });

                    }
                }
        );
    }

    private void delHeaderCache(final String urlStr) {
        Observable.just("").subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io()).subscribe(
                new Action1<String>() {
                    @Override
                    public void call(String s) {
                        map.clear();
                        map.put("urlStr",urlStr);
                        httpCacheDao.subscribe();
                       httpCacheDao.queryByConditionSync(map, new DbCallBack<List<HttpCache>>() {
                            @Override
                            public void onComplete(List<HttpCache> list) {
                                if(list != null && list.size() > 0){
                                    httpCacheDao.subscribe();
                                    httpCacheDao.deleteByColumnNameSync("urlStr", urlStr, new DbCallBack() {
                                        @Override
                                        public void onComplete(Object data) {
                                            httpCacheDao.unsubscribe();
                                        }
                                    });
                                }
                            }

                        });

                    }
                }
        );
    }
}
