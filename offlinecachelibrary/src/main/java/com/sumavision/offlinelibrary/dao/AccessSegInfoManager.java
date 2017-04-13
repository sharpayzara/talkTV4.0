package com.sumavision.offlinelibrary.dao;

import java.util.concurrent.atomic.AtomicInteger;
import android.database.sqlite.SQLiteDatabase;
public class AccessSegInfoManager {

    private AtomicInteger mOpenCounter = new AtomicInteger();

    private static AccessSegInfoManager instance;
    private static SegInfoDataBaseHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;

    public static synchronized void initializeInstance(SegInfoDataBaseHelper helper) {
        if (instance == null) {
            instance = new AccessSegInfoManager();
            mDatabaseHelper = helper;
        }
    }

    public static synchronized AccessSegInfoManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(AccessSegInfoManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }

        return instance;
    }

    public synchronized SQLiteDatabase openDatabase() {
        if(mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
            mDatabase = mDatabaseHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {
        if(mOpenCounter.decrementAndGet() == 0) {
            // Closing database
            mDatabase.close();

        }
    }
}