package com.sumavision.talktv2.presenter.base;

import android.content.Context;

import com.sumavision.talktv2.ui.iview.base.IBaseView;

/**
 * Created by sharpay on 16-5-31.
 */
public class MyGridPresenter extends BasePresenter<IBaseView> {
    public MyGridPresenter(Context context, IBaseView iView) {
        super(context, iView);
    }

    @Override
    public void release() {

    }
}
