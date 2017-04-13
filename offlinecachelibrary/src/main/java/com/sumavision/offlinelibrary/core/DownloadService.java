package com.sumavision.offlinelibrary.core;

import java.util.ArrayList;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.sumavision.R;
import com.sumavision.crack.CrackCallback;
import com.sumavision.crack.OnCrackCompleteListener;
import com.sumavision.offlinelibrary.core.RestartDownloadManage.OnRestartListener;
import com.sumavision.offlinelibrary.dao.AccessDownload;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfoState;
import com.sumavision.offlinelibrary.entity.VideoFormat;
import com.sumavision.offlinelibrary.entity.DownloadInfo.UrlType;
import com.sumavision.offlinelibrary.util.CommonUtils;
import com.sumavision.offlinelibrary.util.OfflineNotification;

import crack.util.ACache;

/**
 * 离线缓存后台进程 控制器
 *
 * @author Administrator
 */
public class DownloadService extends Service implements
        OnCrackCompleteListener, OnRestartListener {
    public static final String TAG = "DownloadService";
    public static int action;
    public static final int ACTION_INVILIDATE = -1;
    public static final int ACTION_DELETE_DOWNLOADING = 2;
    public static final int ACTION_DOWNLOAD_NEW_TASK = 3;
    public static final int ACTION_PAUSE = 4;
    // 第一次注册网络监听不要管 temp flag;
    public boolean firstRegist = true;

    public static final String ACTION_KEY = "action";
    // public static final String KEY_PAUSE_DELETE_ADD = "action";
    public static final String APPNAME_KEY = "appname";
    public static final String APP_EN_NAME_KEY = "app_en_name";
    public static final int PARSE_INVALIDATE = 7200000;// 破解失效时长--2小时
    String appEnName, appName;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private CrackCallback mCrackCallback;
    NotificationManager notificationManager;
    OfflineNotification mOfflineNotification;

    @Override
    public void onCreate() {
        Log.d(TAG, "DownloadService-->>onCreate");
        super.onCreate();
        // String fileDir = CommonUtils.getCachePath(this);
        // if (!TextUtils.isEmpty(fileDir)) {
        //
        // DownloadUtils.RootDir = fileDir;
        // FileUtils.SDPATH = fileDir;
        // } else {
        // Toast.makeText(this, "SD卡不存在，无法缓存", Toast.LENGTH_SHORT).show();
        // return;
        // }
        mCrackCallback = new CrackCallback(getApplicationContext());
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mOfflineNotification = new OfflineNotification(getApplicationContext());
        // Log.d(TAG, "SDPATH-->>" + CommonUtils.getCachePath(this));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        if (intent.hasExtra(APPNAME_KEY)) {
            appName = intent.getStringExtra(APPNAME_KEY);
            appEnName = intent.getStringExtra(APP_EN_NAME_KEY);
            if (appEnName != null) {
                if (appEnName.equals("BaiShiTV")) {
                    NOTIFICATION_ID = 1020;
                } else if (appEnName.equals("FlashMovie")) {
                    NOTIFICATION_ID = 1021;
                } else if (appEnName.equals("melonqvod")) {
                    NOTIFICATION_ID = 1022;
                } else if (appEnName.equals("video1080")) {
                    NOTIFICATION_ID = 1023;
                }

            }
            SharedPreferences sp = getSharedPreferences("notifyId", 0);
            sp.edit().putInt("notifyId", NOTIFICATION_ID).commit();
            if (TextUtils.isEmpty(appEnName)) {
                appEnName = "tvfanphone";
            }
            if (TextUtils.isEmpty(appName)) {
                appName = "电视粉";
            }
            mOfflineNotification.setAppInfo(appName, appEnName);
        }
        DownloadInfo info = (DownloadInfo) intent
                .getSerializableExtra(DownloadManager.extra_loadinfo);
        Bundle bundle = intent.getBundleExtra("bundle");
        if (bundle != null) {
            action = bundle.getInt(ACTION_KEY, ACTION_INVILIDATE);
        } else {
            action = ACTION_INVILIDATE;
        }
        try {
            if (action == ACTION_PAUSE || action == ACTION_DELETE_DOWNLOADING) {
                handler.removeMessages(ERROR_RETURY);
                handler.removeMessages(WIFI_AVAILABLE);
                if (action == ACTION_DELETE_DOWNLOADING) {
                    info.state = DownloadInfoState.DELETE;
                    deleteDownload(info);
                } else {
                    info.state = DownloadInfoState.PAUSE;
                    pauseDownload(info);
                }
                if (!AccessDownload.getInstance(getApplicationContext())
                        .isDownloadingExecute()) {
                    changeFirstWatingStatus();

                    onDowloadRequest();
                }
                handler.removeMessages(CRACK_TIME_OUT);
            } else {
                Log.d(TAG, "ACTION_DOWNLOAD_NEW_TASK-");
                handler.removeMessages(ERROR_RETURY);
                handler.removeMessages(WIFI_AVAILABLE);
                handler.removeMessages(NEW_TASK);
                onDowloadRequest();

            }
            if (!init) {
                registReceiver();
            }
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            wifiLock = wifiManager.createWifiLock(wifiLockString);
            wifiLock.acquire();
        } catch (Exception e) {
            Log.e(TAG, "onStartCommand-!!!-ERROR-!!!-" + e.toString());
        }
        return START_STICKY;
    }

    /*
     * 删除任务
     */
    private void deleteDownload(DownloadInfo info) {

        preDeleteOrPause();
        DownloadManager.getInstance(getApplicationContext())
                .pauseOrDeleteDownloadIngTask(false);
    }

    /*
     * 暂停任务
     */
    private void pauseDownload(DownloadInfo info) {
        preDeleteOrPause();
        DownloadManager.getInstance(getApplicationContext())
                .pauseOrDeleteDownloadIngTask(true);

    }

    /*
     * 暂停或者删除之前，取消下载通知、handler、重启
     */
    private void preDeleteOrPause() {
        stopForeground(true);
        notificationManager.cancel(NOTIFICATION_ID);
        handler.removeMessages(ERROR_RETURY);
        handler.removeMessages(WIFI_AVAILABLE);
        handler.removeMessages(NEW_TASK);
    }

    private static final String wifiLockString = "tvFanWifiLock";
    private WifiLock wifiLock = null;
    private static int onDowloadRequestCount = 0;

    private void onDowloadRequest() {
        Log.e(TAG, "onDownloadRequest");

        handler.removeMessages(ERROR_RETURY);
        handler.removeMessages(WIFI_AVAILABLE);
        handler.removeMessages(NEW_TASK);
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    if (onDowloadRequestCount > 10) {
                        onDowloadRequestCount = 0;
                        return;
                    }
                    if (DownloadManager.getInstance(getApplicationContext())
                            .getActiveThreadPoolsCount() != 0) {
                        onDowloadRequestCount++;
                        // DownloadManager.getInstance(getApplicationContext())
                        // .pauseOrDeleteDownloadIngTask(true);
                        Log.d(TAG, "存在正在下载的任务，等待4秒再继续下载");
                        handler.sendEmptyMessageDelayed(NEW_TASK, 3 * 1000);
                        return;
                    }

                    ArrayList<DownloadInfo> downloadingInfos = AccessDownload
                            .getInstance(getApplicationContext())
                            .queryDownloadInfo(DownloadInfoState.DOWNLOADING);
                    if (downloadingInfos != null && downloadingInfos.size() > 0) {
                        DownloadInfo downloadInfo = downloadingInfos.get(0);
                        Message downMsg = handler.obtainMessage();
                        downMsg.what = EXECUTE_DOWNLOAD;
                        downMsg.obj = downloadInfo;
                        handler.sendMessage(downMsg);
                    }
                }
            }).start();

        } else {
            Toast.makeText(this, "SD卡不存在，无法缓存", Toast.LENGTH_SHORT).show();
        }

    }

    ArrayList<DownloadInfo> downloadingInfos = new ArrayList<DownloadInfo>();

    // 继续下载, 优先下载等待信息, 然后是暂停的信息
    public void downloadOnNetConnected() {
        Log.e(TAG, "downloadOnNetConnected");
        boolean downNext = changeFirstWatingStatus();
        if (downNext) {
            onDowloadRequest();
        } else {
            stopForeground(true);
            stopSelf();
        }
    }

    public static int NOTIFICATION_ID = 1001;

    public void executeDownload(final DownloadInfo downloadInfo) {
        Log.e(TAG, "executeDownload");
        int type;
        if (AccessDownload.getInstance(this).isExistedSegs(downloadInfo)) {
            AccessDownload.getInstance(this).querySegsInfo(downloadInfo);
        } else {
            AccessDownload.getInstance(this).saveSegs(downloadInfo);
        }
        if (downloadInfo.sdcardDir == null
                || TextUtils.isEmpty(downloadInfo.sdcardDir)) {
            String fileDir = CommonUtils.getCachePath(this);
            Log.d(TAG, "SDPATH-->>" + fileDir);
            if (fileDir == null || TextUtils.isEmpty(fileDir)) {
                Toast.makeText(this, "SD卡不存在，无法缓存", Toast.LENGTH_SHORT).show();
                return;
            }
            downloadInfo.sdcardDir = fileDir;
            AccessDownload.getInstance(this).updateDownloadInfo(downloadInfo);

        }
        long currTime = System.currentTimeMillis();
        if (downloadInfo.parseUrl == null
                || PARSE_INVALIDATE < currTime
                - downloadInfo.initUrlDownloadTime) {
            type = VideoFormat.UNKNOW_FORMAT;
            if (downloadInfo.initUrl.contains("-webparse")) {

                downloadInfo.initUrl = downloadInfo.initUrl.substring(0,
                        downloadInfo.initUrl.indexOf("-webparse"));
            } else if (UrlType.TYPE_M3U8 == DownloadUtils
                    .getUrlType(downloadInfo.initUrl)) {
                type = VideoFormat.M3U8_FORMAT;
                downloadInfo.videoFormat = type;
                // downloadInfo.initUrl =
                // "http://172.16.16.181/3/cachingwhileplaying.m3u8";
                downloadInfo.parseUrl = downloadInfo.initUrl;
                AccessDownload.getInstance(getApplicationContext())
                        .updateDownloadInfo(downloadInfo);

            } else if (UrlType.TYPE_MP4 == DownloadUtils
                    .getUrlType(downloadInfo.initUrl)) {
                type = VideoFormat.MP4_FORMAT;
                downloadInfo.videoFormat = type;
                downloadInfo.parseUrl = downloadInfo.initUrl;
                AccessDownload.getInstance(getApplicationContext())
                        .updateDownloadInfo(downloadInfo);
            }
        }
        // if (downloadInfo.initUrl != null
        // && downloadInfo.initUrl.endsWith("-webparse")) {
        // type = VideoFormat.UNKNOW_FORMAT;
        //
        // downloadInfo.initUrl = downloadInfo.initUrl.substring(0,
        // downloadInfo.initUrl.indexOf("-webparse"));
        // }
        else {
            type = downloadInfo.videoFormat;
        }
        Log.d(TAG, "type:" + type);
        Log.d(TAG, "executeDownload-->>initUrl:" + downloadInfo.initUrl);
        serviceDownloadInfo = downloadInfo;

        // 处理加密情况
        final SharedPreferences sp = getSharedPreferences("m3u8_encrypt",
                MODE_PRIVATE);
        String encryptInfo = sp.getString(serviceDownloadInfo.programId + "_"
                + serviceDownloadInfo.subProgramId, "");
        if (!TextUtils.isEmpty(encryptInfo)) {
            String[] tmp = encryptInfo.split(",");
            serviceDownloadInfo.encrypted = true;
            serviceDownloadInfo.keyUrl = tmp[0];
            serviceDownloadInfo.iv = tmp[1];
            serviceDownloadInfo.encryptKeyDownloaded = tmp[2].equals("true") ? true
                    : false;
            if (!serviceDownloadInfo.encryptKeyDownloaded) {
                new Thread() {
                    @Override
                    public void run() {
                        int i = 0;
                        while (i < 3) {
                            boolean result = DownloadUtils.downloadFile(
                                    downloadInfo.keyUrl,
                                    DownloadUtils.getFileDir(downloadInfo));
                            if (result) {
                                downloadingInfos.clear();
                                sp.edit()
                                        .putString(
                                                downloadInfo.programId
                                                        + "_"
                                                        + downloadInfo.subProgramId,
                                                downloadInfo.keyUrl + ","
                                                        + downloadInfo.iv
                                                        + ",true").commit();
                                break;
                            }
                            i++;
                        }
                    }
                }.start();

            }
        }

        if (type == VideoFormat.UNKNOW_FORMAT) {
            mCrackCallback.setListener(this);
            handler.removeMessages(CRACK_TIME_OUT);
            Message msg = handler.obtainMessage(CRACK_TIME_OUT);
            msg.obj = downloadInfo;
            handler.sendMessageDelayed(msg, 20 * 1000);

            String definition = AccessDownload.getInstance(this)
                    .queryDownloadDefinition(downloadInfo);
            // serviceDownloadInfo.initUrl =
            // "http://112.91.94.240/play.html?pickcode=dl4r1qegm3y1xxllu";
            Log.i(TAG, "离线缓存破解：" + serviceDownloadInfo.initUrl);
            mCrackCallback.parse(serviceDownloadInfo.initUrl, definition);
        } else if (type == VideoFormat.M3U8_FORMAT
                || type == VideoFormat.MP4_FORMAT) {
            parseSegs(downloadInfo);
        } else {
            AccessDownload.getInstance(getApplicationContext()).deleteProgram(
                    downloadInfo.programId);
            Toast.makeText(getApplicationContext(), "无法缓存此段",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean wifiDisConnected = false;

    private void restartDownloadTask() {
        if (wifiDisConnected) {
            return;
        }
        Log.d(TAG, "restartDownloadTask-->>");

        /**
         * 1.获取当前正在下载的任务,2.关闭下载任务, 3.重新下载任务
         */
        ArrayList<DownloadInfo> waitingInfoALL = AccessDownload.getInstance(
                getApplicationContext()).queryDownloadInfo(
                DownloadInfoState.WAITTING);
        if (waitingInfoALL != null && waitingInfoALL.size() > 0) {
            ArrayList<DownloadInfo> downloadingInfos = AccessDownload
                    .getInstance(getApplicationContext()).queryDownloadInfo(
                            DownloadInfoState.DOWNLOADING);
            if (downloadingInfos != null && downloadingInfos.size() > 0) {
                final DownloadInfo downloadingInfo = downloadingInfos.get(0);
                Log.d(TAG, "restartDownloadTask-->>pause m3u8 downloading task");
                handler.removeMessages(ERROR_RETURY);
                handler.removeMessages(WIFI_AVAILABLE);
                DownloadManager.getInstance(getApplicationContext())
                        .setCurrentDownloadInfo(downloadingInfo);
                DownloadManager.getInstance(getApplicationContext())
                        .pauseOrDeleteDownloadIngTask(true);
                onDowloadRequest();

            } else {
                Log.d(TAG, "restartDownloadTask-->>New downloading task");
                boolean downNext = changeFirstWatingStatus();
                if (downNext) {
                    onDowloadRequest();
                }
            }
        } else {
            Log.d(TAG, "无下载任务，无需重启下载任务");
        }

    }

    private static int restartCount = 0;
    private static int restartCountMax = 2;

    public boolean changeFirstWatingStatus() {
        if (!AccessDownload.getInstance(getApplicationContext())
                .isDownloadingExecute()) {
            DownloadInfo downloadInfo = null;
            ArrayList<DownloadInfo> downloadingForNetworks = AccessDownload
                    .getInstance(getApplicationContext()).queryDownloadInfo(
                            DownloadInfoState.DOWNLOADING_FOR_NETWORK);
            if (downloadingForNetworks != null
                    && downloadingForNetworks.size() > 0) {
                downloadInfo = downloadingForNetworks.get(0);
            } else {
                ArrayList<DownloadInfo> waittings = AccessDownload.getInstance(
                        getApplicationContext()).queryDownloadInfo(
                        DownloadInfoState.WAITTING);
                if (waittings != null && waittings.size() > 0) {
                    downloadInfo = waittings.get(0);

                }
            }
            if (downloadInfo != null) {
                downloadInfo.state = DownloadInfoState.DOWNLOADING;
                AccessDownload.getInstance(getApplicationContext())
                        .updateDownloadState(downloadInfo);
                // 更新编辑界面
                sendBroadcast(DownloadManager.ACTION_DOWNLOAD_REFRESH,
                        downloadInfo);
                Log.e(TAG, "changeFirstWatingStatus");
                return true;
            }

        } else {
            Log.e(TAG, "has downloading");
            return true;
        }
        return false;
    }

    private boolean init;

    private void registReceiver() {
        Log.e(TAG, "registReceiver");
        init = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction(NOTIFICATION_ID + "_"
                + DownloadManager.ACTION_DOWNLOAD_ERROR);
        filter.addAction(NOTIFICATION_ID + "_"
                + DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        filter.addAction(NOTIFICATION_ID + "_"
                + DownloadManager.ACTION_DOWNLOAD_REFRESH);
        filter.addAction(NOTIFICATION_ID + "_"
                + DownloadManager.ACTION_DOWNLOAD_PAUSE);
        registerReceiver(downloadReceiver, filter);
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter2);
        IntentFilter filter3 = new IntentFilter();
        filter.addAction("android.intent.action.MEDIA_UNMOUNTED");
        registerReceiver(sdcardReceiver, filter3);

    }

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context paramContext, Intent paramIntent) {
            if (firstRegist) {
                firstRegist = false;
                return;
            }
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(paramIntent
                    .getAction())) {
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
                if (netInfo == null) {
                    Toast.makeText(DownloadService.this, "网络已断开", Toast.LENGTH_SHORT).show();
                    wifiDisConnected = true;
                    ArrayList<DownloadInfo> allDownloadingInfos = AccessDownload.getInstance(getApplicationContext())
                            .queryDownloadInfo(DownloadInfoState.DOWNLOADING);
                    stopCurrentDownload(paramContext, allDownloadingInfos);
                } else {
                    switch (netInfo.getType()) {
                        case ConnectivityManager.TYPE_MOBILE:
                            Toast.makeText(DownloadService.this, "2G/3G/4G网络已连接", Toast.LENGTH_SHORT).show();
                            Object tmp = ACache.get(DownloadService.this).getAsObject("CLICKNUM_ALLOWCACHE");
                            if (tmp != null && ((boolean) tmp)) {
                                Toast.makeText(DownloadService.this, R.string.mobile_download, Toast.LENGTH_SHORT).show();
                                wifiDisConnected = false;
                                handler.removeMessages(WIFI_AVAILABLE);
                                handler.sendEmptyMessageDelayed(WIFI_AVAILABLE, 1000);
                                Log.e(TAG, "wifi connect");
                            } else {
                                Toast.makeText(DownloadService.this, R.string.mobile_download_forbid, Toast.LENGTH_SHORT).show();
                                wifiDisConnected = true;
                                ArrayList<DownloadInfo> allDownloadingInfos = AccessDownload.getInstance(getApplicationContext())
                                        .queryDownloadInfo(DownloadInfoState.DOWNLOADING);
                                stopCurrentDownload(paramContext, allDownloadingInfos);
                            }
                            break;
                        case ConnectivityManager.TYPE_WIFI:
                            wifiDisConnected = false;
                            handler.removeMessages(WIFI_AVAILABLE);
                            handler.sendEmptyMessageDelayed(WIFI_AVAILABLE, 1000);
                            Log.e(TAG, "wifi connect");
                            break;
                    }
                }
            }
        }
    };

    private void stopCurrentDownload(Context paramContext, ArrayList<DownloadInfo> allDownloadingInfos) {
        if (allDownloadingInfos.size() > 0) {
            pauseDownload(allDownloadingInfos.get(0));
            DownloadInfo downloadingInfo = allDownloadingInfos
                    .get(0);
            downloadingInfo.state = DownloadInfoState.DOWNLOADING_FOR_NETWORK;
            AccessDownload.getInstance(paramContext)
                    .updateDownloadState(downloadingInfo);
            // 更新界面
            sendBroadcast(DownloadManager.ACTION_DOWNLOAD_REFRESH,
                    downloadingInfo);
        }
        stopForeground(true);
    }

    private BroadcastReceiver sdcardReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context paramContext, Intent paramIntent) {
            String str = paramIntent.getAction();
            if ("android.intent.action.MEDIA_UNMOUNTED".equals(str)) {
                DownloadManager.getInstance(getApplicationContext())
                        .pauseOrDeleteDownloadIngTask(true);
                stopForeground(true);
            }
        }
    };

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, action);
            if (action.equals(NOTIFICATION_ID + "_"
                    + DownloadManager.ACTION_DOWNLOAD_REFRESH)) {
                // 切换任务和删除任务时，暂停更新通知
                DownloadInfo info = (DownloadInfo) intent
                        .getSerializableExtra(DownloadManager.extra_loadinfo);
                if (info != null) {
                    if (info.state == DownloadInfoState.DOWNLOADING) {
                        notificationManager.notify(NOTIFICATION_ID,
                                mOfflineNotification.createNotification(info,
                                        false));
                    }

                } else {
                    int state = intent.getIntExtra("state", -1);
                    if (state == DownloadInfoState.DOWNLOADING) {
                        DownloadInfo tmp = new DownloadInfo();
                        tmp.progress = intent.getIntExtra("progress", 0);
                        tmp.programName = intent.getStringExtra("programName");
                        notificationManager.notify(NOTIFICATION_ID,
                                mOfflineNotification.createNotification(tmp,
                                        false));
                    }
                }
            } else if (action.equals(NOTIFICATION_ID + "_"
                    + DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                Log.i(TAG, "receive broadcast "
                        + DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                DownloadInfo info = (DownloadInfo) intent
                        .getSerializableExtra(DownloadManager.extra_loadinfo);
                if (info != null) {
                    notificationManager
                            .notify(NOTIFICATION_ID, mOfflineNotification
                                    .createNotification(info, true));
                    notificationManager.cancel(NOTIFICATION_ID);
                } else {
                    DownloadInfo tmp = new DownloadInfo();
                    tmp.progress = intent.getIntExtra("progress", 0);
                    tmp.programName = intent.getStringExtra("programName");
                    notificationManager
                            .notify(NOTIFICATION_ID, mOfflineNotification
                                    .createNotification(tmp, false));
                    notificationManager.cancel(NOTIFICATION_ID);
                }
                stopForeground(true);
                handler.sendEmptyMessage(NEW_TASK);
            } else if (action.equals(NOTIFICATION_ID + "_"
                    + DownloadManager.ACTION_DOWNLOAD_ERROR)) {
                stopForeground(true);
                handler.removeMessages(ERROR_RETURY);
                handler.sendEmptyMessageDelayed(ERROR_RETURY, 3000);
            } else if (action.equals(NOTIFICATION_ID + "_"
                    + DownloadManager.ACTION_DOWNLOAD_PAUSE)) {
                stopForeground(true);
                handler.removeMessages(ERROR_RETURY);
                handler.sendEmptyMessage(NEW_TASK);
            } else if (action.equals(NOTIFICATION_ID + "_"
                    + DownloadManager.ACTION_DOWNLOAD_PARSE_ERROR)) {

                stopForeground(true);
            }
        }
    };

    private void sendBroadcast(String action, DownloadInfo info) {
        String preStr = DownloadService.NOTIFICATION_ID + "_";
        Intent intent = new Intent(preStr + action);
        intent.putExtra("loadinfo", info);
        sendBroadcast(intent);
    }

    public static final int WIFI_AVAILABLE = 789;
    public static final int ERROR_RETURY = 788;
    public static final int NEW_TASK = 785;
    public static final int EXECUTE_DOWNLOAD = 786;
    public static final int CRACK_TIME_OUT = 790;

    private DownloadInfo serviceDownloadInfo;
    private  int crackTimeOutCount = 0;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case WIFI_AVAILABLE:
                    downloadOnNetConnected();
                    break;
                case NEW_TASK:
                    startDownNext();
                    break;
                case EXECUTE_DOWNLOAD:
                    executeDownload((DownloadInfo) msg.obj);
                    break;
                case ERROR_RETURY:
                    if (wifiDisConnected) {
                        Log.d(TAG, "handler ERROR_RETURY-->>wifi 不可用");
                        return;
                    }
                    if (!AccessDownload.getInstance(getApplicationContext())
                            .isDownloadingExecute()) {
                        changeFirstWatingStatus();
                        onDowloadRequest();
                    }
                    break;
                case CRACK_TIME_OUT:
                    mCrackCallback.cancelCallback();
                    if (crackTimeOutCount >= 1) {
                        crackTimeOutCount = 0;
                        DownloadInfo temp = (DownloadInfo) msg.obj;
                        temp.state = DownloadInfoState.ERROR;
                        AccessDownload.getInstance(DownloadService.this)
                                .updateDownloadState(temp);
                        sendBroadcast(new Intent(NOTIFICATION_ID + "_"
                                + DownloadManager.ACTION_DOWNLOAD_ERROR));
                    } else {
                        crackTimeOutCount++;
                        onDowloadRequest();
                    }
                    break;
                default:
                    break;
            }
        }

        private void startDownNext() {
            if (CommonUtils.getWifiAvailable(getApplicationContext())) {
                boolean downNext = changeFirstWatingStatus();
                if (downNext) {
                    onDowloadRequest();
                } else {
                    stopForeground(true);
                    stopSelf();
                }
            } else {
                stopForeground(true);
            }
        }

        ;
    };

    private void parseSegs(DownloadInfo downloadInfo) {
        Log.e(TAG, "downloadM3u8");
        if (downloadInfo.videoFormat == VideoFormat.M3U8_FORMAT) {

            downloadInfo.fileName = "game.m3u8";
        }
        startForeground(NOTIFICATION_ID,
                mOfflineNotification.createNotification(downloadInfo, false));
        DownloadManager.getInstance(getApplicationContext()).parseSegs(downloadInfo);
    }

    @Override
    public void OnCrackComplete(String parseUrl, int videoFormat) {
        Log.i(TAG, "OnCrackComplete-->>parseUrl:" + parseUrl);
        // parseUrl =
        // "http://g3.letv.cn/vod/v2/MTMzLzEyLzEwNy9sZXR2LXV0cy8yMC92ZXJfMDBfMjItMzE5Mjg2Mjc2LWF2Yy0xNzg5NzA4LWFhYy05NjAwMC01NjYzMTE2LTEzNDM3MTc5NTktZDY0YjY5ODdmMWFmZDRkZDlhMmI1YmY0YjcwMDE3YzktMTQzMTk1NzQxMDQzNC5tcDQ=?b=1898&mmsid=30915313&tm=1432198911&key=5baf916d2f3bd0a7c83cbc476dfbfb8d&platid=14&splatid=1401&playid=0&tss=no&vtype=51&cvid=1527843657787&payff=0&pip=bed0331c9f0c9560aa69b3471161ccc6&tag=flash&sign=webdisk_109190699&termid=1&pay=0&ostype=windows&hwtype=un";
        if (serviceDownloadInfo.initUrl.contains("pickcode")) {
            videoFormat = 0;
        }
        // videoFormat = 1;
        handler.removeMessages(CRACK_TIME_OUT);
        crackTimeOutCount = 0;
        serviceDownloadInfo.parseUrl = parseUrl;
        serviceDownloadInfo.videoFormat = videoFormat;
        // 破解失败
        if (videoFormat == VideoFormat.UNKNOW_FORMAT) {
            sendBroadcast(new Intent(DownloadManager.ACTION_DOWNLOAD_ERROR));
            return;
        }
        AccessDownload.getInstance(getApplicationContext()).updateDownloadInfo(
                serviceDownloadInfo);
        parseSegs(serviceDownloadInfo);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "DownloadService-->>onDestroy");
        super.onDestroy();
        unregisterReceiver(downloadReceiver);
        unregisterReceiver(networkReceiver);
        unregisterReceiver(sdcardReceiver);
        if (wifiLock != null && wifiLock.isHeld()) {
            wifiLock.release();
        }
    }

    @Override
    public void onRestartDownload() {
        restartDownloadTask();
    }

}
