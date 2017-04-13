package com.sumavision.talktv2.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.offlinelibrary.entity.InternalExternalPathInfo;
import com.sumavision.offlinelibrary.util.CommonUtils;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.DividerItemDecoration;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.PreferencesUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhangyisu on 2016/10/28.
 */

public class ChangeCachePathDialog extends Dialog {

    @BindView(R.id.change_cache_path_rv)
    RecyclerView recyclerView;
    ChangePathRecyclerAdapter adapter;
    int curType; // 当前选择的存储类型，默认是手机存储： 0表示手机存储，1表示sd卡
    List<PathData> list;
    Context mContext;

    public ChangeCachePathDialog(Context context) {
        super(context);
    }

    public ChangeCachePathDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
        setContentView(R.layout.change_cache_path_dialog_layout);
        ButterKnife.bind(this);
        InternalExternalPathInfo internalExternalPathInfo = CommonUtils
                .getInternalExternalPath(context);
        list = new ArrayList<PathData>();
        int rate = 1024 * 1024;
        if (!TextUtils.isEmpty(internalExternalPathInfo.emulatedSDcard)) {
            list.add(new PathData(0, getLeftSpaceInfo(internalExternalPathInfo.emulatedSDcard)));
        }
        if (!TextUtils.isEmpty(internalExternalPathInfo.removableSDcard)) {
            list.add(new PathData(1, getLeftSpaceInfo(internalExternalPathInfo.removableSDcard)));
        }
        int curPathType = getCurrentPath(); // 如果选了sd卡，后来拿出来了呢
        list.get(curPathType).selected = true;
        adapter = new ChangePathRecyclerAdapter(getContext(), list);
        recyclerView.addItemDecoration(new DividerItemDecoration(
                mContext, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @NonNull
    public static String getLeftSpaceInfo(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        int rate = 1024 * 1024;
        StatFs statFs = new StatFs(path);
        double blockSize = statFs.getBlockSize();
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        int totalSize = (int) (blockSize * statFs.getBlockCount() / rate);
        String totalStr = totalSize > 1000 ? df.format((totalSize / 1024.0)) + "G" : (totalSize + "M");
        int leftSize = (int) (blockSize * statFs.getAvailableBlocks() / rate);
        String leftStr = leftSize > 1000 ? df.format((leftSize / 1024.0)) + "G" : (leftSize + "M");
        return "剩余空间：" + leftStr + "/" + totalStr;
    }

    protected ChangeCachePathDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    class ChangePathRecyclerAdapter extends RecyclerView.Adapter<MyViewHolder> {

        LayoutInflater mInflater;
        List<PathData> mList;

        public ChangePathRecyclerAdapter(Context context, List<PathData> list) {
            mInflater = LayoutInflater.from(context);
            mList = list;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.change_cache_path_item, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            PathData pathData = mList.get(position);
            holder.position = position;
            if (pathData.pathType == 0) {
                holder.path_type_text.setText("手机存储");
            } else {
                holder.path_type_text.setText("SD卡");
            }
            holder.leftsize_text.setText(pathData.showInfo);
            if (pathData.selected) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.cache_path_type_text)
        TextView path_type_text;
        @BindView(R.id.rl_change_cache_path_item)
        RelativeLayout rl_item;
        @BindView(R.id.change_cache_path_item_leftsize_text)
        TextView leftsize_text;
        @BindView(R.id.change_cache_path_item_checkbox)
        CheckBox checkBox;
        int position;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.rl_change_cache_path_item)
        public void setCheckBox() {
            if (!checkBox.isChecked()) {
                PathData tmp = list.get(position);
//                checkBox.setChecked(true);
                setCurrentPath(tmp.pathType);
                tmp.selected = true;
                list.get(position > 0 ? 0 : 1).selected = false;
                adapter.notifyDataSetChanged();
                BusProvider.getInstance().post(AppGlobalConsts.EventType.CHANGE_PATH, tmp);
            }
            dismiss();
        }
    }

    public class PathData {
        public int pathType; // 0表示手机存储，1表示sd卡
        //        double leftSize;
//        double totalSize;
        public String showInfo;
        boolean selected;

        PathData(int type, String showInfo) {
            pathType = type;
            this.showInfo = showInfo;
//            this.totalSize = totalSize;
        }
    }

    private int getCurrentPath() {
        return PreferencesUtils.getInt(getContext(), null, "cache_path_type");
    }

    private void setCurrentPath(int type) {
        PreferencesUtils.putInt(getContext(), null, "cache_path_type", type);
        Log.i("ChangeCachePathDialog", "type after set:" + type);
    }
}
