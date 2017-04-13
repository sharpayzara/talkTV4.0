package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.http.GlideProxy;
import com.sumavision.talktv2.model.entity.ProgramListData;
import com.sumavision.talktv2.ui.activity.ProgramDetailActivity;
import com.sumavision.talktv2.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sharpay on 2016/5/26.
 */
public class ProgramListRecyclerAdapter extends RecyclerView.Adapter<ProgramListRecyclerAdapter.MyViewHolder> {

    private Context mContext;
    String currentType;
    String style = "ver";
    List<ProgramListData.ItemsBean> list;
    public ProgramListRecyclerAdapter(Context mContext,String currentType){
        this.list = new ArrayList<ProgramListData.ItemsBean>();
        this.mContext = mContext;
        this.currentType = currentType;
    }
    public void setStyle(String style){
        this.style = style;
    }

    public void setList(List<ProgramListData.ItemsBean> list){
        if(list == null){
            this.list.clear();
            return;
        }
        this.list.addAll(list);
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if(!style.equals("list")){
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_program_list_movie, parent, false);
            ViewGroup.LayoutParams params = itemView.getLayoutParams();
            if(style.equals("ver")){
                params.height = CommonUtil.screenWidth(mContext)/3*160/120 + CommonUtil.dip2px(mContext,34);
            }else{
                params.height = CommonUtil.screenWidth(mContext)/2*110/180+ CommonUtil.dip2px(mContext,34);
            }
            itemView.setLayoutParams(params);
        }else{
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_program_list_news, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.itemName.setText(list.get(position).getName());
        holder.bean = list.get(position);
        if(!TextUtils.isEmpty(list.get(position).getPrompt())){
            holder.remarkTv.setVisibility(View.VISIBLE);
            holder.remarkTv.setText(list.get(position).getPrompt());
        }
        if(style.equals("ver")){
            GlideProxy.getInstance().loadVImage(mContext, list.get(position).getPicurl(),holder.itemImg);
        }else if(style.equals("list")){
            GlideProxy.getInstance().loadHImage(mContext, list.get(position).getPicurl(),holder.itemImg);
        }else{
            GlideProxy.getInstance().loadHImage(mContext, list.get(position).getPicurl(),holder.itemImg);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ProgramListData.ItemsBean bean;
        @BindView(R.id.remark_tv)
        public TextView remarkTv;
        @BindView(R.id.item_name)
        public TextView itemName;
        @BindView(R.id.item_img)
        public ImageView itemImg;

        @OnClick(R.id.item_rlt)
        public void selections(){
                Intent intent = new Intent(mContext, ProgramDetailActivity.class);
                intent.putExtra("idStr",bean.getId());
                intent.putExtra("cpidStr","");
                mContext.startActivity(intent);
        }

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
