package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;

import java.util.ArrayList;

/**
 * Created by zjx on 2016/6/8.
 */
public class ChannelTypeListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> channelTypeDatas;
    private int selector;
    public ChannelTypeListAdapter (Context context, ArrayList<String> channelTypeDatas) {
        this.context = context;
        this.channelTypeDatas = channelTypeDatas;
    }

    @Override
    public int getCount() {
        return channelTypeDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return channelTypeDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder holder = null;
        if(convertView == null) {
            holder = new MyViewHolder();
            convertView = View.inflate(context, R.layout.item_channeltype, null);
            holder.channelTypeName = (TextView) convertView.findViewById(R.id.channel_type_name);
            holder.line = (ImageView) convertView.findViewById(R.id.line);
            convertView.setTag(holder);
        }
        else {
            holder = (MyViewHolder) convertView.getTag();
        }
        holder.channelTypeName.setText(channelTypeDatas.get(position));
        if(selector == position) {
            convertView.setBackgroundColor(Color.parseColor("#ffffff"));
            holder.line.setVisibility(View.VISIBLE);
            holder.channelTypeName.setTextColor(Color.RED);
        }
        else {
            convertView.setBackgroundColor(Color.parseColor("#f1f1f1"));
            holder.line.setVisibility(View.INVISIBLE);
            holder.channelTypeName.setTextColor(Color.BLACK);
        }
        return convertView;
    }

    class MyViewHolder {
        TextView channelTypeName;
        ImageView line;
    }

    public void setSelector (int selector) {
        this.selector = selector;
    }

    public void getSelector(String type) {
        int count = channelTypeDatas.size();
        for(int i=0; i<count; i++) {
            if(type.equals(channelTypeDatas.get(i)))
                selector = i;
        }
    }

    public int getSelector() {
        return selector;
    }
}
