package com.sumavision.offlinelibrary.dao;

import java.util.concurrent.atomic.AtomicInteger;

import android.database.sqlite.SQLiteDatabase;

public class AccessDownloadManager {

	private AtomicInteger mOpenCounter = new AtomicInteger();

	private static AccessDownloadManager instance;
	private static DownloadDataBaseHelper mDatabaseHelper;
	private SQLiteDatabase mDatabase;

	public static  void initializeInstance(
			DownloadDataBaseHelper helper) {
		if (instance == null) {
			instance = new AccessDownloadManager();
			mDatabaseHelper = helper;
		}
	}

	public static synchronized AccessDownloadManager getInstance() {
		if (instance == null) {
			throw new IllegalStateException(
					AccessDownloadManager.class.getSimpleName()
							+ " is not initialized, call initializeInstance(..) method first.");
		}

		return instance;
	}

	public synchronized SQLiteDatabase openDatabase() {
		if (mOpenCounter.incrementAndGet() == 1) {
			// Opening new database
			mDatabase = mDatabaseHelper.getWritableDatabase();
		}
		return mDatabase;
	}
	public synchronized void closeDatabase() {
		if (mOpenCounter.decrementAndGet() == 0) {
			// Closing database
			mDatabase.close();

		}
	}
}