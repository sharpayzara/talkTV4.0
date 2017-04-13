package com.sumavision.offlinelibrary.core.downSegs;

import com.sumavision.offlinelibrary.entity.SegInfo;

/**
 * 视频段下载任务：暂停，失败，删除，当前段下载完成
 * 
 * @author wht
 */
public interface OnSegDownloadListener {

	public void onRemoveThead(String name);

	public void onFailSeg(int index, int threadState, String name,
			long breakPoint);

	public void onDeleteSegs(String name);

	public void onErrorSegs(int index, int threadState, String name,
			long breakPoint);

	public void onPauseSegs(int index, String name, long breakPoint);

	public void onDownSegOk(int index, int threadState, String name);

	// mp4多线程下载 累计已下载大小
	public void updateDownloadedProgress(long size);

	public void onErrorSegs(SegInfo info, String name);

}