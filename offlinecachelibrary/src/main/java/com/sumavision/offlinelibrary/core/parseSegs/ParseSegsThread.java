package com.sumavision.offlinelibrary.core.parseSegs;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.sumavision.offlinelibrary.core.DownloadService;
import com.sumavision.offlinelibrary.core.DownloadUtils;
import com.sumavision.offlinelibrary.core.FileParserUtils;
import com.sumavision.offlinelibrary.dao.AccessDownload;
import com.sumavision.offlinelibrary.dao.AccessSegInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.DownloadStatus;
import com.sumavision.offlinelibrary.entity.VideoFormat;

/**
 * m3u8文件下载线程
 * 
 * @author suma-hpb
 * 
 */
public class ParseSegsThread extends Thread {
	private static final String TAG = "DownloadM3U8FileThread";
	private DownloadInfo downloadInfo;
	private DownloadUtils httpDownload;
	Context context;

	ParseSegsCallback callback;

	public ParseSegsThread(ParseSegsCallback callback,
			DownloadInfo downloadInfo, Context context) {
		this.callback = callback;
		this.downloadInfo = downloadInfo;
		this.context = context;
		initParams();
	}

	private void initParams() {
		httpDownload = new DownloadUtils();
		StringBuilder nameBuild = new StringBuilder();
		nameBuild.append(downloadInfo.programId).append("_")
				.append(downloadInfo.subProgramId).append("_m3u8");
		setName(nameBuild.toString());
	}

	private boolean pause;

	public void pause() {
		pause = true;
		httpDownload.setStopM3u8(true);
	}

	public static final int DOWNLOAD_PARSE_SEGS_OVER = 1;
	public static final int DOWNLOAD_PARSE_SEGS_ERROR = 2;
	public static final int DOWNLOAD_PARSE_SEGS_RESUME = 3;
	public static final int DOWNLOAD_PARSE_SEGS_RESTART = 4;
	public static final int DOWNLOAD_PARSE_SEGS_START = 5;
	public static final int DOWNLOAD_PARSE_UPDATE_SEGCOUNT = 6;

	public void run() {
		Log.d(TAG, "DownloadM3U8FileThread-->>START");
		Message msg = new Message();
		msg.what = handleParse();
		handler.sendMessage(msg);
	}

	public Handler handler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(Message msg) {
			callback.onParseSegsFinish(msg.what, getName());
		};
	};

	private int handleParse() {
		int parseState = DOWNLOAD_PARSE_SEGS_ERROR;
		int segState = DownloadStatus.DOWNLOAD_SEG_UPDATA_FAIL;

		// 超过两个小时的暂停要重新解析
		if (downloadInfo.isDownloadedInitM3u8) {
			if (System.currentTimeMillis() - downloadInfo.initUrlDownloadTime < DownloadService.PARSE_INVALIDATE) {
				if (downloadInfo.segCount != 0) {

					parseState = DOWNLOAD_PARSE_SEGS_RESUME;
				} else {
					parseState = DOWNLOAD_PARSE_UPDATE_SEGCOUNT;

				}
			} else {
				parseState = DOWNLOAD_PARSE_SEGS_RESTART;
			}
		} else {
			parseState = DOWNLOAD_PARSE_SEGS_START;
		}

		switch (parseState) {
		case DOWNLOAD_PARSE_SEGS_START:
			segState = firstParse();
			break;
		case DOWNLOAD_PARSE_UPDATE_SEGCOUNT:
			segState = updateSegcount();
			break;
		case DOWNLOAD_PARSE_SEGS_RESTART:
			segState = handleTimeoutCrackUrl();
			break;
		case DOWNLOAD_PARSE_SEGS_RESUME:
			segState = DownloadStatus.DOWNLOAD_SEG_UPDATA_NONE;
			break;
		case DOWNLOAD_PARSE_SEGS_ERROR:
		default:
			segState = DownloadStatus.DOWNLOAD_SEG_UPDATA_FAIL;
			break;
		}
		return segState;
	}

	private int updateSegcount() {
		int ret = -1;
		int segState = DownloadStatus.DOWNLOAD_SEG_UPDATA_FAIL;
		if (!isMp4()) {

			ret = FileParserUtils.parseM3u8Segs(downloadInfo);
		}
		if (ret == 0) {
			// 更新片段总数
			AccessDownload.getInstance(context).updateProgramInfo(downloadInfo);
			segState = DownloadStatus.DOWNLOAD_SEG_UPDATA_ALL;
		}
		return segState;
	}

	/*
	 * 第一次解析分段下载信息
	 */
	private int firstParse() {
		boolean flag = false;
		int segState = DownloadStatus.DOWNLOAD_SEG_UPDATA_FAIL;
		if (!isMp4()) {
			flag = httpDownload.downM3u8File(downloadInfo);
		} else {
			long dataLength = httpDownload.downloadMp4File(downloadInfo);
			if (dataLength != -1) {
				downloadInfo.dataLength = dataLength;
				flag = true;
			}

		}
		if (flag && !pause) {
			segState = parseSegInfos();
			if (segState != DownloadStatus.DOWNLOAD_SEG_UPDATA_FAIL && segState != DownloadStatus.IS_PLAYLIST) {

				downloadInfo.isDownloadedInitM3u8 = true;
				downloadInfo.initUrlDownloadTime = System.currentTimeMillis();
				AccessDownload.getInstance(context).upadateSegsInfo(
						downloadInfo);
			}
		}
		return segState;
	}

	/*
	 * 下载地址失效，更新下载地址
	 */
	private int handleTimeoutCrackUrl() {

		int segState = DownloadStatus.DOWNLOAD_SEG_UPDATA_FAIL;

		if (!isMp4()) {

			// 处理m3u8，更新每一段的url
			segState = DownloadStatus.DOWNLOAD_SEG_UPDATA_NONE;
			if (httpDownload.downM3u8File(downloadInfo)) {
				if (FileParserUtils.updateM3u8SegsUrl(downloadInfo) == 0) {
					Log.i(TAG, "updateM3u8SegsUrl succeed");
					downloadInfo.initUrlDownloadTime = System
							.currentTimeMillis();
					segState = DownloadStatus.DOWNLOAD_SEG_UPDATA_URL;
					AccessDownload.getInstance(context).upadateSegsInfo(
							downloadInfo);
//					AccessSegInfo.getInstance(context).updateSegsUrl(
//							downloadInfo);
				}
			}
		} else {

			// 根据mp4的总长度判断是否需要重新下载还是更新下载地址
			long dataLength = httpDownload.downloadMp4File(downloadInfo);
			if (downloadInfo.dataLength == dataLength) {
				Log.d(TAG,
						"mp4 url unuseful-->>DownloadStatus.DOWNLOAD_SEG_UPDATA_URL");
				segState = DownloadStatus.DOWNLOAD_SEG_UPDATA_URL;
			} else {
				downloadInfo.dataLength = dataLength;
				downloadInfo.initUrlDownloadTime = System.currentTimeMillis();
				AccessDownload.getInstance(context).upadateSegsInfo(
						downloadInfo);
				segState = DownloadStatus.DOWNLOAD_SEG_UPDATA_ALL;
				Log.d(TAG,
						"mp4 url unuseful-->>DownloadStatus.DOWNLOAD_SEG_UPDATA_ALL");
			}

			if (FileParserUtils.parseMp4Segs(downloadInfo) != 0) {
				Log.d(TAG,
						"mp4 url unuseful-->>DownloadStatus.DOWNLOAD_SEG_UPDATA_FAIL");

				segState = DownloadStatus.DOWNLOAD_SEG_UPDATA_FAIL;

			}

		}
		return segState;
	}

	private boolean isMp4() {
		return downloadInfo.videoFormat == VideoFormat.MP4_FORMAT;
	}

	/*
	 * 解析分片信息
	 */
	private int parseSegInfos() {
		int ret = -1;
		int segState = DownloadStatus.DOWNLOAD_SEG_UPDATA_FAIL;
		if (isMp4()) {

			ret = FileParserUtils.parseMp4Segs(downloadInfo);
		} else {
			ret = FileParserUtils.parseM3u8Segs(downloadInfo);

			// 处理加密的情况
			if (downloadInfo.encrypted) {
				SharedPreferences sp = context.getSharedPreferences(
						"m3u8_encrypt", context.MODE_PRIVATE);
				sp.edit()
						.putString(
								downloadInfo.programId + "_"
										+ downloadInfo.subProgramId,
								downloadInfo.keyUrl + "," + downloadInfo.iv
										+ ","
										+ downloadInfo.encryptKeyDownloaded)
						.commit();
				int i = 0;
				while (i < 3) {
					boolean result = DownloadUtils.downloadFile(
							downloadInfo.keyUrl,
							DownloadUtils.getFileDir(downloadInfo));
					if (result) {
						sp.edit()
								.putString(
										downloadInfo.programId + "_"
												+ downloadInfo.subProgramId,
										downloadInfo.keyUrl + ","
												+ downloadInfo.iv + ",true")
								.commit();
						break;
					}
					i++;
				}
			}
		}
		if (ret == 0) {
			// 更新片段总数
			AccessDownload.getInstance(context).upadateSegsInfo(downloadInfo);
			segState = DownloadStatus.DOWNLOAD_SEG_UPDATA_ALL;
		}
		if (ret == 1) {
			downloadInfo.isDownloadedInitM3u8 = false;
			AccessDownload.getInstance(context).upadateSegsInfo(downloadInfo);
			if (isMp4()) {
				segState = DownloadStatus.DOWNLOAD_SEG_UPDATA_FAIL;
			} else {
				segState = DownloadStatus.IS_PLAYLIST;
			}
		}
		return segState;
	}

};
