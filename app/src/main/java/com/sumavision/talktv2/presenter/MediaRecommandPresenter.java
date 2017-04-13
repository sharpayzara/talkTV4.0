package com.sumavision.talktv2.presenter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.MediaModel;
import com.sumavision.talktv2.model.entity.MediaList;
import com.sumavision.talktv2.model.impl.MediaModelImpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.IMediaRecommandView;
import com.sumavision.talktv2.util.NetworkUtil;

/**
 * 自媒体首页界面的Presenter
 * Created by zjx on 2016/5/31.
 */
public class MediaRecommandPresenter extends BasePresenter<IMediaRecommandView> {
    MediaModel model;
    public MediaRecommandPresenter(Context context, IMediaRecommandView iView) {
        super(context, iView);
        model = new MediaModelImpl();
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

    public void pvLog(String id, Context context) {
        model.pvLog(id, context);
    }

    @Override
    public void release() {
        model.release();
    }

//    public void judgeNetwork(){
////        if(NetworkUtil.isConnectedByState(context) && !NetworkUtil.isWIFIConnected(context)){
////            Toast.makeText(context,"你当前使用的是非WIFI网络，请注意流量!",Toast.LENGTH_LONG).show();
////        }
//    }
}
