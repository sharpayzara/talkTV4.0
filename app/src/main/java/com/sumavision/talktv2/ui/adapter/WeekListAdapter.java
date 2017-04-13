package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;

import java.util.ArrayList;

/**
 * Created by zjx on 2016/6/16.
 */
public class WeekListAdapter extends BaseAdapter {
    private ArrayList<String> weekList;
    private Context mContext;
    private int selector;

    public WeekListAdapter(Context mContext, ArrayList<String> weekList) {
        this.mContext = mContext;
        this.weekList = weekList;
    }

    @Override
    public int getCount() {
        return weekList.size();
    }

    @Override
    public Object getItem(int position) {
        return weekList.get(position);
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
            convertView = View.inflate(mContext, R.layout.item_detail_week, null);
            holder.channelTypeName = (TextView) convertView.findViewById(R.id.channel_type_name);
            holder.line = (ImageView) convertView.findViewById(R.id.line);
            convertView.setTag(holder);
        }
        else {
            holder = (MyViewHolder) convertView.getTag();
        }
        holder.channelTypeName.setText(weekList.get(position));
        if(selector == position) {
            convertView.setBackgroundResource(R.color.white);
            holder.line.setVisibility(View.VISIBLE);
        }
        else {
            convertView.setBackgroundResource(R.color.transparent);
            holder.line.setVisibility(View.GONE);
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
}
