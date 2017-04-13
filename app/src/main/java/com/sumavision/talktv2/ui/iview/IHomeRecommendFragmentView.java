package com.sumavision.talktv2.ui.iview;

import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.model.entity.SpecialListData;
import com.sumavision.talktv2.model.entity.decor.HomeRecommendData;
import com.sumavision.talktv2.model.entity.decor.HomeRecommendUpdateData;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

/**
 * Created by sharpay on 16-5-24.
 */
public interface IHomeRecommendFragmentView extends IBaseView{
    void showProgressBar();
    void hideProgressBar();
    void showErrorView();
    void showEmptyView();
    void showWifiView();
    void showListView(HomeRecommendData homeRecommendData, String nid);
    void updateListView(HomeRecommendUpdateData homeRecommendUpdateData, String nid, String v);
    void showHistory(PlayerHistoryBean history);
    void hideHistory();
    void showSpecialView(SpecialListData specialListData);
}
