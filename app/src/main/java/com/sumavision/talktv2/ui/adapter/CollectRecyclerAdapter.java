package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.http.GlideProxy;
import com.sumavision.talktv2.model.entity.CollectBean;
import com.sumavision.talktv2.model.entity.VideoType;
import com.sumavision.talktv2.ui.activity.MediaDetailActivity;
import com.sumavision.talktv2.ui.activity.ProgramDetailActivity;
import com.sumavision.talktv2.ui.activity.SpecialActivity;
import com.sumavision.talktv2.ui.activity.SpecialDetailActivity;
import com.sumavision.talktv2.ui.widget.MyLinearLayout;
import com.sumavision.talktv2.ui.widget.WidthDropAnimator;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.CommonUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sharpay on 2016/5/26.
 */
public class CollectRecyclerAdapter extends RecyclerView.Adapter<CollectRecyclerAdapter.MyViewHolder> {

    private Context mContext;
    private List<CollectBean> list;

    public CollectRecyclerAdapter(Context mContext, List<CollectBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_collect, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bean = list.get(position);
        if (!TextUtils.isEmpty(list.get(position).getIsdownload()) && list.get(position).getIsdownload().equals("1")) {
            holder.cacheIv.setVisibility(View.VISIBLE);
        } else {
            holder.cacheIv.setVisibility(View.GONE);
        }
        holder.nameTv.setText(list.get(position).getName());
        holder.actorTv.setText(list.get(position).getActor());
        if (list.get(position).getVideoType() == VideoType.movie) {
            holder.scoreTv.setText(list.get(position).getScore());
        } else if(list.get(position).getVideoType() == VideoType.media){
            holder.scoreTv.setText(list.get(position).getPlayCount());
        }else {
            holder.scoreTv.setText(list.get(position).getNewestSelection());
        }
        GlideProxy.getInstance().loadHImage(mContext,list.get(position).getPicurl(),holder.picIv);
        if (list.get(position).isOpened && holder.checkBox.getWidth() == 0) {
            new WidthDropAnimator().animateOpen(holder.checkboxRlt, CommonUtil.dip2px(mContext, 30));
        } else if (!list.get(position).isOpened && holder.checkBox.getWidth() > 0) {
            new WidthDropAnimator().animateClose(holder.checkboxRlt);
        }
        if (list.get(position).isChecked) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CollectBean bean;
        @BindView(R.id.checkbox)
        CheckBox checkBox;
        @BindView(R.id.img_rlt)
        RelativeLayout imgRlt;
        @BindView(R.id.pic_iv)
        ImageView picIv;
        @BindView(R.id.name_tv)
        TextView nameTv;
        @BindView(R.id.score_tv)
        TextView scoreTv;
        @BindView(R.id.actor_tv)
        TextView actorTv;
        @BindView(R.id.cache_iv)
        ImageView cacheIv;
        @BindView(R.id.checkbox_rlt)
        MyLinearLayout checkboxRlt;

        @OnClick({R.id.checkbox_rlt, R.id.collect_rlt})
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.checkbox_rlt:
                    if (checkBox.isChecked()) {
                        checkBox.setChecked(false);
                        bean.isChecked = false;
                    } else {
                        checkBox.setChecked(true);
                        bean.isChecked = true;
                    }
                    BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_C, "msg");
                    break;
                case R.id.collect_rlt:
                    if(bean.getVideoType() == VideoType.media){
                        Intent intent = new Intent(mContext, MediaDetailActivity.class);
                        intent.putExtra("vid", bean.getVid());
                        intent.putExtra("sdkType", bean.getSdkType());
                        intent.putExtra("videoType", bean.getVideoType());
                        mContext.startActivity(intent);
                    }else if(bean.getVideoType() == VideoType.special){
                        Intent intent = new Intent();
                        intent.putExtra("idStr",bean.getSid());
                        intent.setClass(mContext, SpecialDetailActivity.class);
                        mContext.startActivity(intent);
                    }else if(bean.getVideoType() == VideoType.speicalLong){
                        Intent intent = new Intent();
                        intent.putExtra("idStr",bean.getSid());
                        intent.setClass(mContext, SpecialActivity.class);
                        mContext.startActivity(intent);
                    }else{
                        Intent intent = new Intent(mContext, ProgramDetailActivity.class);
                        intent.putExtra("currentType", bean.getCurrType());
                        intent.putExtra("idStr", bean.getSid());
                        intent.putExtra("cpidStr", bean.getCpid());
                        mContext.startActivity(intent);
                    }
                    break;
            }
        }

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
