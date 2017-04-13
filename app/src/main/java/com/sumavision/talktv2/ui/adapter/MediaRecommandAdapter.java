package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jiongbull.jlog.JLog;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.ShareElement;
import com.sumavision.talktv2.http.GlideProxy;
import com.sumavision.talktv2.model.entity.HorVideoData;
import com.sumavision.talktv2.model.entity.MediaRecommand;
import com.sumavision.talktv2.presenter.MediaRecommandPresenter;
import com.sumavision.talktv2.ui.activity.MediaDetailActivity;
import com.sumavision.talktv2.ui.listener.PlayCompleteListener;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.CommonUtil;
import com.sumavision.talktv2.util.NetworkUtil;
import com.sumavision.talktv2.util.NoDoubleClickListener;
import com.sumavision.talktv2.videoplayer.IPlayerClient;
import com.sumavision.talktv2.videoplayer.PlayBean;
import com.sumavision.talktv2.videoplayer.view.VodPlayerVideoView;
import com.waynell.videolist.visibility.items.ListItem;
import com.waynell.videolist.visibility.scroll.ItemsProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by sharpay on 16-6-16.
 */
public class MediaRecommandAdapter extends RecyclerView.Adapter<MediaRecommandAdapter.RecommandHolder> implements ItemsProvider{

    private static final java.lang.String TAG = "MediaRecommandAdapter";
    private final RecyclerView recyclerView;
    private final IPlayerClient iplayerClient;
    List<MediaRecommand> mediaRecommandList;
    Context context;
    private Map<String,VodPlayerVideoView> videoMap;
    public VodPlayerVideoView currVideo;
    public PlayBean currentBean;
    public RelativeLayout currRlt;
    public ImageView lastPic;
    public boolean isComplete = false;
    public RecommandHolder currentHolder;
    private MediaRecommandPresenter presenter;
    public MediaRecommandAdapter(List<MediaRecommand> mediaRecommandList, Context context, RecyclerView recyclerView, IPlayerClient iplayerClient, MediaRecommandPresenter presenter) {
        this.mediaRecommandList = mediaRecommandList;
        this.context = context;
        this.recyclerView = recyclerView;
        this.iplayerClient = iplayerClient;
        this.presenter = presenter;
        videoMap = new HashMap<>();
        VodPlayerVideoView videoView = new VodPlayerVideoView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        videoView.setLayoutParams(params);
        videoView.init(iplayerClient, 0, 2);
        videoMap.put("0",videoView);

        VodPlayerVideoView videoView1 = new VodPlayerVideoView(context);
        videoView1.setLayoutParams(params);
        videoView1.init(iplayerClient, 1, 2);
        videoMap.put("1",videoView1);

        VodPlayerVideoView videoView2 = new VodPlayerVideoView(context);
        videoView2.setLayoutParams(params);
        videoView2.init(iplayerClient, 2, 2);
        videoMap.put("2",videoView2);

    }

    @Override
    public MediaRecommandAdapter.RecommandHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        JLog.i(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media_recommand, parent, false);
        return new RecommandHolder(view);
    }

    @Override
    public void onBindViewHolder(MediaRecommandAdapter.RecommandHolder holder, int position) {
        JLog.i(TAG, "onBindViewHolder" + "; position:" + position);
        MediaRecommand mediaRecommand = mediaRecommandList.get(position);
        holder.bean = mediaRecommand;
        holder.s = mediaRecommand.getPlayerType();
        holder.title.setText(mediaRecommand.getName());
        holder.picIv.setVisibility(View.VISIBLE);
        holder.programCount.setText(mediaRecommand.getPlayCount()+"次" );
        GlideProxy.getInstance().loadHImage(context,mediaRecommand.getPicUrl(),holder.picIv);
        holder.picIv.setClickable(false);
      /*  if(holder.rlt.getChildCount() > 1 && holder.rlt.getChildAt(1) instanceof ImageView){
            holder.rlt.removeViewAt(1);*/
        //}
        for(int i = 1 ;i < holder.rlt.getChildCount();i++){
            //holder.rlt.removeAllViews();
            holder.rlt.removeViewAt(i);
        }

    }

    @Override
    public int getItemCount() {
        return mediaRecommandList.size();
    }

    @Override
    public ListItem getListItem(int i) {
        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(i);
        if (holder instanceof ListItem) {
            return (ListItem) holder;
        }
        return null;
    }

    public void turnPicVideoView (boolean isPic) {
        if (isPic) {
            lastPic.setVisibility(View.VISIBLE);
            if(currRlt.getChildCount() > 1){
                currRlt.removeViewAt(1);
            }
        }
        else {
            lastPic.setVisibility(View.GONE);
            if(currRlt.getChildCount() < 2 && currVideo.getParent()==null){
                currRlt.addView(currVideo);
            }
        }
    }

    @Override
    public int listItemSize() {
        return getItemCount();
    }

    public class RecommandHolder extends RecyclerView.ViewHolder implements ListItem,PlayCompleteListener {
        MediaRecommand bean;
        VodPlayerVideoView videoView;
        public int s = 0;
        @BindView(R.id.rlt)
        RelativeLayout rlt;
        @BindView(R.id.detail_rlt)
        RelativeLayout detailRlt;
        @BindView(R.id.pic_iv)
        ImageView picIv;
        /*    @BindView(R.id.player_iv)
            VodPlayerVideoView videoView;*/
        @BindView(R.id.program_title)
        TextView title;
        @BindView(R.id.program_count)
        TextView programCount;
        public int position;

        public RecommandHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ViewGroup.LayoutParams params = rlt.getLayoutParams();
            params.height = CommonUtil.screenWidth(context)*9/16;
            rlt.setLayoutParams(params);
        }
        @OnClick(R.id.detail_rlt)
        void onClick(){
            Intent intent = new Intent(context, MediaDetailActivity.class);
            intent.putExtra("vid",bean.getCode());
            intent.putExtra("videoType",bean.getVideoType());
            intent.putExtra("sdkType",bean.getSdkType());
            context.startActivity(intent);
        }

        @OnClick(R.id.pic_iv)
        void clickPlay() {
            if((ShareElement.isIgnoreNetChange == -1 && NetworkUtil.getCurrentNetworkType(context) == ConnectivityManager.TYPE_MOBILE)||ShareElement.isIgnoreNetChange ==1) {
                if(onClickPlayListener != null)
                    onClickPlayListener.onClickPlay();
            }
        }

        @Override
        public void setActive(View view, int i) {
            currentHolder = this;
            position = i;
            if(lastPic != null)
                lastPic.setClickable(false);
            picIv.setClickable(true);
            picIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onNetSateListener != null)
                        onNetSateListener.netSateListener();
                }
            });
            if(s == 2){
                VodPlayerVideoView temp = new VodPlayerVideoView(context);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                temp.setLayoutParams(params);
                temp.init(iplayerClient, 2, 2);
                videoMap.put("2",temp);
            }
            if(currVideo!=null && currVideo.getVideoType()==0) {
                EventBus.getDefault().unregister(currVideo);
                currVideo.stopPreLoading();
            }
            videoView = videoMap.get(s+"");
            Log.i(TAG, "player type:" + s);
            if(s == 0 && !EventBus.getDefault().isRegistered(videoView)) {
                EventBus.getDefault().register(videoView);
            }
            if( videoView.getParent() != null){
                ((RelativeLayout)videoView.getParent()).removeViewAt(1);
            }
            if(rlt.getChildCount() > 1){
                rlt.removeViewAt(1);
            }
            videoView.setCompleteListener((PlayCompleteListener) this);
            videoView.setBackPlay(false);
            videoView.setSLPlay(true);
            videoView.setProgramName(mediaRecommandList.get(i).getName());
            videoView.setProgramNameVisiable(false);
            Log.i(TAG, "setActive startPlay()");
            currentBean = mediaRecommandList.get(i).getPlayBean();
            if(!TextUtils.isEmpty(currentBean.getUrl())&&TextUtils.isEmpty(currentBean.getmId())) {
                String url = currentBean.getUrl()+"&MovieName="+mediaRecommandList.get(i).getName()+"&MovieId="+currentBean.getProgramId()+"&MovieType=4";
                currentBean.setUrl(url);
            }
            currVideo = videoView;
            currRlt = rlt;
            isComplete = false;
            lastPic = picIv;
            currentBean.setmId(currentBean.getProgramId());
            if(ShareElement.isIgnoreNetChange == 1 ||
                    (ShareElement.isIgnoreNetChange == -1 && NetworkUtil.getCurrentNetworkType(context) == ConnectivityManager.TYPE_MOBILE))
                return;
            rlt.addView(videoView);
            videoView.startPlay(mediaRecommandList.get(i).getPlayBean());
            presenter.pvLog(bean.getCode(), context);
            picIv.setVisibility(View.GONE);
        }

        /**
         * 当在sohu SDK的源走onPause之后再进行onResume调用
         */
        public void sohu4Resume() {
            if(ShareElement.isIgnoreNetChange == -1 && NetworkUtil.getCurrentNetworkType(context) == ConnectivityManager.TYPE_MOBILE) {
                if(onNetSateListener != null)
                    onNetSateListener.netSateListener();
                return;
            }
            else if(ShareElement.isIgnoreNetChange == 1)
                return;
            VodPlayerVideoView temp = new VodPlayerVideoView(context);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            temp.setLayoutParams(params);
            temp.init(iplayerClient, 1, 2);
            videoMap.put("1",temp);
            videoView = temp;

            if( videoView.getParent() != null){
                ((RelativeLayout)videoView.getParent()).removeViewAt(1);
            }
            if(rlt.getChildCount() > 1){
                rlt.removeViewAt(1);
            }

            rlt.addView(videoView);
            videoView.setCompleteListener((PlayCompleteListener) this);
            picIv.setVisibility(View.GONE);
            videoView.setBackPlay(false);
            videoView.setSLPlay(true);
            videoView.setProgramNameVisiable(false);
            Log.i(TAG, "setActive startPlay()");
            videoView.startPlay(currentBean);
            presenter.pvLog(bean.getCode(), context);
            currVideo = videoView;
            currRlt = rlt;
            isComplete = false;
        }

        @Override
        public void deactivate(View view, int i) {
            if(videoView != null){
                videoView.stop();
                picIv.setVisibility(View.VISIBLE);
            }
            if(rlt.getChildCount() > 1){
                rlt.removeViewAt(1);
            }
        }

        @Override
        public void playComplete() {
            if(videoView.isLandScape() && videoView.getParent() != null){
                final RelativeLayout rlt = (RelativeLayout)videoView.getParent();
                ImageView replay= new ImageView(context);
                replay.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
                replay.setImageResource(R.mipmap.replay_xh);
                replay.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View view) {
                        HorVideoData data = new HorVideoData();
                        if(rlt.getChildCount() > 0 ){
                            rlt.removeViewAt(0);
                        }
                        data.setCurrBean(currentBean);
                        data.setLandscape(true);
                        data.setVideoView(videoView);
                        BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_A,data);
                        isComplete = false;
                    }
                });
                rlt.removeViewAt(0);
                rlt.addView(replay);
            }else{

                ImageView replay= new ImageView(context);
                replay.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
                replay.setImageResource(R.mipmap.replay_xh);
                replay.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View view) {
//                        setActive(view,position);
                        if(rlt.getChildCount() > 1){
                            rlt.removeViewAt(1);
                        }
                        rlt.addView(videoView);
                        videoView.startPlay(currentBean);
                        videoView.setHistory(0, true);
                        isComplete = false;
                    }
                });
                picIv.setVisibility(View.VISIBLE);
                rlt.addView(replay);
                if(videoView.getParent() != null){
                    ((RelativeLayout)videoView.getParent()).removeView(videoView);
                }
            }
            isComplete = true;
        }
    }

    public interface OnNetSateListener {
        void netSateListener();
    }
    private OnNetSateListener onNetSateListener;
    public void setOnNetSateListener (OnNetSateListener onNetSateListener) {
        this.onNetSateListener = onNetSateListener;
    }

    public interface OnClickPlayListener {
        void onClickPlay();
    }
    private OnClickPlayListener onClickPlayListener;
    public void setOnClickPlayListener (OnClickPlayListener onClickPlayListener) {
        this.onClickPlayListener = onClickPlayListener;
    }
}