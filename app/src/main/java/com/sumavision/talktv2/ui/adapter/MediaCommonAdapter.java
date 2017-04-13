package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.http.GlideProxy;
import com.sumavision.talktv2.model.entity.MediaList;
import com.sumavision.talktv2.ui.activity.MediaDetailActivity;
import com.sumavision.talktv2.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sharpay on 16-6-16.
 */
public class MediaCommonAdapter extends RecyclerView.Adapter<MediaCommonAdapter.RecommandHolder> {

    Context context;
    MediaItemCommonAdapter adapter;
    List<MediaList.ItemsBean> list;
    public MediaCommonAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    public List<MediaList.ItemsBean> getList() {
        return list;
    }

    public void setList(List<MediaList.ItemsBean> list) {
        this.list.addAll(list);
    }

    @Override
    public RecommandHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media_item_common, parent, false);
        RecommandHolder holder = new RecommandHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecommandHolder holder, int position) {
        GlideProxy.getInstance().loadHImage(context, list.get(position).getPicUrl(),holder.picIv);
        holder.nameTv.setText(list.get(position).getName());
        holder.durationTv.setText(CommonUtil.getNewDataString(list.get(position).getDuration())+"");
        holder.playCountTv.setText(list.get(position).getPlayCount()+"次");
        holder.bean = list.get(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class RecommandHolder extends RecyclerView.ViewHolder {
        MediaList.ItemsBean bean;
        @BindView(R.id.pic_iv)
        ImageView picIv;
        @BindView(R.id.name_tv)
        TextView nameTv;
        @BindView(R.id.duration_tv)
        TextView durationTv;
        @BindView(R.id.play_count_tv)
        TextView playCountTv;
        @BindView(R.id.item_rlt)
        RelativeLayout itemRlt;
        @OnClick(R.id.item_rlt)
        void onClick(){
            Intent intent = new Intent(context, MediaDetailActivity.class);
            intent.putExtra("vid",bean.getCode());
            intent.putExtra("videoType",bean.getVideoType());
            intent.putExtra("sdkType",bean.getSdkType());
            context.startActivity(intent);
        }
        public RecommandHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}