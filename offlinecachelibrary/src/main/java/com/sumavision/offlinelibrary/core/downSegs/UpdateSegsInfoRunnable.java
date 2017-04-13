package com.sumavision.offlinelibrary.core.downSegs;

import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import android.util.Log;

import com.sumavision.offlinelibrary.core.DownloadManageHelper;
import com.sumavision.offlinelibrary.core.DownloadManager;
import com.sumavision.offlinelibrary.dao.AccessSegInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.SegInfo;

/**
 * 下载并解析完m3u8文件之后，update数据库
 * 
 * @author suma-hpb
 * 
 */
public class UpdateSegsInfoRunnable implements Runnable {
	DownloadInfo downloadInfo;
	private Context context;

	private boolean isUpdateSeginfos;
	private boolean isOnlyUpdateUrl;

	public UpdateSegsInfoRunnable(Context context, DownloadInfo downloadInfo,
			boolean isUpdateSeginfos, boolean isOnlyUpdateUrl) {
		this.downloadInfo = downloadInfo;
		this.context = context;
		this.isUpdateSeginfos = isUpdateSeginfos;
		this.isOnlyUpdateUrl = isOnlyUpdateUrl;
	}

	@Override
	public void run() {
		try {
			if (getSegInfos() == -1) {
				return;
			}
			StringBuilder keyBuild = new StringBuilder();
			keyBuild.append(downloadInfo.programId).append("_")
					.append(downloadInfo.subProgramId);
			DownloadManageHelper.getInstance(context).addSegInfos(
					keyBuild.toString(), downloadInfo.segInfos);
		} catch (Exception e) {
			Log.e("UpdateSegsInfoRunnable", e.toString());
			DownloadManager.getInstance(context).onDownloadError();
			return;
		}
		DownloadManager.getInstance(context).startDownloadSegs();
	}

	/*
	 * 获取片段的信息
	 */
	private int getSegInfos() {
		// 保存下载片段的信息
		if (downloadInfo.segInfos != null && isUpdateSeginfos) {
			if (AccessSegInfo.getInstance(context).isExistedFromSegTable(
					downloadInfo)) {
				AccessSegInfo.getInstance(context).deleteFromSegTable(
						downloadInfo);
			}
			AccessSegInfo.getInstance(context).bulkSave(downloadInfo);

		} else {
			if (isOnlyUpdateUrl) {
				AccessSegInfo.getInstance(context).updateSegsUrl(downloadInfo);
			}
			CopyOnWriteArrayList<SegInfo> seginfos = AccessSegInfo.getInstance(
					context).querySegInfos(downloadInfo);
			if (seginfos != null && seginfos.size() > 0) {
				downloadInfo.segInfos = seginfos;
				downloadInfo.segCount = seginfos.size();
			} else {
				Log.e("UpdateSegsInfoRunnable", "ERROR");
				DownloadManager.getInstance(context).onDownloadError();
				return -1;
			}
		}
		return 0;
	}
}