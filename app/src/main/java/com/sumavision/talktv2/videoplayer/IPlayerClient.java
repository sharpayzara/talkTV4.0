package com.sumavision.talktv2.videoplayer;

import android.media.AudioManager;
import android.view.Window;

/**
 * Created by Zhangyisu on 2016/6/6.
 *
 * VideoView的使用者需要实现的接口，供VideoView进行回调
 */

public interface IPlayerClient {
    void halfFullScreenSwitch(int landscapeState);
    AudioManager getAudioManager();
    Window getWindow();
}
