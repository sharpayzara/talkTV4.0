package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.SeriesDetail;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhoutao on 2016/5/26.
 */
public class GridCacheRecyclerAdapter extends RecyclerView.Adapter<GridCacheRecyclerAdapter.MyViewHolder> {

    private Context context;
    private int src;
    List<SeriesDetail.SourceBean> list;
    public GridCacheRecyclerAdapter(int src, List<SeriesDetail.SourceBean> list){
        this.src = src;
        this.list = list;
    }

    public void setList(List<SeriesDetail.SourceBean> list){
        this.list.addAll(list);
    }
    public List<SeriesDetail.SourceBean> getList(){
        return list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(src, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.itemText.setText(list.get(position).getEpi());
        holder.setBean(list.get(position));
        if(list.get(position).isCached()){
            holder.selectedIv.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(list.get(position).getLocalPath())) {
                holder.cachedIv.setVisibility(View.VISIBLE);
                holder.downloadingIv.setVisibility(View.GONE);
            } else {
                holder.cachedIv.setVisibility(View.GONE);
                holder.downloadingIv.setVisibility(View.VISIBLE);
            }
        } else {
            holder.downloadingIv.setVisibility(View.GONE);
            holder.cachedIv.setVisibility(View.GONE);
        }
        if (holder.bean.isdownload()) {
            holder.selectedIv.setVisibility(View.VISIBLE);
        } else {
            holder.selectedIv.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private SeriesDetail.SourceBean bean;
        @BindView(R.id.item_text)
        TextView itemText;
        @BindView(R.id.selected_iv)
        ImageView selectedIv;
        @BindView(R.id.downloading_iv)
        ImageView downloadingIv;
        @BindView(R.id.tv_cached_img_in_popupwindow)
        ImageView cachedIv;
//        private boolean isSelect;

        @OnClick(R.id.item_rlt)
        public void selections() {
            if (bean.isCached()) {
                Toast.makeText(context, R.string.cached_tip, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!bean.isdownload()) {
                selectedIv.setVisibility(View.VISIBLE);
//                isSelect = true;
                bean.setIsdownload(true);
                BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_CACHE, bean);
            } else {
                selectedIv.setVisibility(View.GONE);
                bean.setIsdownload(false);
//                isSelect = false;
                BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_CACHE_CANCEL, bean);
            }
        }
        public void setBean(SeriesDetail.SourceBean bean){
            this.bean = bean;
        }
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
