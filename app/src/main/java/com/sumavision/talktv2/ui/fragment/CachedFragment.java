package com.sumavision.talktv2.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.jiongbull.jlog.JLog;
import com.sumavision.offlinelibrary.core.DownloadManager;
import com.sumavision.offlinelibrary.core.DownloadService;
import com.sumavision.offlinelibrary.core.DownloadUtils;
import com.sumavision.offlinelibrary.dao.AccessDownload;
import com.sumavision.offlinelibrary.dao.AccessSegInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfoState;
import com.sumavision.offlinelibrary.util.CommonUtils;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.CacheDeleteSeleteEvent;
import com.sumavision.talktv2.model.entity.ShowEditButtonEvent;
import com.sumavision.talktv2.presenter.CachedFragmentPresenter;
import com.sumavision.talktv2.ui.activity.DragSettingActivity;
import com.sumavision.talktv2.ui.adapter.CachingAdapter;
import com.sumavision.talktv2.ui.fragment.Base.BaseFragment;
import com.sumavision.talktv2.ui.iview.base.IBaseView;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.videoplayer.activity.CachePlayerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import butterknife.BindView;

/**
 * Created by zhoutao on 2016/6/21.
 */
public class CachedFragment extends BaseFragment<CachedFragmentPresenter> implements IBaseView, AdapterView.OnItemClickListener {
    private static final String TAG = "CachedFragment";

    protected CopyOnWriteArrayList<DownloadInfo> downloadInfos = new CopyOnWriteArrayList<DownloadInfo>();
    public CopyOnWriteArrayList<DownloadInfo> downloadInfosWaitDelete = new CopyOnWriteArrayList<DownloadInfo>();
    private boolean editState;
    private boolean initView = true;
    private CachingAdapter cachedAdapter;

    @BindView(R.id.rl_msg_null)
    RelativeLayout rl_msg_null;
    @BindView(R.id.lv_cached)
    ListView listView;
    @BindView(R.id.rl_cached_full)
    RelativeLayout rl_cached_full;
    private long lastClicktime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (downloadReceiver != null) {
            getActivity().unregisterReceiver(downloadReceiver);
        }
        if (cachedAdapter != null)
            BusProvider.getInstance().unregister(cachedAdapter);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_cached;
    }

    @Override
    protected void initPresenter() {
        presenter = new CachedFragmentPresenter(this.getContext(), this);
        presenter.init();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void initView() {
        //获取数据如果没有数据则设置空页面显示
        //否则设置
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadService.NOTIFICATION_ID + "_"
                + DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        getActivity().registerReceiver(downloadReceiver, filter);
        refreshData();
        listView.setOnItemClickListener(this);

    }

    public void showEditLayout1(String refreshEdit) {
        listView.invalidate();
        editState = true;
    }

    public void showbackEditLayout2(String backEdit) {
        for (DownloadInfo tmp : downloadInfosWaitDelete) {
            tmp.pendingState = 0;
        }
        downloadInfosWaitDelete.clear();
        listView.invalidate();
        editState = false;
    }

    public void clickallbtn(boolean clickallbtn) {
        operateAllItem(clickallbtn);
        cachedAdapter.notifyDataSetChanged();
    }

    public void clickdeletebtn(String clickdeletebtn) {
        deleteSelectedItem();
        cachedAdapter.notifyDataSetChanged();
        if (downloadInfos.size() == 0) {
            rl_msg_null.setVisibility(View.VISIBLE);
            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_CACHE_SHOW_EDIT, new ShowEditButtonEvent(0, false));
            ((DragSettingActivity) getActivity()).editCache();
        }
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            JLog.i(TAG, "receive broadcast"
                    + DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            refreshData();
        }
    };

    public void refreshData() {
        if (editState || !initView) {
            return;
        }
        getData();
    }

    private void getData() {
        downloadInfos.clear();
        ArrayList<DownloadInfo> list = AccessDownload.getInstance(getContext())
                .queryDownloadInfo(DownloadInfoState.DOWNLOADED);
        downloadInfos.addAll(list);

        if (downloadInfos != null && downloadInfos.size() > 0) {
            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_CACHE_SHOW_EDIT, new ShowEditButtonEvent(0, true));
            rl_msg_null.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        } else {
            rl_msg_null.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_CACHE_SHOW_EDIT, new ShowEditButtonEvent(0, false));
        }
        if (cachedAdapter == null) {
            cachedAdapter = new CachingAdapter(getContext(), downloadInfos);
            listView.setAdapter(cachedAdapter);
            BusProvider.getInstance().register(cachedAdapter);
        } else {
            cachedAdapter.notifyDataSetChanged();
        }
    }

    private void operateAllItem(boolean select) {
        if (downloadInfos != null && downloadInfos.size() > 0) {
            for (int i = 0; i < downloadInfos.size(); i++) {
                downloadInfos.get(i).pendingState = select ? 1 : 0;
            }
            downloadInfosWaitDelete.clear();
            cachedAdapter.notifyDataSetChanged();
            if (select) {
                downloadInfosWaitDelete.addAll(downloadInfos);
                BusProvider.getInstance().post(AppGlobalConsts.EventType.CHANGE_SELECT_ALL_STATE, new CacheDeleteSeleteEvent(downloadInfosWaitDelete.size(), false));
            } else {
                downloadInfosWaitDelete.removeAll(downloadInfos);
                BusProvider.getInstance().post(AppGlobalConsts.EventType.CHANGE_SELECT_ALL_STATE, new CacheDeleteSeleteEvent(0, true));
            }
        }
    }

    public void deleteSelectedItem() {
        if (downloadInfosWaitDelete.size() == 0) {
            return;
        }
        if (downloadInfosWaitDelete.size() == downloadInfos.size()) {
            downloadInfos.clear();
        } else {
            downloadInfos.removeAll(downloadInfosWaitDelete);
        }
        cachedAdapter.notifyDataSetChanged();
        // 删除数据库
        new Thread(deleteRunnable).start();

    }

    private Runnable deleteRunnable = new Runnable() {

        @Override
        public void run() {
            if (downloadInfosWaitDelete.size() == 0) {
                return;
            }
            ArrayList<DownloadInfo> deleteSegTableList = new ArrayList<DownloadInfo>();
            int currentDownloadIndex = -1;
            for (int j = 0; j < downloadInfosWaitDelete.size(); j++) {
                DownloadInfo downloadInfo = downloadInfosWaitDelete.get(j);
                if (downloadInfo.state == DownloadInfoState.DOWNLOADING) {
                    currentDownloadIndex = j;
                    int action = DownloadService.ACTION_DELETE_DOWNLOADING;
                    startDownloadService(downloadInfo, action);
                } else {
                    deleteSegTableList.add(downloadInfo);
                }

            }
            if (currentDownloadIndex != -1)
                downloadInfosWaitDelete.remove(currentDownloadIndex);
            if (deleteSegTableList.size() > 0) {
                AccessSegInfo.getInstance(getActivity())
                        .deleteByProgramIdAndSubId(deleteSegTableList);
            }
            AccessDownload.getInstance(getActivity())
                    .deleteFromSegsByProgramIdAndSubId(downloadInfosWaitDelete);
            AccessDownload.getInstance(getContext()).deleteProgramSub(
                    downloadInfosWaitDelete);

            for (DownloadInfo item : downloadInfosWaitDelete) {
                String dir = DownloadUtils.getFileDir(item);
                CommonUtils.deleteFile(new File(dir));
            }
            downloadInfosWaitDelete.clear();
        }
    };

    private void startDownloadService(DownloadInfo downloadInfo, int action) {
        Intent intent = new Intent(getContext(), DownloadService.class);
        Bundle bundle = new Bundle();
        bundle.putInt(DownloadService.ACTION_KEY, action);
        intent.putExtra("bundle", bundle);
        intent.putExtra(DownloadService.APPNAME_KEY, "电视粉");
        intent.putExtra(DownloadService.APP_EN_NAME_KEY, "tvfanphone");
        intent.putExtra(DownloadManager.extra_loadinfo, downloadInfo);
        getActivity().startService(intent);
    }

    @Override
    public void onResume() {
        if (cachedAdapter != null){
            cachedAdapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final DownloadInfo downloadInfo = downloadInfos.get(position);
        boolean startServiceFlag = false;
        int action = 0;
        if (editState) {
            if (downloadInfo.pendingState == 0) {
                downloadInfo.pendingState = 1;
                downloadInfosWaitDelete.add(downloadInfo);
                if (downloadInfosWaitDelete.size() == downloadInfos.size()) {
                    BusProvider.getInstance().post(AppGlobalConsts.EventType.CHANGE_SELECT_ALL_STATE, new CacheDeleteSeleteEvent(downloadInfosWaitDelete.size(), false));
                } else {
                    BusProvider.getInstance().post(AppGlobalConsts.EventType.CHANGE_SELECT_ALL_STATE, new CacheDeleteSeleteEvent(downloadInfosWaitDelete.size(), true));
                }
            } else {
                downloadInfo.pendingState = 0;
                downloadInfosWaitDelete.remove(downloadInfo);
                if (downloadInfosWaitDelete.size() != downloadInfos.size()) {
                    BusProvider.getInstance().post(AppGlobalConsts.EventType.CHANGE_SELECT_ALL_STATE, new CacheDeleteSeleteEvent(downloadInfosWaitDelete.size(), true));
                }
            }
            cachedAdapter.notifyDataSetChanged();
        } else {
            Intent intent = new Intent(getContext(), CachePlayerActivity.class);
            intent.putExtra("playUrl", downloadInfo.fileLocation);
            intent.putExtra("programName", downloadInfo.programName);
            startActivity(intent);
        }
        lastClicktime = System.currentTimeMillis();

    }

    public void showEditButton() {
        if (downloadInfos.size() > 0) {
            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_CACHE_SHOW_EDIT, new ShowEditButtonEvent(0, true));
        } else {
            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_CACHE_SHOW_EDIT, new ShowEditButtonEvent(0, false));
        }

    }
}
