package com.sumavision.talktv2.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.ProgramSelection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sharpay on 16-6-16.
 */
public class SeletionItemAdapter extends RecyclerView.Adapter<SeletionItemAdapter.SelectionViewHolder> {
    List<ProgramSelection.ItemsBean.ItemsBean2> list;

    public SeletionItemAdapter(List<ProgramSelection.ItemsBean.ItemsBean2> list) {
        this.list = list;
    }

    @Override
    public SelectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selection_btn, parent, false);
        return new SelectionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SelectionViewHolder holder, int position) {
        holder.bean = list.get(position);
        holder.itemBtn.setText(list.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SelectionViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_btn)
        RadioButton itemBtn;
        ProgramSelection.ItemsBean.ItemsBean2 bean;
       /* @OnCheckedChanged(R.id.item_btn)
        void onCheckedChanged(View view,boolean bool){
            if(bool){
                BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_A,bean);
            }else{
                BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_B,bean);
            }
        }*/
        public SelectionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
