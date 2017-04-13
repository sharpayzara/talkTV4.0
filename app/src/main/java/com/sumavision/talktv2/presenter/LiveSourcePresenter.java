package com.sumavision.talktv2.presenter;

import android.content.Context;

import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.ILiveSourceView;

/**
 * Created by zjx on 2016/6/22.
 */
public class LiveSourcePresenter extends BasePresenter<ILiveSourceView> {

    public LiveSourcePresenter(Context context, ILiveSourceView iView) {
        super(context, iView);
    }

    @Override
    public void release() {

    }
}
