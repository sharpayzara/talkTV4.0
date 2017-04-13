package com.sumavision.talktv2.ui.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.ShareElement;
import com.sumavision.talktv2.model.entity.SeriesDetail;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sharpay on 2016/5/26.
 */
public class SelectionsRecyclerAdapter extends RecyclerView.Adapter<SelectionsRecyclerAdapter.MyViewHolder> {

    private int src;
    ArrayList<SeriesDetail.SourceBean> list;
    ArrayList<MyViewHolder> holders;
    private int playPos = -1;
    private int pageNum ;
    private int cpState = 1;
    public SelectionsRecyclerAdapter(int src, ArrayList<SeriesDetail.SourceBean> list){
        this.src = src;
        this.list = list;
        holders = new ArrayList<>();
    }

    public void setCpState(int cpState) {
        this.cpState = cpState;
    }

    public void setList(List<SeriesDetail.SourceBean> list){
        this.list.clear();
        this.list.addAll(list);
    }

    public void playNext(int playPos) {
//        holders.get(this.playPos).setCache(this.playPos);
        this.playPos = playPos;
        holders.get(playPos).selections();
    }

    public boolean isNextPage(int pos) {
        return pos>=list.size();
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
    public void setPlayPos (int playPos) {
        this.playPos = playPos;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(src, parent, false);
        MyViewHolder holder = new MyViewHolder(itemView);
        holders.add(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.selectionsText.setText(list.get(position).getEpi());
        holder.bean = list.get(position);
        holder.postion = position;
        if(playPos == position && cpState == 1) {
            holder.itemBg.setVisibility(View.VISIBLE);
            ShareElement.lastHolder = holder;
        }
        if (list.get(position).isCached()) {
            if (!TextUtils.isEmpty(list.get(position).getLocalPath())) {
                holder.cachingImg.setVisibility(View.GONE);
                holder.cachedImg.setVisibility(View.VISIBLE);
            } else {
                holder.cachingImg.setVisibility(View.VISIBLE);
                holder.cachedImg.setVisibility(View.GONE);
            }
        } else {
            holder.cachedImg.setVisibility(View.GONE);
            holder.cachingImg.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        SeriesDetail.SourceBean bean;
        int postion;
        @BindView(R.id.item_text)
        public TextView selectionsText;

        @BindView(R.id.item_bg)
        public ImageView itemBg;

        @BindView(R.id.tv_cached_img)
        ImageView cachedImg;

        @BindView(R.id.tv_caching_img)
        ImageView cachingImg;

        @OnClick(R.id.item_rlt)
        public void selections(){
//            if(ShareElement.isIgnoreNetChange == 1)
//                return;
            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_A,bean);
            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_A,new int[]{pageNum, postion});
            if(cpState == 2)
                return;
            if(ShareElement.lastHolder!=null && ShareElement.lastHolder==this)
                return;
            if(ShareElement.lastHolder != null){
                ShareElement.lastHolder.revent();
            }
            itemBg.setVisibility(View.VISIBLE);
            PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", 0f,
                    1, 1f);
            PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", 0f,
                    1, 1f);
            ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(itemBg, pvhX, pvhY).setDuration(500);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    itemBg.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            anim.start();
            ShareElement.lastHolder = this;
        }
        public void revent(){
            if(cpState == 2)
                return;
            PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", 1f,
                    0, 0f);
            PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", 1f,
                    0, 0f);
            ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(itemBg, pvhX, pvhY).setDuration(500);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    itemBg.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            anim.start();
        }

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        /**
         * 恢复缓存标记
         */
        public void setCache(int position) {
            if (list.get(position).isCached()) {
                if (!TextUtils.isEmpty(list.get(position).getLocalPath())) {
                   cachingImg.setVisibility(View.GONE);
                    cachedImg.setVisibility(View.VISIBLE);
                } else {
                    cachingImg.setVisibility(View.VISIBLE);
                    cachedImg.setVisibility(View.GONE);
                }
            } else {
                cachedImg.setVisibility(View.GONE);
                cachingImg.setVisibility(View.GONE);
            }
        }
    }
}
