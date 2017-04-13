package com.sumavision.talktv2.presenter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.MediaModel;
import com.sumavision.talktv2.model.entity.MediaDetail;
import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.model.impl.MediaModelImpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.IMediaDetailView;
import com.sumavision.talktv2.ui.iview.ISpecialDetailView;
import com.sumavision.talktv2.util.NetworkUtil;

/**
 * Created by sharpay on 16-6-17.
 */
public class SpeicalDetailPresenter extends BasePresenter<ISpecialDetailView>{
    MediaModel model;
    public SpeicalDetailPresenter(Context context, ISpecialDetailView iView) {
        super(context, iView);
        model = new MediaModelImpl();
    }

//    public void judgeNetwork(){
//        if(NetworkUtil.isConnectedByState(context) && !NetworkUtil.isWIFIConnected(context)){
//            Toast.makeText(context,"你当前使用的是非WIFI网络，请注意流量!",Toast.LENGTH_LONG).show();
//        }
//    }


    @Override
    public void release() {

    }
}
