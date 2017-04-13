package com.sumavision.talktv2.ui.iview;

import com.sumavision.talktv2.model.entity.CpDataNet;
import com.sumavision.talktv2.model.entity.DetailRecomendData;
import com.sumavision.talktv2.model.entity.ProgramDetail;
import com.sumavision.talktv2.model.entity.SeriesDetail;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

/**
 * 直播首页界面相关的IView
 * Created by zjx on 2016/5/31.
 */
public interface IProgranDetailView extends IBaseView {
    void showProgressBar();
    void hideProgressBar();
    void showErrorView();
    void showEmptyView();
    void showWifiView();
    void fillDetailValue(ProgramDetail programDetail);
    void fillSeriesGridValue(SeriesDetail seriesDetail);
    void fillSeriesVListValue(SeriesDetail seriesDetail);
    void fillRecommendData(DetailRecomendData recomendData);
    void changeCp(ProgramDetail programDetail);
    void fillCpData (CpDataNet cpDataNet);
}
