package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.http.GlideProxy;
import com.sumavision.talktv2.model.entity.SpecialContentList;
import com.sumavision.talktv2.ui.widget.AddCartAnimation;
import com.sumavision.talktv2.util.BusProvider;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sharpay on 16-6-16.
 */
public class SpecialItemAdapter extends RecyclerView.Adapter<SpecialItemAdapter.CommonItemHolder> {
    List<SpecialContentList.ItemsBean> list;
    Context mContext;
    public List<String> playedList;
    public Map<Integer,CommonItemHolder> holders;
    boolean isFlag;//是否有时间轴
    String programId;
    int lastPosition;
    public SpecialItemAdapter(Context mContext, List<SpecialContentList.ItemsBean> list) {
        this.list = list;
        this.mContext = mContext;
        playedList = new ArrayList<>();
        holders = new HashMap<>();
        lastPosition = 0;
    }

    public SpecialItemAdapter(Context mContext, List<SpecialContentList.ItemsBean> list,String programId) {
        this(mContext,list);
        this.programId = programId;
        for(int i = 0;i<list.size();i++){
            if(programId.equals(list.get(i).getId())){
                lastPosition = i;
                break;
            }
        }
    }

    @Override
    public CommonItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(list.size() > 0 ){
            isFlag = TextUtils.isEmpty(list.get(0).getRelease());
        }
        if (isFlag) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_special, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_special_2, parent, false);
        }
        return new CommonItemHolder(view);
    }

    @Override
    public void onBindViewHolder(CommonItemHolder holder, int position) {
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yy/MM/dd HH:mm");
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", java.util.Locale.US);
                if (list.get(position).getRelease() != null){
                    Date date = sdf.parse(list.get(position).getRelease());
                    String release = format.format(date);
                    holder.releaseDataTv.setText(release.substring(0,8));
                    holder.releaseTimeTv.setText(release.substring(9,14));
                }
            } catch (Exception e) {
                holder.releaseTimeTv.setText("");
                holder.releaseDataTv.setText("");
                e.printStackTrace();
            }
        holder.nameTv.setText(list.get(position).getName());
        holder.typeTv.setText(list.get(position).getCate());
        if(playedList.contains(list.get(position).getId())){
            holder.isPlay.setChecked(true);
        }else{
            holder.isPlay.setChecked(false);
        }
        if(!TextUtils.isEmpty(list.get(position).getPlayCount())){
            holder.playCountTv.setText(" " + list.get(position).getPlayCount());
        }else{
            holder.playCountTv.setVisibility(View.GONE);
        }
        GlideProxy.getInstance().loadHImage(mContext,list.get(position).getPicture(),holder.picIv);
        holder.bean = list.get(position);
        holder.bean.setPosition(position);
        if(position == 0 && TextUtils.isEmpty(programId)){
            holder.isPlay.setChecked(true);
        }
        if (!TextUtils.isEmpty(programId) && programId.equals(list.get(position).getId())){
            holder.isPlay.setChecked(true);
        }
        holders.put(position,holder);
        holder.itemRlt.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        holder.nameTv.setTextColor(mContext.getResources().getColor(R.color.user_center_txt));
        if(position == lastPosition){
            holder.itemRlt.setBackgroundColor(mContext.getResources().getColor(R.color.special_bg));
            holder.nameTv.setTextColor(mContext.getResources().getColor(R.color.cache_start_bg));
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CommonItemHolder extends RecyclerView.ViewHolder {
        public SpecialContentList.ItemsBean bean;
        @BindView(R.id.pic_iv)
        public ImageView picIv;
        @BindView(R.id.name_tv)
        TextView nameTv;
        @BindView(R.id.type_tv)
        TextView typeTv;
        @BindView(R.id.release_date_tv)
        TextView releaseDataTv;
        @BindView(R.id.release_time_tv)
        TextView releaseTimeTv;
        @BindView(R.id.play_count_tv)
        TextView playCountTv;
        @BindView(R.id.is_play)
        CheckBox isPlay;
        @BindView(R.id.item_rlt)
        RelativeLayout itemRlt;
        @OnClick(R.id.item_rlt)
        public void click(){
            isPlay.setChecked(true);
            holders.get(lastPosition).itemRlt.setBackgroundColor(mContext.getResources().getColor(R.color.white));
            holders.get(lastPosition).nameTv.setTextColor(mContext.getResources().getColor(R.color.user_center_txt));
            itemRlt.setBackgroundColor(mContext.getResources().getColor(R.color.special_bg));
            isPlay.setTag("true");
            nameTv.setTextColor(mContext.getResources().getColor(R.color.cache_start_bg));
            playedList.add(bean.getId());
            BusProvider.getInstance().post("NEXTPROBLEM",this);
            itemRlt.setBackgroundColor(mContext.getResources().getColor(R.color.special_bg));
            lastPosition = bean.getPosition();
        }

        public CommonItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}