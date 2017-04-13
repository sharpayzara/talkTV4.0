package com.sumavision.talktv2.ui.iview;

import com.sumavision.talktv2.model.entity.PreferenceBean;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

/**
 * 直播首页界面相关的IView
 * Created by sharpay on 2016/5/31.
 */
public interface IPreferenceView extends IBaseView {
    void fillData(PreferenceBean bean);

}
