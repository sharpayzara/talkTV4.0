package com.sumavision.talktv2.presenter;

import android.content.Context;

import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

/**
 * Created by sharpay on 16-5-31.
 */
public class MyListPresenter extends BasePresenter<IBaseView> {
    public MyListPresenter(Context context, IBaseView iView) {
        super(context, iView);
    }

    @Override
    public void release() {

    }
}
