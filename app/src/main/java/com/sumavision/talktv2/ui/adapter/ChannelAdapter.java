package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.decor.LiveData;
import com.sumavision.talktv2.util.CommonUtil;

import java.util.ArrayList;

/**
 * 播放器内直播频道列表的adapter
 * Created by zjx on 2016/6/21.
 */
public class ChannelAdapter extends BaseAdapter{

    private Context mContext;
    private ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> channelList;
    private String channelId;
    public ChannelAdapter(Context context){
        mContext = context;
    }
    public void setChannelList(ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> channelList){
        this.channelList = channelList;
    }

    public void setChannelId (String channelId) {
        this.channelId = channelId;
    }

    public int getSelector() {
        int count = channelList.size();
        for(int i=0; i<count; i++) {
            String id = channelList.get(i).getId();
            if(id.equals(channelId))
                return i;
        }
        return -1;
    }
    @Override
    public int getCount() {
        return channelList.size();
    }

    @Override
    public Object getItem(int position) {
        return channelList.get(position);
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
            convertView = View.inflate(mContext, R.layout.item_channel, null);
            holder.name = (TextView) convertView.findViewById(R.id.tv_channel_name);
            holder.playpic = (ImageView) convertView.findViewById(R.id.playpic);
            holder.currProgram = (TextView) convertView.findViewById(R.id.tv_current_program);
            holder.rl = (RelativeLayout) convertView.findViewById(R.id.rl);
            holder.backGround = (ImageView) convertView.findViewById(R.id.background);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) CommonUtil.dip2px(mContext, 40));// xml中设置高度不起作用，这里强制设置一下
            convertView.setLayoutParams(params);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(channelList.get(position).getName());
        holder.currProgram.setText(channelList.get(position).getCpName());
        if(channelList.get(position).getId().equals(channelId)){
            holder.playpic.setVisibility(View.VISIBLE);
            holder.backGround.setVisibility(View.VISIBLE);
            holder.name.setTextColor(Color.WHITE);
            holder.currProgram.setTextColor(Color.WHITE);
        }else{
            holder.backGround.setVisibility(View.GONE);
            holder.playpic.setVisibility(View.INVISIBLE);
            holder.name.setTextColor(Color.parseColor("#918EA1"));
            holder.currProgram.setTextColor(Color.parseColor("#918EA1"));
        }
        return convertView;
    }

    /**
     * 对频道编号进行修改
     * @param num
     * @return
     */
    private String handleNum(int num){
        String number = "001";
        if (num<10){
            number = "00"+num;
        }else if(num>9&&num<100){
            number = "0"+num;
        }else{
            number = num+"";
        }
        return  number;
    }

    private void setTextColor(TextView tv, boolean isSelected){
        if(isSelected)
            tv.setTextColor(mContext.getResources().getColor(R.color.channel_list_textcolor_selected));
        else
            tv.setTextColor(mContext.getResources().getColor(R.color.channel_list_textcolor));
    }

    private class ViewHolder{
        public RelativeLayout rl;
        public ImageView playpic;
        public TextView name;
        public TextView currProgram;
        public ImageView backGround;
    }
}
