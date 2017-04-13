package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.http.GlideProxy;
import com.sumavision.talktv2.model.entity.YsqBean;
import com.sumavision.talktv2.ui.activity.ProgramDetailActivity;
import com.sumavision.talktv2.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by sharpay on 16-6-16.
 */
public class YsqItemAdapter extends RecyclerView.Adapter<YsqItemAdapter.CommonItemHolder> {
    private Context mContext;
    private List<YsqBean.ItemsBean> list;

    public  void setList(List<YsqBean.ItemsBean> list){
        if(list == null){
            this.list.clear();
        }else{
            this.list.addAll(list);
        }
    }
    public YsqItemAdapter(Context mContext) {
        this.mContext = mContext;
        list = new ArrayList<>();
    }

    @Override
    public CommonItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ysq, parent, false);
        return new CommonItemHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommonItemHolder holder, int position) {
        holder.bean = list.get(position);
        holder.descTv.setText(list.get(position).getIntro());
        holder.introTv.setText(list.get(position).getIntro());
        holder.nameTv.setText(list.get(position).getName());
        holder.titleTv.setText(list.get(position).getTitle());
        holder.nameTv2.setText("《"+list.get(position).getName()+"》");
        holder.introTv.post(new Runnable() {
            @Override
            public void run() {
                if(holder.introTv.getLineCount() > 4){
                    holder.expand.setVisibility(View.VISIBLE);
                }else{
                    holder.expand.setVisibility(View.GONE);
                }
            }
        });

        GlideProxy.getInstance().loadHImage(mContext, list.get(position).getPoster(),holder.posterIb);
        GlideProxy.getInstance().loadHImage(mContext, list.get(position).getPicUrl(),holder.picIv);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CommonItemHolder extends RecyclerView.ViewHolder {
        YsqBean.ItemsBean bean;
        @BindView(R.id.title_tv)
        TextView titleTv;
        @BindView(R.id.recommend_rlt)
        RelativeLayout recommendRlt;
        @BindView(R.id.intro_tv)
        TextView introTv;
        @BindView(R.id.poster_ib)
        ImageButton posterIb;
        @BindView(R.id.pic_iv)
        ImageView picIv;
        @BindView(R.id.name_tv)
        TextView nameTv;
        @BindView(R.id.desc_tv)
        TextView descTv;
        @BindView(R.id.item_llt)
        LinearLayout itemLlt;
        @BindView(R.id.name_tv2)
        TextView nameTv2;
        @BindView(R.id.expand)
        CheckBox expand;
        @OnCheckedChanged(R.id.expand)
        void checkChanged(CompoundButton button, boolean bool){
            if(!bool){
                expand.setText("展开");
                introTv.setMaxLines(4);
            }else{
                expand.setText("收起");
                introTv.setMaxLines(20);
            }
        }
        @OnClick(R.id.item_llt)
        void onClick(){
            Intent intent = new Intent(mContext, ProgramDetailActivity.class);
            intent.putExtra("currentType","电影");
            intent.putExtra("idStr",bean.getId());
            intent.putExtra("cpidStr","");
            mContext.startActivity(intent);
        }
        public CommonItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ViewGroup.LayoutParams params4 = posterIb.getLayoutParams();
            params4.height = CommonUtil.screenWidth(mContext)*110/180 - CommonUtil.dip2px(mContext,40);
            posterIb.setLayoutParams(params4);
        }
    }
}