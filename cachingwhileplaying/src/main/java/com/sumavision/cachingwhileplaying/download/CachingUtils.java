package com.sumavision.cachingwhileplaying.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import net.chilicat.m3u8.Element;
import net.chilicat.m3u8.ParseException;
import net.chilicat.m3u8.PlayList;
import android.text.TextUtils;

import com.sumavision.cachingwhileplaying.entity.CachingWhilePlayingInfo;
import com.sumavision.cachingwhileplaying.entity.SegInfo;

public class CachingUtils {
	/**
	 * 获取下载片段列表
	 * 
	 * @param loadInfo
	 * @return 0：分段ts流 ,1:m3u8重置地址,2:解析失败
	 */
	public static int parseM3u8Seg(CachingWhilePlayingInfo loadInfo) {
		ArrayList<SegInfo> infos = new ArrayList<SegInfo>();
		try {
			PlayList list = PlayList.parse(new FileInputStream(
					new File(DownloadUtils.getFileDir(loadInfo)
							+ DownloadUtils.m3u8File)));
			loadInfo.targetDuration = list.getTargetDuration();
			List<Element> segList = list.getElements();
			String urlPrefix = loadInfo.m3u8.substring(0,
					loadInfo.m3u8.lastIndexOf("/") + 1);
			if (segList != null) {
				for (Element e : segList) {
					// 顶级m3u8文件形式，默认取第一个码率文件地址重新下载
					if (e.isPlayList()) {
						loadInfo.m3u8 = e.getURI().toString();
						return 1;
					}
					SegInfo seg = new SegInfo();
					seg.m3u8Url = loadInfo.m3u8;
					seg.timeLength = String.valueOf(e.getDuration());

					if (e.isDiscontinuity()) {
						seg.isDiscontinuity = true;
					}
					
					String url = e.getURI().toString();
					if (TextUtils.isEmpty(e.getURI().getScheme())) {
						if (url.startsWith("/")) {
							url = url.substring(1);
						}
						seg.downloadUrl = urlPrefix + url;
					} else {
						seg.downloadUrl = url;
					}
					
					infos.add(seg);
				}
			}
			loadInfo.segInfos = infos;
		} catch (ParseException e1) {
			e1.printStackTrace();
			return 2;
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return 2;
		}
		return 0;
	}

}
