package com.sumavision.offlinelibrary.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.sumavision.offlinelibrary.entity.InternalExternalPathInfo;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Environment;
import android.os.Process;
import android.os.storage.StorageManager;
import android.util.Log;

public class CommonUtils {

	private static String TAG = "CommonUtils";

	public static boolean isDownloadingServiceRunning(Context context,
			String serviceName) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> serviceInfo = activityManager
				.getRunningServices(100);
		for (RunningServiceInfo info : serviceInfo) {
			String name = info.service.getClassName();
			if (name.equals(serviceName)) {
				return true;
			}
		}
		return false;

	}

	/**
	 * 读取当前网速
	 */
	public static long getNetSpeed(Context mContext) {
		long readBytes = 0;
		final int uid = Process.myUid();
		readBytes = TrafficStats.getUidRxBytes(uid) == TrafficStats.UNSUPPORTED ? 0
				: TrafficStats.getUidRxBytes(uid);
		// readBytes = TrafficStats.getTotalRxBytes() ==
		// TrafficStats.UNSUPPORTED ? 0
		// : TrafficStats.getTotalRxBytes();
		return readBytes;
	}

	public static int getUid(Context mContext) {
		try {
			PackageManager pm = mContext.getPackageManager();
			ApplicationInfo ai = pm.getApplicationInfo(
					mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
			return ai.uid;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static boolean getWifiAvailable(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
		if (null != netInfo
				&& ConnectivityManager.TYPE_WIFI == netInfo.getType()) {
			return true;
		}
		return false;
	}

	public static void deleteFile(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (File subFile : files) {
					deleteFile(subFile);
				}
				file.delete();
			} else {
				file.delete();
			}
		}
	}

	@SuppressLint("NewApi")
	public static ArrayList<String> getStoragePath(Context context) {
		StorageManager storageManager = (StorageManager) context
				.getSystemService(Context.STORAGE_SERVICE);
		ArrayList<String> path = new ArrayList<String>();
		try {
			Class<?>[] paramClasses = {};
			Method getVolumePathsMethod = StorageManager.class.getMethod(
					"getVolumePaths", paramClasses);
			getVolumePathsMethod.setAccessible(true);
			Object[] params = {};
			Object invoke = getVolumePathsMethod.invoke(storageManager, params);
			String[] temp = (String[]) invoke;
			for (int i = 0; i < ((String[]) invoke).length; i++) {
				// Log.e("msg_request", (temp[i]));
				if (new File(temp[i]).exists()) {
					File tempFile = new File(temp[i] + "/test");
					if (!tempFile.exists()) {
						if (tempFile.mkdirs()) {
							path.add(temp[i]);
							tempFile.deleteOnExit();
						}
					} else {
						path.add(temp[i]);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}

	public static String getCachePath(Context context) {
		String path = "";
		ArrayList<String> dirs = getStoragePath(context);
		if (dirs.size() > 0) {
			path = dirs.get(dirs.size() - 1);
		}

		return path;
	}

	public static InternalExternalPathInfo getInternalExternalPath(
			Context context) {
		ArrayList<String> dirs = getStoragePath(context);
		String emulatedSDcard = null;
		String removableSDcard = null;
		if (dirs.size() == 1) {
//			if (Environment.isExternalStorageRemovable()) {
//				removableSDcard = context.getExternalFilesDir(null).toString();
//			} else {
				emulatedSDcard = context.getExternalFilesDir(null).toString();
//			}
		}
		if (dirs.size() >= 2) {
			String primaryExternalStorage = Environment
					.getExternalStorageDirectory().toString();
			String filePath = context.getExternalFilesDir(null).toString()
					.replace(primaryExternalStorage, "");
			Log.i(TAG, "filePath" + filePath);
			emulatedSDcard = dirs.get(0) + filePath;
			removableSDcard = dirs.get(1) + filePath;
		}

		Log.i(TAG, "removableSDcard:" + removableSDcard);
		Log.i(TAG, "emulatedSDcard:" + emulatedSDcard);
		InternalExternalPathInfo tmp = new InternalExternalPathInfo();
		tmp.emulatedSDcard = emulatedSDcard;
		tmp.removableSDcard = removableSDcard;
		return tmp;
	}
}
