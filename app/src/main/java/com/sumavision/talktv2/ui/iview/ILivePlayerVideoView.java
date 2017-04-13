package com.sumavision.talktv2.ui.iview;

import com.sumavision.talktv2.model.entity.decor.LiveDetailData;
import com.sumavision.talktv2.videoplayer.iview.IBasePlayerView;
import com.sumavision.talktv2.videoplayer.player.IPlayer;

/**
 * Created by zjx on 2016/6/22.
 */
public interface ILivePlayerVideoView extends IBasePlayerView {
    void getDetailSucess(LiveDetailData liveDetailData);
    void getDetailFail(Throwable throwable);
    void isFav(boolean isFav);
    void changeSourceAuto();
    void prepare();
    void onError(IPlayer mp, int what, int extra, String var);

}
