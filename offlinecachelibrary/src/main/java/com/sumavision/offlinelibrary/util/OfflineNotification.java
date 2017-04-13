package com.sumavision.offlinelibrary.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.sumavision.offlinelibrary.entity.DownloadInfo;

public class OfflineNotification {
	Context context;
	String appName;
	String appEnName;

	public OfflineNotification(Context context) {
		this.context = context;
	}

	public void setAppInfo(String appName, String appEnName) {
		this.appName = appName;
		this.appEnName = appEnName;
	}

	public Notification createNotification(DownloadInfo downloadInfo,
			boolean complete) {
		Intent localIntent = new Intent();
		localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		localIntent.setAction(android.content.Intent.ACTION_VIEW);
		String scheme = "cache";
		Uri uri = Uri.fromParts(scheme + appEnName, "cache", null);
		localIntent.setData(uri);
		localIntent.putExtra("com.sumavison.talktv2.enternext", "enterhuancun");

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context);
		builder.setSmallIcon(android.R.drawable.stat_sys_download);
		builder.setProgress(100, downloadInfo.progress, false);
		builder.setContentTitle(!TextUtils.isEmpty(downloadInfo.programName) ? downloadInfo.programName
				: (appName + "视频缓存"));
		builder.setContentText(downloadInfo.progress + "%");
		builder.setContentIntent(PendingIntent.getActivity(context, 0,
				localIntent, 0));
		builder.setAutoCancel(true);
		if (complete) {
			builder.setSmallIcon(android.R.drawable.stat_sys_download_done);
			builder.setDefaults(Notification.DEFAULT_SOUND
					| Notification.DEFAULT_VIBRATE);
		}
		return builder.build();
	}
}
