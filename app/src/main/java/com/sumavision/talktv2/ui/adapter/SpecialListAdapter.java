package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.http.GlideProxy;
import com.sumavision.talktv2.model.entity.SpecialDetail;
import com.sumavision.talktv2.model.entity.SpecialListData;
import com.sumavision.talktv2.ui.activity.SpecialActivity;
import com.sumavision.talktv2.ui.activity.SpecialDetailActivity;
import com.sumavision.talktv2.util.CommonUtil;
import com.sumavision.talktv2.util.TipUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sharpay on 2016/9/6.
 */
public class SpecialListAdapter extends RecyclerView.Adapter<SpecialListAdapter.MyViewHolder> {

    private Context mContext;
    List<SpecialListData.ItemsBean> list;

    public SpecialListAdapter(Context mContext, List<SpecialListData.ItemsBean> specialList) {
        this.mContext = mContext;
        this.list = specialList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_special_home, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bean = list.get(position);
        holder.specialTv.setText(list.get(position).getName());

        GlideProxy.getInstance().loadHImage(mContext, list.get(position).getPicture1(), holder.imgIv);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        SpecialListData.ItemsBean bean;
        @BindView(R.id.img_iv)
        ImageView imgIv;
        @BindView(R.id.special_tv)
        TextView specialTv;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ViewGroup.LayoutParams params = imgIv.getLayoutParams();
            params.height = CommonUtil.screenWidth(mContext) / 2;
            imgIv.setLayoutParams(params);
        }

        @OnClick(R.id.img_iv)
        void onClick() {
            Intent intent = new Intent();
            intent.putExtra("idStr",bean.getId()+"");
            if(bean.getStyle().equals("long")){
                intent.setClass(mContext, SpecialActivity.class);
                mContext.startActivity(intent);
            }else{
                intent.setClass(mContext, SpecialDetailActivity.class);
                mContext.startActivity(intent);
            }
        }
    }
}
