package com.sumavision.cachingwhileplaying.server;

import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.sumavision.cachingwhileplaying.download.CachingWhilePlayingDownloadService;
import com.sumavision.cachingwhileplaying.entity.SegInfo;

public class WebHandler implements IWebHandler {
	public static final int TYPE_M3U8 = 1;
	public static final int TYPE_SEG = 2;
	private Context context;
	private String url;
	private ConcurrentHashMap<Integer, SegInfo> segsDownloadInfo;

	private static final String TAG = "WebHandler";

	public WebHandler(Context context, String url, int type,
			ConcurrentHashMap<Integer, SegInfo> segsDownloadInfo) {
		this.context = context;
		this.url = url;
		this.segsDownloadInfo = segsDownloadInfo;
	}

	@Override
	public boolean getM3u8Exist() {
		if (CachingWhilePlayingNanoHTTPD.m3u8Info != null)
			return CachingWhilePlayingNanoHTTPD.m3u8Info.isDownloaded;
		else
			return false;
	}

	@Override
	public boolean getSegExists(String uri) {
		try {
			int index = parseIndex(uri);
			return CachingWhilePlayingNanoHTTPD.segsInfo.get(index).isDownloaded;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private int parseIndex(String uri) {
		// TODO Auto-generated method stub
		String[] a = uri.split("/");
		String[] b = a[a.length - 1].split("\\.");
		return Integer.valueOf(b[0]);
	}

	private String addPrefix(String s) {
		return "http://localhost:" + CachingLocalServerUtils.SOCKET_PORT + s;
	}

	public void downloadM3u8() {
		Intent intent = new Intent(context,
				CachingWhilePlayingDownloadService.class);
		Bundle bundle = new Bundle();
		bundle.putString(CachingWhilePlayingDownloadService.LOCAL_URL,
				addPrefix(url));
		bundle.putInt(CachingWhilePlayingDownloadService.ACTION_KEY,
				CachingWhilePlayingDownloadService.ACTION_DOWNLOAD_M3U8);
		intent.putExtra("bundle", bundle);
		context.startService(intent);
	}

	public void downloadSegInfo() {
		Intent intent = new Intent(context,
				CachingWhilePlayingDownloadService.class);
		Bundle bundle = new Bundle();
		bundle.putString(CachingWhilePlayingDownloadService.LOCAL_URL,
				addPrefix(url));
		bundle.putInt(CachingWhilePlayingDownloadService.ACTION_KEY,
				CachingWhilePlayingDownloadService.ACTION_DOWNLOAD_SEGINFO);
		intent.putExtra("bundle", bundle);
		context.startService(intent);
	}

	public void resetUrl(String url) {
		this.url = url;
	}

	public void startDownload() {
		Intent intent = new Intent(context,
				CachingWhilePlayingDownloadService.class);
		Bundle bundle = new Bundle();
		bundle.putInt(CachingWhilePlayingDownloadService.ACTION_KEY,
				CachingWhilePlayingDownloadService.ACTION_SEEKSERVICE);
		intent.putExtra("bundle", bundle);
		context.startService(intent);
	}

}
