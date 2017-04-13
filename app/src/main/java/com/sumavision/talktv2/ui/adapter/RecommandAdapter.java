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
import com.sumavision.talktv2.model.entity.DetailRecomendData;
import com.sumavision.talktv2.ui.activity.ProgramDetailActivity;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sharpay on 16-6-15.
 */
public class RecommandAdapter extends RecyclerView.Adapter<RecommandAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<Integer> mDatas;
    private Context mContext;
    private ArrayList<DetailRecomendData.ItemsBean> list;
    private String currType;
    private ProgramDetailActivity activity;
    private int width = 0,height = 0;
    String style;

    public RecommandAdapter(Context context) {
        mContext = context;
        activity = (ProgramDetailActivity) context;
        mInflater = LayoutInflater.from(context);
        list = new ArrayList<>();
    }

    public void setData(ArrayList<DetailRecomendData.ItemsBean> list, String style) {
        this.list.addAll(list);
        this.style = style;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_recommend_horizontal,
                viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        //viewHolder.mImg.setImageResource(mDatas.get(i));
        if("ver".equals(style)){
            GlideProxy.getInstance().loadVImage(mContext, list.get(i).getPicurl(),viewHolder.recommendLogo);
        }else{
            GlideProxy.getInstance().loadHImage(mContext, list.get(i).getPicurl(),viewHolder.recommendLogo);
        }
        viewHolder.recommendTitle.setText(list.get(i).getName());
        viewHolder.itemsBean = list.get(i);
        viewHolder.remarkTv.setText(list.get(i).getPrompt());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.recommend_logo)
        ImageView recommendLogo;
        @BindView(R.id.recommend_title)
        TextView recommendTitle;
        @BindView(R.id.remark_tv)
        TextView remarkTv;
        DetailRecomendData.ItemsBean itemsBean;
        public ViewHolder(View arg0) {
            super(arg0);
            ButterKnife.bind(this,arg0);
            ViewGroup.LayoutParams params = recommendLogo.getLayoutParams();
            if(width == 0 && height == 0){
                    if("ver".equals(style)){
                    params.height = CommonUtil.screenWidth(mContext)*140/360;
                    params.width = params.height * 120 / 160;
                }else{
                    params.width= (int) (CommonUtil.screenWidth(mContext)/2.3);
                    params.height = params.width * 85/ 152;
                }
                width = params.width;
                height = params.height;


            }else{
                params.width= width;
                params.height = height;
            }
            recommendLogo.setLayoutParams(params);

        }

        @OnClick(R.id.goto_detail)
        void gotoDetail () {
                Intent intent = new Intent(mContext, ProgramDetailActivity.class);
                intent.putExtra("idStr", itemsBean.getId());
                mContext.startActivity(intent);
                activity.finish();
        }
    }
}
