package com.sumavision.offlinelibrary.entity;

import java.io.Serializable;

public class SegInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	public int index;// 文件坐标
	public String downloadUrl;// 文件网络地址
	public String timeLength;// 文件时长
	public long dataLength;// 文件长度
	public boolean isDownloaded = false; // 是否下载完毕
	public long breakPoint; // 断点
	public String fileDir;

	public String locationFile;

	// add by wht
	public boolean isDownloading = false;// 是否正在下载

	public String programId;

	public String subId;

	@Deprecated
	public float totalDuration;
	// 单个段的时长 无,
	@Deprecated
	public String singleTimeLength;

	public int downFailCount;
	public int downloadCount;

	public boolean isDiscontinuity;
}