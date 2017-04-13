package com.sumavision.cachingwhileplaying.util;

import java.util.concurrent.ConcurrentHashMap;

import com.sumavision.cachingwhileplaying.entity.BufferedPositionInfo;
import com.sumavision.cachingwhileplaying.entity.SegInfo;
import com.sumavision.cachingwhileplaying.server.CachingWhilePlayingNanoHTTPD;

public class BufferedProgressUtil {
	public static BufferedPositionInfo getBufferedPosition() {

		float bufferedPosition = 0;
		BufferedPositionInfo bufferedPositionInfo = new BufferedPositionInfo();
		try {
			int i = 0;
			ConcurrentHashMap<Integer, SegInfo> map = CachingWhilePlayingNanoHTTPD.segsInfo;
			for (i = CachingWhilePlayingNanoHTTPD.curReqSegIndex; i < CachingWhilePlayingNanoHTTPD.totalSegCount; i++) {
				SegInfo segInfo = map.get(i);
				if (segInfo != null && !segInfo.isDownloaded) {
					bufferedPosition = Float.valueOf(segInfo.singleTimeLength);
					break;
				}
			}
			if (i == CachingWhilePlayingNanoHTTPD.totalSegCount && i != 0) {
				// bufferedPosition = -1;
				bufferedPosition = (float) Math.ceil(map.get(i - 1).totalDuration);
			}
			bufferedPositionInfo.setIndex(i - 1);
			bufferedPositionInfo.setCurBufferedPosition(bufferedPosition);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bufferedPositionInfo;

	}
}
