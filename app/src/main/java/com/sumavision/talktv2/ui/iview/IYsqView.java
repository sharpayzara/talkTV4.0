package com.sumavision.talktv2.ui.iview;

import com.sumavision.talktv2.model.entity.YsqBean;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

/**
 * 发现首页界面的IView
 * Created by zjx on 2016/5/31.
 */
public interface IYsqView extends IBaseView {
    void fillData(YsqBean ysqBean);
    void emptyData();
    void stopRefresh();
    void startRefresh();
}
