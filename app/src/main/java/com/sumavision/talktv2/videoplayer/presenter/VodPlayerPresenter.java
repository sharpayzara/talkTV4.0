package com.sumavision.talktv2.videoplayer.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.sumavision.talktv2.model.impl.VodPlayerModelImpl;
import com.sumavision.talktv2.mycrack.CrackResult;
import com.sumavision.talktv2.mycrack.ParserUtil;
import com.sumavision.talktv2.util.DateUtils;
import com.sumavision.talktv2.videoplayer.PlayBean;
import com.sumavision.talktv2.videoplayer.iview.IVodPlayerView;
import com.sumavision.talktv2.videoplayer.player.IPlayer;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by zhangyisu on 2016/6/8.
 */
public class VodPlayerPresenter extends BasePlayerPresenter {

    private static final int UPDATE_PROGRESS = 1000;
    private final int PLAY_OVERTIME_LOG = 0524655;
    MyHandler handler;
    protected long totalTime;
    IVodPlayerView iVodPlayerView;
    VodPlayerModelImpl vodPlayerModel;
    private boolean isFirst = true;
    private long iBreakTime;


    private Runnable controllDimissRunnable = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(controllDimissRunnable);
            iVodPlayerView.controllDimiss();
        }
    };

    class MyHandler extends Handler {
        public MyHandler(Looper mainLooper) {
            super(mainLooper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_PROGRESS:
                    iBreakTime = player.getCurrentPosition();
                    String curTimeString = DateUtils.formatTime((int) iBreakTime);
                    totalTime = player.getDuration();
                    iVodPlayerView.setTotalTime(DateUtils.formatTime((int) totalTime));
                    if (totalTime != 0) {
                        int curProgress = (int) (iBreakTime * 100 / totalTime);
                        iVodPlayerView.updateSeekbar(curTimeString, curProgress, 0);
                    }

                    Log.e("handleMessage", "handleMessage");
                    if(iBreakTime>=totalTime-20 && !isFirst && totalTime > 0 ) {
                        iVodPlayerView.isPlayNext(true);
                    }else if(totalTime > 0){
                        iVodPlayerView.isPlayNext(false);
                    }
                    isFirst = false;
                   sendSeekMsg();
                    break;
                case PLAY_OVERTIME_LOG:
                    Log.e("msg","log");
                    iVodPlayerView.sendPlayErrorLog("timeout");
                    break;

            }
        }
    }

    public VodPlayerPresenter(Context context, IVodPlayerView iView) {
        super(context, iView);
        this.iVodPlayerView = iView;
        vodPlayerModel = new VodPlayerModelImpl();
        handler = new MyHandler(Looper.getMainLooper());

    }

    public void sendControllMsg() {
        handler.postDelayed(controllDimissRunnable, 5000);
    }

    public void removeControllMsg () {
        handler.removeCallbacks(controllDimissRunnable);
    }

    @Override
    public void onCompletion(IPlayer mp) {
        super.onCompletion(mp);
    }

    @Override
    public void onError(IPlayer mp, int what, int extra, String var) {
        iVodPlayerView.onError(mp,what,extra,var);
    }

    @Override
    public boolean onInfo(IPlayer mp, int what, int extra) {
        super.onInfo(mp, what, extra);
        switch (what) {
            case IPlayer.MEDIA_INFO_BUFFERING_START:

                break;
            case IPlayer.MEDIA_INFO_BUFFERING_END:
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onPrepared(IPlayer mp) {
        super.onPrepared(mp);
        sendSeekMsg();
        removePlayOverTimeMsg();
        iVodPlayerView.onPrepare();
    }

    @Override
    public void stop() {
        isStop = true;
        super.stop();
//        player.release();
        removeSeekMsg();
    }

    @Override
    public void startPlay(final String url) {
//        this.url = url;
//        if (pu != null) {
//            pu.stop();
//        }
//        pu = new ParserUtil(context.getApplicationContext(), this, url, 1);

        if (playerType == -1) {// 系统播放器，不用调用start方法
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.i("BasePlayerPresenter", "调用系统播放器");
                    try {
                        player.setDataSource(url);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            return;
        }
        try {
            player.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.start();
        sendPlayOverTimeMsg();
    }

    public void changeDefinition(int pos) {
        player.changeDefinition(pos);
    }

    public void startPlay(PlayBean playBean) {
        isStop = false;
        switch (playerType) {
            case -1:
            case 0:
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
                    pu = new ParserUtil(context.getApplicationContext(), this, playBean.getUrl(), 1);
                }
                break;
            default:
                try {
                    player.setDataSource(playBean);
                    player.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void seekTo (int progress) {
        player.seekTo(progress);
    }

    /**
     *
     * @param progress
     * @param fromUser
     */
    public void onProgressChanged(int progress, boolean fromUser) {
        if(fromUser) {
            long curTime = progress * totalTime/100;
            if(curTime >= totalTime-2)
                return;
            String curTimeString = DateUtils.formatTime((int) curTime);
            iVodPlayerView.updateSeekbar(curTimeString, progress, 0);
            player.seekTo(curTime);
        }
    }

    public void removeSeekMsg() {
        if(handler != null)
            handler.removeMessages(UPDATE_PROGRESS);
    }

    public void sendSeekMsg() {
        if(handler != null) {
            handler.removeMessages(UPDATE_PROGRESS);
            handler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 1000);
        }
    }

    public void getDefaultDefintion (Context context, CrackResult result,
                                     ArrayList<String> urls, ArrayList<String> denfintions) {

        iVodPlayerView.getDefaultDefintion(vodPlayerModel.getDefaultDefintion(context, result, urls, denfintions));
    }

    public void getPPTVDefintion(Context context, ArrayList<String> urls, ArrayList<String> denfintions) {
        iVodPlayerView.getDefaultDefintion(vodPlayerModel.getPPTVDefintion(context, urls, denfintions, player.getSupportDefinitions()));
    }

    /**
     *
     * @return
     */
    public long getBreakTime () {
        return iBreakTime;
    }

    public int getTotalTime () {
        return (int) totalTime;
    }

    /**
     * 断点播放
     * @param ibreak
     */
    public void playForBreak (long ibreak) {
        String currTime = DateUtils.formatTime((int) ibreak);
        totalTime = player.getDuration();
        if(totalTime > 0) {
            int progress = (int) (ibreak * 100 / totalTime);
            iVodPlayerView.updateSeekbar(currTime, progress, 0);
            player.seekTo(ibreak);
        }
    }

    public void playErrorLog(String programId, String mid, String errorType) {
        vodPlayerModel.playErrorLog(programId, mid, errorType);
    }

    public void sendPlayOverTimeMsg() {
        Log.e("msg","send");
        handler.sendEmptyMessageDelayed(PLAY_OVERTIME_LOG, 15000);
    }

    public void removePlayOverTimeMsg () {
        Log.e("msg","move");
        handler.removeMessages(PLAY_OVERTIME_LOG);
    }

    @Override
    public void release() {
        super.release();
        mHandler.removeCallbacksAndMessages(null);
        handler.removeCallbacksAndMessages(null);
        if (pu != null)
            pu.stop();
    }
}
