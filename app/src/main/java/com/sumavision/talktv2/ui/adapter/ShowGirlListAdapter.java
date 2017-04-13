package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.melot.meshow.room.ChatRoom;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.http.GlideProxy;
import com.sumavision.talktv2.model.entity.ShowGirlList;
import com.sumavision.talktv2.util.CommonUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sharpay on 16-6-16.
 */
public class ShowGirlListAdapter extends RecyclerView.Adapter<ShowGirlListAdapter.RecommandHolder> {

    Context context;
    MediaItemCommonAdapter adapter;
    List<ShowGirlList.RoomListBean> list;
    public String pathPrefix;
    public ShowGirlListAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    public List<ShowGirlList.RoomListBean> getList() {
        return list;
    }

    public void setList(List<ShowGirlList.RoomListBean> list) {
        this.list.addAll(list);
    }

    public void setPathPrefix(String pathPrefix) {
        this.pathPrefix = pathPrefix;
    }

    @Override
    public RecommandHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_showgirl, parent, false);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = CommonUtil.screenWidth(context)/2*136/180+ CommonUtil.dip2px(context,36);
        view.setLayoutParams(params);
        RecommandHolder holder = new RecommandHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecommandHolder holder, int position) {
        holder.bean = list.get(position);
        GlideProxy.getInstance().loadHImage(context,pathPrefix+list.get(position).getPoster_path_272(),holder.itemImg);
        holder.itemName.setText(list.get(position).getNickname());
        if(list.get(position).getLiveType() == 0){
            holder.liveFlag.setVisibility(View.GONE);
        }else{
            holder.liveFlag.setVisibility(View.VISIBLE);
        }
        holder.remarkTv.setText(list.get(position).getOnlineCount()+"");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class RecommandHolder extends RecyclerView.ViewHolder {
        ShowGirlList.RoomListBean bean;
        @BindView(R.id.item_name)
        TextView itemName;
        @BindView(R.id.item_img)
        ImageView itemImg;
        @BindView(R.id.item_frame)
        FrameLayout itemFrame;
        @BindView(R.id.remark_tv)
        TextView remarkTv;
        @BindView(R.id.item_rlt)
        RelativeLayout itemRlt;
        @BindView(R.id.live_flag)
        ImageView liveFlag;

        @OnClick(R.id.item_rlt)
        void onClick() {
            Intent intent = new Intent(context, ChatRoom.class);
            // 房间ID(必选)
            intent.putExtra("roomId", bean.getRoomId());
            context.startActivity(intent);
            MobclickAgent.onEvent(context, "4xxdjl");
        }

        public RecommandHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}