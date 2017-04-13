package com.sumavision.cachingwhileplaying.entity;

public class DownloadInfoState {
	public static int WAITTING = 0;
	public static int DOWNLOADING = 1;
	public static int DOWNLOADED = 2;
	public static int PAUSE = 3;
	public static int ERROR = 4;
	public static int PARSE_ERROR = 5;
}
