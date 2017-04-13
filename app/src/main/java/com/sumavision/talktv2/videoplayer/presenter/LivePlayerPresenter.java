package com.sumavision.talktv2.videoplayer.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.entity.decor.LiveData;
import com.sumavision.talktv2.model.entity.decor.LiveDetailData;
import com.sumavision.talktv2.model.impl.LivePlayerVideoViewModelImpl;
import com.sumavision.talktv2.mycrack.ParserUtil;
import com.sumavision.talktv2.ui.iview.ILivePlayerVideoView;
import com.sumavision.talktv2.videoplayer.PlayBean;
import com.sumavision.talktv2.videoplayer.iview.IBasePlayerView;
import com.sumavision.talktv2.videoplayer.player.IPlayer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/6/13.
 */
public class LivePlayerPresenter extends BasePlayerPresenter {

    private LivePlayerVideoViewModelImpl livePlayerVideoViewModel;
    private ILivePlayerVideoView iView;
    private final int CHANGESOURCE = 0154566;
    private Handler hd = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(CHANGESOURCE == msg.what) {
                removeChangeSourceMsg();
                iView.changeSourceAuto();
            }
        }
    };

    public LivePlayerPresenter(Context context, IBasePlayerView iBaseView, ILivePlayerVideoView iView) {
        super(context, iBaseView);
        livePlayerVideoViewModel = new LivePlayerVideoViewModelImpl();
        this.iView = iView;
    }

    @Override
    public void onCompletion(IPlayer mp) {
        super.onCompletion(mp);
    }

    @Override
    public void onError(IPlayer mp, int what, int extra, String var) {
        iView.onError(mp,what,extra,var);
    }

    @Override
    public boolean onInfo(IPlayer mp, int what, int extra) {
        super.onInfo(mp, what, extra);
        return false;
    }

    @Override
    public void onPrepared(IPlayer mp) {
        removeChangeSourceMsg();
        log("remove  "+"prepare");
        iView.prepare();
    }

    @Override
    public void startPlay(String url) {
        this.url = url;
        if (pu != null) {
            pu.stop();
        }
        pu = new ParserUtil(context.getApplicationContext(), this, url, 2);

    }

    @Override
    public void startPlay(PlayBean playBean) {
        if (!TextUtils.isEmpty(playBean.getPlayPath())) {
            try {
                player.setDataSource(playBean.getPlayPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            player.start();
        } else {
            this.url = playBean.getUrl();
            if (pu != null) {
                pu.stop();
            }
            pu = new ParserUtil(context.getApplicationContext(), this, playBean.getUrl(), 2);
        }
        sendChangeSourceMsg();
        log("send  "+"start");
    }

    public void log(String str) {
            Log.e("sendMsg", str);
    }
    public void sendChangeSourceMsg () {
        hd.sendEmptyMessageDelayed(CHANGESOURCE, 20000L);
    }

    public void removeChangeSourceMsg () {
        hd.removeMessages(CHANGESOURCE);
    }

    /**
     * 获取节目单列表
     * @param channelId
     */
    public void getProgramData(String channelId) {
        livePlayerVideoViewModel.getLiveDetailData(channelId, new CallBackListener<LiveDetailData>() {
            @Override
            public void onSuccess(LiveDetailData liveDetailData) {
                iView.getDetailSucess(liveDetailData);
            }

            @Override
            public void onFailure(Throwable throwable) {
                iView.getDetailFail(throwable);
            }
        });
    }

    /**
     * 是否被收藏
     * @param channelId
     */
    public void isFav(String channelId) {
        livePlayerVideoViewModel.isFav(channelId, iView);
    }

    /**
     * 收藏channel
     * @param collectDatas
     * @param channelBean
     */
    public void favChannel(ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> collectDatas,
                           LiveData.ContentBean.TypeBean.ChannelBean channelBean) {
        livePlayerVideoViewModel.favChannel(collectDatas, channelBean);
    }

    /**
     * 取消收藏
     * @param collectDatas
     * @param channelId
     */
    public void cancelFav(ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> collectDatas, String channelId){
        livePlayerVideoViewModel.cancelFav(collectDatas, channelId);
    }

    public int getCurrType(ArrayList<String> types, String type) {
        return livePlayerVideoViewModel.getCurrType(types, type);
    }

    public void playErrorLog(String channelId, int sourceId, String errorType) {
        livePlayerVideoViewModel.playErrorLog(channelId, sourceId, errorType);
    }

    public void livePlayLog(String channelId, String mid){
        livePlayerVideoViewModel.livePlayLog(channelId, mid , context);
    }

    @Override
    public void release() {
        super.release();
        pu.stop();
    }
}
