package com.sumavision.talktv2.http.interceptor;

import com.jiongbull.jlog.JLog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 *  desc  网络请求的拦截
 *  @author  yangjh
 *  created at  16-5-25 下午7:23
 */
public class LoggingInterceptor implements Interceptor {
    private static Map<String,String> map = new HashMap<String,String>();
    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        long t1 = System.nanoTime();

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        JLog.e(String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers()));
        return response;
    }
}
