package com.sumavision.offlinelibrary.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.util.Log;

import com.sumavision.offlinelibrary.core.downSegs.UpdateSegsInfoRunnable;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.SegInfo;

/**
 * downloadManage 管理所有下载节目的SegInfo，每添加一个下载任务都会讲相应的信息加入segsInfoMap
 * 
 * @author suma-hpb
 * 
 */
public class DownloadManageHelper {
	private Context context;
	static DownloadManageHelper databaseManager;
	private boolean initFlag = false;
	private ConcurrentHashMap<String, CopyOnWriteArrayList<SegInfo>> segsInfoMap = null;
	private ExecutorService sqlThreadPool;

	public static synchronized DownloadManageHelper getInstance(Context context) {
		synchronized (DownloadManageHelper.class) {
			if (databaseManager == null) {
				databaseManager = new DownloadManageHelper(context.getApplicationContext());
			}
			return databaseManager;
		}
	}

	private DownloadManageHelper(Context paramContext) {
		context = paramContext;
		init();
	}

	private void init() {
		if (!initFlag) {
			sqlThreadPool = Executors.newSingleThreadExecutor();
			segsInfoMap = new ConcurrentHashMap<String, CopyOnWriteArrayList<SegInfo>>();
			initFlag = true;
		}
	}

	public void addSegInfos(String key, CopyOnWriteArrayList<SegInfo> segInfos) {
		segsInfoMap.put(key, segInfos);
	}

	public SegInfo getSegInfo(String key, int index) {
		if (segsInfoMap.containsKey(key)) {
			return segsInfoMap.get(key).get(index);
		} else {
			Log.e("DownloadManageHelper", "no seg info");
		}
		return new SegInfo();
	}

	public void clearSegInfo(String key) {
		segsInfoMap.remove(key);
	}

	public void updateSegsInfo(DownloadInfo info, boolean isUpdateSeginfos,
			boolean isOnlyUpdateUrl) {
		sqlThreadPool.submit(new UpdateSegsInfoRunnable(context, info,
				isUpdateSeginfos, isOnlyUpdateUrl));
	}

	public void addSqlTask(Runnable thread) {
		sqlThreadPool.submit(thread);
	}
}
