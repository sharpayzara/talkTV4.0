package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.http.GlideProxy;
import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.ui.activity.MediaDetailActivity;
import com.sumavision.talktv2.ui.activity.ProgramDetailActivity;
import com.sumavision.talktv2.ui.activity.SpecialDetailActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sharpay on 16-6-15.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private List<PlayerHistoryBean> list;
    private Context mContext;

    public HistoryAdapter(Context context, List<PlayerHistoryBean> list) {
        mInflater = LayoutInflater.from(context);
        this.list = list;
        this.mContext = context;
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = mInflater.inflate(R.layout.item_history_selections,
                viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        viewHolder.bean = list.get(position);
        viewHolder.itemName.setText(list.get(position).getProgramName());
        GlideProxy.getInstance().loadHImage(mContext,  viewHolder.bean.getPicUrl(), viewHolder.pic);
        String intro = "";
        if ( list.get(position).getPointTime() == -1) {
            intro = "观看至结束";
        } else {
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
        }
        viewHolder.itemIntro.setText(intro);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_intro)
        TextView itemIntro;
        @BindView(R.id.item_name)
        TextView itemName;
        @BindView(R.id.pic)
        ImageView pic;

        PlayerHistoryBean bean;
        public ViewHolder(View arg0) {
            super(arg0);
            ButterKnife.bind(this, arg0);
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
    }
}
