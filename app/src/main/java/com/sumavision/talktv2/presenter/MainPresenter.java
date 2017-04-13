package com.sumavision.talktv2.presenter;

import android.content.Context;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.IMainView;

/**
 * Created by sharpay on 16-5-23.
 */
public class MainPresenter extends BasePresenter<IMainView> {

    public MainPresenter(Context context, IMainView iView) {
        super(context, iView);
    }

    @Override
    public void release() {

    }
}
