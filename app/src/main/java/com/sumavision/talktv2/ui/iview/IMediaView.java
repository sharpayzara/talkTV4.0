package com.sumavision.talktv2.ui.iview;

import com.sumavision.talktv2.model.entity.MediaTopic;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

/**
 * 自媒体首页界面的IView
 * Created by zjx on 2016/5/31.
 */
public interface IMediaView extends IBaseView {
    void fillTopicData(MediaTopic mediaTopic);
    void showProgressBar();
    void hideProgressBar();
    void showErrorView();
    void showWifiView();
}
