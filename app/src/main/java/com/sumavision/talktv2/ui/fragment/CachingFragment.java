package com.sumavision.talktv2.ui.fragment;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sumavision.offlinelibrary.core.DownloadManager;
import com.sumavision.offlinelibrary.core.DownloadService;
import com.sumavision.offlinelibrary.core.DownloadUtils;
import com.sumavision.offlinelibrary.dao.AccessDownload;
import com.sumavision.offlinelibrary.dao.AccessSegInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfoState;
import com.sumavision.offlinelibrary.util.CommonUtils;
import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.CacheDeleteSeleteEvent;
import com.sumavision.talktv2.model.entity.ShowEditButtonEvent;
import com.sumavision.talktv2.model.entity.decor.CachingDataItem;
import com.sumavision.talktv2.presenter.CachingFragmentPresenter;
import com.sumavision.talktv2.ui.activity.DragSettingActivity;
import com.sumavision.talktv2.ui.adapter.CachingAdapter;
import com.sumavision.talktv2.ui.fragment.Base.BaseFragment;
import com.sumavision.talktv2.ui.iview.base.IBaseView;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.CommonUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import crack.util.ACache;

/**
 * Created by zhoutao on 2016/6/21.
 */
public class CachingFragment extends BaseFragment<CachingFragmentPresenter> implements IBaseView, AdapterView.OnItemClickListener {

    @BindView(R.id.rv_caching)
    ListView listView;
    @BindView(R.id.rl_msg_null)
    RelativeLayout rl_msg_null;
    @BindView(R.id.rl_caching_full)
    RelativeLayout rl_caching_full;
    @BindView(R.id.rl_caching_btn)
    RelativeLayout rl_caching_btn;
    @BindView(R.id.btn_caching_pause)
    Button btn_caching_pause;
    @BindView(R.id.btn_caching_start)
    Button btn_caching_start;

    private CachingAdapter cachingAdapter;
    private int checkNum; // 记录选中的条目数量
    private List<CachingDataItem> results = new ArrayList<>();

    protected CopyOnWriteArrayList<DownloadInfo> downloadInfos = new CopyOnWriteArrayList<DownloadInfo>();
    public CopyOnWriteArrayList<DownloadInfo> downloadInfosWaitDelete = new CopyOnWriteArrayList<DownloadInfo>();

    private boolean editState;
    private boolean initView = true;
    private long lastClicktime;

    private final int AFTER_DELETE_ALL = 1000;

    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AFTER_DELETE_ALL:
                    if (downloadInfos.size() == 0) {
                        rl_msg_null.setVisibility(View.VISIBLE);
                        BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_CACHE_SHOW_EDIT, new ShowEditButtonEvent(1, false));
                        ((DragSettingActivity) getActivity()).editCache();
                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppGlobalConsts.ISEDITSTATE = false;
        BusProvider.getInstance().unregister(this);
        if (cachingAdapter != null)
            BusProvider.getInstance().unregister(cachingAdapter);
        if (cachingDownloadReceiver != null) {
            getActivity().unregisterReceiver(cachingDownloadReceiver);
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_caching;
    }

    @Override
    protected void initPresenter() {
        presenter = new CachingFragmentPresenter(getContext(), this);
        presenter.init();
    }

    @Override
    public void onResume() {
        if (cachingAdapter != null){
            cachingAdapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void initView() {

        refreshData();
        if (downloadInfos != null && downloadInfos.size() > 0) {
            startRemainTask();
        }

        listView.setMinimumHeight(CommonUtil.dip2px(this.getContext(), 80));
        if (!AppGlobalConsts.ISEDITSTATE) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
                    CachingAdapter.ViewHolder holder = (CachingAdapter.ViewHolder) view.getTag();
                    // 改变CheckBox的状态
                    holder.cb_cached_imgselect.toggle();
                    // 将CheckBox的选中状况记录下来
                    results.get(position).isCheck = holder.cb_cached_imgselect.isChecked();
                    // 调整选定条目
                    if (holder.cb_cached_imgselect.isChecked() == true) {
                        checkNum++;
                    } else {
                        checkNum--;
                    }
                }
            });
            listView.invalidate();
        } else {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                }

            });
        }
        registerReceiver();
        listView.setOnItemClickListener(this);
    }

    public void showEditLayout1(String refreshEdit) {
        rl_caching_btn.setVisibility(View.GONE);
        listView.invalidate();
        editState = true;
    }

    public void showbackEditLayout2(String backEdit) {
        for (DownloadInfo tmp : downloadInfosWaitDelete) {
            tmp.pendingState = 0;
        }
        downloadInfosWaitDelete.clear();
        rl_caching_btn.setVisibility(View.VISIBLE);
        listView.invalidate();
        editState = false;
    }

    @OnClick(R.id.btn_caching_pause)
    public void clickPause() {
        int downloadingIndex = -1;
        for (int i = 0; i < downloadInfos.size(); i++) {
            DownloadInfo downloadInfo = downloadInfos.get(i);
            if (downloadInfo.state == DownloadInfoState.WAITTING) {
                downloadInfo.state = DownloadInfoState.PAUSE;
                AccessDownload.getInstance(getContext()).updateDownloadState(
                        downloadInfo);
            }

            if (downloadInfo.state == DownloadInfoState.DOWNLOADING) {
                downloadingIndex = i;
            }
        }
        if (downloadingIndex != -1) {
            DownloadInfo tmp = downloadInfos.get(downloadingIndex);
            tmp.state = DownloadInfoState.PAUSE;
            AccessDownload.getInstance(getContext()).updateDownloadState(tmp);
            startDownloadService(tmp, DownloadService.ACTION_PAUSE);
        }
        cachingAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.btn_caching_start)
    public void clickStart() {
        boolean downloadSet = false;
        int downloadingIndex = -1;
        for (int i = 0; i < downloadInfos.size(); i++) {
            DownloadInfo downloadInfo = downloadInfos.get(i);
            if (downloadInfo.state == DownloadInfoState.DOWNLOADING) {
                downloadingIndex = i;
                break;
            }
        }
        if (downloadingIndex != -1) {
            for (int i = 0; i < downloadInfos.size(); i++) {
                DownloadInfo downloadInfo = downloadInfos.get(i);
                if (downloadInfo.state == DownloadInfoState.PAUSE) {
                    downloadInfo.state = DownloadInfoState.WAITTING;
                    AccessDownload.getInstance(getContext()).updateDownloadState(
                            downloadInfo);
                }
            }
            cachingAdapter.notifyDataSetChanged();
            return;
        }

        for (int i = 0; i < downloadInfos.size(); i++) { // 找到第一个pause的，改成downloading，其余的pause变成waiting
            DownloadInfo downloadInfo = downloadInfos.get(i);
            if (downloadInfo.state == DownloadInfoState.PAUSE) {
                if (!downloadSet) {
                    downloadInfo.state = DownloadInfoState.DOWNLOADING;
                    downloadSet = true;
                } else {
                    downloadInfo.state = DownloadInfoState.WAITTING;
                }
                AccessDownload.getInstance(getContext()).updateDownloadState(
                        downloadInfo);
            }
        }
        startRemainTask();
        cachingAdapter.notifyDataSetChanged();
    }

    public void clickallbtn(boolean clickallbtn) {
        operateAllItem(clickallbtn);
        cachingAdapter.notifyDataSetChanged();
//        }
    }

    public void clickdeletebtn(String clickdeletebtn) {
        deleteSelectedItem();
        cachingAdapter.notifyDataSetChanged();
//        if (downloadInfos.size() == 0) {
//            rl_msg_null.setVisibility(View.VISIBLE);
//            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_CACHE_SHOW_EDIT, new ShowEditButtonEvent(1, false));
//            ((DragSettingActivity) getActivity()).editCache();
//        }
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadService.NOTIFICATION_ID + "_"
                + DownloadManager.ACTION_DOWNLOAD_ERROR);
        filter.addAction(DownloadService.NOTIFICATION_ID + "_"
                + DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        filter.addAction(DownloadService.NOTIFICATION_ID + "_"
                + DownloadManager.ACTION_DOWNLOAD_REFRESH);
        filter.addAction(DownloadService.NOTIFICATION_ID + "_"
                + DownloadManager.ACTION_DOWNLOAD_PAUSE);
        filter.addAction(DownloadService.NOTIFICATION_ID + "_"
                + DownloadManager.ACTION_DOWNLOAD_ERROR_RETURY);
        filter.addAction(DownloadService.NOTIFICATION_ID + "_"
                + DownloadManager.ACTION_DOWNLOAD_PARSE_ERROR);
        getActivity().registerReceiver(cachingDownloadReceiver, filter);
    }

    private BroadcastReceiver cachingDownloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (editState) {
                DownloadInfo info = (DownloadInfo) intent
                        .getSerializableExtra(DownloadManager.extra_loadinfo);
                if (info != null) {
                    switch (info.state) {
                        case DownloadInfoState.DOWNLOADED:
                            // 删除已完成的任务
                            for (DownloadInfo item : downloadInfos) {
                                if (item.programId == info.programId
                                        && item.subProgramId == info.subProgramId) {
                                    downloadInfos.remove(item);
                                    cachingAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                            break;
                        case DownloadInfoState.DOWNLOADING:
                        case DownloadInfoState.ERROR:
                            changeInfoState(info);
                            break;

                        default:
                            break;
                    }
                }
            } else {
                if (intent.getAction().equals(
                        DownloadService.NOTIFICATION_ID + "_"
                                + DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    Log.i("CachingFragment", "receive broadcast "
                            + DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                }
                refreshData();
            }

        }

        private void changeInfoState(DownloadInfo info) {
            for (DownloadInfo item : downloadInfos) {
                if (item.programId == info.programId
                        && item.subProgramId == info.subProgramId) {
                    item.state = info.state;
                    cachingAdapter.notifyDataSetChanged();
                }
            }
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
        ArrayList<DownloadInfo> allDownloadingInfos = AccessDownload
                .getInstance(getContext()).queryDownloadInfo();
        downloadInfos.addAll(allDownloadingInfos);

        if (downloadInfos != null && downloadInfos.size() > 0) {
            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_CACHE_SHOW_EDIT, new ShowEditButtonEvent(1, true));
            rl_msg_null.setVisibility(View.GONE);
            rl_caching_full.setVisibility(View.VISIBLE);
            listView.setVisibility(View.VISIBLE);
            if (cachingAdapter == null) {
                cachingAdapter = new CachingAdapter(getContext(), downloadInfos);
                listView.setAdapter(cachingAdapter);
                BusProvider.getInstance().register(cachingAdapter);
            } else {
                cachingAdapter.notifyDataSetChanged();
            }
        } else {
            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_CACHE_SHOW_EDIT, new ShowEditButtonEvent(1, false));
            rl_msg_null.setVisibility(View.VISIBLE);
            rl_caching_full.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.GONE);
        }

    }

    private void startRemainTask() {

        ConnectivityManager connMgr = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
        if (null != netInfo
                && ConnectivityManager.TYPE_MOBILE == netInfo.getType()) {
            Object tmp = BaseApp.getACache().getAsObject("CLICKNUM_ALLOWCACHE");
            if (tmp != null && ((boolean) tmp)) {
                Log.i("OfflineCacheUtil", "2G/3G/4G网络已连接，继续下载");
                Toast.makeText(getContext(), R.string.mobile_download, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), R.string.mobile_download_forbid, Toast.LENGTH_LONG).show();
                return;
            }
        }

        ActivityManager am = (ActivityManager) getActivity().getSystemService(
                Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> temps = am.getRunningServices(30);
        int size = temps.size();
        if (size > 0) {
            boolean onGoing = false;
            for (int i = 0; i < size; i++) {
                if (temps.get(i).service.getClassName().equals(
                        DownloadService.class.getName())
                        && temps.get(i).service.getPackageName().equals(
                        getActivity().getPackageName())) {
                    onGoing = true;
                    break;
                }
            }
            Intent intent = new Intent(getActivity(), DownloadService.class);
            if (!onGoing) {
                intent.putExtra(DownloadService.APPNAME_KEY,
                        getString(R.string.app_name));
                intent.putExtra(DownloadService.APP_EN_NAME_KEY, "tvfanphone");
            }
            getActivity().startService(intent);
        }
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
                BusProvider.getInstance().post(AppGlobalConsts.EventType.CHANGE_SELECT_ALL_STATE, new CacheDeleteSeleteEvent(downloadInfosWaitDelete.size(), true));
            }
        } else {
            if (downloadInfo.state == DownloadInfoState.DOWNLOADING) {
                if ((System.currentTimeMillis() - lastClicktime) < 1000) {
                    return;
                }
                downloadInfo.state = DownloadInfoState.PAUSE;
                AccessDownload.getInstance(getContext()).updateDownloadState(
                        downloadInfo);
                action = DownloadService.ACTION_PAUSE;
                startServiceFlag = true;
            } else if (downloadInfo.state == DownloadInfoState.WAITTING) {
                downloadInfo.state = DownloadInfoState.PAUSE;
                AccessDownload.getInstance(getContext()).updateDownloadState(
                        downloadInfo);
            } else {

                // 处理移动网络
                ConnectivityManager connMgr = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
                if (netInfo == null) {
                    Toast.makeText(getContext(), "网络已断开", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        Object tmp = ACache.get(getContext()).getAsObject("CLICKNUM_ALLOWCACHE");
                        if (tmp != null && ((boolean) tmp)) {
                            Toast.makeText(getContext(), com.sumavision.R.string.mobile_download, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), com.sumavision.R.string.mobile_download_forbid, Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }

                if (AccessDownload.getInstance(getContext())
                        .isDownloadingExecute()) {
                    downloadInfo.state = DownloadInfoState.WAITTING;
                } else {
                    if ((System.currentTimeMillis() - lastClicktime) < 1000) {
                        return;
                    }
                    downloadInfo.state = DownloadInfoState.DOWNLOADING;
                    action = DownloadService.ACTION_DOWNLOAD_NEW_TASK;
                    startServiceFlag = true;
                }
                AccessDownload.getInstance(getContext()).updateDownloadState(
                        downloadInfo);
            }

            if (startServiceFlag) {
                startDownloadService(downloadInfo, action);
            }

        }
        lastClicktime = System.currentTimeMillis();
        cachingAdapter.notifyDataSetChanged();
    }

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

    private void operateAllItem(boolean select) {
        if (downloadInfos != null && downloadInfos.size() > 0) {
            for (int i = 0; i < downloadInfos.size(); i++) {
                downloadInfos.get(i).pendingState = select ? 1 : 0;
            }
            downloadInfosWaitDelete.clear();
            cachingAdapter.notifyDataSetChanged();
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
        cachingAdapter.notifyDataSetChanged();
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
            handler.sendEmptyMessage(AFTER_DELETE_ALL);
        }
    };

    public void showEditButton() {
        if (downloadInfos.size() > 0) {
            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_CACHE_SHOW_EDIT, new ShowEditButtonEvent(1, true));
        } else {
            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_CACHE_SHOW_EDIT, new ShowEditButtonEvent(1, false));
        }
    }
}
