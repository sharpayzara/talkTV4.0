package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.http.GlideProxy;
import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.ui.activity.MediaDetailActivity;
import com.sumavision.talktv2.ui.activity.ProgramDetailActivity;
import com.sumavision.talktv2.ui.activity.SpecialDetailActivity;
import com.sumavision.talktv2.ui.widget.MyLinearLayout;
import com.sumavision.talktv2.ui.widget.WidthDropAnimator;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.CommonUtil;
import com.sumavision.talktv2.util.TipUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhoutao on 2016/5/26.
 */
public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.MyViewHolder> {

    private Context mContext;
    private List<PlayerHistoryBean> list;

    public HistoryRecyclerAdapter(Context mContext, List<PlayerHistoryBean> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_watch_history, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bean =list.get(position);
        holder.nameTv.setText(list.get(position).getProgramName());
        GlideProxy.getInstance().loadHImage(mContext, holder.bean.getPicUrl(), holder.imgRlt);
        String intro = "00:00:00";
        int hour = (int) (list.get(position).getPointTime() / 3600);
        int min = (int) ( list.get(position).getPointTime() - (hour * 3600)) / 60;
        int sec = (int) (list.get(position).getPointTime() - (hour * 3600) - min * 60);
        StringBuffer times = new StringBuffer("观看至");
        if (hour < 10) {
            times.append("0").append(hour).append(":");
        } else {
            times.append(hour).append(":");
        }
        if (min < 10) {
            times.append("0").append(min).append(":");
        } else {
            times.append(min).append(":");
        }
        if (sec < 10) {
            times.append("0").append(sec);
        } else {
            times.append(+sec);
        }
        intro = times.toString();
        holder.introTv.setText(intro);
        if(list.get(position).isOpened && holder.checkBox.getWidth() == 0){
            new WidthDropAnimator().animateOpen(holder.checkboxRlt,CommonUtil.dip2px(mContext,30));
        }else if(!list.get(position).isOpened &&  holder.checkBox.getWidth() > 0){
            new WidthDropAnimator().animateClose(holder.checkboxRlt);
        }
        if(list.get(position).isChecked){
            holder.checkBox.setChecked(true);
        }else{
            holder.checkBox.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        PlayerHistoryBean bean;
       @BindView(R.id.checkbox)
        CheckBox checkBox;
       @BindView(R.id.img_rlt)
        ImageView imgRlt;
       @BindView(R.id.name_tv)
        TextView nameTv;
       @BindView(R.id.intro_tv)
        TextView introTv;
       @BindView(R.id.cache_iv)
        ImageView cacheIv;
       @BindView(R.id.checkbox_rlt)
        MyLinearLayout checkboxRlt;

        @OnClick(R.id.checkbox_rlt)
        void onClick(){
            if(checkBox.isChecked()){
                checkBox.setChecked(false);
                bean.isChecked = false;
            }else{
                checkBox.setChecked(true);
                bean.isChecked = true;
            }
            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_C,"msg");
        }

        @OnClick(R.id.enter_detail)
        void gotoDetail () {
            if(bean.getMediaType() == 1) {
                Intent intent = new Intent(mContext, ProgramDetailActivity.class);
                intent.putExtra("idStr", bean.getProgramId());
                intent.putExtra("currentType", bean.getProgramType());
                mContext.startActivity(intent);
            }
            else if(bean.getMediaType() == 2){
                Intent intent = new Intent(mContext, MediaDetailActivity.class);
                intent.putExtra("vid", bean.getProgramId());
                intent.putExtra("sdkType", bean.getSdkType());
                intent.putExtra("videoType", bean.getVideoType());
                mContext.startActivity(intent);
            }else if(bean.getMediaType() == 3){
                Intent intent = new Intent();
                intent.putExtra("idStr",bean.getSpecialId());
                intent.putExtra("programIdStr",bean.getProgramId());
                intent.setClass(mContext, SpecialDetailActivity.class);
                mContext.startActivity(intent);
            }
        }
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
