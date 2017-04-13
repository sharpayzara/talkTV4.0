package com.sumavision.talktv2.ui.iview;

import com.sumavision.talktv2.model.entity.decor.LiveData;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

import java.util.ArrayList;

/**
 * 直播首页界面相关的IView
 * Created by zjx on 2016/5/31.
 */
public interface ILiveView extends IBaseView {
    void getLiveData(LiveData.ContentBean contentBean);
    void showProgressBar();
    void hideProgressBar();
    void showErrorView();
    void showEmptyView();
    void showWifiView();
    void collectSuccess(ArrayList<String> channelTypeList, ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> channelList);
    void cancelCollect();
    void queryAllCollect(ArrayList<String> channelTypeList, ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> channelList);
}
