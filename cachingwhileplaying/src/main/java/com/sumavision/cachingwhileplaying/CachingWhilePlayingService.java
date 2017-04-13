package com.sumavision.cachingwhileplaying;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.sumavision.cachingwhileplaying.download.DownloadManager;
import com.sumavision.cachingwhileplaying.download.DownloadUtils;
import com.sumavision.cachingwhileplaying.entity.CachingWhilePlayingInfo;
import com.sumavision.cachingwhileplaying.entity.PreLoadingResultInfo;
import com.sumavision.cachingwhileplaying.server.CachingLocalServerUtils;
import com.sumavision.cachingwhileplaying.server.CachingWhilePlayingNanoHTTPD;
import com.sumavision.offlinelibrary.dao.AccessDownload;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfoState;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import crack.util.ACache;
import de.greenrobot.event.EventBus;

public class CachingWhilePlayingService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    boolean needParse;
    int action;
    private String programId;
    private String subId;
    private String url;
    public static final int ACTION_INVILIDATE = -1;
    public static final int ACTION_DOWNLOAD_SEGINFO = 6;
    public static final int ACTION_STOP = 7;
    public static final int ACTION_SEEKSERVICE = 10;
    public static final int ACTION_START_SERVICE = 11;
    public static final int ACTION_STOP_SERVICE = 13;
    public static final int ACTION_PRE_LOADING_START = 12;
    public static final int ACTION_PRE_LOADING_CHANGE_SOURCE_START = 17;
    public static final int ACTION_PRE_LOADING_STOP = 16;

    public static final String ACTION_KEY = "action";
    public static final String LOCAL_URL = "localUrl";
    public static final String TAG = "CachingWhilePlayingService";

    private static final int CRACK_MAX_TIME = 30000;
    private static final int CRACK_ERROR = 20;
    private static final int HANDLE_CRACK_RESULT = 22;
    protected static final int NO_NEED_TO_CRACK = 21;
    protected static final int POST_EVENT = 23;
    private Handler eventHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CRACK_ERROR:
                    break;
                case NO_NEED_TO_CRACK:
                    PreLoadingResultInfo tmp = (PreLoadingResultInfo) msg.obj;
                    crackResult(tmp);
                    break;
                case HANDLE_CRACK_RESULT:
                    handleCrackResult((PreLoadingResultInfo) msg.obj);
                    break;
                case POST_EVENT:
                    EventBus.getDefault().postSticky((PreLoadingResultInfo) msg.obj);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public enum app_type {
        tvfan(1), tvfanHD(2), dota(3), dota2(4), lol(5);
        final int value;

        app_type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter2);
    }

    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context paramContext, Intent paramIntent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(paramIntent
                    .getAction())) {
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
                if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    Log.e(TAG, "wifi connect");
                    if (DownloadManager.downNextPools != null)
                        seekService();
                }
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        action = intent.getIntExtra(ACTION_KEY, ACTION_INVILIDATE);
        if (action == ACTION_START_SERVICE) {
            Log.i(TAG, TAG + "ACTION_START_SERVICE");
            app_type appType = (app_type) intent
                    .getSerializableExtra("appType");
            CachingLocalServerUtils.getInstance(this).setPort(
                    appType.getValue());
            DownloadUtils.RootDir = getExternalFilesDir(null) + "";
        } else if (action == ACTION_STOP_SERVICE) {
            stopSelf();
        } else if (action == ACTION_PRE_LOADING_START) {
            Log.i(TAG, TAG + "ACTION_PRE_LOADING_START");
            programId = intent.getStringExtra("programId");
            subId = intent.getStringExtra("subId");
            Log.i(TAG, "subId-------------------------------->" + subId);
            url = intent.getStringExtra("url");
            Log.i("PlayerActivity", "url=" + url);
            needParse = intent.getBooleanExtra("needParse", true);
            if (url.contains("pcs.baidu")) {
                needParse = false;
            }
            PreLoadingResultInfo preLoadingResultInfo = new PreLoadingResultInfo();
            preLoadingResultInfo.path = url;
            preLoadingResultInfo.subId = subId;
            preLoadingResultInfo.type = "m3u8";
//            Message message = eventHandler.obtainMessage();
//            message.what = NO_NEED_TO_CRACK;
//            message.obj = preLoadingResultInfo;
//            eventHandler.sendMessageDelayed(message, 250);
            handleCrackResult(preLoadingResultInfo);

        } else if (action == ACTION_PRE_LOADING_CHANGE_SOURCE_START) {
            String url = intent.getStringExtra("url");
            changeSource(url);
        } else if (action == ACTION_PRE_LOADING_STOP) {
            cachingWhilePlayingStop();
//            stopSelf(startId);
        } else if (action == ACTION_STOP) {
            stop();
        } else if (action == ACTION_SEEKSERVICE) {
            seekService();
        }

        return START_STICKY;
    }

    private void changeSource(String url) {
        PreLoadingResultInfo preLoadingResultInfo = new PreLoadingResultInfo();
        preLoadingResultInfo.isChangeSource = true;
        preLoadingResultInfo.path = cachingWhilePlayingInit(url, true);
        EventBus.getDefault().post(preLoadingResultInfo);

    }

    public void crackResult(PreLoadingResultInfo preLoadingResultInfo) {
        Message msg = new Message();
        msg.what = HANDLE_CRACK_RESULT;
        msg.obj = preLoadingResultInfo;
//        eventHandler.sendMessage(msg);
    }

    private void cachingWhilePlayingStop() {
        shutdownThreadPools();
        CachingLocalServerUtils.getInstance(this).stopLocalHttpServerService();
        deleteFiles(String.valueOf(programId));
    }

    private void shutdownThreadPools() {
        Log.e(TAG, "shutdownThreadPools");
        DownloadManager downloadManager = DownloadManager.getInstance(this);
        if (downloadManager != null) {
            downloadManager.shutDownThreadPools();
        }
    }

    private static ConcurrentHashMap<String, DeleteFileThread> mDeleteFileThreads = new ConcurrentHashMap<String, DeleteFileThread>();

    class DeleteFileThread extends Thread {
        private String programId;
        private boolean interrupted;

        public DeleteFileThread(String programId) {
            this.programId = programId;
        }

        public String getProgramId() {
            return programId;
        }

        public void stop(boolean interrupted) {
            this.interrupted = interrupted;
        }

        @Override
        public void run() {
            try {
                deleteLocalFiles();
                mDeleteFileThreads.remove(programId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void deleteLocalFiles() {
            String path = getExternalFilesDir(null) + File.separator
                    + DownloadUtils.sdCardfileDir;
            File file = new File(path);
            if (file.exists()) {
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    for (File tempFile : files) {
                        // if (!TextUtils.isEmpty(programId)
                        // && tempFile.getName().startsWith(programId)) {
                        File targetFile = new File(tempFile.getAbsolutePath()
                                + System.currentTimeMillis());
                        tempFile.renameTo(targetFile);
                        if (targetFile.isDirectory()) {
                            File[] filess = targetFile.listFiles();
                            for (File tempF : filess) {
                                if (interrupted) {
                                    Log.i(TAG, "delete file interrupted");
                                    return;
                                } else {
                                    tempF.delete();
                                }
                            }
                        }
                        if (targetFile.delete()) {
                            Log.e(TAG, "delete file:" + targetFile.getName());
                        }
                    }
                    // }
                }
            }
        }
    }

    private void deleteFiles(String programId) {
        stopPreDeletingFiles();
        DeleteFileThread deleteFileThread = new DeleteFileThread(programId);
        deleteFileThread.start();
        mDeleteFileThreads.put(deleteFileThread.getProgramId(),
                deleteFileThread);
    }

    /**
     * 停止之前所有的删除任务
     */
    private void stopPreDeletingFiles() {
        for (DeleteFileThread tmp : mDeleteFileThreads.values()) {
            tmp.stop(true);
        }
        mDeleteFileThreads.clear();
    }

    private String cachingWhilePlayingInit(String url, boolean changeSource) {
        if (!changeSource) {
            creatCachingPools();
            CachingLocalServerUtils.getInstance(this)
                    .startLocalHttpServerService();
        }
        createInfo(programId, subId, url);
        String cachingWhilePlayingUrl = changeSourceToCachingWhilePlayingUrl(
                programId, subId, url);
        if (CachingWhilePlayingNanoHTTPD.segsInfo != null) {
            CachingWhilePlayingNanoHTTPD.segsInfo.clear();
        }
        stopPreDeletingFiles();
        onDowloadM3u8Request();
        return cachingWhilePlayingUrl;
    }

    private void creatCachingPools() {
        DownloadManager downloadManager = DownloadManager.getInstance(this);
        downloadManager.creatThreadPools();
    }

    private void createInfo(String programId, String subId, String m3u8Url) {
        CachingWhilePlayingInfo info = new CachingWhilePlayingInfo();
        info.programId = programId;
        info.subId = subId;
        info.m3u8 = m3u8Url;
        info.localUrl = changeSourceToCachingWhilePlayingUrl(programId, subId,
                m3u8Url);
        info.isDownloaded = false;
        info.isDownloading = false;
        CachingWhilePlayingNanoHTTPD.m3u8Info = info;
    }

    private String changeSourceToCachingWhilePlayingUrl(String programId,
                                                        String subId, String m3u8Url) {
        StringBuilder sb = new StringBuilder();
        sb.append(programId).append("_").append(subId);
        String customDir = sb.toString();
        String localFileDir = "http://localhost:"
                + CachingLocalServerUtils.SOCKET_PORT
                + "/"
                + DownloadUtils.sdCardfileDir
                + File.separator
                + customDir
                + File.separator
                + DownloadUtils.localFileName;
        return localFileDir;
    }

    private boolean needCaching(String type) {
        if (TextUtils.isEmpty(type)) {
            return false;
        }
        return type.equals("m3u8");
    }

    private void seekService() {
        Log.e(TAG, "seekService");
        DownloadManager downloadManager = DownloadManager
                .getInstance(CachingWhilePlayingService.this);
        if (DownloadManager.downNextPools.getActiveCount() == 0
                && DownloadManager.downloadSegCount < CachingWhilePlayingNanoHTTPD.totalSegCount)
            downloadManager.downNextNew(-1, DownloadManager.Editon);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        eventHandler.removeMessages(CRACK_ERROR);
//        eventHandler.removeMessages(NO_NEED_TO_CRACK);
        unregisterReceiver(networkReceiver);
    }

    private synchronized void onDowloadM3u8Request() {
        Log.e(TAG, "onDownloadm3u8Request");

        if (CachingWhilePlayingNanoHTTPD.m3u8Info != null
                && !CachingWhilePlayingNanoHTTPD.m3u8Info.isDownloading
                && !CachingWhilePlayingNanoHTTPD.m3u8Info.isDownloaded) {
            DownloadManager downloadManager = DownloadManager.getInstance(this);
            downloadManager.m3u8DownloadTempCount = 0;
            downloadManager
                    .downloadM3u8File(CachingWhilePlayingNanoHTTPD.m3u8Info);
        }

    }

    private void stop() {
        DownloadManager.stop();
    }

    private void handleCrackResult(PreLoadingResultInfo preLoadingResultInfo) {
        Log.i(TAG, "preLoadingResultInfo.subId-------->"
                + preLoadingResultInfo.subId + "" + "subId-------->" + subId);

//        if (!TextUtils.isEmpty(preLoadingResultInfo.type) && (preLoadingResultInfo.type.equals("updatePlugins") || preLoadingResultInfo.type.equals("complete"))) {
//            EventBus.getDefault().postSticky(preLoadingResultInfo);
//            return;
//            updateCrackDialog = new UpdateCrackDialog(this, R.style.dialog);
//            updateCrackDialog.show();
//            updateCrackDialog.setCancelable(false);
//            if (updateCrackDialog != null) {
//                updateCrackDialog.dismiss();
//            }
//            if (parserUtil != null) {
//                parserUtil.stop();
//            }
//            parserUtil = new ParserUtil(this, this, url, 0, subId);
//            eventHandler.removeMessages(CRACK_ERROR);
//            eventHandler.sendEmptyMessageDelayed(CRACK_ERROR,
//                    CRACK_MAX_TIME);
//        }

        if (preLoadingResultInfo.subId != subId) {
            Log.i(TAG, "not the current crack result, subId not fit");
            return;
        }
        if (TextUtils.isEmpty(preLoadingResultInfo.path)) {
            preLoadingResultInfo.path = "";
            EventBus.getDefault().postSticky(preLoadingResultInfo);
            return;
        }

        Log.i("PlayerActivity", "破解地址=" + preLoadingResultInfo.path);
//        if (eventHandler != null) {
//            eventHandler.removeMessages(CRACK_ERROR);
//        }
        preLoadingResultInfo.crackPath = preLoadingResultInfo.path;
        if (!TextUtils.isEmpty(preLoadingResultInfo.path)
                && needCaching(preLoadingResultInfo.type)) {
            preLoadingResultInfo.path = cachingWhilePlayingInit(
                    preLoadingResultInfo.path, false);
            Log.i(TAG, "need cachingwhileplaying");
        }

        preLoadingResultInfo.isChangeSource = false;
        Message msg = eventHandler.obtainMessage(POST_EVENT);
        msg.obj = preLoadingResultInfo;
        eventHandler.sendMessageDelayed(msg, 2000);
    }
}
