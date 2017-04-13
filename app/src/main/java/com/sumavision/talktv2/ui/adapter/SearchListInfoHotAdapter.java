package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jiongbull.jlog.JLog;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.http.GlideProxy;
import com.sumavision.talktv2.model.entity.decor.SearchInfoItem;
import com.sumavision.talktv2.ui.activity.MediaDetailActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhoutao on 2016/8/24.
 * 这是搜索结果页面短视频显示的adapter
 */
public class SearchListInfoHotAdapter extends RecyclerView.Adapter<SearchListInfoHotAdapter.MyViewHolder> {
    private Context context;
    private List<SearchInfoItem> results;
    public SearchListInfoHotAdapter(Context context){
        this.context = context;
        results = new ArrayList<>();
    }

    public void setListData(List<SearchInfoItem> hotResultes){
        if (results.size()>0){
            results.clear();
        }
        JLog.e("re",results.toString());
        results.addAll(hotResultes);
        notifyDataSetChanged();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searchinfo_list, parent, false);
        /*ViewGroup.LayoutParams params = itemView.getLayoutParams();
            params.height = CommonUtil.screenWidth(context)/3*80/120 + CommonUtil.dip2px(context,34);
        itemView.setLayoutParams(params);*/
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.bean = results.get(position);
        holder.tv_searchinfo_title.setText(results.get(position).name);
        if (results.get(position).score != 0 || results.get(position).score != 0.0 ){
            holder.tv_searchinfo_grade.setText(results.get(position).score+"");
        }
        holder.tv_searchinfo_date.setText(results.get(position).cate);
        holder.tv_searchinfo_character.setText(results.get(position).actor);
        holder.tv_searchinfo_year.setText(results.get(position).year);
        GlideProxy.getInstance().loadHImage(context,results.get(position).picUrl,holder.iv_searchinfo_img);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        SearchInfoItem bean;
        @BindView(R.id.tv_searchinfo_title)
        public TextView tv_searchinfo_title;
        @BindView(R.id.tv_searchinfo_grade)
        public TextView tv_searchinfo_grade;
        @BindView(R.id.tv_searchinfo_date)
        public TextView tv_searchinfo_date;
        @BindView(R.id.tv_searchinfo_character)
        public TextView tv_searchinfo_character;
        @BindView(R.id.tv_searchinfo_year)
        public TextView tv_searchinfo_year;
        @BindView(R.id.iv_searchinfo_img)
        public ImageView iv_searchinfo_img;
        @BindView(R.id.ll_item)
        public RelativeLayout ll_item;

        @OnClick(R.id.ll_item)
        public void ClickItem(){
            //获取到当前条目的节目id
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
            MobclickAgent.onEvent(context, "4ssjg");
        }
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
