package com.sumavision.talktv2.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.sumavision.offlinelibrary.core.DownloadService;
import com.sumavision.offlinelibrary.dao.AccessDownload;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfoState;
import com.sumavision.offlinelibrary.entity.InternalExternalPathInfo;
import com.sumavision.offlinelibrary.util.CommonUtils;
import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.SeriesDetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/6/29.
 */
public class OfflineCacheUtil {

    public static void startCache(Context context, List<SeriesDetail.SourceBean> cacheList, String programId, String programName, String defSelected) {
        try {
            if (cacheList != null && cacheList.size() > 0) {
//				Collections.reverse(datas);
                AccessDownload accessDownload = AccessDownload
                        .getInstance(context);
                for (SeriesDetail.SourceBean sourceBean : cacheList) {
                    sourceBean.setCached(true); // 置为已缓存状态，方便马上显示在界面上
                    sourceBean.setIsdownload(false); // 把选中缓存的红色对号去掉
                    DownloadInfo downloadInfo = new DownloadInfo();
                    downloadInfo.programId = programId;
                    downloadInfo.subProgramId = sourceBean.getId();
                    downloadInfo.programPic = sourceBean.getPicUrl();
                    downloadInfo.definition = defSelected;

                    InternalExternalPathInfo internalExternalPathInfo = CommonUtils
                            .getInternalExternalPath(context);
                    String path = null;

                    // 获取用户设置的存储路径，0内部存储，1sd卡
                    int type = PreferencesUtils.getInt(context, null,
                            "cache_path_type");
                    if (type == 1) {
                        if (internalExternalPathInfo.removableSDcard != null) {
                            path = internalExternalPathInfo.removableSDcard;
                        } else {
                            // 如果没有外置，则判断有没有内置的，如果有则设置默认路径为内置
                            if (internalExternalPathInfo.emulatedSDcard != null) {
                                PreferencesUtils.putInt(context, null,
                                        "cache_path_type", 0);
                                path = internalExternalPathInfo.emulatedSDcard;
                            } else {
                                Toast.makeText(context,
                                        "无外置sd卡，无法缓存，请修改存储路径为手机存储",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    } else {
                        if (internalExternalPathInfo.emulatedSDcard != null) {
                            path = internalExternalPathInfo.emulatedSDcard;
                        } else {
                            if (internalExternalPathInfo.removableSDcard != null) {
                                PreferencesUtils.putInt(context, null,
                                        "cache_path_type", 1);
                                path = internalExternalPathInfo.removableSDcard;
                            } else {
                                Toast.makeText(context,
                                        "无内置存储空间，无法缓存",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }

                    downloadInfo.sdcardDir = path;
                    if (!TextUtils.isEmpty(sourceBean.getName())) {
                        downloadInfo.programName = sourceBean.getName();
                    } else {
                        downloadInfo.programName = programName + " " + sourceBean.getEpi();
                    }
                    downloadInfo.initUrl = getInitUrl(sourceBean);
                    downloadInfo.state = DownloadInfoState.WAITTING;
                    accessDownload.save(downloadInfo);
                }
                ArrayList<DownloadInfo> downloadingInfos = accessDownload
                        .queryDownloadInfo(DownloadInfoState.DOWNLOADING);
                Toast.makeText(context, "请到缓存中心查看进度",
                        Toast.LENGTH_SHORT).show();
                if (downloadingInfos == null || downloadingInfos.size() == 0 && checkNetwork(context)) {
                    ArrayList<DownloadInfo> waittings = accessDownload
                            .queryDownloadInfo(DownloadInfoState.WAITTING);
                    if (waittings != null && waittings.size() > 0) {
                        DownloadInfo downloadInfo = waittings.get(0);
                        downloadInfo.state = DownloadInfoState.DOWNLOADING;
                        accessDownload.updateDownloadState(downloadInfo);
                    }
                    startCacheService(context);

                }
//                cacheRequest();
            }
//            if (episodeData != null) {
//                ((ProgramDetailHalfActivity)getParentFragment()).filterCacheInfo(episodeData, programData);
//                adapter.notifyDataSetChanged();
//            }
//            getFragmentManager().popBackStack();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    // 检查网络，以下情况允许继续下载，返回true
    // 1.当前为wifi
    // 2.当前为移动数据，但用户已设置允许移动数据缓存
    private static boolean checkNetwork(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
        if (null != netInfo && ConnectivityManager.TYPE_WIFI == netInfo.getType()) {
            return true;
        }
        if (null != netInfo && ConnectivityManager.TYPE_MOBILE == netInfo.getType()) {
            Object tmp = BaseApp.getACache().getAsObject("CLICKNUM_ALLOWCACHE");
            if (tmp != null && ((boolean) tmp)) {
                Log.i("OfflineCacheUtil", "2G/3G/4G网络已连接，继续下载");
//                Toast.makeText(context, R.string.mobile_download, Toast.LENGTH_LONG).show();
                return true;
            } else {
                Toast.makeText(context, R.string.mobile_download_forbid, Toast.LENGTH_LONG).show();
                return false;
            }
        }
        Toast.makeText(context, R.string.download_network_problem, Toast.LENGTH_LONG).show();
        return false;
    }

    private static void startCacheService(Context context) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(DownloadService.APPNAME_KEY,
                "电视粉");
        intent.putExtra(DownloadService.APP_EN_NAME_KEY, "tvfanphone");
        context.startService(intent);
    }

    private static String getInitUrl(SeriesDetail.SourceBean jishuData) {
        if (TextUtils.isEmpty(jishuData.getPlayUrl())) {
            return jishuData.getPlayUrl() + "-webparse";
        }
        return jishuData.getPlayUrl();
    }

    public static void filterCacheInfo(Context context, List<SeriesDetail.SourceBean> jiShuDatas, String programId) {
        if (jiShuDatas == null || jiShuDatas.size() == 0) {
            return;
        }
//        HashMap<String, DownloadInfo> map = getDownloadInfoHashMap(context, programId);
        for (SeriesDetail.SourceBean jishuData : jiShuDatas) {
            boolean isSet = false;
            DownloadInfo tmp  = map.get(jishuData.getId());
            if (tmp != null) {
                jishuData.setCached(true);
                if (tmp.state == DownloadInfoState.DOWNLOADED) {
                    jishuData.setLocalPath(tmp.fileLocation);
                }
            }
//            for (DownloadInfo downloadedInfo : downloadedInfos) {
//                if (programId.equals(downloadedInfo.programId)
//                        && jishuData.getId().equals(downloadedInfo.subProgramId)) {
//                    jishuData.setCached(true);
//                    if (downloadedInfo.state == DownloadInfoState.DOWNLOADED) {
//                        jishuData.setLocalPath(downloadedInfo.fileLocation);
//                    }
//                    isSet = true;
//                    break;
//                }
//            }
//            if (!isSet) {
//                jishuData.setCached(false);
//            }
        }
    }

    public static void filterCacheInfo(Context context, List<SeriesDetail.SourceBean> jiShuDatas, HashMap<String, DownloadInfo> map) {
        if (jiShuDatas == null || jiShuDatas.size() == 0) {
            return;
        }
        for (SeriesDetail.SourceBean jishuData : jiShuDatas) {
            boolean isSet = false;
            DownloadInfo tmp = map.get(jishuData.getId());
            if (tmp != null) {
                jishuData.setCached(true);
                if (tmp.state == DownloadInfoState.DOWNLOADED) {
                    jishuData.setLocalPath(tmp.fileLocation);
                }
            }
        }
    }

    static HashMap<String, DownloadInfo> map = new HashMap<String, DownloadInfo>();

    @NonNull
    public static HashMap<String, DownloadInfo> getDownloadInfoHashMap(Context context, String programId) {
        map.clear();
        AccessDownload accessDownload = AccessDownload.getInstance(context);
        ArrayList<DownloadInfo> downloadedInfos = accessDownload.queryDownloadInfos(programId);
//        HashMap<String, DownloadInfo> map = new HashMap<String, DownloadInfo>();
        for (DownloadInfo tmp : downloadedInfos) {
            String programName = tmp.subProgramId;
//            int index = programName.lastIndexOf(" ");
//            String epi = programName.substring(index + 1);
            map.put(programName, tmp);
//            map.put(tmp.programName, tmp);
        }
        return map;
    }

}
