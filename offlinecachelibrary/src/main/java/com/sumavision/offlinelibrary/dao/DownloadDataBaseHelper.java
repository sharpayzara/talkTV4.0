/**
 * 
 */
package com.sumavision.offlinelibrary.dao;

import com.sumavision.offlinelibrary.core.DownloadManager;
import com.sumavision.offlinelibrary.dao.SegsConstants;
import com.sumavision.offlinelibrary.entity.DownloadInfoState;
import com.sumavision.offlinelibrary.entity.VideoFormat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

public class DownloadDataBaseHelper extends SQLiteOpenHelper {
	public static final String TAG = "DownloadDataBaseHelper";
	private static String DB_NAME = "talktv_download.db";
	private static String DB_TABLE = "download";
	private static String pendingState = "pendingState";
	private static String parseUrl = "parseUrl";
	public static String sdcardDir = "sdcardDir";
	public static String definition = "definition"; // 清晰度
	private static final int DB_VERSION = 9;

	public DownloadDataBaseHelper(Context context) {
		this(context, true);
	}

	@SuppressLint("NewApi")
	public DownloadDataBaseHelper(Context context, boolean enableWAL) {
		super(context, DB_NAME, null, DB_VERSION);
		if (enableWAL && Build.VERSION.SDK_INT >= 11) {
			getWritableDatabase().enableWriteAheadLogging();
		}
	}

	@Override
	public synchronized void close() {
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		onUpgrade(db, 0, DB_VERSION);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(DownloadManager.TAG, "onUpgrade-->>" + "oldVersion:" + oldVersion
				+ "newVersion" + newVersion);
		if (oldVersion < 2) {
			// no logic to upgrade from these older version, just recreate
			// the DB
			Log.i(DownloadManager.TAG,
					"Upgrading downloads database from version " + oldVersion
							+ " to version " + newVersion
							+ ", which will destroy all old data");
			oldVersion = 1;
		} else if (oldVersion > newVersion) {
			// user must have downgraded software; we have no way to know
			// how to downgrade the
			// DB, so just recreate it
			Log.i(TAG, "Downgrading downloads database from version "
					+ oldVersion + " (current version is " + newVersion
					+ "), destroying all old data");
			oldVersion = 1;
		}
		for (int version = oldVersion + 1; version <= newVersion; version++) {
			upgradeTo(db, version);
		}

	}

	@SuppressLint("NewApi")
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, 0, DB_VERSION);
	}

	/**
	 * Upgrade database from (version - 1) to version.
	 */
	private void upgradeTo(SQLiteDatabase db, int version) {
		switch (version) {
		case 1:
		case 2:
		case 3:
			createDownloadTable(db);
			break;
		case 4:
		case 5:
			break;
		case 6:
			createSegsTable(db);
			break;
		case 7:
			addColumn(db, DB_TABLE, pendingState, "varchar(100)");
			break;
		case 8:
			deleteDownloadingMp4TaskFrom_download(db);
			deleteDownloadingMp4TaskFrom_programSegsManager(db);
			RecreateSegsTable(db);
			addColumn(db, SegsConstants.PROGRAM_SEGSMANAGER_TABLE,
					SegsConstants.SEGS_DATA_LENGTH, "varchar(100) default '0' ");
			addColumn(db, SegsConstants.PROGRAM_SEGSMANAGER_TABLE,
					SegsConstants.SEGS_DOWNLOADED_LENGTH,
					"varchar(100) default '0' ");
			addColumn(db, SegsConstants.PROGRAM_SEGSMANAGER_TABLE,
					SegsConstants.SEGS_SEGS_COUNT, "varchar(100) default '0' ");
			addColumn(db, DB_TABLE, parseUrl, "varchar(100) default '' ");
			addColumn(db, DB_TABLE, sdcardDir, "varchar(100) default '' ");
			updateExistedDownloadTaskSdcard(db);
			break;
		case 9:
			addColumn(db, DB_TABLE, definition, "varchar(100) default '' ");
			break;
		default:
			throw new IllegalStateException("Don't know how to upgrade to "
					+ version);
		}
	}

	/*
	 * ������ǰ�װʱ��ɾ���������ص�mp4��Ƶ
	 */
	public void deleteDownloadingMp4TaskFrom_download(SQLiteDatabase db) {
		try {

			String deleteSql = "delete from download where state <>? and videoFormat = ? ";
			String[] bindArgs = new String[] {
					String.valueOf(DownloadInfoState.DOWNLOADED),
					String.valueOf(VideoFormat.MP4_FORMAT) };
			db.execSQL(deleteSql, bindArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * ������ǰ�װʱ��ɾ���������ص�mp4��Ƶ
	 */
	public void deleteDownloadingMp4TaskFrom_programSegsManager(
			SQLiteDatabase db) {
		try {
			String deleteSql = "delete from "
					+ SegsConstants.PROGRAM_SEGSMANAGER_TABLE + " where "
					+ SegsConstants.SEGS_VIDEO_FORMAT + " = ? ";
			String[] bindArgs = new String[] { String
					.valueOf(VideoFormat.MP4_FORMAT) };
			db.execSQL(deleteSql, bindArgs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates the table that'll hold the download information.
	 */
	private void createDownloadTable(SQLiteDatabase db) {
		try {
			db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
			db.execSQL("CREATE TABLE IF NOT EXISTS " + DB_TABLE + " ("
					+ "_id integer primary key autoincrement,"
					+ "programid varchar(100)," + "subid varchar(100),"
					+ "programname varchar(100)," + "url varchar(100),"
					+ "programpic varchar(100)," + "state varchar(100),"
					+ "progress varchar(100)," + "timelength varchar(100),"
					+ "localurl varchar(100)," + "downloadId varchar(100),"
					+ "videoFormat varchar(100)," + "modifytime timestamp)");
		} catch (SQLException ex) {
			Log.e(TAG, "couldn't create table in downloads database");
			throw ex;
		}
	}

	private void updateExistedDownloadTaskSdcard(SQLiteDatabase db) {
		String sql = "";
		sql = "update download set sdcardDir=? where state <>? ";

		Object[] bindArgs = { "/mnt/sdcard", DownloadInfoState.DOWNLOADED };
		try {
			db.execSQL(sql, bindArgs);
			Log.d(TAG, "updateExistedDownloadTask-->>succeed");
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "updateExistedDownloadTask-->>fail");
		}
	}

	/**
	 * Creates the table that'll hold the download information.
	 */
	private void createSegsTable(SQLiteDatabase db) {
		try {
			Log.d(TAG, "SegInfoDataBaseHelper-->>createSegInfoTable");
			db.execSQL("DROP TABLE IF EXISTS "
					+ SegsConstants.PROGRAM_SEGSMANAGER_TABLE);
			db.execSQL("CREATE TABLE IF NOT EXISTS "
					+ SegsConstants.PROGRAM_SEGSMANAGER_TABLE + " ("
					+ "_id integer primary key autoincrement,"
					+ SegsConstants.SEGS_PROGRAMID + " varchar(100),"
					+ SegsConstants.SEGS_SUBID + " varchar(100),"
					+ SegsConstants.SEGS_SUM_SEG_DOWNLOADED + " varchar(25),"
					+ SegsConstants.SEGS_INIT_URL_DOWNLOAD_TIME
					+ " varchar(100),"
					+ SegsConstants.SEGS_IS_DOWNLOADED_INIT_M3U8
					+ " varchar(100),"
					+ SegsConstants.SEGS_NEXT_DOWNLOAD_SEG_INDEX
					+ " varchar(25)," + SegsConstants.SEGS_SEG_STEP
					+ " varchar(25)," + SegsConstants.SEGS_VIDEO_FORMAT
					+ " varchar(100))");
		} catch (SQLException ex) {
			Log.e(TAG, "couldn't create table in downloads database");
			throw ex;
		}
	}

	private void RecreateSegsTable(SQLiteDatabase db) {
		try {
			Log.d(TAG, "SegInfoDataBaseHelper-->>createSegInfoTable");
			db.execSQL("CREATE TABLE IF NOT EXISTS "
					+ SegsConstants.PROGRAM_SEGSMANAGER_TABLE + " ("
					+ "_id integer primary key autoincrement,"
					+ SegsConstants.SEGS_PROGRAMID + " varchar(100),"
					+ SegsConstants.SEGS_SUBID + " varchar(100),"
					+ SegsConstants.SEGS_SUM_SEG_DOWNLOADED + " varchar(25),"
					+ SegsConstants.SEGS_INIT_URL_DOWNLOAD_TIME
					+ " varchar(100),"
					+ SegsConstants.SEGS_IS_DOWNLOADED_INIT_M3U8
					+ " varchar(100),"
					+ SegsConstants.SEGS_NEXT_DOWNLOAD_SEG_INDEX
					+ " varchar(25)," + SegsConstants.SEGS_SEG_STEP
					+ " varchar(25)," + SegsConstants.SEGS_VIDEO_FORMAT
					+ " varchar(100))");
		} catch (SQLException ex) {
			Log.e(TAG, "couldn't create table in downloads database");
			throw ex;
		}
	}

	/**
	 * Add a column to a table using ALTER TABLE.
	 * 
	 * @param dbTable
	 *            name of the table
	 * @param columnName
	 *            name of the column to add
	 * @param columnDefinition
	 *            SQL for the column definition
	 */
	private void addColumn(SQLiteDatabase db, String dbTable,
			String columnName, String columnDefinition) {
		db.execSQL("ALTER TABLE " + dbTable + " ADD COLUMN " + columnName + " "
				+ columnDefinition);
	}

	public String dropTable(SQLiteDatabase db, String tableName) {
		if (tableName == null) {
			return null;
		}

		try {
			String DROP_TABLE = "DROP TABLE IF EXISTS " + tableName;
			db.execSQL(DROP_TABLE);
		} catch (Exception ex) {
		}

		db.close();

		return tableName;
	}

}
