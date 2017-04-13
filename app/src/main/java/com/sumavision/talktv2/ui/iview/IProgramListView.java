package com.sumavision.talktv2.ui.iview;

import com.sumavision.talktv2.model.entity.ProgramListTopic;
import com.sumavision.talktv2.model.entity.ProgramSelection;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

/**
 * 发现首页界面的IView
 * Created by sharpay on 2016/6/25.
 */
public interface IProgramListView extends IBaseView {
    void showProgressBar();
    void hideProgressBar();
    void showErrorView();
    void showEmptyView();
    void showWifiView();
    void fillTopicAndSeletionData(ProgramSelection selectionData,ProgramListTopic programListTopic);
}
