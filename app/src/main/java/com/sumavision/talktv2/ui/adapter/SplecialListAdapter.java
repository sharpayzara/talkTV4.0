package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.http.GlideProxy;
import com.sumavision.talktv2.model.entity.SpecialContentList;
import com.sumavision.talktv2.ui.activity.ProgramDetailActivity;
import com.sumavision.talktv2.ui.activity.SpecialDetailActivity;
import com.sumavision.talktv2.util.CommonUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sharpay on 16-6-16.
 */
public class SplecialListAdapter extends RecyclerView.Adapter<SplecialListAdapter.RecommandHolder> {

    Context mContext;
    @BindView(R.id.img_iv)
    ImageView imgIv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.desc_tv)
    TextView descTv;
    List<SpecialContentList.ItemsBean> list;

    public SplecialListAdapter(Context mContext,List<SpecialContentList.ItemsBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public RecommandHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_special_list, parent, false);
        return new RecommandHolder(view);
    }

    @Override
    public void onBindViewHolder(RecommandHolder holder, int position) {
        holder.bean = list.get(position);
        GlideProxy.getInstance().loadHImage(mContext, list.get(position).getPicture(), holder.imgIv);
        holder.descTv.setText(list.get(position).getDesc());
        holder.titleTv.setText(list.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class RecommandHolder extends RecyclerView.ViewHolder {
        SpecialContentList.ItemsBean bean;
        @BindView(R.id.img_iv)
        ImageView imgIv;
        @BindView(R.id.title_tv)
        TextView titleTv;
        @BindView(R.id.desc_tv)
        TextView descTv;

        @OnClick({R.id.img_iv, R.id.desc_tv})
        public void onClick(View view) {
            Intent intent = new Intent(mContext, ProgramDetailActivity.class);
            intent.putExtra("idStr",bean.getId());
            intent.putExtra("cpidStr","");
            mContext.startActivity(intent);
        }

        public RecommandHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ViewGroup.LayoutParams params = imgIv.getLayoutParams();
            params.height = (CommonUtil.screenWidth(mContext) - CommonUtil.dip2px(mContext,30)) * 9 / 16;
            imgIv.setLayoutParams(params);
        }
    }

}