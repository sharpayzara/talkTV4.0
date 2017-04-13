package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.ClassifyItem;
import com.sumavision.talktv2.model.entity.SeriesDetail;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sharpay on 16-6-16.
 */
public class ProgramListSwitchAdapter extends RecyclerView.Adapter<ProgramListSwitchAdapter.SwitchViewHolder> {


    private Context mContext;
    private List<ClassifyItem> list;
    private PopupWindow popupWindow;
    public ProgramListSwitchAdapter(Context mContext, List<ClassifyItem> list,PopupWindow popupWindow) {
        this.mContext = mContext;
        this.list = list;
        this.popupWindow = popupWindow;
    }

    @Override
    public SwitchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SwitchViewHolder holder = new SwitchViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_switch, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(SwitchViewHolder holder, int position) {
        holder.bean = list.get(position);
        holder.itemName.setText(list.get(position).name);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SwitchViewHolder extends RecyclerView.ViewHolder {
        ClassifyItem bean;
        @BindView(R.id.item_name)
        TextView itemName;

        @OnClick(R.id.item_name)
        void onClick(){
            popupWindow.dismiss();
            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_A,bean);
        }
        public SwitchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public void  setSeriesData(List<SeriesDetail.SourceBean> list){
        list.addAll(list);
    }
}
