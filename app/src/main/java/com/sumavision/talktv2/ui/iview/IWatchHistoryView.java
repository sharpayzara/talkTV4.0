package com.sumavision.talktv2.ui.iview;

import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

import java.util.List;

/**
  *  desc  查看历史view
  *  @author  yangjh
  *  created at  16-12-8 上午3:18
  */
public interface IWatchHistoryView extends IBaseView {
     void showListData(List<PlayerHistoryBean> beanList);
     void showProgressBar();
     void hideProgressBar();
     void showEmptyView();
}
