package com.sumavision.talktv2.presenter;

import android.content.Context;

import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

/**
 * Created by zhoutao on 2016/6/24.
 */
public class CachedFragmentPresenter extends BasePresenter<IBaseView> {
    Context mContext ;
    public CachedFragmentPresenter(Context context, IBaseView iView) {
        super(context, iView);
        mContext = context;
    }

    @Override
    public void release() {

    }
}
