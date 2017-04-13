package com.sumavision.talktv2.presenter;

import android.content.Context;

import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.YsqModel;
import com.sumavision.talktv2.model.entity.YsqBean;
import com.sumavision.talktv2.model.impl.YsqModelmpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.IYsqView;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

/**
 * Created by sharpay on 2016/6/6.
 */
public class FindPwdPresenter extends BasePresenter<IBaseView> {

    public FindPwdPresenter(Context context, IBaseView iView) {
        super(context, iView);
    }

    @Override
    public void release() {

    }
}
