package com.sumavision.talktv2.ui.iview;

import com.sumavision.talktv2.model.entity.decor.LiveDetailData;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

/**
 * 直播详情相关的IView
 * Created by zjx on 2016/6/14.
 */
public interface ILiveDetailView extends IBaseView {
    void getLiveDetail(LiveDetailData liveDetailData);
}
