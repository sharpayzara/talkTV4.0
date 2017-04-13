package com.sumavision.talktv2.ui.iview;

import com.sumavision.talktv2.model.entity.ProgramListData;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

/**
 * 自媒体首页界面的IView
 * Created by sharpay on 2016/5/31.
 */
public interface IProgramListCommonView extends IBaseView {
    void fillData(ProgramListData programListData);
    void showProgressBar();
    void hideProgressBar();
    void showErrorView();
    void showWifiView();
    void emptyData();
}
