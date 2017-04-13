package com.sumavision.talktv2.presenter;

import android.content.Context;

import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.WatchHistoryModel;
import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.model.impl.WatchHistoryModelImpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.IWatchHistoryView;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

import java.util.List;

public class CollectPresenter extends BasePresenter<IBaseView> {

    public CollectPresenter(Context context, IBaseView iView) {
        super(context, iView);
    }

    @Override
    public void release() {

    }
}
