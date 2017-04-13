package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.NoDoubleClickListener;

import java.util.List;

/**
 * Created by zhoutao on 2016/5/26.
 */
public class ClassifyPopRecycleAdapter extends RecyclerView.Adapter<ClassifyPopRecycleAdapter.MyViewHolder> {

    private Context context;
    private List<String> results;
    public ClassifyPopRecycleAdapter(Context context, List<String> results){
        this.results = results;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_classifypop_recycle, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.textView.setText(results.get(position));
        holder.textView.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_A,results.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_item_classifypop);
        }
    }

}
