package com.sumavision.talktv2.videoplayer.iview;

import android.widget.ImageView;

import com.sumavision.talktv2.mycrack.CrackResult;
import com.sumavision.talktv2.videoplayer.player.IPlayer;

/**
 * Created by zhangyisu on 2016/6/15.
 */
public interface IBasePlayerView {

    void setTime(String timeValue);

    ImageView getBattery();

    void onInfo(IPlayer mp, int what, int extra);

    void onCrackComplete(CrackResult result);

    void onCrackFailed();
    void onPrepare();
    void onComplation();

}
