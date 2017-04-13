package com.sumavision.talktv2.presenter;

import android.content.Context;

import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

/**
 * Created by sharpay on 2016/7/31.
 */
public class WebADPresenter extends BasePresenter<IBaseView> {
    Context mContext;
    public WebADPresenter(Context context, IBaseView iView) {
        super(context, iView);
    }

    @Override
    public void release() {

    }
}
