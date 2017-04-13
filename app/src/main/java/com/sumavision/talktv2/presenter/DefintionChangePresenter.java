package com.sumavision.talktv2.presenter;

import android.content.Context;

import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.IDefintionChangeDialogView;

/**
 * Created by zjx on 2016/6/28.
 */
public class DefintionChangePresenter extends BasePresenter<IDefintionChangeDialogView> {
    public DefintionChangePresenter(Context context, IDefintionChangeDialogView iView) {
        super(context, iView);
    }

    @Override
    public void release() {

    }
}
