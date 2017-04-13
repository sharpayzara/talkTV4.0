package com.sumavision.cachingwhileplaying.server;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class CachingLocalServerUtils {
	private static final String TAG = "CachingLocalServerUtils";
	private static CachingLocalServerUtils instance;

	public static CachingLocalServerUtils getInstance(Context context) {
		if (instance == null) {
			instance = new CachingLocalServerUtils(context);
		}
		return instance;
	}

	Context context;

	private CachingLocalServerUtils(Context context) {
		this.context = context;
	}

	public static int SOCKET_PORT;
	private CachingWhilePlayingServer server;
	private LocalServerThread localServerThread;

	private class LocalServerThread extends Thread {
		public void run() {
			try {
				server.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	public void startLocalHttpServerService() {
		CachingWhilePlayingNanoHTTPD.transmitFlag = true;
		CachingWhilePlayingNanoHTTPD.curReqSegIndex = 0;
		CachingWhilePlayingNanoHTTPD.totalSegCount = 0;
		if (server != null) {
			return;
		}
		String path = context.getExternalFilesDir(null) + "/";
		File dir = new File(path);
		server = new CachingWhilePlayingServer("localhost", SOCKET_PORT, dir,
				false, context);
		if (localServerThread != null) {
			return;
		}
		localServerThread = new LocalServerThread();
		localServerThread.start();
		Log.e(TAG, "start local server");
	}

	public void stopLocalHttpServerService() {
		CachingWhilePlayingNanoHTTPD.transmitFlag = false;
		if (server == null || localServerThread == null) {
			return;
		}
		localServerThread = null;
//		server.stop();
//		server = null;
		Log.e(TAG, "stop local server");
	}

	public void setPort(int value) {
		SOCKET_PORT = value + 38347;
	}
}
