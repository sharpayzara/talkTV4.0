package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.decor.LiveDetailData;

import java.util.ArrayList;

/**
 * Created by zjx on 2016/6/21.
 */
public class LiveProgramListAdapter extends BaseAdapter {

    private ArrayList<LiveDetailData.ContentBean.DayBean.ProgramBean> programBeens;
    private Context mContext;
    private int state;
    private int programNum;

    public LiveProgramListAdapter (Context mContext, ArrayList<LiveDetailData.ContentBean.DayBean.ProgramBean> programBeens, int state, int programNum) {
        this.mContext = mContext;
        this.programBeens = programBeens;
        this.state = state;
        this.programNum = programNum;
    }

    @Override
    public int getCount() {
        return programBeens.size();
    }

    @Override
    public Object getItem(int position) {
        return programBeens.get(position);
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
            convertView = View.inflate(mContext, R.layout.item_program, null);
            holder.name = (TextView) convertView.findViewById(R.id.programname);
            holder.time = (TextView) convertView.findViewById(R.id.programtime);
            holder.playpic = (ImageView) convertView.findViewById(R.id.play_pic);
            convertView.setTag(holder);
        }
        else {
            holder = (MyViewHolder) convertView.getTag();
        }

        if(state==1 && position==programNum)
            holder.playpic.setVisibility(View.VISIBLE);
        else
            holder.playpic.setVisibility(View.INVISIBLE);
        if(state == 1 && position >= programNum || state == 2) {
            holder.time.setTextColor(Color.parseColor("#ffffff"));
            holder.name.setTextColor(Color.parseColor("#ffffff"));
        }
        else {
            holder.time.setTextColor(Color.parseColor("#e1e1e1"));
            holder.name.setTextColor(Color.parseColor("#e1e1e1"));
        }
        holder.name.setText(programBeens.get(position).getProgramName());
        holder.time.setText(programBeens.get(position).getCpTime() + "-" + programBeens.get(position).getEndTime());

        return convertView;
    }

    class MyViewHolder {
        TextView name;
        TextView time;
        ImageView playpic;
    }

    public void setState(int state) {
        this.state = state;
    }
}
