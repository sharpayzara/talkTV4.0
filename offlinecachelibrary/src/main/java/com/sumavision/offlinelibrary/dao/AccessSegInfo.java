package com.sumavision.offlinelibrary.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sumavision.offlinelibrary.core.DownloadManager;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.SegInfo;

public class AccessSegInfo {

	private static AccessSegInfo instance;

	public static AccessSegInfo getInstance(Context context) {
		synchronized (AccessSegInfo.class) {
			if (instance == null) {
				instance = new AccessSegInfo(context);
			}
			return instance;
		}
	}

	private AccessSegInfo(Context context) {
		AccessSegInfoManager.initializeInstance(new SegInfoDataBaseHelper(
				context.getApplicationContext()));

	}

	public boolean isExistedFromSegTable(DownloadInfo dInfo) {

		String querrySql = "select count(*) from "
				+ DaoConstants.DOWNLOAD_SEGINFO_TABLE + " where "
				+ DaoConstants.SEG_PROGRAM_ID + " = ? and "
				+ DaoConstants.SEG_SUBID + " = ? ";
		String id = String.valueOf(dInfo.programId);
		String subId = String.valueOf(dInfo.subProgramId);
		String[] bindArgs = { id, subId };
		SQLiteDatabase db = AccessSegInfoManager.getInstance().openDatabase();
		try {
			db.beginTransaction();
			Cursor cursor = db.rawQuery(querrySql, bindArgs);
			if (cursor != null) {
				cursor.moveToFirst();
				long howLong = cursor.getLong(0);
				cursor.close();
				db.setTransactionSuccessful();
				if (howLong > 0) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
		return false;
	}

	/**
	 * 批量存储seg信息
	 * 
	 * @author wht
	 */
	@SuppressWarnings("deprecation")
	public void bulkSave(DownloadInfo dInfo) {
		float tillDuration = 0;
		String sql = null;
		sql = "INSERT INTO  " + DaoConstants.DOWNLOAD_SEGINFO_TABLE + " ("
				+ DaoConstants.SEG_PROGRAM_ID + "," + DaoConstants.SEG_SUBID
				+ "," + DaoConstants.SEG_INDEX + "," + DaoConstants.SEG_URL
				+ "," + DaoConstants.SEG_TIME_LENGTH + ","
				+ DaoConstants.SEG_DATA_LENGTH + ","
				+ DaoConstants.SEG_BREAKPOINT + ","
				+ DaoConstants.SEG_ISDOWNLOADED + ","
				+ DaoConstants.SEG_ISDOWNLOADING + ","
				+ DaoConstants.SEG_FILE_DIR + ","
				+ DaoConstants.SEG_LOCATION_FILE + ","
				+ DaoConstants.SEG_TOTAL_COUNT + ","
				+ DaoConstants.SEG_SINGLE_TIME_LENGTH + ","
				+ DaoConstants.SEG_DOWNLOADCOUNT + ","
				+ DaoConstants.SEG_MODIFY_TIME
				+ ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		SQLiteDatabase db = AccessSegInfoManager.getInstance().openDatabase();
		try {

			db.beginTransaction();
			for (int i = 0; i < dInfo.segInfos.size(); i++) {
				SegInfo segInfo = dInfo.segInfos.get(i);
				segInfo.index = i;
				segInfo.singleTimeLength = String.valueOf(tillDuration);
				tillDuration += Float.valueOf(segInfo.timeLength);
				segInfo.totalDuration = tillDuration;
				long time = new Date().getTime();
				Timestamp timestamp = new Timestamp(time);
				Object[] bindArgs = { segInfo.programId, segInfo.subId,
						segInfo.index, segInfo.downloadUrl, segInfo.timeLength,
						segInfo.dataLength, segInfo.breakPoint,
						segInfo.isDownloaded ? 1 : 0,
						segInfo.isDownloading ? 1 : 0, segInfo.fileDir,
						segInfo.locationFile, segInfo.totalDuration,
						segInfo.singleTimeLength, 0, timestamp };
				db.execSQL(sql, bindArgs);
			}
			db.setTransactionSuccessful();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			AccessSegInfoManager.getInstance().closeDatabase();
		}

	}

	public void updateSegsUrl(DownloadInfo dInfo) {
		String sql;

		sql = "update " + DaoConstants.DOWNLOAD_SEGINFO_TABLE + " set "
				+ DaoConstants.SEG_URL + " = ? " + "where "
				+ DaoConstants.SEG_INDEX + " =? and "
				+ DaoConstants.SEG_PROGRAM_ID + " = ? and "
				+ DaoConstants.SEG_SUBID + " = ? ";

		SQLiteDatabase db = AccessSegInfoManager.getInstance().openDatabase();
		try {
			db.beginTransaction();
			for (int i = 0; i < dInfo.segInfos.size(); i++) {
				SegInfo segInfo = dInfo.segInfos.get(i);
				Object[] bindArgs = { segInfo.downloadUrl, segInfo.index,
						segInfo.programId, segInfo.subId };
				db.execSQL(sql, bindArgs);
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			AccessSegInfoManager.getInstance().closeDatabase();
		}
	}

	public void updateSegBreakPoint(SegInfo segInfo) {
		String sql;

		sql = "update " + DaoConstants.DOWNLOAD_SEGINFO_TABLE + " set "
				+ DaoConstants.SEG_BREAKPOINT + " = ? " + " where "
				+ DaoConstants.SEG_INDEX + " =? and "
				+ DaoConstants.SEG_PROGRAM_ID + " = ? and "
				+ DaoConstants.SEG_SUBID + " = ? ";

		String breakPoint = String.valueOf(segInfo.breakPoint);

		String index = String.valueOf(segInfo.index);
		Object[] bindArgs;
		bindArgs = new Object[] { breakPoint, index, segInfo.programId,
				segInfo.subId };
		SQLiteDatabase db = AccessSegInfoManager.getInstance().openDatabase();
		try {
			db.beginTransaction();
			db.execSQL(sql, bindArgs);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			AccessSegInfoManager.getInstance().closeDatabase();
		}
	}

	public void updateSegInfo(SegInfo segInfo) {
		String sql;

		sql = "update " + DaoConstants.DOWNLOAD_SEGINFO_TABLE + " set "
				+ DaoConstants.SEG_URL + " = ?," + DaoConstants.SEG_BREAKPOINT
				+ " = ?," + DaoConstants.SEG_FILE_DIR + " = ?,"
				+ DaoConstants.SEG_DATA_LENGTH + " = ?,"
				+ DaoConstants.SEG_ISDOWNLOADED + " = ?,"
				+ DaoConstants.SEG_ISDOWNLOADING + " = ?,"
				+ DaoConstants.SEG_LOCATION_FILE + " = ?,"
				+ DaoConstants.SEG_TIME_LENGTH + " = ? " + " where "
				+ DaoConstants.SEG_INDEX + " =? and "
				+ DaoConstants.SEG_PROGRAM_ID + " = ? and "
				+ DaoConstants.SEG_SUBID + " = ? ";

		String segUrl = segInfo.downloadUrl;
		String breakPoint = String.valueOf(segInfo.breakPoint);
		String fileDir = segInfo.fileDir;
		String dataLength = String.valueOf(segInfo.dataLength);
		String isDownloaded = String.valueOf(segInfo.isDownloaded ? 1 : 0);
		String isDownloading = String.valueOf(segInfo.isDownloading ? 1 : 0);

		String locationFile = String.valueOf(segInfo.locationFile);
		String timeLength = String.valueOf(segInfo.timeLength);
		String index = String.valueOf(segInfo.index);
		Object[] bindArgs;
		bindArgs = new Object[] { segUrl, breakPoint, fileDir, dataLength,
				isDownloaded, isDownloading, locationFile, timeLength, index,
				segInfo.programId, segInfo.subId };
		SQLiteDatabase db = AccessSegInfoManager.getInstance().openDatabase();
		try {
			db.beginTransaction();
			db.execSQL(sql, bindArgs);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			AccessSegInfoManager.getInstance().closeDatabase();
		}
	}

	public void deleteByProgramIdAndSubId(ArrayList<DownloadInfo> downloadInfos) {
		String sql = "";
		sql = "delete from " + DaoConstants.DOWNLOAD_SEGINFO_TABLE + " where "
				+ DaoConstants.SEG_PROGRAM_ID + " = ? and "
				+ DaoConstants.SEG_SUBID + " = ? ";
		SQLiteDatabase db = AccessSegInfoManager.getInstance().openDatabase();
		String[] bindArgs = null;
		try {
			db.beginTransaction();
			for (DownloadInfo info : downloadInfos) {
				bindArgs = new String[] { String.valueOf(info.programId),
						String.valueOf(info.subProgramId) };
				db.execSQL(sql, bindArgs);
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			AccessSegInfoManager.getInstance().closeDatabase();
		}
	}

	public void deleteFromSegTable(DownloadInfo downloadInfo) {

		String sql = "";
		sql = "delete from " + DaoConstants.DOWNLOAD_SEGINFO_TABLE + " where "
				+ DaoConstants.SEG_PROGRAM_ID + " = ? and "
				+ DaoConstants.SEG_SUBID + " = ? ";
		SQLiteDatabase db = AccessSegInfoManager.getInstance().openDatabase();
		String[] bindArgs = new String[] {
				String.valueOf(downloadInfo.programId),
				String.valueOf(downloadInfo.subProgramId) };
		try {
			db.beginTransaction();
			db.execSQL(sql, bindArgs);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			AccessSegInfoManager.getInstance().closeDatabase();
			Log.d(DownloadManager.TAG, "delete table " + downloadInfo.programId
					+ "-" + downloadInfo.subProgramId + " succeed");
		}
	}

	public SegInfo querySegInfo(SegInfo segInfo) {
		String querrySql = "select * from "
				+ DaoConstants.DOWNLOAD_SEGINFO_TABLE + " where "
				+ DaoConstants.SEG_INDEX + " = ? and "
				+ DaoConstants.SEG_PROGRAM_ID + " = ? and "
				+ DaoConstants.SEG_SUBID + " = ? ";
		String[] bindArgs = new String[] { String.valueOf(segInfo.index),
				String.valueOf(segInfo.programId),
				String.valueOf(segInfo.subId) };
		Cursor cursor = null;
		SegInfo data = null;
		SQLiteDatabase db = AccessSegInfoManager.getInstance().openDatabase();
		try {
			db.beginTransaction();
			cursor = db.rawQuery(querrySql, bindArgs);
			while (cursor.moveToNext()) {
				data = new SegInfo();
				data.programId = segInfo.programId;
				data.subId = segInfo.subId;
				data.index = segInfo.index;
				data.downloadUrl = cursor.getString(cursor
						.getColumnIndex(DaoConstants.SEG_URL));
				data.timeLength = cursor.getString(cursor
						.getColumnIndex(DaoConstants.SEG_TIME_LENGTH));
				data.dataLength = cursor.getInt(cursor
						.getColumnIndex(DaoConstants.SEG_DATA_LENGTH));
				data.isDownloaded = cursor.getInt(cursor
						.getColumnIndex(DaoConstants.SEG_ISDOWNLOADED)) == 1 ? true
						: false;
				data.isDownloading = cursor.getInt(cursor
						.getColumnIndex(DaoConstants.SEG_ISDOWNLOADING)) == 1 ? true
						: false;
				data.breakPoint = cursor.getInt(cursor
						.getColumnIndex(DaoConstants.SEG_BREAKPOINT));
				data.fileDir = cursor.getString(cursor
						.getColumnIndex(DaoConstants.SEG_FILE_DIR));
				data.locationFile = cursor.getString(cursor
						.getColumnIndex(DaoConstants.SEG_LOCATION_FILE));
				data.downloadCount = cursor.getInt(cursor
						.getColumnIndex(DaoConstants.SEG_DOWNLOADCOUNT));

			}
			cursor.close();
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.endTransaction();
			AccessSegInfoManager.getInstance().closeDatabase();
		}
		return data;

	}

	public CopyOnWriteArrayList<SegInfo> querySegInfos(DownloadInfo dInfo) {
		if (dInfo == null) {
			return new CopyOnWriteArrayList<SegInfo>();
		}
		String querrySql = "select * from "
				+ DaoConstants.DOWNLOAD_SEGINFO_TABLE + " where "
				+ DaoConstants.SEG_PROGRAM_ID + " = ? and "
				+ DaoConstants.SEG_SUBID + " = ? order by "
				+ DaoConstants.SEG_INDEX;
		String[] bindArgs = new String[] { String.valueOf(dInfo.programId),
				String.valueOf(dInfo.subProgramId) };
		CopyOnWriteArrayList<SegInfo> seginfos = new CopyOnWriteArrayList<SegInfo>();
		Cursor cursor = null;
		SQLiteDatabase db = AccessSegInfoManager.getInstance().openDatabase();
		try {
			db.beginTransaction();
			cursor = db.rawQuery(querrySql, bindArgs);
			while (cursor.moveToNext()) {
				SegInfo seg = new SegInfo();
				seg.programId = dInfo.programId;
				seg.subId = dInfo.subProgramId;
				seg.index = cursor.getInt(cursor
						.getColumnIndex(DaoConstants.SEG_INDEX));
				seg.downloadUrl = cursor.getString(cursor
						.getColumnIndex(DaoConstants.SEG_URL));
				seg.timeLength = cursor.getString(cursor
						.getColumnIndex(DaoConstants.SEG_TIME_LENGTH));
				seg.dataLength = cursor.getLong(cursor
						.getColumnIndex(DaoConstants.SEG_DATA_LENGTH));
				seg.isDownloaded = cursor.getInt(cursor
						.getColumnIndex(DaoConstants.SEG_ISDOWNLOADED)) == 1 ? true
						: false;
				seg.isDownloading = cursor.getInt(cursor
						.getColumnIndex(DaoConstants.SEG_ISDOWNLOADING)) == 1 ? true
						: false;
				seg.breakPoint = cursor.getLong(cursor
						.getColumnIndex(DaoConstants.SEG_BREAKPOINT));
				seg.fileDir = cursor.getString(cursor
						.getColumnIndex(DaoConstants.SEG_FILE_DIR));
				seg.locationFile = cursor.getString(cursor
						.getColumnIndex(DaoConstants.SEG_LOCATION_FILE));
				seg.downloadCount = cursor.getInt(cursor
						.getColumnIndex(DaoConstants.SEG_DOWNLOADCOUNT));
				seginfos.add(seg);

			}
			cursor.close();
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			return new CopyOnWriteArrayList<SegInfo>();
		} finally {

			db.endTransaction();
			AccessSegInfoManager.getInstance().closeDatabase();
		}
		return seginfos;

	}
}
