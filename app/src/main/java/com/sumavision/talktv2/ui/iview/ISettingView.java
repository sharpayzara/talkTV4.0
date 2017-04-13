package com.sumavision.talktv2.ui.iview;

import com.sumavision.talktv2.model.entity.VersionData;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

/**
 * 搜索界面的IView
 * Created by zjx on 2016/5/31.
 */
public interface ISettingView extends IBaseView {
    void showUpdateView(int errCode, String msg,VersionData version);
}
