package com.sumavision.talktv2.ui.iview;

import com.sumavision.talktv2.ui.iview.base.IBaseView;

import java.util.ArrayList;

/**
 * 搜索界面的IView
 * Created by zjx on 2016/5/31.
 */
public interface ISearchMainFragmentView extends IBaseView {
    void showProgressBar();
    void hideProgressBar();
    void showErrorView();
    void showEmptyView();
    void showWifiView();
    void showHotList(ArrayList<String> items);

}
