package com.sumavision.talktv2.presenter;

import android.content.Context;

import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.IFindView;

/**
 * Created by zjx on 2016/5/31.
 */
public class FindPresenter extends BasePresenter<IFindView> {
    public FindPresenter(Context context, IFindView iView) {
        super(context, iView);
    }

    @Override
    public void release() {

    }
}
