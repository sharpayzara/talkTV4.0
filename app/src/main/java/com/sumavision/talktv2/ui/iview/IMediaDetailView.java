package com.sumavision.talktv2.ui.iview;

import com.sumavision.talktv2.model.entity.MediaDetail;
import com.sumavision.talktv2.ui.iview.base.IBaseView;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * Created by sharpay on 16-6-17.
 */
public interface IMediaDetailView extends IBaseView {
    void showProgressBar();
    void hideProgressBar();
    void showErrorView();
    void showWifiView();
    void fillDetailValue(MediaDetail mediaDetail);
    void loadDetailValue(MediaDetail mediaDetail);
    void share3d(SHARE_MEDIA plat);
}
