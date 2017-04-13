package com.sumavision.talktv2.ui.iview;

import com.sumavision.talktv2.model.entity.decor.SearchInfoItem;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

import java.util.ArrayList;

/**
 * 搜索界面的IView
 * Created by zjx on 2016/5/31.
 */
public interface ISearchListInfoView extends IBaseView {
    void showProgressBar();
    void hideProgressBar();
    void showErrorView();
    void showEmptyView();
    void showWifiView();
    void hidePPPro();
    void showSearchList(ArrayList<SearchInfoItem> searchInfoItems,int page);
}
