package com.sumavision.talktv2.ui.iview;

import android.content.Context;

import com.sumavision.talktv2.model.entity.MediaList;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

/**
 * 自媒体首页界面的IView
 * Created by zjx on 2016/5/31.
 */
public interface IMediaRecommandView extends IBaseView {
    void fillListData(MediaList list);
    void showProgressBar();
    void hideProgressBar();
    void showErrorView();
    void showWifiView();
    void emptyData();
}
