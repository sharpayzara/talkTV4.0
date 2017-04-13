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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sharpay on 16-6-16.
 */
public class ArtsCacheAdapter extends RecyclerView.Adapter<ArtsCacheAdapter.SelectionsViewHolder> {


    private Context mContext;
    private List<SeriesDetail.SourceBean> seriesList;

    public ArtsCacheAdapter(Context mContext) {
        this.mContext = mContext;
        seriesList = new ArrayList<>();
    }

    @Override
    public SelectionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SelectionsViewHolder holder = new SelectionsViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_arts_cache, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(SelectionsViewHolder holder, int position) {
        holder.itemCount.setText(seriesList.get(position).getCount());
        holder.itemName.setText(seriesList.get(position).getEpi() +"  "+ seriesList.get(position).getName());
        if (seriesList.get(position).isCached()) {
            if (!TextUtils.isEmpty(seriesList.get(position).getLocalPath())) {
                holder.cachedIv.setVisibility(View.VISIBLE);
                holder.cachingIv.setVisibility(View.INVISIBLE);
                holder.cachedTv.setVisibility(View.VISIBLE);
                holder.cachingTv.setVisibility(View.INVISIBLE);
            } else {
                holder.cachedIv.setVisibility(View.INVISIBLE);
                holder.cachingIv.setVisibility(View.VISIBLE);
                holder.cachedTv.setVisibility(View.INVISIBLE);
                holder.cachingTv.setVisibility(View.VISIBLE);
            }
        } else {
            holder.cachedIv.setVisibility(View.INVISIBLE);
            holder.cachingIv.setVisibility(View.INVISIBLE);
            holder.cachedTv.setVisibility(View.INVISIBLE);
            holder.cachingTv.setVisibility(View.INVISIBLE);
        }
        holder.setBean(seriesList.get(position));
        if (holder.bean.isdownload()) {
            holder.selectedIv.setVisibility(View.VISIBLE);
        } else {
            holder.selectedIv.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return seriesList.size();
    }

    public class SelectionsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_name)
        TextView itemName;
        @BindView(R.id.item_count)
        TextView itemCount;
        @BindView(R.id.selected_iv)
        ImageView selectedIv;

        @BindView(R.id.art_cached_iv_in_cache_popup)
        ImageView cachedIv;
        @BindView(R.id.art_cached_tv_in_cache_popup)
        TextView cachedTv;

        @BindView(R.id.art_caching_iv_in_cache_popup)
        ImageView cachingIv;
        @BindView(R.id.art_caching_tv_in_cache_popup)
        TextView cachingTv;

//        private boolean isSelect;
        private SeriesDetail.SourceBean bean;
        @OnClick(R.id.item_rlt)
        void click(){
            if (bean.isCached()) {
                Toast.makeText(mContext, R.string.cached_tip, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!bean.isdownload()) {
                selectedIv.setVisibility(View.VISIBLE);
//                isSelect = true;
                bean.setIsdownload(true);
                BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_CACHE, bean);
            } else {
                selectedIv.setVisibility(View.GONE);
//                isSelect = false;
                bean.setIsdownload(false);
                BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_CACHE_CANCEL, bean);
            }
        }
        public SelectionsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
        public void setBean(SeriesDetail.SourceBean bean){
            this.bean = bean;
        }
    }

    public void setSeriesData(List<SeriesDetail.SourceBean> list){
        seriesList.addAll(list);
    }

    public void clearSeriesData () {
        seriesList.clear();
    }
}
