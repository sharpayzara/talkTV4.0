package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.util.CommonUtil;

import java.util.ArrayList;

/**
 * 播放器中的频道列表Adapter
 * Created by zjx on 2016/6/21.
 */
public class ChannelTypeAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> channelTypeList;
    private int iSelector = 0;
    public ChannelTypeAdapter(Context context,ArrayList<String> list) {
        mContext = context;
        channelTypeList = list;
    }

    public void setSelector(int i) {
        iSelector = i;
    }

    public int getSelector() {
        return iSelector;
    }
    @Override
    public int getCount() {
        return channelTypeList.size();
    }

    @Override
    public Object getItem(int position) {
        return channelTypeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_channel_type, null);
            holder.selected = (ImageView) convertView.findViewById(R.id.iv_channel_type_selected);
            holder.name = (TextView) convertView.findViewById(R.id.tv_channel_type_name);
            holder.back = (ImageView) convertView.findViewById(R.id.background);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) CommonUtil.dip2px(mContext, 40));// xml中设置高度不起作用，这里强制设置一下
            convertView.setLayoutParams(params);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(channelTypeList.get(position));
        if(iSelector == position){
            holder.selected.setVisibility(View.VISIBLE);
            holder.back.setVisibility(View.VISIBLE);
        }else{
            holder.selected.setVisibility(View.INVISIBLE);
            holder.back.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }


    private class ViewHolder{
        public ImageView selected;
        public TextView name;
        public ImageView back;
    }
}
