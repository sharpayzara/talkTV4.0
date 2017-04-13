package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.ShareElement;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhoutao on 2016/5/26.
 */
public class MediaSelectionsRecyclerAdapter extends RecyclerView.Adapter<MediaSelectionsRecyclerAdapter.MyViewHolder> {

    private Context context;
    private int src;
    public MediaSelectionsRecyclerAdapter(int src){
        this.src = src;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(src, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.item_media_title)
        public TextView itemMediaTitle;

        @OnClick(R.id.item_rlt)
        public void selections(){
            if(ShareElement.mediaLastHolder != null){
                ShareElement.mediaLastHolder.revent();
            }
            itemMediaTitle.setTextColor(context.getResources().getColor(R.color.red));

            ShareElement.mediaLastHolder = this;
        }
        public void revent(){
            itemMediaTitle.setTextColor(context.getResources().getColor(R.color.media_title_color));
        }
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
