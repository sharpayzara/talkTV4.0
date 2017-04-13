package com.sumavision.talktv2.presenter;

import android.content.Context;
import android.util.Log;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.MediaModel;
import com.sumavision.talktv2.model.entity.MediaList;
import com.sumavision.talktv2.model.impl.MediaModelImpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.IMediaCommonView;
import com.sumavision.talktv2.util.NetworkUtil;

/**
 * 自媒体首页界面的Presenter
 * Created by zjx on 2016/5/31.
 */
public class MediaCommonPresenter extends BasePresenter<IMediaCommonView> {
    MediaModel model;
    public MediaCommonPresenter(Context context, IMediaCommonView iView) {
        super(context, iView);
        model = new MediaModelImpl();
    }

    @Override
    public void release() {
        model.release();
    }

    public void getMediaListData(Integer page,Integer size,String txt){
        model.loadMediaList(new CallBackListener<MediaList>() {
            @Override
            public void onSuccess(MediaList mediaList) {
                iView.hideProgressBar();
                iView.fillListData(mediaList);
            }

            @Override
            public void onFailure(Throwable throwable) {
                iView.hideProgressBar();
                if (!NetworkUtil.isConnectedByState(BaseApp.getContext())) {
                    iView.showWifiView();
                }else {
                    iView.showErrorView();
                }
                Log.e("error", throwable.toString());
            }
        },page,size,txt);
    }
}
