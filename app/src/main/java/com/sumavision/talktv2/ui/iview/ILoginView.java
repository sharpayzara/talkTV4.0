package com.sumavision.talktv2.ui.iview;

import com.sumavision.talktv2.model.entity.AppKeyResult;
import com.sumavision.talktv2.ui.iview.base.IBaseView;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * 登陆的view操作
 * Created by sharpay on 2016/8/19.
 */
public interface ILoginView extends IBaseView {
    void returnView();
    void showProgressBar();
    void hiddenProgressBar();
    void login3rd(SHARE_MEDIA plat,AppKeyResult appKeyResult);
}
