package com.sumavision.talktv2.presenter;

import android.content.Context;

import com.sumavision.talktv2.model.PreferenceModel;
import com.sumavision.talktv2.model.impl.PreferenceModelImpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

/**
 * Created by sharpay on 2016/6/6.
 */
public class FeedBackPresenter extends BasePresenter<IBaseView> {
    PreferenceModel model;

    public FeedBackPresenter(Context context, IBaseView iView) {
        super(context, iView);
        model = new PreferenceModelImpl();
    }
    public void sendFeedBack(String problem,String suggest,String contact){
        model.sendFeedBack(problem,suggest,contact);
    }
    @Override
    public void release() {
        model.release();
    }
}
