package com.sumavision.talktv2.presenter;

import android.content.Context;

import com.sumavision.talktv2.model.LiveChannelListDialogModel;
import com.sumavision.talktv2.model.entity.decor.LiveData;
import com.sumavision.talktv2.model.impl.LiveChannelListDialogModelImpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.ILiveChannelListDialogView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 直播播放器内的频道列表的Presenter
 * Created by zjx on 2016/6/20.
 */
public class LiveChannelListDialogPresenter extends BasePresenter<ILiveChannelListDialogView>{

    private LiveChannelListDialogModel liveChannelListDialogModel;

    public LiveChannelListDialogPresenter(Context context, ILiveChannelListDialogView iView) {
        super(context, iView);

        liveChannelListDialogModel = new LiveChannelListDialogModelImpl();
    }

    public void getSelector(String channelId, HashMap<Integer, ArrayList<LiveData.ContentBean.TypeBean.ChannelBean>> channelDatas){
        iView.getSelector(liveChannelListDialogModel.getSelector(channelId, channelDatas));
    }

    @Override
    public void release() {

    }
}
