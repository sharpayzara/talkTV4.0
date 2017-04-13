package com.sumavision.talktv2.http;

import android.content.Context;
import android.text.TextUtils;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jiongbull.jlog.JLog;
import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.dao.RxDao;
import com.sumavision.talktv2.dao.ormlite.DbCallBack;
import com.sumavision.talktv2.http.interceptor.HttpCacheInterceptor;
import com.sumavision.talktv2.http.interceptor.LoggingInterceptor;
import com.sumavision.talktv2.model.entity.ActionUrl;
import com.sumavision.talktv2.model.entity.HttpCache;
import com.sumavision.talktv2.model.entity.decor.BaseData;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.sumavision.talktv2.util.CryptTool;
import com.sumavision.talktv2.util.NetworkUtil;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.internal.DiskLruCache;
import okhttp3.internal.io.FileSystem;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 *  desc 获取retrofit实例
 *  @author  yangjh
 *  created at  16-5-23 上午10:50
 */
public class SumaHttpsClient<E extends BaseData> {
    private static RxDao httpCacheDao = new RxDao(BaseApp.getContext(), HttpCache.class);
    private static Map<String, String> map = new HashMap<String, String>();
    private static HashMap<String, Object> retrofitMap = new HashMap<>();
    private static Object sumaRetrofit;
    protected static final Object monitor = new Object();
    private static Retrofit retrofit;
    public static OkHttpClient okHttpClient;
    public static OkHttpClient.Builder okHttpBuilder;
    private static File cacheFile;
    static RxDao rxDao = new RxDao(BaseApp.getContext(), ActionUrl.class);

    private SumaHttpsClient() {
    }

    private static void initRetrofitClient(String host) {
        initOkHttpClient();
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(host)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)

                .build();
    }

    private static void initOkHttpClient() {
        if (okHttpClient == null) {
            // 因为BaseUrl不同所以这里Retrofit不为静态，但是OkHttpClient配置是一样的,静态创建一次即可
            cacheFile = new File(BaseApp.getContext().getCacheDir(),
                    "HttpCache"); // 指定缓存路径
            Cache cache = new Cache(cacheFile, 1024 * 1024 * 200); // 指定缓存大小200Mb
            // 云端响应头拦截器，用来配置缓存策略
            okHttpBuilder = new OkHttpClient.Builder();
            okHttpBuilder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            okHttpClient = okHttpBuilder
                    .readTimeout(15, TimeUnit.SECONDS)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .addNetworkInterceptor(new StethoInterceptor())//chrome工具调试的中间件
                    .addInterceptor(new HttpCacheInterceptor())
                    .addInterceptor(new LoggingInterceptor())
                    //.addInterceptor(new HttpLoggingInterceptor())
                    .cache(cache)
                    .sslSocketFactory(getSocketFactory(BaseApp.getContext()))
                    .build();

        }
    }

    public static <T> T getRetrofitInstance(Class<T> cls, String host) {
        synchronized (monitor) {
            if (sumaRetrofit == null || !retrofitMap.containsKey(cls.getName())) {
                initRetrofitClient(host);
                sumaRetrofit = retrofit.create(cls);
                retrofitMap.put(cls.getName(), sumaRetrofit);
            } else {
                return (T) retrofitMap.get(cls.getName());
            }
            return (T) sumaRetrofit;
        }
    }

    public static <T> T getRetrofitNewInstance(Class<T> cls,String host) {
        synchronized (monitor) {
            if (TextUtils.isEmpty(host)) {
                setAppGlobalHost();
            }
            initRetrofitClient(host);
            sumaRetrofit = retrofit.create(cls);
            return (T) sumaRetrofit;
        }

    }

    public static <T> T getRetrofitInstance(Class<T> cls) {
        synchronized (monitor) {
            if(TextUtils.isEmpty(AppGlobalVars.userCenterApiHost)){
                setAppGlobalHost();
            }
            if (sumaRetrofit == null || !retrofitMap.containsKey(cls.getName())) {
                initRetrofitClient(AppGlobalVars.userCenterApiHost);
                sumaRetrofit = retrofit.create(cls);
                retrofitMap.put(cls.getName(), sumaRetrofit);
            } else {
                return (T) retrofitMap.get(cls.getName());
            }
            return (T) sumaRetrofit;
        }
    }
    public static void setAppGlobalHost(){
        List<ActionUrl> list = (List<ActionUrl>) rxDao.queryForAll();
        if(list != null && list.size() > 0){
            ActionUrl actionUrl = list.get(0);
            AppGlobalVars.globalSoHost = actionUrl.getGlobal_so();
            AppGlobalVars.mepgApiHost = actionUrl.getMepg_api();
            AppGlobalVars.upgcApiHost= actionUrl.getUpgc_api();
            AppGlobalVars.liveApiHost = actionUrl.getLiveapi();
            AppGlobalVars.logApiHost = actionUrl.getMepg_log();
            AppGlobalVars.userCenterApiHost = actionUrl.getUsercenter();
            AppGlobalVars.duibaHost = actionUrl.getDuiba();
            AppGlobalVars.shareApiHost = actionUrl.getShare_api();
        }
    }

    public static <T extends BaseData> Subscription subscribe(Callable<Observable<T>> callable, final Action1<T> action, final Action1<Throwable> throwableAction, final Class cls) {
        Subscription subscription = null;
        try {
            final Observable<T> observable = callable.call();
            subscription = observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(action, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (throwable.toString().contains("HTTP 304 Not Modified")) {
                                Field[] fields = throwable.getClass().getDeclaredFields();
                                String url = "";
                                for (Field field : fields) {
                                    field.setAccessible(true);
                                    if (field.getName().equalsIgnoreCase("response")) {
                                        try {
                                            Response res = (Response) field.get(throwable);
                                            url = res.raw().request().url().toString();
                                            break;
                                        } catch (IllegalAccessException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                T t = null;
                                try {
                                    t = (T) new Gson().fromJson(getCacheContent(url), cls);
                                    t.setCacheSource(true);
                                    action.call(t);
                                } catch (Exception e) {
                                    delHeaderCache(url);
                                    e.printStackTrace();
                                    throwableAction.call(throwable);
                                }

                            } else {
                                throwableAction.call(throwable);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subscription;
    }

    public static String getCacheContent(String url) throws Exception {
        String path = url;
        Scanner sc = null;
        try {
            sc = new Scanner(getFromCache(path));
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuilder str = new StringBuilder();
        String s;
        if (sc != null) {
            while (sc.hasNext() && (s = sc.nextLine()) != null) {
                str.append(s);
            }
        }

        return str.toString();
    }

    public static String getCacheControl() {
        return NetworkUtil.isConnectedByState(BaseApp.getContext()) ? HttpCacheInterceptor.CACHE_CONTROL_NETWORK : HttpCacheInterceptor.CACHE_CONTROL_CACHE;
    }


    public static FilterInputStream getFromCache(String url) throws Exception {
        DiskLruCache cache = DiskLruCache.create(FileSystem.SYSTEM, cacheFile,
                201105, 2, 1024 * 1024 * 100);
        cache.flush();
        String key = CryptTool.md5Digest(url);
        final DiskLruCache.Snapshot snapshot;
        try {
            snapshot = cache.get(key);
            if (snapshot == null) {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
        okio.Source source = snapshot.getSource(1);
        BufferedSource metadata = Okio.buffer(source);
        FilterInputStream bodyIn = new FilterInputStream(metadata.inputStream()) {
            @Override
            public void close() {
                snapshot.close();
                try {
                    super.close();
                } catch (IOException e) {
                    JLog.e("IOEXcpteion");
                    e.printStackTrace();
                }
            }
        };
        return bodyIn;
    }

    public static void delHeaderCache(final String urlStr) {
        Observable.just("").subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io()).subscribe(
                new Action1<String>() {
                    @Override
                    public void call(String s) {
                        map.clear();
                        map.put("urlStr", urlStr);

                        List<HttpCache> list = httpCacheDao.queryByCondition(map);
                        if (list != null && list.size() > 0) {
                            httpCacheDao.subscribe();
                            httpCacheDao.deleteByColumnNameSync("urlStr", urlStr, new DbCallBack() {
                                @Override
                                public void onComplete(Object data) {
                                    httpCacheDao.unsubscribe();
                                }
                            });
                        }
                    }
                }
        );
    }

    private static final String KEY_STORE_TYPE_BKS = "bks";//证书类型
    private static final String KEY_STORE_TYPE_P12 = "PKCS12";//证书类型


    private static final String KEY_STORE_PASSWORD = "TVfan2026";//证书密码（应该是客户端证书密码）
    private static final String KEY_STORE_TRUST_PASSWORD = "TVfan2016";//授信证书密码（应该是服务端证书密码）

    public static SSLSocketFactory getSocketFactory(Context context) {


        InputStream trust_input = context.getResources().openRawResource(R.raw.ca);//服务器授信证书
        InputStream client_input = context.getResources().openRawResource(R.raw.tvfan);//客户端证书
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            KeyStore trustStore = KeyStore.getInstance(KEY_STORE_TYPE_P12);
            trustStore.load(trust_input, KEY_STORE_TRUST_PASSWORD.toCharArray());
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE_P12);
            keyStore.load(client_input, KEY_STORE_PASSWORD.toCharArray());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, KEY_STORE_PASSWORD.toCharArray());
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
            SSLSocketFactory factory = sslContext.getSocketFactory();
            return factory;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                trust_input.close();
                client_input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
