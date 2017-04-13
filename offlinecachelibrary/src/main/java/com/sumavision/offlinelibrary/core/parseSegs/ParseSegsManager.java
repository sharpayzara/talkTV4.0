package com.sumavision.offlinelibrary.core.parseSegs;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.DownloadStatus;

public class ParseSegsManager extends ParseSegsCallback {
	ParseSegsListener listener;
	ParseSegsThread parseSegsThread;
	private ConcurrentHashMap<String, ParseSegsThread> mRunningThreads = new ConcurrentHashMap<String, ParseSegsThread>();
	private static final String TAG = "ParseSegsManager";

	public ParseSegsManager(ParseSegsListener listener) {
		this.listener = listener;
	}

	public void startDownload(DownloadInfo downloadInfo, Context context) {
		parseSegsThread = new ParseSegsThread(this, downloadInfo, context);
		if (!mRunningThreads.containsKey(parseSegsThread.getName())) {
			parseSegsThread.start();
			Log.i(TAG, "add parse Thread:" + parseSegsThread.getName());
			mRunningThreads.put(parseSegsThread.getName(), parseSegsThread);
		} else {
			Log.e(TAG, "is downloading, no need to add");
		}
	}

	public void stopDownload(String programId, String subId) {
		String key = getKey(programId, subId);
		Log.i(TAG, "remove parse Thread:" + key);
		if (!TextUtils.isEmpty(key)) {
			mRunningThreads.get(key).pause();
			mRunningThreads.remove(key);
		}
	}

	private String getKey(String programId, String subId) {
		Set<String> keys = mRunningThreads.keySet();
		for (String key : keys) {
			if (key.startsWith(programId) && key.contains(subId)) {
				return key;
			}
		}
		return "";
	}

	private int failCount = 0;

	@Override
	public void onParseSegsFinish(int state, String name) {
		parseSegsThread = null;
		String[] splits = name.split("_");
		String key = getKey(splits[0], splits[1]);
		Log.i(TAG, "remove parse Thread:" + key);
		if (!TextUtils.isEmpty(key)) {
			mRunningThreads.remove(key);
		}
		if (state != DownloadStatus.DOWNLOAD_SEG_UPDATA_FAIL) {
			failCount = 0;
		}
		switch (state) {
		case DownloadStatus.DOWNLOAD_SEG_UPDATA_ALL:
			listener.onPareseSegsStart(true);
			break;
		case DownloadStatus.DOWNLOAD_SEG_UPDATA_NONE:
			listener.onPareseSegsStart(false);
			break;
		case DownloadStatus.DOWNLOAD_SEG_UPDATA_URL:
			listener.onPareseSegsResume();
			break;

		case DownloadStatus.DOWNLOAD_SEG_UPDATA_FAIL:
			if (failCount < 5) {

				listener.onPareseSegsFail();
				failCount++;
			} else {
				failCount = 0;
				listener.onDownloadError();
			}
			break;
		case DownloadStatus.DOWNLOAD_SEG_UPDATA_ERROR:
			listener.onDownloadError();
			break;
		case DownloadStatus.IS_PLAYLIST:
			listener.onParseSegIsPlaylist();
			break;
		default:
			break;
		}
	}
}
