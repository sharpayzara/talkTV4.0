package com.sumavision.talktv2.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.ShareElement;
import com.sumavision.talktv2.http.GlideProxy;
import com.sumavision.talktv2.model.entity.MediaDetail;
import com.sumavision.talktv2.ui.activity.MediaDetailActivity;
import com.sumavision.talktv2.util.CommonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sharpay on 16-6-16.
 */
public class MediaItemCommonAdapter extends RecyclerView.Adapter<MediaItemCommonAdapter.CommonItemHolder> {
    private Activity mActivity;
    private List<MediaDetail.RelatedBean> list;
    private HashMap<Integer, CommonItemHolder> holders;

    public MediaItemCommonAdapter(Activity mActivity) {
        this.mActivity = mActivity;
        list = new ArrayList<>();
        holders = new HashMap<>();
    }

    public void setList(List<MediaDetail.RelatedBean> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public CommonItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media_item_common, parent, false);
        return new CommonItemHolder(view);
    }

    @Override
    public void onBindViewHolder(CommonItemHolder holder, int position) {
        holder.bean = list.get(position);
        GlideProxy.getInstance().loadHImage(mActivity, list.get(position).getPicurl(),holder.picIv);
        holder.nameTv.setText(list.get(position).getName());
        holder.durationTv.setText(""+ CommonUtil.getNewDataString(list.get(position).getDuration()));
        holder.playCountTv.setText(list.get(position).getPlayCount()+"次");
        holders.put(position, holder);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CommonItemHolder extends RecyclerView.ViewHolder {
        MediaDetail.RelatedBean bean;
        @BindView(R.id.pic_iv)
        ImageView picIv;
        @BindView(R.id.img_rlt)
        RelativeLayout imgRlt;
        @BindView(R.id.name_tv)
        TextView nameTv;
        @BindView(R.id.duration_tv)
        TextView durationTv;
        @BindView(R.id.play_count_tv)
        TextView playCountTv;

        @OnClick(R.id.item_rlt)
        void onClick(){
            Intent intent = new Intent(mActivity, MediaDetailActivity.class);
            intent.putExtra("vid",bean.getCode());
            intent.putExtra("sdkType",bean.getSdkType());
            intent.putExtra("videoType",bean.getVideoType());
            mActivity.startActivity(intent);
        }

        public void setColor () {
            nameTv.setTextColor(Color.RED);
        }

        public void revent () {
            nameTv.setTextColor(Color.parseColor("#c4c4c4"));
        }
        public CommonItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void playWhat(int playPos) {
        if(ShareElement.mediaRecommendLastHolder != null)
            ShareElement.mediaRecommendLastHolder.revent();
        holders.get(playPos).setColor();
        ShareElement.mediaRecommendLastHolder = holders.get(playPos);
    }
}