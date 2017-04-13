package com.sumavision.offlinelibrary.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.chilicat.m3u8.Element;
import net.chilicat.m3u8.EncryptionInfo;
import net.chilicat.m3u8.ParseException;
import net.chilicat.m3u8.PlayList;
import android.text.TextUtils;
import android.util.Log;

import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.SegInfo;

public class FileParserUtils {

	private static final String TAG = "FileParserUtils";

	/**
	 * 获取下载片段列表
	 * 
	 * @param loadInfo
	 * @return 0：分段ts流 ,1:m3u8重置地址,2:解析失败
	 */
	public static int parseM3u8Segs(DownloadInfo loadInfo) {
		CopyOnWriteArrayList<SegInfo> infos = new CopyOnWriteArrayList<SegInfo>();
		try {
			PlayList list = PlayList.parse(new FileInputStream(new File(
					DownloadUtils.getFileDir(loadInfo) + "game.m3u8")));

			// 处理加密的情况
			EncryptionInfo encryptionInfo = null;
			if (list.getElements().size() > 0) {
				encryptionInfo = list.getElements().get(0).getEncryptionInfo();
			}
			String key;
			if (encryptionInfo != null) {
				Log.i(TAG, "encryptionInfo:" + encryptionInfo);
				key = "METHOD=" + encryptionInfo.getMethod() + ",URI=\""
						+ encryptionInfo.getURI() + "\"";
				Log.i(TAG, "key:" + key);
				loadInfo.encrypted = true;
				loadInfo.keyUrl = encryptionInfo.getURI().toString();
				loadInfo.iv = encryptionInfo.getIv();
				loadInfo.encryptKeyDownloaded = false;
			}

			loadInfo.targetDuration = list.getTargetDuration();
			List<Element> segList = list.getElements();
			String urlPrefix;
			if (loadInfo.parseUrl != null
					&& !TextUtils.isEmpty(loadInfo.parseUrl)) {
				urlPrefix = loadInfo.parseUrl.substring(0,
						loadInfo.parseUrl.lastIndexOf("/") + 1);
			} else {
				urlPrefix = loadInfo.initUrl.substring(0,
						loadInfo.initUrl.lastIndexOf("/") + 1);
			}
			String fileDir = DownloadUtils.getFileDir(loadInfo);
			if (segList != null) {
				int index = 0;
				for (Element e : segList) {
					// 顶级m3u8文件形式，默认取第一个码率文件地址重新下载
					if (e.isPlayList()) {
//						loadInfo.initUrl = e.getURI().toString();
						loadInfo.parseUrl = e.getURI().toString();
						return 1;
					}
					SegInfo seg = new SegInfo();
					seg.timeLength = String.valueOf(e.getDuration());
					String url = e.getURI().toString();

					if (e.isDiscontinuity()) {
						seg.isDiscontinuity = true;
					}

					if (TextUtils.isEmpty(e.getURI().getScheme())) {
						if (url.startsWith("/")) {
							url = url.substring(1);
						}
						seg.downloadUrl = urlPrefix + url;
					} else {
						seg.downloadUrl = url;
					}

					// 添加数据缓存
					seg.programId = loadInfo.programId;
					seg.subId = loadInfo.subProgramId;
					seg.downFailCount = 0;
					seg.fileDir = fileDir;
					seg.locationFile = fileDir;
					seg.index = index;

					infos.add(seg);
					index++;
				}
			}
			loadInfo.segInfos = infos;
			loadInfo.segCount = infos.size();
		} catch (ParseException e1) {
			e1.printStackTrace();
			return 2;
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return 2;
		}
		return 0;
	}

	/**
	 * 获取下载片段列表
	 * 
	 * @param dInfo
	 * @return 0：分段mp4流 ,1:解析失败
	 */
	public static int parseMp4Segs(DownloadInfo dInfo) {
		long block = 0;

		int segCount = 5;
		String fileDir;
		long fsize = dInfo.dataLength;
		CopyOnWriteArrayList<SegInfo> infos = new CopyOnWriteArrayList<SegInfo>();

		try {

			// 计算每条线程下载的数据长度
			block = ((fsize % segCount) == 0) ? (fsize / segCount) : (fsize
					/ segCount + 1);
			fileDir = DownloadUtils.getFileDir(dInfo);
			for (int index = 0; index < segCount; index++) {
				SegInfo seg = new SegInfo();
				seg.index = index;
				seg.programId = dInfo.programId;
				seg.subId = dInfo.subProgramId;
				seg.downloadUrl = dInfo.parseUrl;
				seg.locationFile = fileDir;
				seg.fileDir = fileDir;
				seg.downFailCount = 0;
				seg.dataLength = block;
				seg.timeLength = "0.0";
				seg.isDownloaded = false;
				seg.isDownloading = false;
				seg.breakPoint = 0;
				infos.add(seg);
			}
			dInfo.segInfos = infos;
			dInfo.segCount = segCount;
			dInfo.dataLength = fsize;
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
	}

	public static int updateM3u8SegsUrl(DownloadInfo loadInfo) {
		try {
			CopyOnWriteArrayList<SegInfo> infos = new CopyOnWriteArrayList<SegInfo>();
			PlayList list = PlayList.parse(new FileInputStream(new File(
					DownloadUtils.getFileDir(loadInfo) + "game.m3u8")));
			List<Element> segList = list.getElements();
			String urlPrefix;
			if (loadInfo.parseUrl != null
					&& !TextUtils.isEmpty(loadInfo.parseUrl)) {
				urlPrefix = loadInfo.parseUrl.substring(0,
						loadInfo.parseUrl.lastIndexOf("/") + 1);
			} else {
				urlPrefix = loadInfo.initUrl.substring(0,
						loadInfo.initUrl.lastIndexOf("/") + 1);
			}
			if (segList != null) {
				int index = 0;
				for (Element e : segList) {
					// 顶级m3u8文件形式，默认取第一个码率文件地址重新下载
					if (e.isPlayList()) {
						loadInfo.initUrl = e.getURI().toString();
						return 1;
					}
					SegInfo seg = new SegInfo();
					String url = e.getURI().toString();
					if (TextUtils.isEmpty(e.getURI().getScheme())) {
						if (url.startsWith("/")) {
							url = url.substring(1);
						}
						seg.downloadUrl = urlPrefix + url;
					} else {
						seg.downloadUrl = url;
					}
					seg.index = index;
					seg.programId = loadInfo.programId;
					seg.subId = loadInfo.subProgramId;
					infos.add(seg);
					index++;
				}
				loadInfo.segInfos = infos;
			}

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return 2;
		} catch (ParseException e1) {
			e1.printStackTrace();
			return 2;
		}
		return 0;
	}
}
