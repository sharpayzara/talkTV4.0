/**
 * 
 */
package com.sumavision.offlinelibrary.dao;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.sumavision.offlinelibrary.core.DownloadUtils;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfoState;
import com.sumavision.offlinelibrary.entity.VideoFormat;
import com.sumavision.offlinelibrary.util.CommonUtils;

public class AccessDownload {
	private static AccessDownload instance;
	private static final String TAG = "AccessDownload";

	public static AccessDownload getInstance(Context context) {
		if (instance == null) {
			synchronized (AccessDownload.class) {
				if (instance == null) {
					instance = new AccessDownload(context);
				}
			}
		}
		return instance;
	}

	private AccessDownload(Context context) {
		AccessDownloadManager.initializeInstance(new DownloadDataBaseHelper(
				context.getApplicationContext()));

	}

	public boolean isExisted(DownloadInfo downloadInfo, SQLiteDatabase db) {

		String querrySql = "select count(*) from download where programid = ? and subid= ?";
		String id = String.valueOf(downloadInfo.programId);
		String subId = String.valueOf(downloadInfo.subProgramId);
		String[] bindArgs = { id, subId };
		db.beginTransaction();
		Cursor cursor = db.rawQuery(querrySql, bindArgs);
		if (cursor != null) {
			cursor.moveToFirst();
			long howLong = cursor.getLong(0);
			cursor.close();
			db.setTransactionSuccessful();
			db.endTransaction();
			if (howLong > 0) {
				return true;
			}
		}
		return false;

	}
	public boolean isExists(DownloadInfo downloadInfo){
		boolean flag = false;
		SQLiteDatabase db = AccessDownloadManager.getInstance().openDatabase();
		if(isExisted(downloadInfo,db)){
			flag = true;
		}
		AccessDownloadManager.getInstance().closeDatabase();
		return flag;
	}

	public boolean save(DownloadInfo data) {
		String sql = null;
		sql = "INSERT INTO download (programid,subid,programname,url,programpic,state,progress,timelength,localurl,downloadId,videoFormat,modifytime,parseUrl,sdcardDir,definition) VALUES (?,?,?, ?, ?,?, ?,?, ?,?, ?,?, ?,?,?)";
		SQLiteDatabase db = AccessDownloadManager.getInstance().openDatabase();
		db.beginTransaction();
		try {
			long time = new Date().getTime();
			Timestamp timestamp = new Timestamp(time);
			if (data.programName == null)
				data.programName = "";
			if (data.initUrl == null)
				data.initUrl = "";
			if (data.programPic == null)
				data.programPic = "";
			if (data.downloadId == 0) {
				data.downloadId = -1;
			}
			if (data.videoFormat != VideoFormat.M3U8_FORMAT
					&& data.videoFormat != VideoFormat.MP4_FORMAT) {
				data.videoFormat = VideoFormat.UNKNOW_FORMAT;
			}
			if (data.parseUrl == null) {
				data.parseUrl = "";
			}
			if (data.sdcardDir == null) {
				data.sdcardDir = "";
			}
			if (data.definition == null) {
				data.definition = "";
			}
			if (!isExisted(data, db)) {
				Object[] bindArgs = { data.programId, data.subProgramId,
						data.programName, data.initUrl, data.programPic,
						data.state, data.progress, data.timeLength,
						data.fileLocation, data.downloadId, data.videoFormat,
						timestamp, data.parseUrl, data.sdcardDir,
						data.definition };
				db.execSQL(sql, bindArgs);
			} else {
				// updateDownloadState(data, db);
			}

			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			AccessDownloadManager.getInstance().closeDatabase();
		}
		return true;

	}

	/**
	 * 检测当前是否有节目在下载
	 * 
	 * @param downloadInfo
	 */
	public boolean isDownloadingExecute() {
		String querrySql = "select count(*) from download where state = ? ";
		String[] bindArgs = new String[] { "1" };
		SQLiteDatabase db = AccessDownloadManager.getInstance().openDatabase();
		db.beginTransaction();
		Cursor cursor = db.rawQuery(querrySql, bindArgs);
		if (cursor != null) {
			cursor.moveToFirst();
			long howLong = cursor.getLong(0);
			cursor.close();
			db.setTransactionSuccessful();
			if (howLong > 0) {
				db.endTransaction();
				return true;
			}
		}
		db.endTransaction();
		return false;
	}

	/**
	 * 查询下载状态
	 * 
	 * @param pid
	 * @param subid
	 * @return
	 */
	public int findStateByProgramIdAndSubId(String pid, String subid) {
		int state = 0;
		StringBuilder sqlBuild = new StringBuilder(
				"select * form download where programid=");
		sqlBuild.append(pid).append(" and subid=").append(subid);
		SQLiteDatabase db = AccessDownloadManager.getInstance().openDatabase();
		try {
			Cursor cursor = db.rawQuery(sqlBuild.toString(), null);
			while (cursor.moveToNext()) {
				state = cursor.getInt(cursor.getColumnIndex("state"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			AccessDownloadManager.getInstance().closeDatabase();
		}
		return state;
	}

	/**
	 * 更新下载节目状态
	 * 
	 * @param downloadInfo
	 */
	public void updateDownloadState(DownloadInfo downloadInfo) {
		String sql = "";
		sql = "update download set state=?  where programid=? and subid=? ";
		String programId = String.valueOf(downloadInfo.programId);
		String subId = String.valueOf(downloadInfo.subProgramId);
		String state = String.valueOf(downloadInfo.state);
		Object[] bindArgs = { state, programId, subId };
		SQLiteDatabase db = AccessDownloadManager.getInstance().openDatabase();
		try {
			db.execSQL(sql, bindArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			AccessDownloadManager.getInstance().closeDatabase();
		}
	}

	/**
	 * 更新下载节目状态
	 * 
	 * @param downloadInfo
	 */
	public void updateDownloadsState(
			CopyOnWriteArrayList<DownloadInfo> downloadInfosWaitDelete) {
		SQLiteDatabase db = AccessDownloadManager.getInstance().openDatabase();
		try {
			db.beginTransaction();
			String sql = "update download set state=?  where programid=? and subid=? ";
			for (DownloadInfo info : downloadInfosWaitDelete) {
				String programId = String.valueOf(info.programId);
				String subId = String.valueOf(info.subProgramId);
				String state = String.valueOf(info.state);
				Object[] bindArgs = { state, programId, subId };
				db.execSQL(sql, bindArgs);
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			AccessDownloadManager.getInstance().closeDatabase();
		}
	}

	/**
	 * 
	 * @param downloadInfo
	 */
	public void updateProgramInfo(DownloadInfo downloadInfo) {

		ContentValues values = new ContentValues();
		if (downloadInfo.segCount > 0) {
			values.put("segCount", downloadInfo.segCount);
		}

		String[] whereArgs = new String[] {
				String.valueOf(downloadInfo.programId),
				String.valueOf(downloadInfo.subProgramId) };
		SQLiteDatabase db = AccessDownloadManager.getInstance().openDatabase();
		try {
			db.update(SegsConstants.PROGRAM_SEGSMANAGER_TABLE, values,
					"programid=? and subid=? ", whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			AccessDownloadManager.getInstance().closeDatabase();
		}
	}

	/**
	 * 可更新状态、进度、本地m3u8文件、时长等
	 * 
	 * @param downloadInfo
	 */
	public void updateDownloadInfo(DownloadInfo downloadInfo) {

		ContentValues values = new ContentValues();
		if (downloadInfo.state > 0) {
			values.put("state", downloadInfo.state);
		}
		if (downloadInfo.progress > 0) {
			values.put("progress", downloadInfo.progress);
		}
		if (!TextUtils.isEmpty(downloadInfo.fileLocation)) {
			values.put("localurl", downloadInfo.fileLocation);
		}
		if (downloadInfo.timeLength > 0) {
			values.put("timelength", downloadInfo.timeLength);
		}
		if (downloadInfo.initUrl != null
				&& !downloadInfo.initUrl.endsWith("-webparse")) {
			values.put("url", downloadInfo.initUrl);
		}
		if (downloadInfo.videoFormat != VideoFormat.UNKNOW_FORMAT) {
			values.put("videoFormat", downloadInfo.videoFormat);
		}
		if (downloadInfo.parseUrl != null
				|| !TextUtils.isEmpty(downloadInfo.parseUrl)) {
			values.put("parseUrl", downloadInfo.parseUrl);
		}
		if (downloadInfo.sdcardDir != null
				|| !TextUtils.isEmpty(downloadInfo.sdcardDir)) {
			values.put("sdcardDir", downloadInfo.sdcardDir);
		}

		String[] whereArgs = new String[] {
				String.valueOf(downloadInfo.programId),
				String.valueOf(downloadInfo.subProgramId) };
		SQLiteDatabase db = AccessDownloadManager.getInstance().openDatabase();
		try {
			db.update("download", values, "programid=? and subid=? ", whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			AccessDownloadManager.getInstance().closeDatabase();
		}
	}

	/**
	 * 更新下载节目进度
	 * 
	 * @param downloadInfo
	 */
	public void updateDownloadProgress(DownloadInfo downloadInfo) {
		String sql = "update download set progress =? where programid=? and subid=? ";
		String programId = String.valueOf(downloadInfo.programId);
		String subId = String.valueOf(downloadInfo.subProgramId);
		String progress = String.valueOf(downloadInfo.progress);
		Object[] bindArgs;
		bindArgs = new Object[] { progress, programId, subId };
		SQLiteDatabase db = AccessDownloadManager.getInstance().openDatabase();
		try {
			db.execSQL(sql, bindArgs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			AccessDownloadManager.getInstance().closeDatabase();
		}
	}

	/**
	 * 根据下载状态下载节目信息列表
	 * 
	 * @param programId
	 * @param programSubId
	 */
	public ArrayList<DownloadInfo> queryDownloadInfo(int state) {
		String querrySql = "select * from download where state = ? order by modifytime asc ";
		String[] bindArgs = new String[] { String.valueOf(state) };
		ArrayList<DownloadInfo> temp = new ArrayList<DownloadInfo>();
		Cursor cursor = null;
		SQLiteDatabase db = AccessDownloadManager.getInstance().openDatabase();
		try {

			db.beginTransaction();
			cursor = db.rawQuery(querrySql, bindArgs);
			while (cursor.moveToNext()) {
				DownloadInfo data = new DownloadInfo();
				data.programId = cursor.getString(cursor
						.getColumnIndex("programid"));
				data.subProgramId = cursor.getString(cursor
						.getColumnIndex("subid"));
				data.programName = cursor.getString(cursor
						.getColumnIndex("programname"));
				data.initUrl = cursor.getString(cursor.getColumnIndex("url"));
				data.programPic = cursor.getString(cursor
						.getColumnIndex("programpic"));
				data.state = cursor.getInt(cursor.getColumnIndex("state"));
				data.progress = cursor
						.getInt(cursor.getColumnIndex("progress"));
				data.fileLocation = cursor.getString(cursor
						.getColumnIndex("localurl"));
				data.timeLength = cursor.getInt(cursor
						.getColumnIndex("timelength"));
				data.downloadId = cursor.getLong(cursor
						.getColumnIndex("downloadId"));
				data.videoFormat = cursor.getInt(cursor
						.getColumnIndex("videoFormat"));
				data.parseUrl = cursor.getString(cursor
						.getColumnIndex("parseUrl"));
				data.sdcardDir = cursor.getString(cursor
						.getColumnIndex("sdcardDir"));
				temp.add(data);
			}
			cursor.close();
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			AccessDownloadManager.getInstance().closeDatabase();
		}
		return temp;

	}

	/**
	 * 获取未完成下载的节目信息列表
	 * 
	 * @param programId
	 * @param programSubId
	 */
	public ArrayList<DownloadInfo> queryDownloadInfo() {
		String querrySql = "select * from download where state <>? and state <>? order by modifytime asc ";
		ArrayList<DownloadInfo> temp = new ArrayList<DownloadInfo>();
		Cursor cursor = null;
		String[] bindArgs = new String[] {
				String.valueOf(DownloadInfoState.DOWNLOADED),
				String.valueOf(DownloadInfoState.DELETE) };
		SQLiteDatabase db = AccessDownloadManager.getInstance().openDatabase();
		try {
			db.beginTransaction();
			cursor = db.rawQuery(querrySql, bindArgs);
			while (cursor.moveToNext()) {
				DownloadInfo data = new DownloadInfo();
				data.programId = cursor.getString(cursor
						.getColumnIndex("programid"));
				data.subProgramId = cursor.getString(cursor
						.getColumnIndex("subid"));
				data.programName = cursor.getString(cursor
						.getColumnIndex("programname"));
				data.initUrl = cursor.getString(cursor.getColumnIndex("url"));
				data.programPic = cursor.getString(cursor
						.getColumnIndex("programpic"));
				data.state = cursor.getInt(cursor.getColumnIndex("state"));
				data.progress = cursor
						.getInt(cursor.getColumnIndex("progress"));
				data.fileLocation = cursor.getString(cursor
						.getColumnIndex("localurl"));
				data.timeLength = cursor.getInt(cursor
						.getColumnIndex("timelength"));
				data.downloadId = cursor.getLong(cursor
						.getColumnIndex("downloadId"));
				data.videoFormat = cursor.getInt(cursor
						.getColumnIndex("videoFormat"));
				data.pendingState = cursor.getInt(cursor
						.getColumnIndex("pendingState"));
				data.parseUrl = cursor.getString(cursor
						.getColumnIndex("parseUrl"));
				data.sdcardDir = cursor.getString(cursor
						.getColumnIndex("sdcardDir"));
				temp.add(data);
			}
			cursor.close();
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.endTransaction();
			AccessDownloadManager.getInstance().closeDatabase();
		}
		return temp;

	}

	/**
	 * 根据节目id、子节目id查询下载节目信息
	 * 
	 * @param programId
	 * @param programSubId
	 */
	public DownloadInfo queryCacheProgram(DownloadInfo downloadInfo) {
		String querrySql = "select * from download where  programid=? and subid=? order by modifytime desc";
		String[] bindArgs = new String[] {
				String.valueOf(downloadInfo.programId),
				String.valueOf(downloadInfo.subProgramId) };
		DownloadInfo temp = new DownloadInfo();
		Cursor cursor = null;
		SQLiteDatabase db = AccessDownloadManager.getInstance().openDatabase();
		try {
			db.beginTransaction();
			cursor = db.rawQuery(querrySql, bindArgs);
			while (cursor.moveToNext()) {
				DownloadInfo data = new DownloadInfo();
				data.programId = cursor.getString(cursor
						.getColumnIndex("programid"));
				data.subProgramId = cursor.getString(cursor
						.getColumnIndex("subid"));
				data.programName = cursor.getString(cursor
						.getColumnIndex("programname"));
				data.initUrl = cursor.getString(cursor.getColumnIndex("url"));
				data.programPic = cursor.getString(cursor
						.getColumnIndex("programpic"));
				data.state = cursor.getInt(cursor.getColumnIndex("state"));
				data.progress = cursor
						.getInt(cursor.getColumnIndex("progress"));
				data.fileLocation = cursor.getString(cursor
						.getColumnIndex("localurl"));
				data.timeLength = cursor.getInt(cursor
						.getColumnIndex("timelength"));
				data.downloadId = cursor.getLong(cursor
						.getColumnIndex("downloadId"));
				data.videoFormat = cursor.getInt(cursor
						.getColumnIndex("videoFormat"));
				data.parseUrl = cursor.getString(cursor
						.getColumnIndex("parseUrl"));
				data.sdcardDir = cursor.getString(cursor
						.getColumnIndex("sdcardDir"));
				temp = data;
			}
			cursor.close();
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.endTransaction();
			AccessDownloadManager.getInstance().closeDatabase();
		}
		return temp;
	}

	// 根据节目id查询该节目中已经下载剧集信息
	public ArrayList<DownloadInfo> queryDownloadInfos(String programId) {
		String querrySql = "select * from download where programid = ? order by modifytime asc ";
		String[] bindArgs = new String[] { String.valueOf(programId) };
		ArrayList<DownloadInfo> temp = new ArrayList<DownloadInfo>();
		Cursor cursor = null;
		SQLiteDatabase db = AccessDownloadManager.getInstance().openDatabase();
		try {

			db.beginTransaction();
			cursor = db.rawQuery(querrySql, bindArgs);
			while (cursor.moveToNext()) {
				DownloadInfo data = new DownloadInfo();
				data.programId = cursor.getString(cursor
						.getColumnIndex("programid"));
				data.subProgramId = cursor.getString(cursor
						.getColumnIndex("subid"));
				data.programName = cursor.getString(cursor
						.getColumnIndex("programname"));
				data.initUrl = cursor.getString(cursor.getColumnIndex("url"));
				data.programPic = cursor.getString(cursor
						.getColumnIndex("programpic"));
				data.state = cursor.getInt(cursor.getColumnIndex("state"));
				data.progress = cursor
						.getInt(cursor.getColumnIndex("progress"));
				data.fileLocation = cursor.getString(cursor
						.getColumnIndex("localurl"));
				data.timeLength = cursor.getInt(cursor
						.getColumnIndex("timelength"));
				data.downloadId = cursor.getLong(cursor
						.getColumnIndex("downloadId"));
				data.videoFormat = cursor.getInt(cursor
						.getColumnIndex("videoFormat"));
				data.parseUrl = cursor.getString(cursor
						.getColumnIndex("parseUrl"));
				data.sdcardDir = cursor.getString(cursor
						.getColumnIndex("sdcardDir"));
				temp.add(data);
			}
			cursor.close();
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			AccessDownloadManager.getInstance().closeDatabase();
		}
		return temp;
	}

	/**
	 * 缓存时查询所需下载的清晰度
	 * 
	 * @param downloadInfo
	 */
	public String queryDownloadDefinition(DownloadInfo downloadInfo) {
		String sql = "select * from download where programid=? and subid=?";
		SQLiteDatabase db = AccessDownloadManager.getInstance().openDatabase();
		db.beginTransaction();
		String[] bindArgs = { String.valueOf(downloadInfo.programId),
				String.valueOf(downloadInfo.subProgramId) };
		String definition = "";
		try {
			Cursor cursor = db.rawQuery(sql, bindArgs);
			while (cursor.moveToNext()) {
				definition = cursor.getString(cursor
						.getColumnIndex("definition"));
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			AccessDownloadManager.getInstance().closeDatabase();
		}
		return definition;
	}

	/**
	 * 根据节目id删除节目信息
	 * 
	 * @param programId
	 * @param programSubId
	 */
	public void deleteProgram(String programId) {
		String sql = "delete from download where programid=?";
		String[] bindArgs = { programId };

		SQLiteDatabase db = AccessDownloadManager.getInstance().openDatabase();
		try {
			db.beginTransaction();
			db.execSQL(sql, bindArgs);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			AccessDownloadManager.getInstance().closeDatabase();
		}
	}

	/**
	 * 根据节目、子节目id删除节目信息
	 * 
	 * @param programId
	 * @param programSubId
	 */
	public void deleteProgramSub(long programId, long programSubId) {
		String sql = "";
		sql = "delete from download where programid=? and subid=?";
		Long[] bindArgs = { programId, programSubId };

		SQLiteDatabase db = AccessDownloadManager.getInstance().openDatabase();
		try {
			db.beginTransaction();
			db.execSQL(sql, bindArgs);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			AccessDownloadManager.getInstance().closeDatabase();
		}
	}

	/**
	 * 删除下载节目信息,并可删除本地文件
	 * 
	 * @param downloadInfo
	 * @param deleteLocalFile
	 */
	public void deleteProgramSub(DownloadInfo downloadInfo,
			boolean deleteLocalFile) {
		String sql = "";
		sql = "delete from download where programid=? and subid=?";
		String[] bindArgs = new String[] {
				String.valueOf(downloadInfo.programId),
				String.valueOf(downloadInfo.subProgramId) };

		SQLiteDatabase db = AccessDownloadManager.getInstance().openDatabase();
		try {
			db.beginTransaction();
			db.execSQL(sql, bindArgs);
			db.setTransactionSuccessful();
//			if (deleteLocalFile) {
//				String dir = DownloadUtils.getFileDir(downloadInfo);
//				File file = new File(dir);
//				CommonUtils.deleteFile(file);
//			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			AccessDownloadManager.getInstance().closeDatabase();
		}
	}

	/**
	 * 批量删除下载节目信息并删除本地文件
	 * 
	 * @param infoList
	 */
	public void deleteProgramSub(List<DownloadInfo> infoList) {
		String sql = "";
		sql = "delete from download where programid=? and subid=?";
		SQLiteDatabase db = AccessDownloadManager.getInstance().openDatabase();
		try {
			db.beginTransaction();
			for (DownloadInfo item : infoList) {
				String[] bindArgs = { item.programId,
						item.subProgramId };

				db.execSQL(sql, bindArgs);
				// String dir = DownloadUtils.getFileDir(item);
				// CommonUtils.deleteFile(new File(dir));
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			AccessDownloadManager.getInstance().closeDatabase();
		}
	}

	/**
	 * 批量更新编辑状态
	 * 
	 * @param infoList
	 */
	public void updatePendingState(List<DownloadInfo> infoList) {
		String sql = "";
		sql = "update download set pendingState=? where programid=? and subid=?";
		SQLiteDatabase db = AccessDownloadManager.getInstance().openDatabase();
		try {
			db.beginTransaction();
			for (DownloadInfo item : infoList) {
				Object[] bindArgs = { String.valueOf(item.pendingState),
						String.valueOf(item.programId),
						String.valueOf(item.subProgramId) };
				if (item.pendingState == 1) {
					Log.d("CachingFragment", "updatePendingState-->>"
							+ item.programId + item.subProgramId
							+ "pendingState:" + item.pendingState);
				}

				db.execSQL(sql, bindArgs);
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			AccessDownloadManager.getInstance().closeDatabase();
		}
	}

	public boolean isExistedSegs(DownloadInfo downloadInfo) {

		String querrySql = "select count(*) from "
				+ SegsConstants.PROGRAM_SEGSMANAGER_TABLE + " where "
				+ SegsConstants.SEGS_PROGRAMID + " = ? and "
				+ SegsConstants.SEGS_SUBID + " = ? ";
		String id = String.valueOf(downloadInfo.programId);
		String subId = String.valueOf(downloadInfo.subProgramId);
		String[] bindArgs = { id, subId };
		SQLiteDatabase db = AccessDownloadManager.getInstance().openDatabase();

		Cursor cursor = null;
		long howLong = 0;
		try {
			db.beginTransaction();
			cursor = db.rawQuery(querrySql, bindArgs);
			if (cursor != null) {
				cursor.moveToFirst();
				howLong = cursor.getLong(0);
				cursor.close();
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			AccessDownloadManager.getInstance().closeDatabase();
		}
		if (howLong > 0) {
			return true;
		}
		return false;

	}

	/**
	 * 存储m3u8数据信息
	 * 
	 * @param info
	 * @return
	 */
	public void saveSegs(DownloadInfo info) {
		Log.d(TAG, "AccessDownloadSegsManager-->>save " + info.programId
				+ info.subProgramId + toString(info));
		String sql = null;
		sql = "INSERT INTO  " + SegsConstants.PROGRAM_SEGSMANAGER_TABLE + " ("
				+ SegsConstants.SEGS_PROGRAMID + "," + SegsConstants.SEGS_SUBID
				+ "," + SegsConstants.SEGS_SUM_SEG_DOWNLOADED + ","
				+ SegsConstants.SEGS_INIT_URL_DOWNLOAD_TIME + ","
				+ SegsConstants.SEGS_IS_DOWNLOADED_INIT_M3U8 + ","
				+ SegsConstants.SEGS_NEXT_DOWNLOAD_SEG_INDEX + ","
				+ SegsConstants.SEGS_SEG_STEP + ","
				+ SegsConstants.SEGS_VIDEO_FORMAT + ","
				+ SegsConstants.SEGS_DATA_LENGTH + ","
				+ SegsConstants.SEGS_DOWNLOADED_LENGTH + ","
				+ SegsConstants.SEGS_SEGS_COUNT
				+ ") VALUES (?,?,?,?,?,?,?,?,?,?,?)";
		SQLiteDatabase db = AccessDownloadManager.getInstance().openDatabase();
		db.beginTransaction();
		try {
			Object[] bindArgs = { info.programId, info.subProgramId,
					info.sumSegDownloaded, info.initUrlDownloadTime,
					info.isDownloadedInitM3u8 ? 1 : 0,
					info.nextDownloadSegIndex, info.segStep, info.videoFormat,
					info.dataLength, info.downloadedLength, info.segCount };
			db.execSQL(sql, bindArgs);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			AccessDownloadManager.getInstance().closeDatabase();
		}
	}

	/**
	 * 查询m3u8数据信息
	 * 
	 * @param info
	 * @return
	 */
	public DownloadInfo querySegsInfo(DownloadInfo info) {
		Cursor cursor = null;
		SQLiteDatabase db = AccessDownloadManager.getInstance().openDatabase();

		String querrySql = "select * from "
				+ SegsConstants.PROGRAM_SEGSMANAGER_TABLE + " where "
				+ SegsConstants.SEGS_PROGRAMID + " = ? and "
				+ SegsConstants.SEGS_SUBID + " = ? ";
		String[] bindArgs = new String[] { String.valueOf(info.programId),
				String.valueOf(info.subProgramId) };
		try {
			db.beginTransaction();
			cursor = db.rawQuery(querrySql, bindArgs);
			while (cursor.moveToNext()) {
				info.sumSegDownloaded = Integer
						.valueOf(cursor.getString(cursor
								.getColumnIndex(SegsConstants.SEGS_SUM_SEG_DOWNLOADED)));
				info.initUrlDownloadTime = Long
						.valueOf(cursor.getString(cursor
								.getColumnIndex(SegsConstants.SEGS_INIT_URL_DOWNLOAD_TIME)));
				info.isDownloadedInitM3u8 = cursor
						.getInt(cursor
								.getColumnIndex(SegsConstants.SEGS_IS_DOWNLOADED_INIT_M3U8)) == 1 ? true
						: false;
				info.nextDownloadSegIndex = Integer
						.valueOf(cursor.getInt(cursor
								.getColumnIndex(SegsConstants.SEGS_NEXT_DOWNLOAD_SEG_INDEX)));
				info.segStep = Integer.valueOf(cursor.getInt(cursor
						.getColumnIndex(SegsConstants.SEGS_SEG_STEP)));
				info.videoFormat = Integer.valueOf(cursor.getString(cursor
						.getColumnIndex(SegsConstants.SEGS_VIDEO_FORMAT)));
				String tmp = cursor.getString(cursor
						.getColumnIndex(SegsConstants.SEGS_DATA_LENGTH));
				info.dataLength = Long.valueOf(tmp != null ? tmp : "0");
				tmp = cursor.getString(cursor
						.getColumnIndex(SegsConstants.SEGS_DOWNLOADED_LENGTH));
				info.downloadedLength = Long.valueOf(tmp != null ? tmp : "0");
				tmp = cursor.getString(cursor
						.getColumnIndex(SegsConstants.SEGS_SEGS_COUNT));
				info.segCount = Integer.valueOf(tmp != null ? tmp : "0");

			}

			if (cursor != null) {
				cursor.close();
			}
			Log.d(TAG, "AccessDownloadSegsManager-->>query " + info.programId
					+ info.subProgramId + toString(info));
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.endTransaction();
			AccessDownloadManager.getInstance().closeDatabase();
		}
		return info;
	}

	/**
	 * 更新m3u8信息数据，下一段需要下载的段落等
	 * 
	 * @param info
	 */
	public void upadateSegsInfo(DownloadInfo info) {

		SQLiteDatabase db = AccessDownloadManager.getInstance().openDatabase();
		String sql;
		try {
			sql = "update " + SegsConstants.PROGRAM_SEGSMANAGER_TABLE + " set "
					+ SegsConstants.SEGS_SUM_SEG_DOWNLOADED + " = ?,"
					+ SegsConstants.SEGS_INIT_URL_DOWNLOAD_TIME + " = ?,"
					+ SegsConstants.SEGS_NEXT_DOWNLOAD_SEG_INDEX + " = ?,"
					+ SegsConstants.SEGS_SEG_STEP + " = ?,"
					+ SegsConstants.SEGS_VIDEO_FORMAT + " = ?,"
					+ SegsConstants.SEGS_IS_DOWNLOADED_INIT_M3U8 + " = ?,"
					+ SegsConstants.SEGS_DATA_LENGTH + " = ?,"
					+ SegsConstants.SEGS_DOWNLOADED_LENGTH + " = ?,"
					+ SegsConstants.SEGS_SEGS_COUNT + " = ?" + " where "

					+ SegsConstants.SEGS_PROGRAMID + " = ? and "
					+ SegsConstants.SEGS_SUBID + " = ? ";

			String sumSegDownloaded = String.valueOf(info.sumSegDownloaded);
			String initUrlDownloadTime = String
					.valueOf(info.initUrlDownloadTime);
			String nextDownloadIndex = String
					.valueOf(info.nextDownloadSegIndex);
			String segStep = String.valueOf(info.segStep);
			String videoFormat = String.valueOf(info.videoFormat);
			String isDownloadedInitM3u8 = String
					.valueOf(info.isDownloadedInitM3u8 == true ? 1 : 0);
			String dataLength = String.valueOf(info.dataLength);
			String downloadedLength = String.valueOf(info.downloadedLength);
			String segCount = String.valueOf(info.segCount);
			Object[] bindArgs;
			bindArgs = new Object[] { sumSegDownloaded, initUrlDownloadTime,
					nextDownloadIndex, segStep, videoFormat,
					isDownloadedInitM3u8, dataLength, downloadedLength,
					segCount, info.programId, info.subProgramId };

			db.beginTransaction();
			db.execSQL(sql, bindArgs);
			// Log.d(TAG, "AccessDownloadSegsManager-->>upadate" +
			// info.programId
			// + info.subProgramId + toString(info));
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			AccessDownloadManager.getInstance().closeDatabase();
		}
	}

	public void deleteFromSegsByProgramIdAndSubId(
			CopyOnWriteArrayList<DownloadInfo> downloadInfosWaitDelete) {
		String sql = "";
		sql = "delete from " + SegsConstants.PROGRAM_SEGSMANAGER_TABLE
				+ " where " + SegsConstants.SEGS_PROGRAMID + " = ? and "
				+ SegsConstants.SEGS_SUBID + " = ? ";
		SQLiteDatabase db = AccessDownloadManager.getInstance().openDatabase();
		String[] bindArgs = null;
		try {
			db.beginTransaction();
			for (DownloadInfo info : downloadInfosWaitDelete) {
				bindArgs = new String[] { String.valueOf(info.programId),
						String.valueOf(info.subProgramId) };
				db.execSQL(sql, bindArgs);
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			AccessDownloadManager.getInstance().closeDatabase();
		}
	}

	public void deleteFromTableSegs(DownloadInfo downloadInfo) {

		String sql = "";
		sql = "delete from " + SegsConstants.PROGRAM_SEGSMANAGER_TABLE
				+ " where " + SegsConstants.SEGS_PROGRAMID + " = ? and "
				+ SegsConstants.SEGS_SUBID + " = ? ";
		SQLiteDatabase db = AccessDownloadManager.getInstance().openDatabase();
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
			AccessDownloadManager.getInstance().closeDatabase();
			Log.d(TAG, "delete table " + downloadInfo.programId + "-"
					+ downloadInfo.subProgramId + " succeed");
		}
	}

	private String toString(DownloadInfo info) {
		String str = "";
		str = str + "\n sumSegDownloaded:" + info.sumSegDownloaded
				+ " segStep:" + info.segStep + " nextDownloadSegIndex:"
				+ info.nextDownloadSegIndex + "\n" + " initUrlDownloadTime:"
				+ info.initUrlDownloadTime + "  isDownloadedInitM3u8:"
				+ info.isDownloadedInitM3u8 + " videoFormat:"
				+ info.videoFormat;
		return str;
	}
}
