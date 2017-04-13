package com.sumavision.talktv2.presenter;

import android.content.Context;

import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

/**
 * Created by zhoutao on 2016/6/26.
 */
public class CachingFragmentPresenter extends BasePresenter<IBaseView> {
    Context mContext ;
    public CachingFragmentPresenter(Context context, IBaseView iView) {
        super(context, iView);
        mContext = context;
    }

    @Override
    public void release() {

    }
}
