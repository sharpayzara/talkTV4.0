package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.ShareElement;
import com.sumavision.talktv2.model.entity.ArtsChangeProgramMsg;
import com.sumavision.talktv2.model.entity.SeriesDetail;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sharpay on 16-6-16.
 */
public class ArtsSelectionsAdapter extends RecyclerView.Adapter<ArtsSelectionsAdapter.SelectionsViewHolder> {


    private Context mContext;
    private List<SeriesDetail.SourceBean> seriesList;
    private HashMap<Integer, SelectionsViewHolder> holders;
    private int pos;
    private int cpState = 1;

    public ArtsSelectionsAdapter(Context mContext) {
        this.mContext = mContext;
        seriesList = new ArrayList<>();
        holders = new HashMap<>();
    }

    public void setCpState(int cpState) {
        this.cpState = cpState;
    }

    @Override
    public SelectionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SelectionsViewHolder holder = new SelectionsViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_arts_selections2, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(SelectionsViewHolder holder, int position) {
        holder.itemCount.setText(seriesList.get(position).getEpi() );
        holder.itemName.setText(seriesList.get(position).getName());
        holder.bean = seriesList.get(position);
        holder.position = position;
        if(holders.get(position)==null)
            holders.put(position, holder);

        if(position==pos && cpState == 1) {
            holder.itemName.setTextColor(Color.RED);
            ShareElement.artsPopuHolder = holder;
        }
        else
            holder.itemName.setTextColor(Color.GRAY);
        if (seriesList.get(position).isCached()) {
            if (!TextUtils.isEmpty(seriesList.get(position).getLocalPath())) {
                holder.cachedIv.setVisibility(View.VISIBLE);
                holder.cachingIv.setVisibility(View.GONE);
                holder.cachedTv.setVisibility(View.VISIBLE);
                holder.cachingTv.setVisibility(View.GONE);
            } else {
                holder.cachedIv.setVisibility(View.GONE);
                holder.cachingIv.setVisibility(View.VISIBLE);
                holder.cachedTv.setVisibility(View.GONE);
                holder.cachingTv.setVisibility(View.VISIBLE);
            }
        } else {
            holder.cachedIv.setVisibility(View.GONE);
            holder.cachingIv.setVisibility(View.GONE);
            holder.cachedTv.setVisibility(View.GONE);
            holder.cachingTv.setVisibility(View.GONE);
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

        @BindView(R.id.art_cached_iv_in_select_popup)
        ImageView cachedIv;
        @BindView(R.id.art_caching_iv_in_select_popup)
        ImageView cachingIv;
        @BindView(R.id.art_cached_tv_in_select_popup)
        TextView cachedTv;
        @BindView(R.id.art_caching_tv_in_select_popup)
        TextView cachingTv;

        private SeriesDetail.SourceBean bean;
        private int position;

        public SelectionsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        @OnClick(R.id.arts_selection)
        public void selections(){
//            if(ShareElement.isIgnoreNetChange == 1)
//                return;
            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_A,bean);
            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_A,new int[]{0,position});
            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_A,new ArtsChangeProgramMsg(2, position));
            if(cpState == 2)
                return;
            if(position == pos)
                return;
            changeView();
        }

        public void changeView(){
            if(ShareElement.artsPopuHolder != null) {
                ShareElement.artsPopuHolder.revent();
            }
            itemName.setTextColor(Color.RED);
            ShareElement.artsPopuHolder = this;
        }

        public void revent () {
            itemName.setTextColor(Color.GRAY);
        }
    }

    public void  setSeriesData(List<SeriesDetail.SourceBean> list, int pos){
        this.pos = pos;
        seriesList.clear();
        seriesList.addAll(list);
    }

    public void clearHolders(){
        holders.clear();
    }

    public void changeProgram (int pos) {
        /*if(ShareElement.artsPopuHolder!=null&&ShareElement.artsPopuHolder == holders.get(pos))
            return;
        if(holders.get(pos) != null)
            holders.get(pos).changeView();
        else {
            this.pos = pos;
        }*/
        this.pos = pos;
    }
}
