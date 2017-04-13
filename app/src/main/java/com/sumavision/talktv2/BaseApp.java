package com.sumavision.talktv2;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.jiongbull.jlog.JLog;
import com.marswin89.marsdaemon.DaemonClient;
import com.marswin89.marsdaemon.DaemonConfigurations;
import com.pplive.sdk.PPTVSdkStatusListener;
import com.sohuvideo.api.SohuPlayerSDK;
import com.sumavision.cachingwhileplaying.CachingWhilePlayingService;
import com.sumavision.talktv2.common.ACache;
import com.sumavision.talktv2.util.AppUtil;
import com.sumavision.talktv2.util.CommonUtil;
import com.sumavision.talktv2.wakeup.Receiver1;
import com.sumavision.talktv2.wakeup.Receiver2;
import com.sumavision.talktv2.wakeup.Service1;
import com.sumavision.talktv2.wakeup.Service2;

import crack.cracker.JarCracker;

//import com.squareup.leakcanary.RefWatcher;


/**
 *  desc  初始化Application
 *  @author  yangjh
 *  created at  16-5-18 下午4:37
 */
public class BaseApp extends Application {
//    private RefWatcher refWatcher;
    private static BaseApp instance;
    private static ACache mCache;
    private DaemonClient mDaemonClient;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = (BaseApp) getApplicationContext();
        mCache = ACache.get(instance);
        //开启leak内存检测
//        refWatcher = LeakCanary.install(this);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());

        JLog.init(this)
                .setDebug(BuildConfig.DEBUG);

//        startCachingWhilePlayingService(getApplicationContext());
        if (CommonUtil.getCurProcessName(this).equals(getPackageName())) {
            startCachingWhilePlayingService(getApplicationContext());
            // 搜狐sdk初始化
            SohuPlayerSDK.init(getApplicationContext());

//            // PPTVsdk初始化
//            String param = String.format("%s=%s",
//                    PPTVSdkParam.Config_Tunnel, "2435");
//            PPTVSdkMgr.getInstance().init(this, null, param, null, mListener);
//            String serviceName = "com.pplive.sdk.service";
//            boolean b = PPTVSdkMgr.getInstance().bindService(serviceName);

            JarCracker.getInstance().init(getApplicationContext());
            TalkTvExcepiton.getInstance().init(getApplicationContext());
        }
    }

   /* public static RefWatcher getRefWatcher(Context context) {
        BaseApp application = (BaseApp) context.getApplicationContext();
        return application.refWatcher;
    }*/
    public static Context getContext() {
        return instance;
    }
    public static ACache getACache(){
        if (mCache == null){
            mCache = ACache.get(instance);
        }
        return mCache;
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        if (AppUtil.wakeUpCheck()) {
            mDaemonClient = new DaemonClient(createDaemonConfigurations());
            mDaemonClient.onAttachBaseContext(base);
        }
    }

    public static PPTVSdkStatusListener mListener = new PPTVSdkStatusListener() {

        @Override
        public void onInitBegin() {
            Log.e("pptv_sdk", "onInitBegin");
        }

        @Override
        public void onInitEnd(int error) {
            Log.e("pptv_sdk", "onInitEnd: " + error);
        }

    };

    private void startCachingWhilePlayingService(Context context) {
        Intent intent = new Intent(context, CachingWhilePlayingService.class);
        intent.putExtra(CachingWhilePlayingService.ACTION_KEY,
                CachingWhilePlayingService.ACTION_START_SERVICE);
        intent.putExtra("appType", CachingWhilePlayingService.app_type.tvfan);
        startService(intent);
    }


    private DaemonConfigurations createDaemonConfigurations(){
        DaemonConfigurations.DaemonConfiguration configuration1 = new DaemonConfigurations.DaemonConfiguration(
                "com.sumavision.talktv2:process1",
                Service1.class.getCanonicalName(),
                Receiver1.class.getCanonicalName());
        DaemonConfigurations.DaemonConfiguration configuration2 = new DaemonConfigurations.DaemonConfiguration(
                "com.sumavision.talktv2:process2",
                Service2.class.getCanonicalName(),
                Receiver2.class.getCanonicalName());
        DaemonConfigurations.DaemonListener listener = new MyDaemonListener();
        //return new DaemonConfigurations(configuration1, configuration2);//listener can be null
        return new DaemonConfigurations(configuration1, configuration2, listener);
    }


    class MyDaemonListener implements DaemonConfigurations.DaemonListener{
        @Override
        public void onPersistentStart(Context context) {
        }

        @Override
        public void onDaemonAssistantStart(Context context) {
        }

        @Override
        public void onWatchDaemonDaed() {
        }
    }
}