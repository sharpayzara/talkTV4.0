package com.sumavision.cachingwhileplaying.download;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.sumavision.cachingwhileplaying.entity.BufferedPositionInfo;
import com.sumavision.cachingwhileplaying.entity.CachingWhilePlayingInfo;
import com.sumavision.cachingwhileplaying.entity.DownloadInfoState;
import com.sumavision.cachingwhileplaying.entity.SegInfo;
import com.sumavision.cachingwhileplaying.server.CachingLocalServerUtils;
import com.sumavision.cachingwhileplaying.server.CachingWhilePlayingNanoHTTPD;
import com.sumavision.cachingwhileplaying.util.BufferedProgressUtil;
import com.sumavision.cachingwhileplaying.util.NetworkUtil;

import de.greenrobot.event.EventBus;

public class DownloadManager {

    private static Context context;

    private static final String TAG = "DownloadManager";
    private boolean hasCreatedPools = false;
    static DownloadManager downloadManager;

    public static synchronized DownloadManager getInstance(Context context) {
        if (downloadManager == null) {
            downloadManager = new DownloadManager(context);
        }
        return downloadManager;
    }

    private DownloadManager(Context paramContext) {
        context = paramContext;
    }

    private DownloadM3U8FileThread downloadM3u8Thread;
    private ThreadPoolExecutor m3u8Threads;

    public void downloadM3u8File(CachingWhilePlayingInfo downloadInfo) {
        try {
            downloadM3u8Thread = new DownloadM3U8FileThread(downloadInfo,
                    new InfoHandler(downloadManager, downloadInfo));
            m3u8Threads.submit(downloadM3u8Thread);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class DownloadM3U8FileThread extends Thread {
        private CachingWhilePlayingInfo downloadInfo;
        private InfoHandler infoHandler;

        public DownloadM3U8FileThread(CachingWhilePlayingInfo downloadInfo,
                                      InfoHandler infoHandler) {
            this.downloadInfo = downloadInfo;
            this.infoHandler = infoHandler;
        }

        public void run() {
            boolean downloaded = false;
            CachingWhilePlayingNanoHTTPD.m3u8Info.isDownloading = true;
            downloaded = DownloadUtils.downInitData(downloadInfo);
            if (downloaded) {
                infoHandler.sendEmptyMessage(DOWNLOAD_M3U8FILE_OVER);
            } else {
                infoHandler.sendEmptyMessage(DOWNLOAD_ERROR);
            }
        }

    }

    ;

    // private DownloadSegThread downloadSegThread;
    private static final int mvSegsPoolsNum = 2;
    private static final int downNextPoolsNum = 3;

    public static final int DOWNLAOD_M3U8FILE_COUNT_EVERYTIME = 10;
    public static ThreadPoolExecutor downNextPools;
    private static ThreadPoolExecutor mvReqPools;
    private static boolean timerFlag = false;
    private static int mvReqIndex = 0;
    private static int index;

    public void downSegs(SegInfo info, boolean mvReq) {

        DownloadSegThread downloadSegThread = null;
        try {
            // if (downloadSegThread != null) {
            // downloadSegThread = null;
            // }
            if (!timerFlag) {
                timerFlag = true;
                index = info.index;
            }
            if (mvReq && index != info.index && !info.isDownloaded
                    && !info.isDownloading) {
                mvReqIndex = mvReqIndex + 1;
                index = info.index;

            }
            downloadSegThread = new DownloadSegThread(info);
            downloadSegThread.setAutoDownloadNext(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mvReq) {
            try {
                stopDownNext = false;
                if (mvReqPools != null && downloadSegThread != null) {
                    mvReqPools.submit(downloadSegThread);
                }
            } catch (RejectedExecutionException e) {
                stopDownNext = true;
                Log.e(TAG, "mvReqPools have shutdown");

            }
        } else {
            try {
                downNextPools.submit(downloadSegThread);
            } catch (RejectedExecutionException e) {
                stopDownNext = true;
                Log.e(TAG, "downNextPools have shutdown");
            }
        }

    }

    // 用于数据库操作的单线程池
    private ExecutorService sqlThreadPool;

    public void shutDownThreadPools() {
        try {
            hasCreatedPools = false;
            Log.e(TAG, "shutDownThreadPools");
            timerFlag = false;
            stopDownNext = true;
            if (m3u8Threads != null) {
                if (downloadM3u8Thread != null) {
                    downloadM3u8Thread.interrupt();
                }
                m3u8Threads.shutdownNow();
            }
            if (downNextPools != null) {
                downNextPools.shutdownNow();
            }
            if (mvReqPools != null) {
                mvReqPools.shutdownNow();
            }

        } catch (Exception e) {
            Log.e(TAG, "shutDownThreadPools    " + e);
            e.printStackTrace();
        }

    }

    public void creatThreadPools() {
        if (!hasCreatedPools) {
            Log.e(TAG, "creatThreadPools");
            try {
                timerFlag = false;
                sqlThreadPool = Executors.newSingleThreadExecutor();
                m3u8Threads = (ThreadPoolExecutor) Executors
                        .newCachedThreadPool();
                downNextPools = (ThreadPoolExecutor) Executors
                        .newFixedThreadPool(downNextPoolsNum);
                mvReqPools = (ThreadPoolExecutor) Executors
                        .newFixedThreadPool(mvSegsPoolsNum);
            } catch (Exception e) {
                Log.e(TAG, "creatThreadPools  Exception");
                e.printStackTrace();
            }
            hasCreatedPools = true;
        }

    }

    private class DownloadSegThread implements Runnable {
        private SegInfo downloadInfo;
        private boolean autoDownloadNext;

        private int netNotAvailableTryTime = 0;

        public DownloadSegThread(SegInfo segInfo) {
            this.downloadInfo = segInfo;
        }

        public void run() {
            // 下载开始再进行数据库更新
            downloadInfo.isDownloading = true;
            int returnValue = DOWNLOAD_SEG_FAIL;
            while (returnValue == DOWNLOAD_SEG_FAIL
                    || returnValue == DOWNLOAD_SEG_SOCKETTIMEOUT) {
                returnValue = DownloadUtils.downloadVideo(downloadInfo,
                        segInfoHandler);
                if (Thread.currentThread().isInterrupted()) {
                    Log.i(TAG,
                            "returned from downloadVideo(), thread interrupted");
                    return;
                }
                if (returnValue == DOWNLOAD_SEG_OK) {

                    Message msg = new Message();
                    msg.what = DOWNLOAD_SEG_OK;
                    msg.arg1 = downloadInfo.index;
                    msg.obj = downloadInfo.m3u8Url;
                    if (autoDownloadNext) {
                        msg.arg2 = 1;
                    } else {
                        msg.arg2 = 0;
                    }
                    segInfoHandler.sendMessageDelayed(msg, 10);
                } else if (returnValue == DOWNLOAD_SEG_DOWN_NEXT) {
                    Message msg = new Message();
                    msg.what = DOWNLOAD_SEG_DOWN_NEXT;
                    msg.arg1 = downloadInfo.index;
                    if (autoDownloadNext) {
                        Log.e(TAG, "DOWNLOAD_SEG_DOWN_NEXT autoDownloadNext");
                        msg.arg2 = 1;
                    } else {
                        msg.arg2 = 0;
                    }
                    segInfoHandler.sendMessage(msg);
                } else if (returnValue == DOWNLOAD_SEG_WAITTING) {
                    Message msg = new Message();
                    msg.what = DOWNLOAD_SEG_WAITTING;
                    msg.arg1 = downloadInfo.index;
                    Log.e(TAG, downloadInfo.locationFile + "---->>"
                            + "DOWNLOAD_SEG_WAITTING");
                    segInfoHandler.sendMessage(msg);
                } else if (returnValue == DOWNLOAD_SEG_INTERRUPT) {
                    Message msg = new Message();
                    msg.what = DOWNLOAD_SEG_INTERRUPT;
                    msg.arg1 = downloadInfo.index;
                    Log.e(TAG, downloadInfo.locationFile + "---->>"
                            + "DOWNLOAD_SEG_INTERRUPT");
                    segInfoHandler.sendMessage(msg);
                } else {
                    if (autoDownloadNext) {
                        Log.e(TAG, "DOWNLOAD_SEG_FAIL autoDownloadNext");
                    }
                    if (!hasCreatedPools) {
                        break;
                    }
                    boolean netAvailable = NetworkUtil
                            .isNetworkAvailable(context);
                    if (netAvailable) {
                        Log.i(TAG, "network available, download the seg again");
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }
                        continue;
                    } else {
                        if (netNotAvailableTryTime > 5) {
                            break;
                        } else {
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                break;
                            }
                            netNotAvailableTryTime++;
                            continue;
                        }
                    }

                }
                break;
            }
        }

        public void setAutoDownloadNext(boolean autoDownloadNext) {
            this.autoDownloadNext = autoDownloadNext;
        }

    }

    private static void createLocalM3u8File(CachingWhilePlayingInfo downloadInfo) {
        DownloadUtils.createLocalM3U8File(downloadInfo);
    }

    public static void stop() {

        Log.e(TAG, "downloadManager stop");
    }

    public static void deleteDownloadingFile() {
        download_delete = true;

        Log.e(TAG, "downloadManager delelte");
    }

    // m3u8 downloadProgress
    public static final int DOWNLOAD_PROGRESS = 1;
    public static final int DOWNLOAD_COMPLETE = 2;
    public static final int DOWNLOAD_PAUSE = 3;
    public static final int DOWNLOAD_ERROR = 4;
    public static final int DOWNLOAD_MP4_COMPLETE = 5;
    public static final int DOWNLOAD_MP4_ERROR = 6;
    public static final int DOWNLOAD_MP4_PROGRESS = 7;
    public static final int DOWNLOAD_M3U8FILE_OVER = 8;
    public static final int DOWNLOAD_SEG_OK = 9;
    public static final int DOWNLOAD_SEG_FAIL = 10;
    public static final int DOWNLOAD_SEG_STOP = 11;
    public static final int DOWNLOAD_PARSESEG_ERROR = 12;
    public static final int DOWNLOAD_SEG_SOCKETTIMEOUT = 13;
    public static final int DOWNLOAD_SEG_PAUSE = 14;
    public static final int DOWNLOAD_SEG_DOWN_NEXT = 15;
    public static final int DOWNLOAD_SEG_WAITTING = 16;
    // public static final int DOWNLOAD_SEG_SEEk = 17;
    public static final int DOWNLOAD_SEG_INTERRUPT = 18;
    public static final int CHECK_NETWORK = 19;
    public static final int DOWNLOAD_SEG_BAIDU_403 = 20;
    private static final int PARSE_M3U8FILE_OVER = 21;

    private void downlSegInfoByIndex(int index) {
        SegInfo info = CachingWhilePlayingNanoHTTPD.segsInfo.get(index);
        if (info != null) {
            downSegs(info, false);
        }
    }

    private SegInfo getSegInfoByIndex(int index) {

        SegInfo info = CachingWhilePlayingNanoHTTPD.segsInfo.get(index);
        return info;
    }

    public static boolean download_delete = false;

    public void exit() {
        context = null;
    }

    public static int Editon = 0;
    public Handler segInfoHandler = new Handler() {

        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case PARSE_M3U8FILE_OVER:
                    Log.e(TAG, "PARSE_M3U8FILE_OVER");
                    CachingWhilePlayingInfo downloadInfo = (CachingWhilePlayingInfo) msg.obj;
                    int result = msg.arg1;
                    downloadManager.parseM3u8Over(downloadInfo, result);
                    break;
                case DOWNLOAD_ERROR:
                    Log.e(TAG, "download m3u8 failed");
                    break;
                case DOWNLOAD_COMPLETE:
                    Log.e(TAG, "DOWNLOAD_COMPLETE");
                    CachingWhilePlayingInfo completeDownloadInfo = (CachingWhilePlayingInfo) msg.obj;
                    downloadManager.downloadComplete(completeDownloadInfo);
                    break;
                case DOWNLOAD_SEG_OK:
                    if (!hasCreatedPools) {
                        return;
                    }
                    final int index = msg.arg1;
                    String url = (String) msg.obj;
                    downloadManager.downOk(index, url);
                    BufferedPositionInfo bufPositionInfo = BufferedProgressUtil
                            .getBufferedPosition();
                    Log.i(TAG,
                            "EventBus post bufferedPosition:"
                                    + bufPositionInfo.getCurBufferedPosition()
                                    + ",index:" + bufPositionInfo.getIndex());
                    EventBus.getDefault().post(bufPositionInfo);
                    new Thread() {
                        public void run() {
                            downloadManager.downNextNew(index, 0);
                        }

                        ;
                    }.start();
                    break;
                case DOWNLOAD_SEG_FAIL:
                    if (!hasCreatedPools) {
                        return;
                    }
                    boolean netAvailable = NetworkUtil.isNetworkAvailable(context);
                    if (netAvailable) {
                        int errorIndex = msg.arg1;
                        downloadManager.downFail(errorIndex);
                    } else {
                        Message newMsg = segInfoHandler.obtainMessage();
                        newMsg.what = DOWNLOAD_SEG_FAIL;
                        newMsg.arg1 = msg.arg1;
                        sendMessageDelayed(newMsg, 3000);
                    }
                    break;
                case DOWNLOAD_SEG_DOWN_NEXT:
                    int nextSegIndex = msg.arg1;
                    downloadManager.downNext(nextSegIndex);
                    break;
                case DOWNLOAD_SEG_WAITTING:
                    Log.e(TAG, "DOWNLOAD_SEG_WAITTING");
                    break;
                case CHECK_NETWORK:
                default:
                    break;
            }
        }

        ;
    };

    public int m3u8DownloadTempCount;

    public int segTryTimeLimit = 0;

    public class InfoHandler extends Handler {
        private DownloadManager mDownloadManager;
        private CachingWhilePlayingInfo downloadInfo;

        public InfoHandler(DownloadManager downloadManager,
                           CachingWhilePlayingInfo downloadInfo) {
            mDownloadManager = downloadManager;
            this.downloadInfo = downloadInfo;
        }

        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DOWNLOAD_ERROR:
                    Log.e(TAG, "download m3u8 failed");
                    if (CachingWhilePlayingNanoHTTPD.m3u8Info != null) {
                        CachingWhilePlayingNanoHTTPD.m3u8Info.isDownloaded = false;
                        CachingWhilePlayingNanoHTTPD.m3u8Info.isDownloading = false;
                    }
                    boolean netAvailable = NetworkUtil.isNetworkAvailable(context);
                    if (netAvailable && m3u8DownloadTempCount <= 5) {
                        mDownloadManager.downloadM3u8File(downloadInfo);
                        m3u8DownloadTempCount++;
                    } else {
                        Toast.makeText(context, "当前网络不稳定", Toast.LENGTH_LONG)
                                .show();
                        BufferedPositionInfo bufferedPositionInfo = new BufferedPositionInfo();
                        bufferedPositionInfo.setCurBufferedPosition(-1);
                        EventBus.getDefault().post(bufferedPositionInfo);
                    }
                    break;
                case DOWNLOAD_M3U8FILE_OVER:
                    Log.e(TAG, "DOWNLOAD_M3U8FILE_OVER");
                    new Thread() {
                        @Override
                        public void run() {
                            Log.i(TAG, "parse m3u8 file");
                            int ret = CachingUtils.parseM3u8Seg(downloadInfo);
                            Message msg = new Message();
                            msg.what = PARSE_M3U8FILE_OVER;
                            msg.arg1 = ret;
                            msg.obj = downloadInfo;
                            segInfoHandler.sendMessage(msg);
                        }
                    }.start();
//                    mDownloadManager.parseM3u8Over(downloadInfo);
                    break;
                default:
                    break;
            }
        }
    }

    private void downloadComplete(CachingWhilePlayingInfo completeDownloadInfo) {
        Log.e(TAG, "downloadComplete");
        completeDownloadInfo.state = DownloadInfoState.DOWNLOADED;
        StringBuilder sb = new StringBuilder();
        sb.append(completeDownloadInfo.programId).append("_")
                .append(completeDownloadInfo.subId);
        String customDir = sb.toString();
        String localFileDir = "http://localhost:"
                + CachingLocalServerUtils.SOCKET_PORT + "/"
                + DownloadUtils.sdCardfileDir + File.separator + customDir
                + File.separator + DownloadUtils.localFileName;
        completeDownloadInfo.localUrl = localFileDir;
        completeDownloadInfo.progress = 100;
    }

    private void downNext(int nextSegIndex) {
        Log.e(TAG, "downNext");
        SegInfo nextSeg = getSegInfoByIndex(nextSegIndex + 1);
        if (nextSeg != null && !nextSeg.isDownloaded) {
            downlSegInfoByIndex(nextSegIndex + 1);
        }
    }

    public static int segCount;
    public static int downloadSegCount = 0;
    public static boolean stopDownNext = false;

    public void downNextNew(int nextSegIndex, int edition) {
        int downloadCount = 0; // 用于记录下载的段数，每次最多下载3段，防止过多的下载任务加入线程池排队
        nextSegIndex = CachingWhilePlayingNanoHTTPD.curReqSegIndex;
        while (!Thread.interrupted()
                && (downNextPools.getActiveCount() < downNextPoolsNum && (nextSegIndex < CachingWhilePlayingNanoHTTPD.totalSegCount))) {
            if (downloadCount == 3) {
                downloadCount = 0;
                break;
            }
            if (stopDownNext) {
                Log.e(TAG, "stop downNext");
                return;
            }
            SegInfo segsDownloadInfo = CachingWhilePlayingNanoHTTPD.segsInfo
                    .get(nextSegIndex);

            if (segsDownloadInfo != null && !segsDownloadInfo.isDownloaded
                    && !segsDownloadInfo.isDownloading) {

                Log.e(TAG, "downNextNew-->> nextSegIndex:" + nextSegIndex);
                downSegs(segsDownloadInfo, false);
                downloadCount++;
            }
            nextSegIndex++;
        }

    }

    private void downOk(int index, String m3u8url) {
        Log.e(TAG, "downOk");
        SegInfo tempInfo = CachingWhilePlayingNanoHTTPD.segsInfo.get(index);
        if (tempInfo != null && tempInfo.m3u8Url.equals(m3u8url)) {
            tempInfo.isDownloaded = true;
            tempInfo.isDownloading = false;
            downloadSegCount++;
        }
    }

    private void downFail(int errorIndex) {
        Log.e(TAG, "downFail");
        SegInfo tempErrInfo = CachingWhilePlayingNanoHTTPD.segsInfo
                .get(errorIndex);
        if (tempErrInfo != null) {
            tempErrInfo.isDownloaded = false;
            tempErrInfo.isDownloading = false;
            downSegs(tempErrInfo, false);
        }
    }

    private void parseM3u8Over(CachingWhilePlayingInfo downloadInfo, int ret) {
        Log.e(TAG, "parseM3u8Over");
//        int ret = CachingUtils.parseM3u8Seg(downloadInfo);
        if (ret == 1) {
            CachingWhilePlayingNanoHTTPD.m3u8Info.isDownloading = false;
            CachingWhilePlayingNanoHTTPD.m3u8Info.isDownloaded = false;
            getInstance(context).downloadM3u8File(downloadInfo);
        } else if (ret == 0) {
            createLocalM3u8File(downloadInfo);
            CachingWhilePlayingNanoHTTPD.totalSegCount = downloadInfo.segInfos
                    .size();
            downloadSegCount = 0;
            Log.w(TAG, "downloadInfo.segCount:" + downloadInfo.segInfos.size());
            sqlThreadPool.execute(new UpdateM3U8InfoRunnable(downloadInfo));
        }
    }

    // 下载并解析完m3u8文件之后，开始下载视频段
    class UpdateM3U8InfoRunnable implements Runnable {
        CachingWhilePlayingInfo downloadInfo;

        public UpdateM3U8InfoRunnable(CachingWhilePlayingInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
        }

        @Override
        public void run() {
            // 更新数据库缓存
            float tillDuration = 0;
            try {
                for (int i = 0; i < downloadInfo.segInfos.size(); i++) {
                    SegInfo segInfo = downloadInfo.segInfos.get(i);
                    segInfo.index = i;
                    String s = segInfo.timeLength.substring(0,
                            segInfo.timeLength.length() - 1);
                    segInfo.singleTimeLength = String.valueOf(tillDuration);
                    tillDuration += Float.valueOf(s);
                    segInfo.totalDuration = tillDuration;

                    CachingWhilePlayingNanoHTTPD.segsInfo.put(i, segInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            CachingWhilePlayingNanoHTTPD.m3u8Info.isDownloading = false;
            CachingWhilePlayingNanoHTTPD.m3u8Info.isDownloaded = true;
            if (CachingWhilePlayingNanoHTTPD.segsInfo.size() > 0) {
                stopDownNext = false;
                downNextNew(0, 0);
            }

        }
    }

}
