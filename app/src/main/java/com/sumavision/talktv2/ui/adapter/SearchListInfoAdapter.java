package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.http.GlideProxy;
import com.sumavision.talktv2.model.entity.decor.SearchInfoItem;
import com.sumavision.talktv2.ui.activity.ProgramDetailActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhoutao on 2016/8/24.
 * 这是搜索结果页面长视频显示的adapter
 */
public class SearchListInfoAdapter extends RecyclerView.Adapter<SearchListInfoAdapter.MyViewHolder> {
    private Context context;
    private List<SearchInfoItem> results;
    public SearchListInfoAdapter(Context context){
        this.context = context;
        results = new ArrayList<>();
    }

    public void setListData(List<SearchInfoItem> playResultes){
        if (results.size()>0){
            results.clear();
        }
        results.addAll(playResultes);
        notifyDataSetChanged();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.play_item_search, parent, false);
        /*ViewGroup.LayoutParams params = itemView.getLayoutParams();
            params.height = CommonUtil.screenWidth(context)/3*80/120 + CommonUtil.dip2px(context,34);
        itemView.setLayoutParams(params);*/
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.bean = results.get(position);
        holder.tv_searchinfo_title_playitem.setText(results.get(position).name);
        if (results.get(position).score != 0 || results.get(position).score != 0.0 ){
            holder.tv_searchinfo_grade_playitem.setText(results.get(position).score+"");
        }
        holder.tv_searchinfo_date_playitem.setText(results.get(position).cate);
        holder.tv_searchinfo_character_playitem.setText(results.get(position).actor);
        holder.tv_searchinfo_year_playitem.setText(results.get(position).year);
        GlideProxy.getInstance().loadVImage(context,results.get(position).picUrl,holder.iv_searchinfo_img_playitem);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        SearchInfoItem bean;
        @BindView(R.id.tv_searchinfo_title_playitem)
        public TextView tv_searchinfo_title_playitem;
        @BindView(R.id.tv_searchinfo_grade_playitem)
        public TextView tv_searchinfo_grade_playitem;
        @BindView(R.id.tv_searchinfo_date_playitem)
        public TextView tv_searchinfo_date_playitem;
        @BindView(R.id.tv_searchinfo_character_playitem)
        public TextView tv_searchinfo_character_playitem;
        @BindView(R.id.tv_searchinfo_year_playitem)
        public TextView tv_searchinfo_year_playitem;
        @BindView(R.id.iv_searchinfo_img_playitem)
        public ImageView iv_searchinfo_img_playitem;
        @BindView(R.id.ll_item)
        public RelativeLayout ll_item;

        @OnClick(R.id.ll_item)
        public void ClickItem(){
            //获取到当前条目的节目id
           /* if (!bean.cate.equals("短视频")){
                Intent intent = new Intent(context, ProgramDetailActivity.class);
                intent.putExtra("idStr",bean.code);
                intent.putExtra("cpidStr","");
                context.startActivity(intent);
            }else{
                Intent intent1 = new Intent(context, MediaDetailActivity.class);
                intent1.putExtra("vid",bean.code);
                if (TextUtils.isEmpty(bean.videoType)){
                    bean.videoType = "-1";
                }
                intent1.putExtra("videoType",Integer.parseInt(bean.videoType));
                if (TextUtils.isEmpty(bean.sdkType)) {
                    bean.sdkType = "-1";
                }
                intent1.putExtra("sdkType",Integer.parseInt(bean.sdkType));
                context.startActivity(intent1);
            }*/
            Intent intent = new Intent(context, ProgramDetailActivity.class);
            intent.putExtra("idStr",bean.code);
            intent.putExtra("cpidStr","");
            context.startActivity(intent);
            MobclickAgent.onEvent(context, "4ssjg");
        }
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
