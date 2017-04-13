package com.sumavision.talktv2.ui.iview;
import com.sumavision.talktv2.model.entity.ADInfoItem;
import com.sumavision.talktv2.model.entity.ClassifyItem;
import com.sumavision.talktv2.model.entity.ClassifyUpdataItem;
import com.sumavision.talktv2.model.entity.Gank;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

import java.util.ArrayList;
import java.util.List;

/**
 *  desc  干货view
 *  @author  yangjh
 *  created at  16-5-24 下午9:14
 */
public interface IRecommendView extends IBaseView {
    void showGankList(List<Gank> gankList);
    void showProgressBar();
    void hideProgressBar();
    void showWifiView();
    void showErrorView();
    void showClassifyView(ArrayList<ClassifyItem> classifies);
    void updataClassifyView(ArrayList<ClassifyUpdataItem> classifyUpdates);
    void setADInfo(ADInfoItem adInfo);
}
