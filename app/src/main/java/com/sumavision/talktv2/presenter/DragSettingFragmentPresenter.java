package com.sumavision.talktv2.presenter;

import android.content.Context;

import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

/**
 * 个人定制页面的presenter
 * Created by zhoutao on 16-5-24.
 */
public class DragSettingFragmentPresenter extends BasePresenter<IBaseView> {

    Context mContext ;
    public DragSettingFragmentPresenter(Context context, IBaseView iView) {
        super(context, iView);
        mContext = context;
    }

    @Override
    public void release() {
        /*if (subscription != null) {
            subscription.unsubscribe();
        }*/
    }

}
