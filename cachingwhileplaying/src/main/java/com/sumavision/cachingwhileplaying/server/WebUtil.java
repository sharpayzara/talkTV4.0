package com.sumavision.cachingwhileplaying.server;

import com.sumavision.cachingwhileplaying.download.DownloadUtils;

import android.text.TextUtils;
import android.util.Log;

public class WebUtil {
	public static boolean isM3u8(String uri) {
		if (!TextUtils.isEmpty(uri)) {
			if (uri.endsWith(DownloadUtils.localFileName)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isSegUrl(String uri) {
		if (!TextUtils.isEmpty(uri)) {
			if (uri.endsWith(".flv")) {
				return true;
			}
		}
		return false;
	}

	public static Integer getIndexFromUrl(String url) {
		int index;
		try {
			int startIndex = url.lastIndexOf("/") + 1;
			int endIndex = url.length() - 4;
			String s = url.substring(startIndex, endIndex);
			index = Integer.valueOf(s);
		} catch (Exception e) {
			index = -1;
		}
		return index;
	}

	public static boolean isNearbyUrl(String currentUrl, String lastUrl) {
		int currentIndex = getIndexFromUrl(currentUrl);
		int lastIndex = getIndexFromUrl(lastUrl);
		Log.e("WebUtil", "isNearbyUrl currentIndex=" + currentIndex
				+ ",lastIndex=" + lastIndex);
		// if (currentIndex - lastIndex <= 3 && currentIndex - lastIndex >= 0) {
		if (currentIndex == lastIndex) {
			return true;
		} else {
			return false;
		}
	}
}
