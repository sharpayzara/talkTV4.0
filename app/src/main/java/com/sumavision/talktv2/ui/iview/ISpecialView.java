package com.sumavision.talktv2.ui.iview;

import com.sumavision.talktv2.model.entity.ProgramDetail;
import com.sumavision.talktv2.model.entity.SpecialContentList;
import com.sumavision.talktv2.model.entity.SpecialDetail;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

/**
 * Created by sharpay on 16-6-17.
 */
public interface ISpecialView extends IBaseView {
    void showProgressBar();
    void hideProgressBar();
    void showErrorView();
    void showWifiView();
    void showDetailView(SpecialDetail detailData);
    void fillSpecialList(SpecialContentList specialContentList);
}
