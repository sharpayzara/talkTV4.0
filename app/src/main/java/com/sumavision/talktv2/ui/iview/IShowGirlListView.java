package com.sumavision.talktv2.ui.iview;

import com.sumavision.talktv2.model.entity.MediaList;
import com.sumavision.talktv2.model.entity.ShowGirlList;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

/**
 * 自媒体首页界面的IView
 * Created by zjx on 2016/5/31.
 */
public interface IShowGirlListView extends IBaseView {
    void fillListData(ShowGirlList list);
    void showProgressBar();
    void hideProgressBar();
    void showErrorView();
    void showWifiView();
    void emptyData();
}
