package com.sumavision.cachingwhileplaying.download;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.sumavision.cachingwhileplaying.entity.CachingWhilePlayingInfo;
import com.sumavision.cachingwhileplaying.entity.SegInfo;
import com.sumavision.cachingwhileplaying.server.CachingLocalServerUtils;

public class DownloadUtils {
	public static String RootDir;
	public static final String sdCardfileDir = "TVFan/cachingwhileplaying";
	public static final String localFileName = "cachingwhileplaying.m3u8";
	public static final String m3u8File = "remotefile.m3u8";
	private static final String TAG = "DownloadUtils";

	public static String getFileDir(CachingWhilePlayingInfo downloadInfo) {
		StringBuilder sb = new StringBuilder();
		sb.append(downloadInfo.programId).append("_")
				.append(downloadInfo.subId);
		String customDir = sb.toString();
		String dir = RootDir + File.separator + sdCardfileDir + File.separator
				+ customDir + File.separator;
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}
		return dir;
	}

	public static String getFileDir(SegInfo segInfo) {
		String locationFile = segInfo.locationFile;
		String fileDir = null;
		if (locationFile != null) {
			fileDir = locationFile.replace("http://localhost:"
					+ CachingLocalServerUtils.SOCKET_PORT, RootDir);
		}

		return fileDir;
	}

	public static boolean isInitFileExists(CachingWhilePlayingInfo downloadInfo) {
		File file = new File(getFileDir(downloadInfo), localFileName);
		if (file.exists()) {
			return true;
		}
		return false;
	}

	public static UrlType getUrlType(String url) {
		if (TextUtils.isEmpty(url)) {
			return UrlType.TYPE_UNKNOW;
		}
		if (url.contains("pcs.baidu")) {
			return UrlType.TYPE_M3U8;
		}
		String pattern1 = "^.*\\.m3u8$";
		if (url.matches(pattern1)) {
			return UrlType.TYPE_M3U8;
		}
		String pattern2 = "^.*\\.flv$";
		if (url.matches(pattern2)) {
			return UrlType.TYPE_FLV;
		}
		String pattern3 = "^.*\\.mp4$";
		if (url.matches(pattern3)) {
			return UrlType.TYPE_MP4;
		}

		return UrlType.TYPE_M3U8;
	}

	public static boolean downInitData(CachingWhilePlayingInfo downloadInfo) {
		boolean returnValue = false;
		String url = downloadInfo.m3u8;
//		Log.e(TAG, url);
		String fileName = m3u8File;
		File file = new File(getFileDir(downloadInfo), fileName);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return returnValue;
			}
		} else {
			try {
				file.delete();
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return returnValue;
			}
		}
		URL uri;
		try {
			uri = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
			byte[] buf = new byte[1024];
			conn.setReadTimeout(5000);
			conn.setConnectTimeout(5000);
			conn.setRequestProperty("User-Agent",
					"AppleCoreMedia/1.0.0.9B206 (iPad; U; CPU OS 5_1_1 like Mac OS X; zh_cn)");
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			FileOutputStream fos = new FileOutputStream(file);
			int status = conn.getResponseCode();
			if (status == 200) {
				while (true) {
					if (Thread.interrupted()) {
						return false;
					}
					int numRead = bis.read(buf);
					if (numRead <= 0) {
						break;
					} else {
						fos.write(buf, 0, numRead);
					}
				}

			}
			conn.disconnect();
			fos.close();
			is.close();
			returnValue = true;
		} catch (Exception e) {
			returnValue = false;
			Log.e(TAG, "downinitData error:" + e.toString());
		}
		Log.i(TAG, "downInitData: download m3u8 over");
		return returnValue;
	}

	/**
	 * ���ض�
	 * 
	 * @param segInfo
	 * @param handler
	 * @return
	 */
	public static int downloadVideo(SegInfo segInfo, Handler handler) {
//		Log.e(TAG, "start downloadVideo  " + segInfo.locationFile);
		HttpURLConnection conn;
		BufferedInputStream bis;
		InputStream is;
		long downSize = 0;
		String httpUrl = segInfo.downloadUrl;

		String fileDir = getFileDir(segInfo);
		long totalSize = -1;
		int returnValue = DownloadManager.DOWNLOAD_SEG_FAIL;
		File file = new File(fileDir);
		if (httpUrl == null) {
			// add by wht
			if (file.exists()) {
				file.delete();
			}
			return returnValue;
		}
		// Log.e(TAG, fileDir + "," + httpUrl);
		boolean exception = false, pause = false;

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return returnValue;
			}
		} else {

			// Log.e(TAG, "downloading");
			// returnValue = DownloadManager.DOWNLOAD_SEG_WAITTING;
			file.delete();
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return returnValue;
			}
			// return returnValue;

		}
		try {

			URL url = new URL(httpUrl);
			conn = (HttpURLConnection) url.openConnection();
			byte[] buf = new byte[1024 * 8];
			conn.setReadTimeout(20000);
			conn.setConnectTimeout(20000);
			conn.setInstanceFollowRedirects(true);
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("User-Agent",
					"AppleCoreMedia/1.0.0.9B206 (iPad; U; CPU OS 5_1_1 like Mac OS X; zh_cn)");
			// Log.e(TAG, "download segInfoIndex=" + segInfo.index
			// + "  ,breakPoint: " + segInfo.breakPoint);
			is = conn.getInputStream();
			bis = new BufferedInputStream(is);
			RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
			long len = file.length();
			accessFile.seek(len);
			int status = conn.getResponseCode();
			totalSize = conn.getContentLength();
			// Log.e(TAG, "file.length=" + len + "  ,breakPoint: "
			// + segInfo.breakPoint + ",remoteSize=" + totalSize);
			int readSize = 0;
			if (status == 200) {
				long lastReadTime = System.nanoTime();
				while (true) {
					if (Thread.currentThread().isInterrupted()) {
						Log.i(TAG, "thread interrupted");
						// Thread.currentThread().interrupt();
						break;
					}
					int numRead = bis.read(buf);

					if (numRead <= 0) {
						break;
					} else {
						downSize = downSize + numRead;
						readSize += numRead;
						accessFile.write(buf, 0, numRead);
					}
				}
				long curTime = System.nanoTime();
				long timeCost = (curTime - lastReadTime);
//				Log.e(TAG, segInfo.index + "   read time:" + timeCost
//						+ "  downSize:" + downSize + " rate:" + downSize
//						* 1000000 / timeCost + "KB/s");
			}
			conn.disconnect();
			accessFile.close();
			is.close();
			bis.close();
		} catch (SocketTimeoutException e) {
			exception = true;
			returnValue = DownloadManager.DOWNLOAD_SEG_SOCKETTIMEOUT;
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			segInfo.breakPoint = downSize;
			exception = true;
			if (file.exists()) {
				file.delete();
			}
			returnValue = DownloadManager.DOWNLOAD_SEG_FAIL;
//			if (e.toString().contains("403.html")) {
//				returnValue = DownloadManager.DOWNLOAD_SEG_BAIDU_403;
//			}
		}
		segInfo.breakPoint = downSize;
		if (!exception && !pause) {
			if (totalSize == -1) {
				returnValue = DownloadManager.DOWNLOAD_SEG_OK;
			} else {
				if (totalSize == downSize) {
					returnValue = DownloadManager.DOWNLOAD_SEG_OK;
				} else {
					if (file.exists()) {
						file.delete();
					}
					returnValue = DownloadManager.DOWNLOAD_SEG_FAIL;

				}
			}
		}
//		Log.e(TAG, segInfo.locationFile + "   " + returnValue);
		// if (Thread.interrupted()&&returnValue ==
		// DownloadManager.DOWNLOAD_SEG_OK) {
		// return DownloadManager.DOWNLOAD_SEG_SEEk;
		// }
		return returnValue;
	}

	/**
	 * @description ����������Ϣ������
	 * 
	 * @param downloadInfo
	 */
	public static void updateDownloadInfoFile(
			CachingWhilePlayingInfo downloadInfo) {
		try {
			JSONObject jsonObject = new JSONObject();
			if (downloadInfo.segInfos != null) {
				jsonObject.put("segCount", downloadInfo.segInfos.size());
				jsonObject.put("segStep", downloadInfo.segStep);
				jsonObject
						.put("breakpoint",
								downloadInfo.segInfos.get(downloadInfo.segStep).breakPoint);
			}
			jsonObject.put("programId", downloadInfo.programId);
			jsonObject.put("initUrl", downloadInfo.initUrl);
			jsonObject.put("isDownloadedInitM3u8",
					downloadInfo.ism3u8Downloaded);
			jsonObject.put("targetDuration", downloadInfo.targetDuration);
			jsonObject.put("initUrlDownloadTime",
					downloadInfo.initUrlDownloadTime);
			String info = jsonObject.toString();
//			Log.e(TAG, "write ," + info);
			File file = new File(getFileDir(downloadInfo), "info.txt");
			if (file.exists()) {
				file.delete();
			}
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8"));
			writer.write(info);
			writer.write("\n");
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ȡ����������Ϣ
	 * 
	 * @param downloadInfo
	 */
	public static void readLocalInfoData(CachingWhilePlayingInfo downloadInfo) {
		try {
			File file = new File(getFileDir(downloadInfo), "info.txt");
			if (!file.exists()) {
				return;
			}
			BufferedReader reader = new BufferedReader(new FileReader(file));
			StringBuilder builder = new StringBuilder();
			String temp;
			while ((temp = reader.readLine()) != null) {
				builder.append(temp);
			}
			temp = builder.toString();
//			Log.e(TAG, "read , " + temp);
			JSONObject jsonObject = new JSONObject(temp);
			int segCount = jsonObject.optInt("segCount");
			long breakpoint = jsonObject.optLong("breakpoint");
			int segStep = jsonObject.optInt("segStep");
			if (segStep != 0) {
				downloadInfo.segStep = segStep;
			}
			if (breakpoint != 0) {
				downloadInfo.breakPoint = (int) breakpoint;
				if (downloadInfo.segInfos != null
						&& downloadInfo.segInfos.size() > segStep) {
					if (downloadInfo.segInfos.get(segStep) != null) {
						downloadInfo.segInfos.get(segStep).breakPoint = breakpoint;
					}
				}
			}
			downloadInfo.ism3u8Downloaded = jsonObject
					.optBoolean("isDownloadedInitM3u8") ? 1 : 0;
			downloadInfo.initUrlDownloadTime = jsonObject
					.optLong("initUrlDownloadTime");
			String initUrl = jsonObject.optString("initUrl");
			downloadInfo.segCount = segCount;
			if (!TextUtils.isEmpty(initUrl)) {
				downloadInfo.initUrl = initUrl;
			}
			downloadInfo.targetDuration = jsonObject.optInt("targetDuration");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @descrition ��������M3U8�ļ�
	 * @param downloadInfo
	 */

	public static void createLocalM3U8File(CachingWhilePlayingInfo downloadInfo) {
		File file = new File(getFileDir(downloadInfo),
				DownloadUtils.localFileName);
		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8"));
			writer.write("#EXTM3U");
			writer.write("\n");
			long targetDuration = downloadInfo.targetDuration;
			writer.write("#EXT-X-TARGETDURATION:" + targetDuration);
			writer.write("\n");
			writer.write("#EXT-X-VERSION:3");
			writer.write("\n");
			ArrayList<SegInfo> infos = downloadInfo.segInfos;
			// String firDir = getFileDir(downloadInfo);
			StringBuilder sb = new StringBuilder();
			sb.append(downloadInfo.programId).append("_")
					.append(downloadInfo.subId);
			String customDir = sb.toString();
			String firDir = "http://localhost:"
					+ CachingLocalServerUtils.SOCKET_PORT + "/" + sdCardfileDir
					+ File.separator + customDir + File.separator;

//			if (downloadInfo.m3u8.contains("baidu")) {
//				writer.write("#EXTINF:2");
//				writer.write("\n");
//				writer.write(firDir + "-1.flv");
//				writer.write("\n");
//			}

			if (infos != null) {
				for (int i = 0; i < infos.size(); i++) {
					SegInfo info = infos.get(i);
					if (info.isDiscontinuity) {
						writer.write("#EXT-X-DISCONTINUITY");
						writer.write("\n");
					}
					writer.write("#EXTINF:" + info.timeLength);
					writer.write("\n");
					info.locationFile = firDir + i + ".flv";
					// 第一段让播放器直接去网络请求
					// if (i == 0) {
					// info.locationFile = infos.get(0).downloadUrl;
					// }
					writer.write(info.locationFile);
					writer.write("\n");

				}
			}
			writer.write("#EXT-X-ENDLIST");
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
