package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sumavision.talktv2.R;

import java.util.ArrayList;

/**
 * 播放器
 * Created by zjx on 2016/6/21.
 */
public class WeekAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> weekDatas = new ArrayList<>();
    private int selector;

    public WeekAdapter (Context context) {
        this.context = context;
        initData();
    }

    private void initData(){
        weekDatas.add("周一");
        weekDatas.add("周二");
        weekDatas.add("周三");
        weekDatas.add("周四");
        weekDatas.add("周五");
        weekDatas.add("周六");
        weekDatas.add("周日");
    }

    public void setSelector(int selector) {
        this.selector = selector;
    }

    @Override
    public int getCount() {
        return weekDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return weekDatas.get(position);
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
            convertView = View.inflate(context, R.layout.item_weeklist, null);
            holder.week = (TextView) convertView.findViewById(R.id.weekday);
            convertView.setTag(holder);
        }
        else {
            holder = (MyViewHolder) convertView.getTag();
        }
        holder.week.setText(weekDatas.get(position));
        if(selector == position) {
            holder.week.setBackgroundResource(R.mipmap.play_btn_bg_selected);
            holder.week.setTextColor(Color.WHITE);
        }
        else {
            holder.week.setBackgroundResource(R.color.transparent);
            holder.week.setTextColor(Color.parseColor("#918EA1"));
        }
        return convertView;
    }

    class MyViewHolder {
        TextView week;
    }
}
