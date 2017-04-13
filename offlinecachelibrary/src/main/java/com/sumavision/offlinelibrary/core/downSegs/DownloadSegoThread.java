package com.sumavision.offlinelibrary.core.downSegs;

import android.content.Context;
import android.util.Log;

import com.sumavision.offlinelibrary.core.DownloadUtils;
import com.sumavision.offlinelibrary.dao.AccessSegInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.DownloadStatus;
import com.sumavision.offlinelibrary.entity.SegInfo;
import com.sumavision.offlinelibrary.entity.VideoFormat;
import com.sumavision.offlinelibrary.util.CommonUtils;

/**
 * 分段下载线程
 * 
 * @author suma-hpb
 * 
 */
public class DownloadSegoThread extends Thread {
	public static final String TAG = "DownloadSegoThread";
	private SegInfo info;
	private DownloadInfo dInfo;
	private Context context;
	private static final int DOWNLOAD_SEG_FAIL_MAX_RETRY = 10;// 下载失败尝试的最大次数
	DownloadUtils httpDownload;

	public DownloadSegoThread(Context context, SegInfo segInfo,
			DownloadInfo dInfo) {
		this.info = segInfo;
		this.context = context;
		this.dInfo = dInfo;
		StringBuilder nameBuild = new StringBuilder();
		httpDownload = new DownloadUtils();
		nameBuild.append(dInfo.programId).append("_")
				.append(dInfo.subProgramId).append("_seg")
				.append(segInfo.index);
		setName(nameBuild.toString());
		this.state = dInfo.state;
		httpDownload.setState(state);
	}

	private int state;

	public void pauseOrStop(int state) {
		httpDownload.setState(state);
		this.state = state;
	}

	@Override
	public void run() {
		int returnValue;
		DownloadSegsManager.mRunningSegThreads.put(getName(), this);
		Log.i(TAG, "add seg Thread:" + getName());
		if (dInfo.videoFormat == VideoFormat.M3U8_FORMAT) {
			returnValue = httpDownload.downloadM3u8Seg(info);
		} else {
			returnValue = httpDownload.downloadMp4Seg(info, listener);
		}

		handleResult(returnValue);

	}

	private OnSegDownloadListener listener;

	public void setSegsManagerListener(OnSegDownloadListener listener) {
		this.listener = listener;
	}

	private void handleResult(int resultCode) {
		switch (resultCode) {
		case DownloadStatus.DOWNLOAD_SEG_OK:
			listener.onDownSegOk(info.index, state, getName());
			break;
		case DownloadStatus.DOWNLOAD_SEG_WAITTING:
			break;
		case DownloadStatus.DOWNLOAD_SEG_PAUSE:
			Log.e(TAG, "pause>>remove seg Thread:" + getName());

			listener.onPauseSegs(info.index, getName(), info.breakPoint);
			break;
		case DownloadStatus.DOWNLOAD_SEG_DELETE:
			Log.e(TAG, "delete>>remove seg Thread:" + getName());
			listener.onDeleteSegs(getName());
			;
			break;
		case DownloadStatus.DOWNLOAD_SEG_ERROR:
			// listener.onErrorSegs(info.index, state, getName(),
			// info.breakPoint);
			listener.onErrorSegs(info, getName());
			break;
		default:
			// 无网络连接时
			if (!CommonUtils.getWifiAvailable(context)) {
				Log.e(TAG, "wifi error>>remove seg Thread:" + getName());
				listener.onPauseSegs(info.index, getName(), info.breakPoint);
				break;
			}
			if (info.downFailCount < DOWNLOAD_SEG_FAIL_MAX_RETRY) {
				// int delayTime = 1;
				// for (int i = 0; i < info.downFailCount; i++) {
				// delayTime *= 2;
				// }
				listener.onFailSeg(info.index, state, getName(),
						info.breakPoint);
			} else {
				// listener.onErrorSegs(info.index, state, getName(),
				// info.breakPoint);
				listener.onErrorSegs(info, getName());
			}
			break;
		}
	}
}