package com.sumavision.offlinelibrary.core;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.sumavision.offlinelibrary.core.downSegs.DownloadSegsManager;
import com.sumavision.offlinelibrary.core.parseSegs.ParseSegsListener;
import com.sumavision.offlinelibrary.core.parseSegs.ParseSegsManager;
import com.sumavision.offlinelibrary.dao.AccessDownload;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfoState;
import com.sumavision.offlinelibrary.entity.VideoFormat;

public class DownloadManager implements ParseSegsListener {
	public static final String TAG = "DownloadManager";
	public static final String ACTION_DOWNLOAD_REFRESH = "com.sumavision.offlinecache.progressrefresh";
	public static final String ACTION_DOWNLOAD_PAUSE = "com.sumavision.offlinecache.pause";
	public static final String ACTION_DOWNLOAD_COMPLETE = "com.sumavision.offlinecache.complete";
	public static final String ACTION_DOWNLOAD_ERROR = "com.sumavision.offlinecache.error";
	public static final String ACTION_DOWNLOAD_PARSE_ERROR = "com.sumavision.offlinecache.parseError";
	public static final String ACTION_DOWNLOAD_ERROR_RETURY = "com.sumavision.offlinecache.errorRetury";
	private Context context;
	private static DownloadManager downloadManager;
	public DownloadSegsManager downloadSegsManager;
	// public static final String extra_progress = "progress";
	public static final String extra_loadinfo = "loadinfo";

	public static synchronized DownloadManager getInstance(Context context) {
		synchronized (DownloadManager.class) {
			if (downloadManager == null) {
				downloadManager = new DownloadManager(context);
			}
			return downloadManager;
		}

	}

	private DownloadManager(Context paramContext) {
		context = paramContext.getApplicationContext();
		initParams();
		if (downloadSegsManager == null) {
			downloadSegsManager = new DownloadSegsManager(context);
		}
		if (parseSegsManager == null) {
			parseSegsManager = new ParseSegsManager(this);
		}
	}

	public ThreadPoolExecutor downloadThreadPools;// 下载任务线程池
	public static final int POOL_THREAD_MAX = 5;
	private boolean init = false;

	/**
	 * 创建离线下载的线程池
	 */
	public void initParams() {
		if (!init) {
			Log.e(TAG, "initParams");
			downloadThreadPools = (ThreadPoolExecutor) Executors
					.newCachedThreadPool();
			init = true;
		}

	}

	/**
	 * 关闭离线下载的线程池
	 */
	public void shutdownloadThreadPools() {
		try {
			Log.e(TAG, "shutdownThreadPools");
			if (downloadThreadPools != null) {
				downloadThreadPools.shutdownNow();
			}

		} catch (Exception e) {
			Log.e(TAG, "shutdownThreadPools  Exception:" + e.toString());
		}
	}

	public DownloadInfo currentDownloadInfo;
	private ParseSegsManager parseSegsManager;

	public void parseSegs(DownloadInfo downloadInfo) {
		if (downloadInfo != null) {
			currentDownloadInfo = downloadInfo;
			parseSegsManager.startDownload(downloadInfo, context);
		}
	}

	/**
	 * 开始视频段的下载
	 * 
	 * @author wht
	 */
	public void startDownloadSegs() {
		downloadSegsManager.setParams(currentDownloadInfo);
		downloadSegsManager.executeDownload();

	}

	private void initParamsAfterParse() {
		StringBuilder keyBuild = new StringBuilder();
		keyBuild.append(currentDownloadInfo.programId).append("_")
				.append(currentDownloadInfo.subProgramId);
		DownloadManageHelper.getInstance(context).clearSegInfo(
				keyBuild.toString());
	}

	public void setCurrentDownloadInfo(DownloadInfo dInfo) {
		currentDownloadInfo = dInfo;
	}

	/**
	 * 暂停、删除处理
	 */
	public void pauseOrDeleteDownloadIngTask(boolean pause) {
		if (currentDownloadInfo == null) {
			return;
		}
		if (pause) {
			Log.e(TAG, "stop()");
			currentDownloadInfo.state = DownloadInfoState.PAUSE;
		} else {
			Log.e(TAG, "deleteDownloadingFile");
			currentDownloadInfo.state = DownloadInfoState.DELETE;
			downloadSegsManager.deleteDownloadingInfo(currentDownloadInfo);
		}
		StringBuilder keyheader = new StringBuilder();
		keyheader.append(currentDownloadInfo.programId).append("_")
				.append(currentDownloadInfo.subProgramId);
		downloadSegsManager.pauseOrStopThread(keyheader.toString(),
				currentDownloadInfo.state);
		if (currentDownloadInfo.videoFormat == VideoFormat.M3U8_FORMAT) {
			parseSegsManager.stopDownload(
					String.valueOf(currentDownloadInfo.programId),
					String.valueOf(currentDownloadInfo.subProgramId));
		}

	}

	@Override
	public void onPareseSegsStart(boolean isUpdateSeginfos) {
		initParamsAfterParse();
		DownloadManageHelper.getInstance(context).updateSegsInfo(
				currentDownloadInfo, isUpdateSeginfos, false);
	}

	@Override
	public void onPareseSegsResume() {
		initParamsAfterParse();
		DownloadManageHelper.getInstance(context).updateSegsInfo(
				currentDownloadInfo, false, true);
	}

	@Override
	public void onPareseSegsFail() {

		parseSegs(currentDownloadInfo);
	}

	@Override
	public void onDownloadError() {
		if (context != null && currentDownloadInfo != null) {
			Log.d(TAG, "DOWNLOAD_ERROR-->>" + currentDownloadInfo.programId
					+ currentDownloadInfo.subProgramId);
			currentDownloadInfo.state = DownloadInfoState.ERROR;
			AccessDownload.getInstance(context).updateDownloadState(
					currentDownloadInfo);
			sendBroadcast(DownloadManager.ACTION_DOWNLOAD_ERROR,
					currentDownloadInfo);

		}
	}

	// 当m3u8文件为播放列表时，默认取第一个，继续下载
	@Override
	public void onParseSegIsPlaylist() {
//		startDownloadSegs();
		parseSegs(currentDownloadInfo);
	}

	public void executeThread(Runnable r) {
		downloadThreadPools.submit(r);
	}

	public int getActiveThreadPoolsCount() {
		return downloadThreadPools.getActiveCount();
	}

	private void sendBroadcast(String action, DownloadInfo info) {
		String preStr = DownloadService.NOTIFICATION_ID + "_";
		Intent intent = new Intent(preStr + action);
		intent.putExtra(extra_loadinfo, info);
		context.sendBroadcast(intent);
	}

}
