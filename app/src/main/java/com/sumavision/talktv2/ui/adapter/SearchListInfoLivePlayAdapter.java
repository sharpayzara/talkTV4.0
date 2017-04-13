package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.http.GlideProxy;
import com.sumavision.talktv2.model.entity.decor.SearchInfoItem;
import com.sumavision.talktv2.ui.activity.SearchActivity;
import com.sumavision.talktv2.ui.activity.TVFANActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhoutao on 2016/8/24.
 * 这是搜索页面直播结果显示的adapter
 */
public class SearchListInfoLivePlayAdapter extends RecyclerView.Adapter<SearchListInfoLivePlayAdapter.MyViewHolder> {
    private Context context;
    private List<SearchInfoItem> results;
    public SearchListInfoLivePlayAdapter(Context context){
        this.context = context;
        results = new ArrayList<>();
    }

    public void setListData(List<SearchInfoItem> liveFinalResultes){
        if (results.size()>0){
            results.clear();
        }
        results.addAll(liveFinalResultes);
        notifyDataSetChanged();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.liveplay_item_search, parent, false);
        /*ViewGroup.LayoutParams params = itemView.getLayoutParams();
            params.height = CommonUtil.screenWidth(context)/3*80/120 + CommonUtil.dip2px(context,34);
        itemView.setLayoutParams(params);*/
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.bean = results.get(position);
        holder.iv_search_liveplay_item_title.setText(results.get(position).name);
        GlideProxy.getInstance().loadHImage(context,"http://tvfan.cn/photo/channel/image/android"+results.get(position).code+".png",holder.iv_search_liveplay_item_img);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        SearchInfoItem bean;
        @BindView(R.id.iv_search_liveplay_item_title)
        public TextView iv_search_liveplay_item_title;
        @BindView(R.id.iv_search_liveplay_item_img)
        public ImageView iv_search_liveplay_item_img;
        @OnClick(R.id.rl_liveplay_search_item)
        public void ClickItem(){
            //跳转到直播播放器
            Intent intent  = new Intent(context, TVFANActivity.class);
            Bundle b = new Bundle();
            intent.putExtra("enterLivePlay","enterLivePlay");
            intent.putExtra("id",bean.code);
            context.startActivity(intent);
            ((SearchActivity)context).finish();
            /*if(onLiveClick !=null)
                onLiveClick.liveClick(bean.code);*/

            MobclickAgent.onEvent(context, "4ssjg");
        }
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /*public interface OnLiveClick{
        void liveClick(String id);
    }
    private OnLiveClick onLiveClick;
    public void setOnLiveClick(OnLiveClick onLiveClick) {
        this.onLiveClick = onLiveClick;
    }*/
}
