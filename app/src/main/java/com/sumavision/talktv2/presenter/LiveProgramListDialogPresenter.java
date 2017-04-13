package com.sumavision.talktv2.presenter;

import android.content.Context;

import com.sumavision.talktv2.model.LiveProgramListDialogModel;
import com.sumavision.talktv2.model.impl.LiveProgramListDialogModelImpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.ILiveProgramListDialogView;

/**
 * 播放器内节目单列表的presenter
 * Created by zjx on 2016/6/21.
 */
public class LiveProgramListDialogPresenter extends BasePresenter<ILiveProgramListDialogView> {

    LiveProgramListDialogModel liveProgramListDialogModel;
    public LiveProgramListDialogPresenter(Context context, ILiveProgramListDialogView iView) {
        super(context, iView);
        liveProgramListDialogModel = new LiveProgramListDialogModelImpl();
    }


    @Override
    public void release() {

    }
}
