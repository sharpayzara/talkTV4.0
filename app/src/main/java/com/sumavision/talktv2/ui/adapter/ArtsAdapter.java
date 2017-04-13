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
 * Created by sharpay on 16-6-15.
 */
public class ArtsAdapter extends RecyclerView.Adapter<ArtsAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private List<SeriesDetail.SourceBean> seriesList;
    private HashMap<Integer, ViewHolder> holders;
    private int pos;
    private int cpState = 1;

    public ArtsAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.seriesList = new ArrayList<>();
        holders = new HashMap<>();
    }
    public void setList(List<SeriesDetail.SourceBean> list){
        seriesList.clear();
        seriesList.addAll(list);
    }

    public void setCpState(int cpState) {
        this.cpState = cpState;
    }

    public void clearHolders(){
        holders.clear();
    }

    public void setPlayPos(int pos) {
        this.pos = pos;
    }
    @Override
    public int getItemCount() {
        return seriesList.size();
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = mInflater.inflate(R.layout.item_arts_selections,
                viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.itemName.setText(seriesList.get(position).getName());
        viewHolder.itemCount.setText(seriesList.get(position).getEpi());
        viewHolder.bean = seriesList.get(position);
        viewHolder.postion = position;
        if(holders.get(position)==null)
            holders.put(position, viewHolder);

        if(position==pos && cpState == 1) {
            viewHolder.itemName.setTextColor(Color.RED);
            ShareElement.artsAdapterHolder = viewHolder;
        }
        else
            viewHolder.itemName.setTextColor(Color.GRAY);
        if (seriesList.get(position).isCached()) {
            if (!TextUtils.isEmpty(seriesList.get(position).getLocalPath())) {
                viewHolder.cachedImg.setVisibility(View.VISIBLE);
                viewHolder.cachingImg.setVisibility(View.INVISIBLE);
                viewHolder.cachedTv.setVisibility(View.VISIBLE);
                viewHolder.cachingTv.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.cachedImg.setVisibility(View.INVISIBLE);
                viewHolder.cachingImg.setVisibility(View.VISIBLE);
                viewHolder.cachedTv.setVisibility(View.INVISIBLE);
                viewHolder.cachingTv.setVisibility(View.VISIBLE);
            }
        } else {
            viewHolder.cachedImg.setVisibility(View.INVISIBLE);
            viewHolder.cachingImg.setVisibility(View.INVISIBLE);
            viewHolder.cachedTv.setVisibility(View.INVISIBLE);
            viewHolder.cachingTv.setVisibility(View.INVISIBLE);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_name)
        TextView itemName;
        @BindView(R.id.item_count)
        TextView itemCount;
        @BindView(R.id.art_cached_tv)
        TextView cachedTv;
        @BindView(R.id.art_cached_iv)
        ImageView cachedImg;
        @BindView(R.id.art_caching_iv)
        ImageView cachingImg;
        @BindView(R.id.art_caching_tv)
        TextView cachingTv;

        private SeriesDetail.SourceBean bean;
        private int postion;

        public ViewHolder(View arg0) {
            super(arg0);
            ButterKnife.bind(this, arg0);
        }

        @OnClick(R.id.arts_selection)
        public void selections(){
            if(cpState == 2)
                return;
            if(pos == postion)
                return;
            pos = postion;
            changeView();
//            if(ShareElement.isIgnoreNetChange == 1)
//                return;
            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_A,bean);
            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_A,new int[]{0,postion});
            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_A,new ArtsChangeProgramMsg(1, postion));
        }

        public void changeView() {
            if(ShareElement.artsAdapterHolder != null) {
                ShareElement.artsAdapterHolder.revent();
            }
            itemName.setTextColor(Color.RED);
            ShareElement.artsAdapterHolder = this;
        }

        public void revent () {
            itemName.setTextColor(Color.GRAY);
        }
    }
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i< c.length; i++) {
            if (c[i] == 32) {
                c[i] = (char) 12288;
                continue;
            }
            if (c[i]< 127)
                c[i] = (char) (c[i] + 65248);
        }
        return new String(c);
    }

    public void changeProgram (int pos) {
        if(ShareElement.artsAdapterHolder!=null&&ShareElement.artsAdapterHolder == holders.get(pos))
            return;
        this.pos = pos;
        if(holders.get(pos)!=null)
            holders.get(pos).changeView();
    }
}
