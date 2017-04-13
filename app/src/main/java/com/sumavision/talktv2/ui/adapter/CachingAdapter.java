package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfoState;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.http.GlideProxy;
import com.sumavision.talktv2.util.AppGlobalConsts;

import java.util.List;

/**
 * Created by zhoutao on 2016/5/26.
 */
public class CachingAdapter extends BaseAdapter {

    private static final String TAG = "CachingAdapter";
    private Context context;
    private List<DownloadInfo> results;
    private boolean pausestate;

    public CachingAdapter(Context context, List<DownloadInfo> results) {
        this.results = results;
        this.context = context;
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(AppGlobalConsts.EventType.TAG_C)})
    public void showEditLayout2(String refreshEdit) {
        AppGlobalConsts.ISEDITSTATE = true;
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(AppGlobalConsts.EventType.TAG_D)})
    public void showbackEditLayout2(String backEdit) {
        AppGlobalConsts.ISEDITSTATE = false;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Object getItem(int position) {
        return results.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_caching, null);
            holder.tv_caching_title = (TextView) convertView.findViewById(R.id.tv_caching_title);
            holder.tv_caching_size = (TextView) convertView.findViewById(R.id.tv_caching_size);
            holder.iv_caching_img = (ImageView) convertView.findViewById(R.id.iv_caching_img);
            holder.iv_caching_imgtop = (ImageView) convertView.findViewById(R.id.iv_caching_imgtop);
            holder.rl_cached_imgselect = (RelativeLayout) convertView.findViewById(R.id.rl_cached_imgselect);
            holder.pb_caching = (ProgressBar) convertView.findViewById(R.id.pb_caching);
            holder.cb_cached_imgselect = (CheckBox) convertView.findViewById(R.id.cb_cached_imgselect);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final DownloadInfo info = results.get(position);
        if (info.pendingState == 1) {
            Log.d(TAG, "cachingAdapter-->>" + info.programId
                    + info.subProgramId + "pendingState:"
                    + info.pendingState);
        }

        String name = info.programName;
        if (!TextUtils.isEmpty(name)) {
            holder.tv_caching_title.setText(results.get(position).programName);
        }

        int progress = info.progress;
        String stringProgress = String.valueOf(progress);
        if (info.state != DownloadInfoState.DOWNLOADED) {
            holder.tv_caching_size.setText("下载进度：" + stringProgress + "%");
            holder.pb_caching.setProgress(Integer.parseInt(stringProgress));
            holder.pb_caching.setVisibility(View.VISIBLE);
        } else {
            holder.pb_caching.setVisibility(View.GONE);
            holder.tv_caching_size.setVisibility(View.GONE);
        }
        GlideProxy.getInstance().loadHImage(context,results.get(position).programPic,holder.iv_caching_img);

        if (info.state == DownloadInfoState.DOWNLOADING) {
            holder.iv_caching_imgtop
                    .setImageResource(R.mipmap.download_mask_icon);
        } else if (info.state == DownloadInfoState.WAITTING
                || info.state == DownloadInfoState.DOWNLOADING_FOR_NETWORK) {
            holder.iv_caching_imgtop
                    .setImageResource(R.mipmap.waiting_mask_icon);
        } else if (info.state == DownloadInfoState.PAUSE) {
            holder.iv_caching_imgtop
                    .setImageResource(R.mipmap.pause_mask_icon);
        } else if (info.state == DownloadInfoState.ERROR) {
            holder.iv_caching_imgtop
                    .setImageResource(R.mipmap.fail_mask_icon);
        } else if (info.state == DownloadInfoState.PARSE_ERROR) {
        }

        if (AppGlobalConsts.ISEDITSTATE) {
            holder.rl_cached_imgselect.setVisibility(View.VISIBLE);
        } else {
            holder.rl_cached_imgselect.setVisibility(View.GONE);
        }
        // 根据isSelected来设置checkbox的选中状况
        holder.cb_cached_imgselect.setChecked(results.get(position).pendingState == 1 ? true : false);
        return convertView;
    }

    public class ViewHolder {

        public TextView tv_caching_title;
        public TextView tv_caching_size;
        public ImageView iv_caching_img;
        public ImageView iv_caching_imgtop;
        public CheckBox cb_cached_imgselect;
        public RelativeLayout rl_cached_imgselect;
        public ProgressBar pb_caching;

    }
}
