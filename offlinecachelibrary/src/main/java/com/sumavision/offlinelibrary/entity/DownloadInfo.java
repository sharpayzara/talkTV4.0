package com.sumavision.offlinelibrary.entity;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

import android.text.TextUtils;

public class DownloadInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	public String programId;
	public String subProgramId;
	public String initUrl = "";
	// 用来标记原始M3U8文件的下载时间如果 暂停后重新下载时间间隔超过2个小时则表示过期要重新下载
	public long initUrlDownloadTime;
	public UrlType urlType = UrlType.TYPE_UNKNOW;
	public long timeLength = 0;
	// 本地文件位置 用来请求本地SERVER 在segInfo都下载完毕会赋值
	public String fileLocation = "";
	public String downloadName = "";
	// 断点续传的断点 本来要用于M3U8文件segInfo的断点续传 有些网站不支持；现在用来下载MP4 FLV大视频断点续传
	public int breakPoint = 0;
	// m3u8文件的段
	public CopyOnWriteArrayList<SegInfo> segInfos;

	public String fileName = "";

	public static enum UrlType {
		TYPE_M3U8(0x0), TYPE_FLV(0x1), TYPE_MP4(0x2), TYPE_UNKNOW(0x3), TYPE_WEB(
				0x4);

		UrlType(int intValue) {
			this.intValue = intValue;
		}

		private int intValue;

		// use when saveInstateState
		public static UrlType mapIntToValue(int intValue) {
			for (UrlType type : UrlType.values()) {
				if (type.intValue == intValue) {
					return type;
				}
			}
			return UrlType.TYPE_UNKNOW;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o instanceof DownloadInfo) {
			DownloadInfo downloadInfo = (DownloadInfo) o;
			String url = downloadInfo.initUrl;
			if (TextUtils.isEmpty(url)) {
				return false;
			}
			if (downloadInfo.programId == this.programId
					&& downloadInfo.subProgramId == this.subProgramId
					&& url.equals(this.initUrl)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + this.initUrl.hashCode();
		hash = hash * 31 + programId.hashCode();
		hash = hash * 31 + subProgramId.hashCode();
		return hash;
	}

	public String programName;
	// 下载总进度
	public int progress = 0;
	public String programPic;
	// 0 waitting 1 downloading
	public int state;
	public int pendingState = 0;
	// 用来标记M3U8视频子段的下载进度
	public int segStep;
	// M3U8文件的targetDuration;
	public int targetDuration;
	// 标识M3U8文件是否已经下载完成
	public boolean isDownloadedInitM3u8 = false;
	// 段数
	public int segCount;
	public int sumSegDownloaded;
	public int speed;
	public int videoFormat;
	public long downloadId;
	public int nextDownloadSegIndex;
	public long dataLength;
	public long downloadedLength;
	public String parseUrl;
	public String sdcardDir;
	public String definition = "superDef";//清晰度  "standar", "hight", "superDef"

	// 是否加密，一般为AES-128加密
	public boolean encrypted;

	// 用于加密的key文件是否已经下载
	public boolean encryptKeyDownloaded;

	// 用于加密的key文件的下载地址
	public String keyUrl;

	// AES-128加密的IV值
	public String iv;

	public boolean isCheck;


}
