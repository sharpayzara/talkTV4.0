package com.sumavision.offlinelibrary.dao;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

public class SegInfoDataBaseHelper extends SQLiteOpenHelper {
	private static final int DB_VERSION = 6;
	public static final String TAG = "offlinecacheLog";

	public SegInfoDataBaseHelper(Context context) {
		this(context, true);
	}

	@SuppressLint("NewApi")
	public SegInfoDataBaseHelper(Context context, boolean enableWAL) {
		super(context, DaoConstants.DB_NAME, null, DB_VERSION);
		if (enableWAL && Build.VERSION.SDK_INT >= 11) {
			getWritableDatabase().enableWriteAheadLogging();
		}
	}

	@Override
	public synchronized void close() {
		Log.d(TAG, "SegInfoDataBaseHelper-->>close");
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "SegInfoDataBaseHelper-->>onCreate");
		onUpgrade(db, 0, DB_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "SegInfoDataBaseHelper-->>onUpgrade:from " + oldVersion
				+ " to " + newVersion);
		if (oldVersion < 2) {
			// no logic to upgrade from these older version, just recreate
			// the DB
			Log.i(TAG, "Upgrading downloads database from version "
					+ oldVersion + " to version " + newVersion
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
		case 4:
			createSegInfoTable(db);
		case 5:

			break;
		case 6:
			createSegInfoTable(db);
			break;
		// case 7:
		// addColumn(db, DaoConstants.DB_NAME, "dataLength", "varchar(100)");
		// break;
		default:
			throw new IllegalStateException("Don't know how to upgrade to "
					+ version);
		}
	}

	/**
	 * Creates the table that'll hold the download information.
	 */
	private void createSegInfoTable(SQLiteDatabase db) {
		try {
			Log.d(TAG, "SegInfoDataBaseHelper-->>createSegInfoTable");
			db.execSQL("DROP TABLE IF EXISTS "
					+ DaoConstants.DOWNLOAD_SEGINFO_TABLE);
			db.execSQL("CREATE TABLE IF NOT EXISTS "
					+ DaoConstants.DOWNLOAD_SEGINFO_TABLE + " ("
					+ "_id integer primary key autoincrement,"
					+ DaoConstants.SEG_PROGRAM_ID + " varchar(100),"
					+ DaoConstants.SEG_SUBID + " varchar(100),"
					+ DaoConstants.SEG_INDEX + " integer(10),"
					+ DaoConstants.SEG_URL + " varchar(100),"
					+ DaoConstants.SEG_TIME_LENGTH + " varchar(100),"
					+ DaoConstants.SEG_DATA_LENGTH + " varchar(100),"
					+ DaoConstants.SEG_BREAKPOINT + " varchar(100),"
					+ DaoConstants.SEG_ISDOWNLOADED + " varchar(100),"
					+ DaoConstants.SEG_ISDOWNLOADING + " varchar(100),"
					+ DaoConstants.SEG_FILE_DIR + " varchar(100),"
					+ DaoConstants.SEG_LOCATION_FILE + " varchar(100),"
					+ DaoConstants.SEG_TOTAL_COUNT + " float(100),"
					+ DaoConstants.SEG_SINGLE_TIME_LENGTH + " float(100),"
					+ DaoConstants.SEG_DOWNLOADCOUNT + " float(100),"
					+ DaoConstants.SEG_MODIFY_TIME + " timestamp)");
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
