package com.sumavision.cachingwhileplaying.download;

import com.sumavision.cachingwhileplaying.entity.SegInfo;
import com.sumavision.cachingwhileplaying.server.CachingWhilePlayingNanoHTTPD;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * 后台进程 控制�?
 * 
 * @author Administrator
 * 
 */
public class CachingWhilePlayingDownloadService extends Service {

	int from;
	int action;
	public static final int ACTION_INVILIDATE = -1;

	public static final int ACTION_DOWNLOAD_M3U8 = 4;

	public static final int ACTION_DELETE_DOWNLOADING = 2;
	// 新的下载任务添加
	public static final int ACTION_DOWNLOAD_NEW_TASK = 3;
	// 暂停
	public static final int ACTION_PAUSE = 5;

	public static final int ACTION_DOWNLOAD_SEGINFO = 6;

	public static final int ACTION_STOP = 7;
	public static final int ACTION_SHUTDOWNTHREADPOOLS = 8;
	public static final int ACTION_CREAT_DOWNLOADPOOLS_SERVICE = 9;
	public static final int ACTION_SEEKSERVICE = 10;

	public static final String ACTION_KEY = "action";
	public static final String LOCAL_URL = "localUrl";

	public static final String TAG = "DownloadService";
	public static final String WHT = "WHT";

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null) {
			return super.onStartCommand(intent, flags, startId);
		}
		Bundle bundle = intent.getBundleExtra("bundle");
		if (bundle != null) {
			from = bundle.getInt("from");
			action = bundle.getInt(ACTION_KEY, ACTION_INVILIDATE);
		}
		if (action == ACTION_DELETE_DOWNLOADING) {
			DownloadManager.download_delete = true;
			DownloadManager.deleteDownloadingFile();
			stopForeground(true);
			// stopSelf();
		} else if (action == ACTION_DOWNLOAD_M3U8) {
			String tempUrl = bundle.getString("localUrl");
			// if (tempUrl != null && !tempUrl.equals(currentUrl)) {
			// currentUrl = tempUrl;
			onDowloadM3u8Request();
			// }
		} else if (action == ACTION_DOWNLOAD_SEGINFO) {
			String tempUrl = bundle.getString("localUrl");
			if (tempUrl != null && !tempUrl.equals(currentSegUrl)) {
				currentSegUrl = tempUrl;
				onDowloadSegInfoRequest();
			}
		} else if (action == ACTION_STOP) {
			stop();
		} else if (action == ACTION_SHUTDOWNTHREADPOOLS) {
			shutdownThreadPools();
			stopSelf();
		} else if (action == ACTION_CREAT_DOWNLOADPOOLS_SERVICE) {
			creatDownLoadPoolsService();
		} else if (action == ACTION_SEEKSERVICE) {
			seekService();
		}

		return START_STICKY;
	}

	private void seekService() {
		Log.e(TAG, "seekService");
		// new Thread() {
		// public void run() {
		DownloadManager downloadManager = DownloadManager
				.getInstance(CachingWhilePlayingDownloadService.this);
		if (DownloadManager.downNextPools.getActiveCount() == 0
				&& DownloadManager.downloadSegCount < CachingWhilePlayingNanoHTTPD.totalSegCount)
			downloadManager.downNextNew(-1, DownloadManager.Editon);
		// };
		// }.start();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private String currentSegUrl;

	private synchronized void onDowloadM3u8Request() {
		Log.e(TAG, "onDownloadm3u8Request");

		// AccessCachingWhilePlaying access = new
		// AccessCachingWhilePlaying(this);
		// CachingWhilePlayingInfo tempInfo = new CachingWhilePlayingInfo();
		// tempInfo.localUrl = currentUrl;
		// CachingWhilePlayingInfo info = access
		// .queryCachingWhilePlayingInfo(tempInfo);
		if (CachingWhilePlayingNanoHTTPD.m3u8Info != null
				&& !CachingWhilePlayingNanoHTTPD.m3u8Info.isDownloading
				&& !CachingWhilePlayingNanoHTTPD.m3u8Info.isDownloaded) {
			DownloadManager downloadManager = DownloadManager.getInstance(this);
			downloadManager
					.downloadM3u8File(CachingWhilePlayingNanoHTTPD.m3u8Info);
		}

	}

	// xiu gai 2014-3-21 去掉了synchronized
	private void onDowloadSegInfoRequest() {
		Log.e(TAG, "onDownloadsegInfoRequest");

		// AccessSegInfo access = new AccessSegInfo(this);
		// SegInfo tempInfo = new SegInfo();
		// tempInfo.locationFile = currentSegUrl;
		// SegInfo info = access.querySegInfo(tempInfo);

		String[] a = currentSegUrl.split("/");
		String[] b = a[a.length - 1].split("\\.");
		if (b.length > 0) {
			SegInfo info = CachingWhilePlayingNanoHTTPD.segsInfo.get(Integer
					.valueOf(b[0]));
			if (info != null && !info.isDownloading) {
				DownloadManager downloadManager = DownloadManager
						.getInstance(this);
				downloadManager.downSegs(info, true);
			}
		}
		// if (DownloadManager.downNextPools.getActiveCount() == 0
		// && DownloadManager.downloadSegCount <
		// CachingWhilePlayingNanoHTTPD.totalSegCount) {
		// downloadManager.downNextNew(-1, DownloadManager.Editon);
		// }

	}

	private void stop() {
		DownloadManager.stop();
	}

	private void shutdownThreadPools() {
		Log.e(TAG, "shutdownThreadPools");

		DownloadManager downloadManager = DownloadManager.getInstance(this);
		if (downloadManager != null) {
			downloadManager.shutDownThreadPools();
		}

	}

	private void creatDownLoadPoolsService() {
		Log.e(TAG, "creatDownLoadPoolsService");

		DownloadManager downloadManager = DownloadManager.getInstance(this);
		downloadManager.creatThreadPools();

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
