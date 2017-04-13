package com.sumavision.offlinelibrary.core;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.sumavision.offlinelibrary.dao.AccessDownload;

/**
 * 重启任务：解决、监听长时间未进行下载bug
 * 
 * @author suma-hpb
 * 
 */
public class RestartDownloadManage {

	private static RestartDownloadManage instance;

	public static RestartDownloadManage getInstance() {
		synchronized (AccessDownload.class) {
			if (instance == null) {
				instance = new RestartDownloadManage();
			}
			return instance;
		}
	}

	private OnRestartListener listener;

	public void setOnRestartListener(OnRestartListener listener) {
		this.listener = listener;
	}

	private RestartDownloadManage() {

	}

	public void sendCountMessage(int what) {
		countHandler.sendEmptyMessage(what);
	}

	public void postDelayed(long delayMs) {
		queryHandler.postDelayed(runnableForQuery, delayMs);
	}

	public void removeCallback() {
		queryHandler.removeCallbacks(runnableForQuery);
	}

	private static final String TAG = "RestartDownload";
	public static final int waitingTime_restart = 120 * 1000;
	private int countM3u8 = 0;
	private int countMp4 = 0;
	public final static int COUNT_M3U8_FLAG = 8;

	public Handler countHandler = new Handler(Looper.getMainLooper()) {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case COUNT_M3U8_FLAG:
				countM3u8 = countM3u8 + 1;
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	Runnable runnableForQuery = new Runnable() {
		@Override
		public void run() {
			if (queryHandler != null) {
				queryHandler.sendEmptyMessage(8);
				queryHandler.postDelayed(this, waitingTime_restart);
			}

		}
	};

	Handler queryHandler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 8) {
				if (countM3u8 != 0 || countMp4 != 0) {
					countM3u8 = 0;
					countMp4 = 0;
					Log.e(TAG, "DO NOT NEED RESTART");
				} else {
					Log.e(TAG, "restart current downloading task");
					if (listener != null) {
						listener.onRestartDownload();
					}
				}

			}
		}
	};

	public interface OnRestartListener {
		public void onRestartDownload();
	}
}
