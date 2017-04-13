package com.sumavision.talktv2.ui.iview;

import com.sumavision.talktv2.model.entity.ShowGirlTopic;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

/**
 * 发现首页界面的IView
 * Created by sharpay on 2016/5/31.
 */
public interface IShowGirlView extends IBaseView {
    void showProgressBar();
    void hideProgressBar();
    void showErrorView();
    void showWifiView();
    void fillTopicData(ShowGirlTopic topic);
}
