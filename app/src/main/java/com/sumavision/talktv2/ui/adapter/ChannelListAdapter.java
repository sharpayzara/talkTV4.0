package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.http.GlideProxy;
import com.sumavision.talktv2.model.entity.decor.LiveData;
import com.sumavision.talktv2.util.NoDoubleClickListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * Created by zjx on 2016/6/7.
 */
public class ChannelListAdapter extends BaseAdapter {

    private ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> channelDatas;
    private ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> channelDatasForCollect;
    private Context context;
    private GlideProxy glideProxy;
    private String channelId;

    public ChannelListAdapter (Context context,  ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> channelDatas,
                               ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> channelDatasForCollect) {
        this.context = context;
        this.channelDatas = channelDatas;
        this.channelDatasForCollect = channelDatasForCollect;
        glideProxy = GlideProxy.getInstance();
    }

    @Override
    public int getCount() {
        return channelDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return channelDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder holder = null;
        if(convertView == null) {
            LayoutInflater inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView =  inflator.inflate(R.layout.item_channel_list, null);
            holder = new MyViewHolder();
            holder.channelTypeName = (TextView) convertView.findViewById(R.id.channel_type);
            holder.toPlay = (RelativeLayout) convertView.findViewById(R.id.layout_goplay);
            holder.channelPic = (ImageView) convertView.findViewById(R.id.pic);
            holder.channelName = (TextView) convertView.findViewById(R.id.channel_name);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.programName = (TextView) convertView.findViewById(R.id.name);
            holder.collect = (ImageButton) convertView.findViewById(R.id.collect_btn);
            holder.programDetail = (ImageView) convertView.findViewById(R.id.infoBtn);
            holder.top = (RelativeLayout) convertView.findViewById(R.id.top);
            holder.is_play = (ImageView) convertView.findViewById(R.id.is_play);

            convertView.setTag(holder);
        }
        else {
            holder = (MyViewHolder) convertView.getTag();
        }


        //http://tvfan.cn/photo/channel/image/android15.png
        LiveData.ContentBean.TypeBean.ChannelBean channelBean = channelDatas.get(position);

        boolean isFav = isFav(channelBean.getId());

        if(isFav) {
            holder.collect.setBackgroundResource(R.mipmap.playdetail_collect_pressed_btn);
        }
        else {
            holder.collect.setBackgroundResource(R.mipmap.playdetail_collect_nor_btn);

        }

        String picUrl = "http://tvfan.cn/photo/channel/image/android" + channelBean.getId() + ".png";
        glideProxy.loadVImage(context, picUrl, holder.channelPic);
        holder.channelName.setText(channelBean.getName());
        holder.time.setText(channelBean.getCpStartTime()+"-"+channelBean.getCpEndTime());
        holder.programName.setText(channelBean.getCpName());
        holder.channelTypeName.setText(channelBean.getChannelType());

        if(channelId.equals(channelBean.getId())) {
            holder.channelName.setTextColor(Color.RED);
            holder.is_play.setVisibility(View.VISIBLE);
        }
        else {
            holder.channelName.setTextColor(Color.parseColor("#444444"));
            holder.is_play.setVisibility(View.INVISIBLE);
        }
        if(position==0) {
            holder.top.setVisibility(View.VISIBLE);
        }
        else if(!channelDatas.get(position).getChannelType().equals(channelDatas.get(position-1).getChannelType())) {
            holder.top.setVisibility(View.VISIBLE);
        }
        else {
            holder.top.setVisibility(View.GONE);
        }

        initClick(holder, position, isFav);
        return convertView;
    }

    class MyViewHolder {
        TextView channelTypeName;
        RelativeLayout toPlay;
        RelativeLayout top;
        ImageView channelPic;
        ImageView is_play;
        TextView channelName;
        TextView time;
        TextView programName;

        ImageButton collect;
        ImageView programDetail;
    }

    /**
     * 点击事件
     * @param holder
     * @param pos
     * @param isFav
     */
    private void initClick (MyViewHolder holder, final int pos, final boolean isFav) {
        holder.toPlay.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                try {
                    MobclickAgent.onEvent(context, "4zbdst", channelDatas.get(pos).getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (onToPlayListener != null)
                    onToPlayListener.onToPlay(pos);
            }
        });
        holder.collect.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (onCollectListener != null)
                    onCollectListener.onCollect(pos, isFav);
            }
        });
        holder.programDetail.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(onToDetialListener != null)
                    onToDetialListener.onToDetail(pos, isFav);
            }
        });
    }

    /**
     * 是否收藏
     * @param channelId
     * @return
     */
    private boolean isFav (String channelId) {
        boolean isFav = false;
        for(LiveData.ContentBean.TypeBean.ChannelBean channelBean : channelDatasForCollect) {
            if(channelId.equals(channelBean.getId()))
                isFav = true;
        }
        return isFav;
    }

    public void setCurrId (String channelId) {
        this.channelId = channelId;
    }
    /*******************************内部控件的点击监听**********************************************/
    private OnToPlayListener onToPlayListener;
    public interface OnToPlayListener {
        void onToPlay(int position);
    }
    public void setOnToPlayListener (OnToPlayListener onToPlayListener) {
        this.onToPlayListener = onToPlayListener;
    }

    private OnCollectListener onCollectListener;
    public interface OnCollectListener {
        void onCollect(int position, boolean isFav);
    }
    public void setOnCollectListener (OnCollectListener onCollectListener) {
        this.onCollectListener = onCollectListener;
    }

    private OnToDetialListener onToDetialListener;
    public interface OnToDetialListener {
        void onToDetail (int postion, boolean isFav);
    }
    public void setOnToDetialListener (OnToDetialListener onToDetialListener) {
        this.onToDetialListener = onToDetialListener;
    }
}
