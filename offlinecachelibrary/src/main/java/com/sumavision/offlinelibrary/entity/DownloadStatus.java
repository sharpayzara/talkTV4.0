package com.sumavision.offlinelibrary.entity;

public interface DownloadStatus {
	public final static long STATUS_M3U8_PAUSE = 1 << 0;
	public final static long STATUS_M3U8_RUNNING = 1 << 1;
	public final static long STATUS_M3U8_DELETE = 1 << 2;
	public final static long STATUS_M3U8_WAITING = 1 << 3;
	public final static long STATUS_MP4_PAUSE = 1 << 4;
	public final static long STATUS_MP4_RUNNING = 1 << 5;
	public final static long STATUS_MP4_DELETE = 1 << 6;
	public final static long STATUS_MP4_WAITING = 1 << 7;

	public static final int DOWNLOAD_PROGRESS = 1;
	public static final int DOWNLOAD_COMPLETE = 2;
	public static final int DOWNLOAD_PAUSE = 3;
	public static final int DOWNLOAD_ERROR = 4;
	public static final int DOWNLOAD_MP4_COMPLETE = 5;
	public static final int DOWNLOAD_MP4_ERROR = 6;
	public static final int DOWNLOAD_MP4_PAUSE = 7;
	public static final int DOWNLOAD_MP4_WAITING = 8;
	public static final int DOWNLOAD_MP4_PROGRESS = 9;
	public static final int DOWNLOAD_SEG_OK = 11;
	public static final int DOWNLOAD_SEG_FAIL = 12;
	public static final int DOWNLOAD_SEG_STOP = 13;
	public static final int DOWNLOAD_SEG_SOCKETTIMEOUT = 15;
	public static final int DOWNLOAD_SEG_PAUSE = 16;
	public static final int DOWNLOAD_SEG_DOWN_NEXT = 17;
	public static final int DOWNLOAD_SEG_WAITTING = 18;
	public static final int DOWNLOAD_SEG_SEEk = 19;
	public static final int DOWNLOAD_DELETE = 20;
	public static final int DOWNLOAD_SEG_ERROR = 21;
	public static final int DOWNLOAD_SEG_DELETE = 22;

	public static final int DOWNLOAD_SEG_UPDATA_URL = 26;
	public static final int DOWNLOAD_SEG_UPDATA_ALL = 27;
	public static final int DOWNLOAD_SEG_UPDATA_ERROR = 28;
	public static final int DOWNLOAD_SEG_UPDATA_FAIL = 29;
	public static final int DOWNLOAD_SEG_UPDATA_NONE = 30;
	public static final int IS_PLAYLIST = 31;
}
