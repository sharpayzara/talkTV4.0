package com.sumavision.talktv2.videoplayer.iview;

import com.sumavision.talktv2.videoplayer.player.IPlayer;

/**
 * Created by zhangyisu on 2016/6/1.
 */
public interface IVodPlayerView extends IBasePlayerView{
    void updateSeekbar(String currentTime, int curProgress, int curSecondProgress);

    void setTotalTime(String totalTime);

    void controllDimiss();

    void getDefaultDefintion (int defintion);

    int getSeekBarMax ();

    void onError(IPlayer mp, int what, int extra, String var);

    void isPlayNext(boolean isPlayNext);

    void sendPlayErrorLog(String errorType);
}
