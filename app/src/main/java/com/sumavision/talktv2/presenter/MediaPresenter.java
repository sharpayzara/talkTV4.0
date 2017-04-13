package com.sumavision.talktv2.presenter;

import android.content.Context;

import com.jiongbull.jlog.JLog;
import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.MediaModel;
import com.sumavision.talktv2.model.entity.MediaTopic;
import com.sumavision.talktv2.model.impl.MediaModelImpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.IMediaView;
import com.sumavision.talktv2.util.NetworkUtil;

/**
 * Created by sharpay on 2016/6/31.
 */
public class MediaPresenter extends BasePresenter<IMediaView> {
    MediaModel model;
    public MediaPresenter(Context context, IMediaView iView) {
        super(context, iView);
        model = new MediaModelImpl();
    }

    public void getMediaTopicData(){
        iView.showProgressBar();
        model.loadMediaTopic(new CallBackListener<MediaTopic>() {
            @Override
            public void onSuccess(MediaTopic mediaTopic) {
                iView.hideProgressBar();
                iView.fillTopicData(mediaTopic);
            }

            @Override
            public void onFailure(Throwable throwable) {
                iView.hideProgressBar();
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
