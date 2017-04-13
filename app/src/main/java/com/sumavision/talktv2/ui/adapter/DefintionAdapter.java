package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sumavision.talktv2.R;

import java.util.ArrayList;

/**
 * Created by zjx on 2016/6/28.
 */
public class DefintionAdapter extends BaseAdapter {

    private Context context;
    ArrayList<String> datas;
    private int selector;

    public DefintionAdapter (Context context, ArrayList<String> datas) {

        this.datas = datas;
        this.context = context;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = View.inflate(context, R.layout.item_defintion, null);
        TextView defintion = (TextView) convertView.findViewById(R.id.defintion);
        defintion.setTextSize
                (16);
        defintion.setText(datas.get(position));

        if(selector == position)
            defintion.setBackgroundResource(R.color.blue);
        else
            defintion.setBackgroundResource(R.color.transparent);
        return convertView;
    }

    public void setSelector (int selector) {
        this.selector = selector;
    }
}
