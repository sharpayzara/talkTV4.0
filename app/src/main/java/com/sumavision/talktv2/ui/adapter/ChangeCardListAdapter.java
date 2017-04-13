package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.HomeRecommend;

import java.util.List;

/**
 * Created by zjx on 2016/6/7.
 */
public class ChangeCardListAdapter extends BaseAdapter {

    private Context context;
    private List<HomeRecommend> fixLists;
    public ChangeCardListAdapter(Context context, List<HomeRecommend> fixLists) {
        this.context = context;
        this.fixLists = fixLists;
    }

    @Override
    public int getCount() {
        return fixLists.size();
    }

    @Override
    public Object getItem(int position) {
        return fixLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder holder = null;
        if(convertView == null) {
            LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView =  inflator.inflate(R.layout.item_changecard_list, null);
            holder = new MyViewHolder();
            holder.tv_fixcard = (TextView) convertView.findViewById(R.id.tv_fixcard);
            convertView.setTag(holder);
        }
        else {
            holder = (MyViewHolder) convertView.getTag();
        }
        holder.tv_fixcard.setText(fixLists.get(position).card_name);
        return convertView;
    }

    class MyViewHolder {
        TextView tv_fixcard;
    }


}
