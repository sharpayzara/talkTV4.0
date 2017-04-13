package com.sumavision.talktv2.presenter;

import android.content.Context;

import com.jiongbull.jlog.JLog;
import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.ShowGirlModel;
import com.sumavision.talktv2.model.entity.ShowGirlTopic;
import com.sumavision.talktv2.model.impl.ShowGirlTopicModelImpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.IFindView;
import com.sumavision.talktv2.ui.iview.IShowGirlView;
import com.sumavision.talktv2.util.NetworkUtil;

/**
 * Created by zjx on 2016/5/31.
 */
public class ShowGirlPresenter extends BasePresenter<IShowGirlView> {
    ShowGirlModel model;

    public ShowGirlPresenter(Context context, IShowGirlView iView) {
        super(context, iView);
        model = new ShowGirlTopicModelImpl();
    }

    public void loadShowGirlTopicData(){
        iView.showProgressBar();
        model.getShowGirlTopicData(new CallBackListener<ShowGirlTopic>() {
            @Override
            public void onSuccess(ShowGirlTopic showGirlTopic) {
                iView.hideProgressBar();
                iView.fillTopicData(showGirlTopic);
            }

            @Override
            public void onFailure(Throwable throwable) {
                if (!NetworkUtil.isConnectedByState(BaseApp.getContext())) {
                    iView.showWifiView();
                }else {
                    iView.showErrorView();
                }
                JLog.e("error", throwable.toString());
            }
        });
    }

    @Override
    public void release() {
        model.release();
    }
}
