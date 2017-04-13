package com.sumavision.offlinelibrary.entity;

public class DownloadInfoState {
	public final static int WAITTING = 0;
	public final static int DOWNLOADING = 1;
	public final static int DOWNLOADED = 2;
	public final static int PAUSE = 3;
	public final static int ERROR = 4;
	public final static int PARSE_ERROR = 5;
	public final static int DOWNLOADING_KILLED = 6;
	public final static int DELETE = 7;
	public final static int DOWNLOADING_FOR_NETWORK = 8;

}
