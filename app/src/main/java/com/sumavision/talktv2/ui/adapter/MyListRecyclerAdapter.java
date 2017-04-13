package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.dao.MyItemTouchCallback;
import com.sumavision.talktv2.model.entity.HomeRecommend;

import java.util.Collections;
import java.util.List;

/**
 * Created by zhoutao on 2016/5/26.
 */
public class MyListRecyclerAdapter extends RecyclerView.Adapter<MyListRecyclerAdapter.MyViewHolder> implements MyItemTouchCallback.ItemTouchAdapter {

    private Context context;
    private int src;
    private List<HomeRecommend> results;

    public MyListRecyclerAdapter(int src, List<HomeRecommend> results){
        this.results = results;
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
//        holder.imageView.setImageResource(src);
//        holder.imageView.setImageResource(src);
        if (position<3){
            holder.iv_fixcard.setVisibility(View.INVISIBLE);
        }else{

            holder.iv_fixcard.setVisibility(View.VISIBLE);
        }
        holder.tv_fixcard.setText(results.get(position).card_name);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    @Override
    public void onMove(int fromPosition, int toPosition) {
        if (toPosition >2){
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(results, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(results, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
        }
        }

    @Override
    public void onSwiped(int position) {
        results.remove(position);
        notifyItemRemoved(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView tv_fixcard;
        public ImageView iv_fixcard;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_fixcard = (TextView) itemView.findViewById(R.id.tv_fixcard);
            iv_fixcard = (ImageView) itemView.findViewById(R.id.iv_fixcard);
        }
    }
}
