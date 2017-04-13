package com.sumavision.offlinelibrary.core.downSegs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import net.chilicat.m3u8.Element;
import net.chilicat.m3u8.ParseException;
import net.chilicat.m3u8.PlayList;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;

import com.sumavision.offlinelibrary.core.DownloadManageHelper;
import com.sumavision.offlinelibrary.core.DownloadManager;
import com.sumavision.offlinelibrary.core.DownloadService;
import com.sumavision.offlinelibrary.core.DownloadUtils;
import com.sumavision.offlinelibrary.dao.AccessDownload;
import com.sumavision.offlinelibrary.dao.AccessSegInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfoState;
import com.sumavision.offlinelibrary.entity.SegInfo;
import com.sumavision.offlinelibrary.entity.VideoFormat;
//import com.sumavision.offlinelibrary.util.BaiduPanBduss;
import com.sumavision.offlinelibrary.util.CommonUtils;

/**
 * 分段下载管理
 * 
 * @author suma-hpb
 * 
 */
public class DownloadSegsManager implements OnSegDownloadListener {
	public static final String TAG = "DownloadSegsManager";
	private Context context;
	private DownloadInfo currentDownloadInfo;
	public static final String extra_progress = "progress";
	public static final String extra_loadinfo = "loadinfo";
	private AtomicInteger nextDownloadSegIndex = new AtomicInteger(0);
	private AtomicInteger sumSegDownloaded = new AtomicInteger(0);
	private AtomicInteger progressDownloaded = new AtomicInteger(0);
	public static ConcurrentHashMap<String, DownloadSegoThread> mRunningSegThreads = new ConcurrentHashMap<String, DownloadSegoThread>();
	private long totalDownloadedLength = 0;// 已下载文件长度

	public DownloadSegsManager(Context paramContext) {
		this.context = paramContext.getApplicationContext();
	}

	public void setParams(DownloadInfo currentDownloadInfo) {
		this.currentDownloadInfo = currentDownloadInfo;
		// fixScheduleExecuted.set(false);
		// fixList.clear();
		// try {
		// Scanner scanner = new Scanner(new InputStreamReader(
		// new FileInputStream(new File(
		// Environment.getExternalStorageDirectory()
		// + File.separator + "bduss.txt"))));
		// String tmp;
		// while (scanner.hasNextLine()) {
		// tmp = scanner.nextLine().trim();
		// String[] list = tmp.split("\\s");
		// if (list.length == 5) {
		// bdussList.add(list[2].trim());
		// }
		// }
		// scanner.close();
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// }

		updateUrlTime = 0;
	}

	/**
	 * 开始下载任务
	 */
	public void executeDownload() {

		try {
			Log.i(TAG, "executeDownload");
			AccessDownload.getInstance(context).querySegsInfo(
					currentDownloadInfo);
			sumSegDownloaded.getAndSet(currentDownloadInfo.sumSegDownloaded);
			if (isDownloadM3U8()) {
				nextDownloadSegIndex
						.getAndSet(currentDownloadInfo.nextDownloadSegIndex);
			} else {
				nextDownloadSegIndex.getAndSet(currentDownloadInfo.segCount);

			}

			getDownloadedProgress();
			if (!isExist(currentDownloadInfo)) {
				submitThreadsToPool();
			} else {
				Log.d(TAG, "already has added to threadsPool");
			}

		} catch (Exception e) {
			Log.d(TAG, "downSegs Error");
			DownloadManager.getInstance(context).onDownloadError();
		}
	}

	/**
	 * 下载任务是否被添加到线程池中
	 * 
	 * @author wht
	 */
	private boolean isExist(DownloadInfo info) {
		boolean isRunning = false;
		StringBuilder nameBuild = new StringBuilder();
		nameBuild.append(info.programId).append("_").append(info.subProgramId)
				.append("_seg");
		if (mRunningSegThreads.size() > 0) {
			for (String key : mRunningSegThreads.keySet()) {
				if (key.startsWith(nameBuild.toString())) {
					isRunning = true;
					break;
				}
			}
		}

		return isRunning;

	}

	/*
	 * 下载线程池添加线程
	 */
	private void submitThreadsToPool() {
		int count = 0;
		if (isDownloadM3U8()) {
			sumSegDownloaded.set(0);
			for (int index = 0; index < currentDownloadInfo.segCount; index++) {
				SegInfo infoJudge = DownloadManageHelper.getInstance(context)
						.getSegInfo(
								currentDownloadInfo.programId + "_"
										+ currentDownloadInfo.subProgramId,
								index);
				infoJudge.downFailCount = 0;
				if (infoJudge.isDownloaded) {
					sumSegDownloaded.incrementAndGet();
				}
			}
			Log.i(TAG, "sumSegDownloaded" + sumSegDownloaded.get());
			progressDownloaded.set(sumSegDownloaded.get());
			nextDownloadSegIndex.set(0);
		} else {
			// 添加未完成的下载任务
			SegInfo infoJudge = new SegInfo();

			int upper = nextDownloadSegIndex.get() < currentDownloadInfo.segCount ? nextDownloadSegIndex
					.get() : currentDownloadInfo.segCount;
			for (int index = 0; index < upper; index++) {
				infoJudge = DownloadManageHelper.getInstance(context)
						.getSegInfo(
								currentDownloadInfo.programId + "_"
										+ currentDownloadInfo.subProgramId,
								index);
				if (infoJudge != null
						&& !infoJudge.isDownloaded
						&& ((infoJudge.breakPoint < infoJudge.dataLength) || currentDownloadInfo.videoFormat == VideoFormat.M3U8_FORMAT)
						&& count < 5) {
					Log.i(TAG, "submit3ThreadsToPool-->>"
							+ currentDownloadInfo.programId
							+ currentDownloadInfo.subProgramId
							+ "--NOT downloaded thread to pool " + index);
					// downloadSegByIndex(index, false);
					downloadSeg(infoJudge, false);
					count += 1;
				}
			}
		}

		// 判断是否下载完成
		if (sumSegDownloaded.get() >= currentDownloadInfo.segCount
				&& isComplete(currentDownloadInfo.segInfos)) {
			downAllSegsComplete();
		}

		// m3u8:保证线程池满
		if (nextDownloadSegIndex.get() < currentDownloadInfo.segCount) {

			if (isDownloadM3U8()) {
				int size = currentDownloadInfo.segCount;
				for (int index = 0; (index < DownloadManager.POOL_THREAD_MAX
						- count)
						&& (index < size) && nextDownloadSegIndex.get() < size; index++) {
					if (currentDownloadInfo.segInfos.get(nextDownloadSegIndex
							.get()).isDownloaded) {
						nextDownloadSegIndex.incrementAndGet();
						index--;
					} else {
						Log.d(TAG,
								"submit3ThreadsToPool-->>"
										+ "new thread to pool "
										+ nextDownloadSegIndex.get());
						downloadSegByIndex(nextDownloadSegIndex.get(), true);
					}
				}
			}
		}

	}

	/*
	 * 计算所有线程已经下载的数据长度
	 */
	private void getDownloadedProgress() {
		int segCount = currentDownloadInfo.segCount;
		totalDownloadedLength = 0;
		for (int index = 0; index < segCount; index++) {
			SegInfo seginfo = DownloadManageHelper.getInstance(context)
					.getSegInfo(
							currentDownloadInfo.programId + "_"
									+ currentDownloadInfo.subProgramId, index);
			totalDownloadedLength += seginfo.breakPoint;
		}

	}

	/**
	 * 下载索引的视频段
	 * 
	 * @param index
	 *            索引
	 * @param flag
	 *            下一段視頻段的计数是否加1
	 */
	private void downloadSegByIndex(int index, boolean flag) {

		SegInfo info = DownloadManageHelper.getInstance(context).getSegInfo(
				currentDownloadInfo.programId + "_"
						+ currentDownloadInfo.subProgramId, index);
		if (info != null) {
			// // 加入失败，加入下一个
			// int tryTime = 1; // 最多尝试segCount次
			// while (!downloadSeg(info, flag)) {
			// index++;
			// if (index == currentDownloadInfo.segCount) {
			// if (tryTime < currentDownloadInfo.segCount) {
			// index = 0;
			// } else {
			// break;
			// }
			// }
			// info = DownloadManageHelper.getInstance(context).getSegInfo(
			// currentDownloadInfo.programId + "_"
			// + currentDownloadInfo.subProgramId, index);
			// tryTime++;
			// }

			downloadSeg(info, flag);

		}
	}

	// private DownloadSegoThread downloadSegoThread;
	private int curBduss = 0;
	private static int updateUrlTime = 0;
	Object sync = new Object();

	/**
	 * 标识是否为下载下一段
	 * 
	 * @param info
	 * @param flag
	 */
	public boolean downloadSeg(SegInfo info, boolean flag) {
		if (info.subId != currentDownloadInfo.subProgramId
				|| info.programId != currentDownloadInfo.programId
				|| currentDownloadInfo.state != DownloadInfoState.DOWNLOADING) {
			return false;
		}
		DownloadSegoThread downloadSegoThread = new DownloadSegoThread(context,
				info, currentDownloadInfo);
		downloadSegoThread.setSegsManagerListener(this);
		boolean submitOk = true;

		// 执行之前检查是否正在下载或者已经下载完成
		if (mRunningSegThreads.get(downloadSegoThread.getName()) != null
				|| info.isDownloaded) {
			Log.i(TAG, "downloading or downloaded seg " + info.index);
			return false;
		}
		try {
			DownloadManager.getInstance(context).executeThread(
					downloadSegoThread);
		} catch (RejectedExecutionException e) {
			Log.e(TAG, e.toString());
			submitOk = false;
		}

		if (submitOk) {
			if (flag) {
				nextDownloadSegIndex.incrementAndGet();
			}
			// mRunningSegThreads.put(downloadSegoThread.getName(),
			// downloadSegoThread);
			// Log.i(TAG, "add seg Thread:" + downloadSegoThread.getName());
		}
		return true;

	}

	private void downAllSegsComplete() {
		Log.e(TAG, currentDownloadInfo.programId
				+ currentDownloadInfo.subProgramId + "downComplete");

		currentDownloadInfo.state = DownloadInfoState.DOWNLOADED;

		StringBuilder sb = new StringBuilder();
		sb.append(currentDownloadInfo.programId).append(
				currentDownloadInfo.subProgramId);
		String customDir = sb.toString();
		StringBuilder localFileDirBuild = new StringBuilder(
				currentDownloadInfo.sdcardDir).append(File.separator);
		localFileDirBuild.append(DownloadUtils.sdCardfileDir)
				.append(File.separator).append(customDir)
				.append(File.separator);
		if (isDownloadM3U8()) {
			// DownloadUtils.writeFailInfoToFile(currentDownloadInfo);
			DownloadUtils.createLocalM3U8File(currentDownloadInfo);
			localFileDirBuild.append("localgame.m3u8");
			context.getSharedPreferences("m3u8_encrypt", Context.MODE_PRIVATE)
					.edit()
					.remove(currentDownloadInfo.programId + "_"
							+ currentDownloadInfo.subProgramId);

		} else {
			localFileDirBuild.append("wht.mp4");

		}
		currentDownloadInfo.fileLocation = localFileDirBuild.toString();
		currentDownloadInfo.progress = 100;
		AccessDownload.getInstance(context).updateDownloadInfo(
				currentDownloadInfo);
		/* 删除segoinfo的数据库 */
		AccessSegInfo.getInstance(context).deleteFromSegTable(
				currentDownloadInfo);
		// 删除节目进度的数据库
		AccessDownload.getInstance(context).deleteFromTableSegs(
				currentDownloadInfo);
		if (context != null) {
			Log.i(TAG, "send broadcast "
					+ DownloadManager.ACTION_DOWNLOAD_COMPLETE);
			// sendBroadcast(DownloadManager.ACTION_DOWNLOAD_COMPLETE, 100,
			// currentDownloadInfo);
			String pre = DownloadService.NOTIFICATION_ID + "_";
			Intent intent = new Intent(pre
					+ DownloadManager.ACTION_DOWNLOAD_COMPLETE);
			intent.putExtra("programName", currentDownloadInfo.programName);
			intent.putExtra(extra_progress, currentDownloadInfo.progress);
			context.sendBroadcast(intent);

			SharedPreferences pushSp = context.getSharedPreferences(
					"pushMessage", Context.MODE_PRIVATE);
			Editor edit = pushSp.edit();
			edit.putBoolean("tip_cahce", true);
			edit.commit();
		}
	}

	@Override
	public void onRemoveThead(String name) {
		Log.e(TAG, "remove>>remove seg Thread:" + name);
		mRunningSegThreads.remove(name);
	}

	@Override
	public void onFailSeg(int index, int threadState, String name,
			long breakPoint) {
		mRunningSegThreads.remove(name);
		Log.e(TAG, "fail>>remove seg Thread:" + name);
		if (threadState == DownloadInfoState.PAUSE
				|| threadState == DownloadInfoState.DELETE
				|| threadState == DownloadInfoState.ERROR) {
			return;
		}
		SegInfo failInfo = DownloadManageHelper.getInstance(context)
				.getSegInfo(name.substring(0, name.lastIndexOf("_")), index);

		if (failInfo != null) {
			Log.e(TAG, "downFail>>redownload>>subId:"
					+ currentDownloadInfo.subProgramId + "--" + "errorIndex--"
					+ index + "->>" + " downFailRetryCount:"
					+ failInfo.downFailCount);
			failInfo.downFailCount += 1;
			failInfo.isDownloaded = false;
			if (!isDownloadM3U8()) {
				failInfo.breakPoint = breakPoint;
			}
			downloadSeg(failInfo, false);

		}
	}

	@Override
	public void onPauseSegs(int index, String name, long breakPoint) {
		Log.e(TAG, "pause>>remove seg Thread:" + name);
		mRunningSegThreads.remove(name);
		updateDownloadThreadBreakpoint(index, name, breakPoint);
		// 停止节目其他下载线程
		String subStr = name.substring(0, name.lastIndexOf("_"));
		pauseOrStopThread(subStr, DownloadInfoState.PAUSE);

	}

	@Override
	public void onDeleteSegs(String name) {
		Log.e(TAG, "delete>>remove seg Thread:" + name);
		mRunningSegThreads.remove(name);
		// 停止节目其他下载线程
		String subStr = name.substring(0, name.lastIndexOf("_"));
		pauseOrStopThread(subStr, DownloadInfoState.DELETE);
	}

	@Override
	public void onErrorSegs(int index, int threadState, String name,
			long breakPoint) {
		Log.e(TAG, "error>>remove seg Thread:" + name);
		mRunningSegThreads.remove(name);
		updateDownloadThreadBreakpoint(index, name, breakPoint);
		// 停止节目其他下载线程
		String subStr = name.substring(0, name.lastIndexOf("_"));
		pauseOrStopThread(subStr, DownloadInfoState.ERROR);

		if (isDownloadM3U8()
				&& (threadState == DownloadInfoState.PAUSE
						|| threadState == DownloadInfoState.DELETE || threadState == DownloadInfoState.ERROR)) {
			return;
		}
		String[] ids = name.split("_");
		DownloadInfo tInfo = new DownloadInfo();
		tInfo.programId = ids[0];
		tInfo.subProgramId = ids[1];
		if (CommonUtils.getWifiAvailable(context)) {
			tInfo.state = DownloadInfoState.ERROR;
		} else {
			tInfo.state = DownloadInfoState.WAITTING;
		}
		AccessDownload.getInstance(context).updateDownloadState(tInfo);
		if (tInfo.programId == currentDownloadInfo.programId
				&& tInfo.subProgramId == currentDownloadInfo.subProgramId) {
			// 更新编辑界面
			currentDownloadInfo.state = DownloadInfoState.ERROR;
			sendBroadcast(DownloadManager.ACTION_DOWNLOAD_ERROR, 0,
					currentDownloadInfo);

		}

	}

	// 出错时不停止当前下载任务，而是将错误信息记录下来
	@Override
	public void onErrorSegs(SegInfo info, String name) {
		Log.e(TAG, "error>>remove seg Thread:" + name);
		mRunningSegThreads.remove(name);

		info.isDownloaded = false;
		info.isDownloading = false;
		AccessSegInfo.getInstance(context).updateSegInfo(info);
		currentDownloadInfo.sumSegDownloaded = sumSegDownloaded
				.incrementAndGet();

		synchronized (sync) {
			downSegNext();
		}

	}

	private void updateDownloadThreadBreakpoint(int index, String name,
			long breakPoint) {
		if (isDownloadM3U8()) {
			return;
		}
		String[] ids = name.split("_");
		SegInfo tInfo = new SegInfo();
		tInfo.programId = ids[0];
		tInfo.subId = ids[1];
		tInfo.index = index;

		tInfo.breakPoint = breakPoint;
		AccessSegInfo.getInstance(context).updateSegBreakPoint(tInfo);
		Log.d(TAG, "updateDownloadThreadBreakpoint-->>" + tInfo.programId
				+ tInfo.subId + "--index(" + index + "):breakPoint="
				+ breakPoint);

	}

	public void pauseOrStopThread(String subStr, int state) {
		Set<String> keys = mRunningSegThreads.keySet();
		for (String key : keys) {
			if (key.contains(subStr)) {
				Log.e(TAG, "stop seg Thread:" + key);
				mRunningSegThreads.get(key).pauseOrStop(state);
			}
		}
	}

	@Override
	public void onDownSegOk(int index, int threadState, String name) {

		Log.i(TAG, "remove seg Thread:" + name);
		Log.d(TAG, "downSegOk-->>" + index + "   sumSegDownloaded:"
				+ sumSegDownloaded.get());
		mRunningSegThreads.remove(name);
		if (threadState == DownloadInfoState.PAUSE
				|| threadState == DownloadInfoState.DELETE
				|| threadState == DownloadInfoState.ERROR) {
			return;
		}
		String[] ids = name.split("_");
		if (currentDownloadInfo == null
				|| !currentDownloadInfo.programId.equals(ids[0])
				|| !currentDownloadInfo.subProgramId.equals(ids[1])) {
			return;
		}
		SegInfo tempInfo = DownloadManageHelper.getInstance(context)
				.getSegInfo(name.substring(0, name.lastIndexOf("_")), index);
		if (tempInfo != null) {
			tempInfo.isDownloaded = true;
			tempInfo.breakPoint = tempInfo.dataLength;
			AccessSegInfo.getInstance(context).updateSegInfo(tempInfo);

			currentDownloadInfo.sumSegDownloaded = sumSegDownloaded
					.incrementAndGet();
			currentDownloadInfo.nextDownloadSegIndex = nextDownloadSegIndex
					.get();
			AccessDownload.getInstance(context).upadateSegsInfo(
					currentDownloadInfo);

			// 下载进度加1
			progressDownloaded.incrementAndGet();

			if (isDownloadM3U8()) {
				// 更新m3u8的下载进度
				int percent = (int) (((progressDownloaded.get()) * 100.0) / currentDownloadInfo.segCount);
				if (percent < 100) {
					currentDownloadInfo.progress = percent;
					AccessDownload.getInstance(context).updateDownloadProgress(
							currentDownloadInfo);
					// sendBroadcast(DownloadManager.ACTION_DOWNLOAD_REFRESH, 0,
					// currentDownloadInfo);
					String pre = DownloadService.NOTIFICATION_ID + "_";
					Intent intent = new Intent(pre
							+ DownloadManager.ACTION_DOWNLOAD_REFRESH);
					intent.putExtra("state", currentDownloadInfo.state);
					intent.putExtra("programName",
							currentDownloadInfo.programName);
					intent.putExtra(extra_progress,
							currentDownloadInfo.progress);
					context.sendBroadcast(intent);
				}

			}

		}

		synchronized (sync) {
			downSegNext();
		}
	}

	private void downSegNext() {
		int nextSegIndex;
		nextSegIndex = nextDownloadSegIndex.get();
		Log.d(TAG, "nextSegIndex:"
				+ nextSegIndex
				+ " ActiveThreadPoolsCount:"
				+ DownloadManager.getInstance(context)
						.getActiveThreadPoolsCount() + " size:"
				+ currentDownloadInfo.segCount + " sumSegDownloaded:"
				+ sumSegDownloaded.get());
		if (nextSegIndex >= currentDownloadInfo.segCount) {
			if (sumSegDownloaded.get() >= currentDownloadInfo.segCount) {
				// if (isDownloadM3U8()) {
				// Log.i(TAG, "write log");
				// DownloadUtils.writeFailInfoToFile(currentDownloadInfo,
				// updateUrlTime + 1);
				// }
				if (!isComplete(currentDownloadInfo.segInfos)
				// && updateUrlTime < BaiduPanBduss.bdussList.size()
				) {
					// updateUrl();
					// doAgain();
					Log.i(TAG, "do it again, the " + (updateUrlTime + 1)
							+ "th time");
					submitThreadsToPool();
				} else {
					downAllSegsComplete();
				}
			}
			return;
		}
		if (isDownloadM3U8()) {
			while (currentDownloadInfo.segInfos.get(nextSegIndex).isDownloaded) {
				nextSegIndex = nextDownloadSegIndex.incrementAndGet();
			}
			downloadSegByIndex(nextDownloadSegIndex.get(), true);
		}

	}

	// 重新下载m3u8，更新
	private void updateUrl() {
		Log.i(TAG, "update url start");
		updateUrlTime++;
		try {
			// int bdussIndex = currentDownloadInfo.parseUrl.indexOf("&BDUSS");
			// if (bdussIndex != -1) {
			// curBduss++;
			// curBduss = curBduss % BaiduPanBduss.bdussList.size();
			// currentDownloadInfo.parseUrl = currentDownloadInfo.parseUrl
			// .substring(0, bdussIndex)
			// + "&BDUSS="
			// + BaiduPanBduss.bdussList.get(curBduss)
			// + currentDownloadInfo.parseUrl
			// .substring(currentDownloadInfo.parseUrl
			// .indexOf("&type"));
			// Log.i(TAG, "parseUrl:" + currentDownloadInfo.parseUrl);
			// }
			URL url;
			url = new URL(currentDownloadInfo.parseUrl);
			byte[] buffer = new byte[1024 * 8];
			int readSize;
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			conn.setRequestProperty("User-Agent",
					"AppleCoreMedia/1.0.0.9B206 (iPad; U; CPU OS 5_1_1 like Mac OS X; zh_cn)");
			System.out.println("response code:" + conn.getResponseCode());
			if (conn.getResponseCode() == 200) {
				InputStream is = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(new File(
						DownloadUtils.getFileDir(currentDownloadInfo),
						"another.m3u8"));
				while ((readSize = is.read(buffer)) >= 0) {
					fos.write(buffer, 0, readSize);
				}
				fos.close();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		PlayList list = null;
		try {
			list = PlayList.parse(new FileInputStream(new File(DownloadUtils
					.getFileDir(currentDownloadInfo), "another.m3u8")));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		if (list != null) {
			List<Element> segList = list.getElements();
			String urlPrefix;
			if (currentDownloadInfo.parseUrl != null
					&& !TextUtils.isEmpty(currentDownloadInfo.parseUrl)) {
				urlPrefix = currentDownloadInfo.parseUrl.substring(0,
						currentDownloadInfo.parseUrl.lastIndexOf("/") + 1);
			} else {
				urlPrefix = currentDownloadInfo.initUrl.substring(0,
						currentDownloadInfo.initUrl.lastIndexOf("/") + 1);
			}
			if (segList != null) {
				int index = 0;
				for (Element e : segList) {
					if (!currentDownloadInfo.segInfos.get(index).isDownloaded) {
						String url = e.getURI().toString();
						if (TextUtils.isEmpty(e.getURI().getScheme())) {
							if (url.startsWith("/")) {
								url = url.substring(1);
							}
							currentDownloadInfo.segInfos.get(index).downloadUrl = urlPrefix
									+ url;
						} else {
							currentDownloadInfo.segInfos.get(index).downloadUrl = url;
						}
					}

					index++;
				}
			}
		}
		Log.i(TAG, "update url over");
	}

	// 判断是否全部下载完成
	private boolean isComplete(CopyOnWriteArrayList<SegInfo> segInfos) {
		int i;
		for (i = 0; i < segInfos.size(); i++) {
			if (!segInfos.get(i).isDownloaded) {
				break;
			}
		}
		if (i == segInfos.size()) {
			return true;
		} else {
			return false;
		}
	}

	public void deleteDownloadingInfo(final DownloadInfo downloadInfo) {
		DownloadManageHelper.getInstance(context).addSqlTask(new Runnable() {

			@Override
			public void run() {
				AccessDownload.getInstance(context).deleteProgramSub(
						downloadInfo, true);

				String dir = DownloadUtils.getFileDir(downloadInfo);
				File file = new File(dir);
				CommonUtils.deleteFile(file);

				AccessDownload.getInstance(context).deleteFromTableSegs(
						downloadInfo);
				AccessSegInfo.getInstance(context).deleteFromSegTable(
						downloadInfo);

			}
		});

	}

	private void sendBroadcast(String action, int progress, DownloadInfo info) {
		String preStr = DownloadService.NOTIFICATION_ID + "_";
		Intent intent = new Intent(preStr + action);
		intent.putExtra(extra_progress, progress);
		intent.putExtra(extra_loadinfo, info);
		context.sendBroadcast(intent);
	}

	private boolean isDownloadM3U8() {

		return currentDownloadInfo.videoFormat == VideoFormat.M3U8_FORMAT;
	}

	@Override
	public void updateDownloadedProgress(long size) {
		synchronized (DownloadSegsManager.class) {
			totalDownloadedLength += size;
			sendMp4Notication();
		}

	}

	/*
	 * 更新mp4的下载进度
	 */
	// @Override
	public void sendMp4Notication() {

		float mp4Progress = totalDownloadedLength;
		float mp4TotalSize = currentDownloadInfo.dataLength;
		if (context != null && currentDownloadInfo != null) {
			if (currentDownloadInfo.state != DownloadInfoState.DOWNLOADING) {
				return;
			}
			Log.d(TAG, "MP4_PROGRESS:" + mp4Progress / 1000 / 1000 + "M /"
					+ mp4TotalSize / 1000000 + "M");
			int percent = 0;
			try {
				percent = (int) ((mp4Progress * 100.0) / mp4TotalSize);
			} catch (Exception e) {
			}

			if (percent > currentDownloadInfo.progress && percent < 100) {
				currentDownloadInfo.progress = percent;
				AccessDownload.getInstance(context).updateDownloadProgress(
						currentDownloadInfo);
				Intent intent = new Intent(DownloadService.NOTIFICATION_ID
						+ "_" + DownloadManager.ACTION_DOWNLOAD_REFRESH);
				intent.putExtra(extra_loadinfo, currentDownloadInfo);
				context.sendBroadcast(intent);
			}

		}
	}

}
