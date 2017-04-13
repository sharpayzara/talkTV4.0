package com.sumavision.offlinelibrary.core;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.sumavision.offlinelibrary.core.downSegs.OnSegDownloadListener;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfo.UrlType;
import com.sumavision.offlinelibrary.entity.DownloadInfoState;
import com.sumavision.offlinelibrary.entity.DownloadStatus;
import com.sumavision.offlinelibrary.entity.SegInfo;

;

/**
 * 离线缓存文件处理及http通信工具类
 * 
 * @author suma-hpb
 * 
 */
public class DownloadUtils {
	public static final String sdCardfileDir = "/TVFan/cache";
	private static final String TAG = "DownloadUtils";
	public static String RootDir = "";

	public static String getFileDir(DownloadInfo downloadInfo) {
		String dir = "";
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(downloadInfo.programId).append(downloadInfo.subProgramId);
			String customDir = sb.toString();
			dir = downloadInfo.sdcardDir + File.separator + sdCardfileDir
					+ File.separator + customDir + File.separator;
			File file = new File(dir);
			if (!file.exists()) {
				file.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "getFileDir  ERROR:" + e.toString());
		}

		return dir;

	}

	private boolean stopM3u8;

	public void setStopM3u8(boolean stopM3u8) {
		this.stopM3u8 = stopM3u8;
	}

	/*
	 * 获取mp4视频总长度，子线程中运行
	 */
	public long downloadMp4File(DownloadInfo dInfo) {
		long fsize = -1;
		HttpURLConnection conn = null;
		try {

			conn = (HttpURLConnection) new URL(dInfo.parseUrl).openConnection();
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			conn.setRequestProperty("User-Agent",
					"AppleCoreMedia/1.0.0.9B206 (iPad; U; CPU OS 5_1_1 like Mac OS X; zh_cn)");
			fsize = conn.getContentLength();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}

		}
		return fsize;
	}

	public boolean downM3u8File(DownloadInfo downloadInfo) {
		boolean returnValue = false;
		String url = downloadInfo.parseUrl;
		String fileName = downloadInfo.fileName;
		File file = new File(getFileDir(downloadInfo), fileName);
		createFile(file);
		URL uri;
		HttpURLConnection conn = null;
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			uri = new URL(url);
			conn = (HttpURLConnection) uri.openConnection();
			byte[] buf = new byte[1024];
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(10000);
			conn.setRequestProperty("User-Agent",
					"AppleCoreMedia/1.0.0.9B206 (iPad; U; CPU OS 5_1_1 like Mac OS X; zh_cn)");
			is = conn.getInputStream();
			fos = new FileOutputStream(file);
			int status = conn.getResponseCode();
			if (status == 200) {
				while (!stopM3u8) {
					int numRead = is.read(buf);
					if (numRead <= 0) {
						break;
					} else {
						fos.write(buf, 0, numRead);
					}
				}

			}
			returnValue = true;
		} catch (Exception e) {
			returnValue = false;
			e.printStackTrace();
			Log.e(TAG, "downinitData error:" + e.toString());
		} finally {

			try {
				if (fos != null) {
					fos.close();
				}
				if (is != null) {
					is.close();
				}
				if (conn != null) {

					conn.disconnect();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return returnValue;
	}

	private static void createFile(File file) {
		try {
			if (!file.exists()) {
				file.createNewFile();
			} else {
				file.delete();
				file.createNewFile();
			}
		} catch (Exception e) {
			Log.e(TAG, "createFile:" + e.toString());
		}

	}

	private int state;

	public void setState(int state) {
		this.state = state;
	}

	/**
	 * 下载段
	 * 
	 * @param segInfo
	 * @param handler
	 * @return
	 */
	public int downloadM3u8Seg(SegInfo segInfo) {

		String httpUrl = segInfo.downloadUrl;

		int returnValue = DownloadStatus.DOWNLOAD_SEG_FAIL;
		File file = new File(segInfo.fileDir, segInfo.index + ".flv");
		if (httpUrl == null) {
			if (file.exists()) {
				file.delete();
			}
			// segInfo.failInfo += "httpUrl == null" + " ";
			return returnValue;
		}
		createFile(file);

		boolean pause = false;
		HttpURLConnection conn = null;
		BufferedInputStream bis = null;
		InputStream is = null;
		long totalSize = -1;
		long downSize = 0;
		RandomAccessFile accessFile = null;
		try {
			URL url = new URL(httpUrl);
			conn = (HttpURLConnection) url.openConnection();
			byte[] buf = new byte[1024 * 8];
			conn.setReadTimeout(15000);
			conn.setConnectTimeout(15000);
			conn.setInstanceFollowRedirects(true);
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("User-Agent",
					"AppleCoreMedia/1.0.0.9B206 (iPad; U; CPU OS 5_1_1 like Mac OS X; zh_cn)");
			totalSize = conn.getContentLength();
			is = conn.getInputStream();
			bis = new BufferedInputStream(is);
			accessFile = new RandomAccessFile(file, "rwd");
			long len = file.length();
			accessFile.seek(len);
			int status = conn.getResponseCode();
			if (status == 200) {
				int numRead = -1;
				while ((numRead = bis.read(buf)) != -1) {
					if (state == DownloadInfoState.PAUSE) {

						returnValue = DownloadStatus.DOWNLOAD_SEG_PAUSE;
						pause = true;
						Log.e(TAG, "pause->>index= " + segInfo.index);
						break;

					} else if (state == DownloadInfoState.DELETE) {
						returnValue = DownloadStatus.DOWNLOAD_SEG_DELETE;
						pause = true;
						Log.e(TAG, "delete->>index= " + segInfo.index);
						break;
					} else if (state == DownloadInfoState.ERROR) {
						pause = true;
						returnValue = DownloadStatus.DOWNLOAD_SEG_ERROR;
						Log.e(TAG, "error-->>index= " + segInfo.index);
						break;
					}
					downSize += numRead;
					accessFile.write(buf, 0, numRead);
				}

			} else {
				// segInfo.failInfo += status + " ";
				return DownloadStatus.DOWNLOAD_SEG_ERROR;
			}
		} catch (SocketTimeoutException e) {
			Log.e(TAG, e.toString());
			// segInfo.failInfo += e.toString() + " ";
			return DownloadStatus.DOWNLOAD_SEG_FAIL;
		} catch (SocketException e) {
			Log.e(TAG, e.toString());
			// segInfo.failInfo += e.toString() + " ";
			return DownloadStatus.DOWNLOAD_SEG_FAIL;
		} catch (EOFException e) {
			Log.e(TAG, e.toString());
			// segInfo.failInfo += e.toString() + " ";
			return DownloadStatus.DOWNLOAD_SEG_FAIL;
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			// segInfo.failInfo += e.toString() + " ";
			if (e.toString().endsWith("403.html")) {
				return DownloadStatus.DOWNLOAD_SEG_ERROR;
			} else {
				return DownloadStatus.DOWNLOAD_SEG_FAIL;
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			// segInfo.failInfo += e.toString() + " ";
			return DownloadStatus.DOWNLOAD_SEG_ERROR;
		} finally {
			try {
				if (accessFile != null) {
					accessFile.close();
				}
				if (is != null) {
					is.close();
				}
				if (bis != null) {
					bis.close();
				}
				conn.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (pause) {
			return returnValue;
		}
		if (totalSize == -1 || totalSize == downSize) {
			returnValue = DownloadStatus.DOWNLOAD_SEG_OK;
		} else {
			// segInfo.failInfo += "totalSize != downSize" + " ";
			returnValue = DownloadStatus.DOWNLOAD_SEG_FAIL;
		}
		return returnValue;
	}

	public int downloadMp4Seg(SegInfo seg, OnSegDownloadListener listener) {

		boolean pause = false;
		long dataLength = seg.dataLength;
		long breakPoint = seg.breakPoint;
		int returnValue = DownloadStatus.DOWNLOAD_SEG_FAIL;
		BufferedInputStream bis = null;
		InputStream is = null;
		RandomAccessFile accessFile = null;
		HttpURLConnection conn = null;

		long progress = 0;
		if (breakPoint < dataLength) {// 未下载完成
			URL downUrl = null;
			int index = seg.index;

			try {

				downUrl = new URL(seg.downloadUrl);
				conn = (HttpURLConnection) downUrl.openConnection();
				conn.setConnectTimeout(10000);
				conn.setReadTimeout(15000);
				conn.setRequestMethod("GET");

				byte[] buffer = new byte[1024 * 8];

				long startPos = dataLength * index + seg.breakPoint;// 开始位置
				long endPos = dataLength * (index + 1) - 1;// 结束位置\

				conn.setRequestProperty("Range", "bytes=" + startPos + "-"
						+ endPos);// 设置获取实体数据的范围
				conn.setRequestProperty("Connection", "Keep-Alive");

				is = conn.getInputStream();
				bis = new BufferedInputStream(is);

				File file = new File(seg.fileDir, "wht.mp4");
				accessFile = new RandomAccessFile(file, "rwd");
				accessFile.seek(startPos);
				int status = conn.getResponseCode();
				long temp = seg.breakPoint;
				int offset = 0;
				if (status == 206) {
					while ((offset = bis.read(buffer)) != -1 && !pause) {
						if (state == DownloadInfoState.PAUSE) {

							Log.e(TAG, "pause->>index= " + index);
							pause = true;
							seg.breakPoint = breakPoint;
							returnValue = DownloadStatus.DOWNLOAD_SEG_PAUSE;
							break;

						} else if (state == DownloadInfoState.DELETE) {
							Log.e(TAG, "delete->>index= " + index);
							pause = true;
							returnValue = DownloadStatus.DOWNLOAD_SEG_DELETE;
							break;
						} else if (state == DownloadInfoState.ERROR) {
							Log.e(TAG, "error-->>index= " + index);
							pause = true;
							seg.breakPoint = breakPoint;
							returnValue = DownloadStatus.DOWNLOAD_SEG_ERROR;
							break;
						}
						accessFile.write(buffer, 0, offset);
						breakPoint += offset;
						progress = breakPoint - temp;
						if (progress > 100 * 1024) {

							listener.updateDownloadedProgress(progress);
							temp = breakPoint;
						}

					}
				} else {
					pause = true;
					seg.breakPoint = breakPoint;
					returnValue = DownloadStatus.DOWNLOAD_SEG_ERROR;
				}

			} catch (SocketTimeoutException e) {
				Log.e(TAG, e.toString());
				seg.breakPoint = breakPoint;
				return DownloadStatus.DOWNLOAD_SEG_FAIL;
			} catch (SocketException e) {
				Log.e(TAG, e.toString());
				seg.breakPoint = breakPoint;
				return DownloadStatus.DOWNLOAD_SEG_FAIL;
			} catch (EOFException e) {
				Log.e(TAG, e.toString());
				seg.breakPoint = breakPoint;
				return DownloadStatus.DOWNLOAD_SEG_FAIL;
			} catch (IOException e) {
				Log.e(TAG, e.toString());
				e.printStackTrace();
				seg.breakPoint = breakPoint;
				return DownloadStatus.DOWNLOAD_SEG_FAIL;
			} catch (Exception e) {
				pause = true;
				seg.breakPoint = breakPoint;
				e.printStackTrace();
				returnValue = DownloadStatus.DOWNLOAD_SEG_ERROR;
			} finally {
				try {
					if (accessFile != null) {
						accessFile.close();
					}
					if (is != null) {
						is.close();
					}
					// if (bis != null) {
					// bis.close();
					// }
					conn.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (!pause) {
					returnValue = DownloadStatus.DOWNLOAD_SEG_OK;
				}
			}

		}
		return returnValue;
	}

	/* 无需破解的源，获取视频格式 */
	public static UrlType getUrlType(String url) {
		if (TextUtils.isEmpty(url)) {
			return UrlType.TYPE_UNKNOW;
		}
		if (url.contains("pcs.baidu")) {
			return UrlType.TYPE_M3U8;
		}
		// if (url.contains("qq") || url.contains("QQ") || url.contains("letv"))
		// {
		// return UrlType.TYPE_MP4;
		// }
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

		if (url.endsWith("html")) {
			return UrlType.TYPE_UNKNOW;
		}
		if (url.contains("moretv.com.cn")){
			return UrlType.TYPE_MP4;
		}
		return UrlType.TYPE_UNKNOW;
	}

	/**
	 * @descrition 创建本地M3U8文件
	 * @param downloadInfo
	 */
	public static void createLocalM3U8File(DownloadInfo downloadInfo) {
		File file = new File(getFileDir(downloadInfo), "localgame.m3u8");
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
			if (downloadInfo.encrypted) {
				writer.write("#EXT-X-KEY:METHOD=AES-128,URI=\"video.key\",IV="
						+ downloadInfo.iv);
				writer.write("\n");
			}
			StringBuilder sb = new StringBuilder();
			sb.append(downloadInfo.programId).append(downloadInfo.subProgramId);
			String customDir = sb.toString();
			String firDir = downloadInfo.sdcardDir + File.separator
					+ sdCardfileDir + File.separator + customDir
					+ File.separator;
			if (downloadInfo.segInfos != null) {
				int size = downloadInfo.segInfos.size();
				for (int i = 0; i < size; i++) {
					SegInfo info = downloadInfo.segInfos.get(i);
					// 只针对下载完成的段生成最终的m3u8文件
					if (info.isDownloaded) {

						if (info.isDiscontinuity) {
							writer.write("#EXT-X-DISCONTINUITY");
							writer.write("\n");
						}

						writer.write("#EXTINF:" + info.timeLength);
						writer.write("\n");
						info.locationFile = firDir + i + ".flv";
						writer.write(info.locationFile);
						writer.write("\n");
					}
				}
			}
			writer.write("#EXT-X-ENDLIST");
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean downloadFile(String url, String destDir) {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url)
					.openConnection();
			InputStream inputStream = conn.getInputStream();
			FileOutputStream fileOutputStream = new FileOutputStream(destDir
					+ File.separator + "video.key");
			byte[] buffer = new byte[4096];
			while (inputStream.read(buffer) != -1) {
				fileOutputStream.write(buffer);
			}
			return true;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param downloadInfo
	 * @param curLogSeqNo
	 *            当前是第几次记录log日志
	 */

	public static void writeFailInfoToFile(DownloadInfo downloadInfo,
			int curLogSeqNo) {
		try {
			String fileDir = downloadInfo.sdcardDir + File.separator
					+ sdCardfileDir + File.separator + "fail.log";
			File file = new File(fileDir);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file, true), "UTF-8"));
			bw.write("**********************curNO. = " + curLogSeqNo
					+ "************************\n");
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd, HH:mm:ss");
			bw.write("time: " + format.format(new Date()) + "\n");
			bw.write("programId:" + downloadInfo.programId);
			bw.write("\n");
			bw.write("subId:" + downloadInfo.subProgramId);
			bw.write("\n");
			int size = downloadInfo.segInfos.size();
			bw.write("total segs count:" + size + "\n");
			int failCount = 0;
			for (int i = 0; i < size; i++) {
				SegInfo tmp = downloadInfo.segInfos.get(i);
				if (!tmp.isDownloaded) {
					failCount++;
					// bw.write("fail segIndex:" + i + ";\tfail reason:"
					// + tmp.failInfo + "\n");
				}
			}
			bw.write("failed segs number:" + failCount + "\n");
			bw.write("fail rate:" + (double) failCount / size * 100 + "%\n");
			bw.flush();
			bw.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
