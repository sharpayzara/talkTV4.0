package com.sumavision.talktv2.presenter;

import android.content.Context;

import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

/**
 * Created by zhoutao on 2016/6/24.
 */
public class AboutUsFragmentPresenter extends BasePresenter<IBaseView> {
    Context mContext ;
    public AboutUsFragmentPresenter(Context context, IBaseView iView) {
        super(context, iView);
        mContext = context;
    }

    @Override
    public void release() {

    }
}
