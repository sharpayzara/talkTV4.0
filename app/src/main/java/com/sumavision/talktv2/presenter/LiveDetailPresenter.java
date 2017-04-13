package com.sumavision.talktv2.presenter;

import android.content.Context;
import android.widget.Toast;

import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.LiveDetailModel;
import com.sumavision.talktv2.model.entity.decor.LiveDetailData;
import com.sumavision.talktv2.model.impl.LiveDetailModelmpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.ILiveDetailView;
import com.sumavision.talktv2.util.NetworkUtil;

/**
 * 直播详情界面相关的IView
 * Created by zjx on 2016/6/14.
 */
public class LiveDetailPresenter extends BasePresenter<ILiveDetailView> {

    private LiveDetailModel liveDetailModel;
    public LiveDetailPresenter(Context context, ILiveDetailView iView) {
        super(context, iView);
        liveDetailModel = new LiveDetailModelmpl();
    }

    /**
     * 获取频道详情数据
     */
    public void getLiveDetail (String channelId) {
        liveDetailModel.getLiveDetailData(channelId, new CallBackListener<LiveDetailData>() {
            @Override
            public void onSuccess(LiveDetailData liveDetailData) {
                iView.getLiveDetail(liveDetailData);
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }

//    public void judgeNetwork(){
////        if(NetworkUtil.isConnectedByState(context) && !NetworkUtil.isWIFIConnected(context)){
////            Toast.makeText(context,"你当前使用的是非WIFI网络，请注意流量!",Toast.LENGTH_LONG).show();
////        }
//    }

    public void favChannel (String chnnnelId) {
        liveDetailModel.favChannel(chnnnelId);
    }

    public void cancelFav (String channelId) {
        liveDetailModel.cancelChannel(channelId);
    }
    @Override
    public void release() {

    }
}
