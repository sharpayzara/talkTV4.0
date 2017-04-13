package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.decor.LiveDetailData;

import java.util.ArrayList;

/**
 * 直播节目单列表adapter
 * Created by zjx on 2016/6/16.
 */
public class LiveProgramAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<LiveDetailData.ContentBean.DayBean.ProgramBean> programDatas = new ArrayList<>();

    private int state;
    private int programNum;

    public LiveProgramAdapter (Context context, int state, int programNum) {
        mContext = context;
        this.state = state;
        this.programNum = programNum;
    }

    public void setProgramDatas(ArrayList<LiveDetailData.ContentBean.DayBean.ProgramBean> programDatas) {
        this.programDatas = programDatas;
    }
    @Override
    public int getCount() {
        return programDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return programDatas.get(position);
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
            convertView = View.inflate(mContext, R.layout.item_program_live, null);
            holder.program = (TextView) convertView.findViewById(R.id.program_name);
            holder.time = (TextView) convertView.findViewById(R.id.program_time);
            holder.remind = (ImageButton) convertView.findViewById(R.id.remind);
            convertView.setTag(holder);
        }
        else {
            holder = (MyViewHolder) convertView.getTag();
        }

        if(state == 1 && position == programNum ) {
            holder.remind.setVisibility(View.VISIBLE);
            holder.program.setTextColor(Color.RED);
        }
        else {
            holder.program.setTextColor(Color.parseColor("#000000"));
            holder.remind.setVisibility(View.INVISIBLE);
        }

        LiveDetailData.ContentBean.DayBean.ProgramBean programBean = programDatas.get(position);
        holder.program.setText(programBean.getProgramName());
        holder.time.setText(programBean.getCpTime()+"-"+programBean.getEndTime());

        return convertView;
    }

    class MyViewHolder {
        TextView program;
        TextView time;
        ImageButton remind;
    }
}
