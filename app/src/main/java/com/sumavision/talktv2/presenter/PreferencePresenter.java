package com.sumavision.talktv2.presenter;

import android.content.Context;

import com.sumavision.talktv2.model.PreferenceModel;
import com.sumavision.talktv2.model.entity.PreferenceBean;
import com.sumavision.talktv2.model.impl.PreferenceModelImpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.IPreferenceView;

/**
 * Created by zjx on 2016/5/31.
 */
public class PreferencePresenter extends BasePresenter<IPreferenceView> {
    Context mContext;
    PreferenceModel model;
    public PreferencePresenter(Context context, IPreferenceView iView) {
        super(context, iView);
        mContext = context;
        model = new PreferenceModelImpl();
    }
    public void loadPreferenceStr(){
        iView.fillData(model.loadPreferenceStr());
    }
    public void savePreferenceStr(PreferenceBean bean){
        model.savePreferenceStr(bean);
    }
    public void sendPreferenceStr(PreferenceBean bean){model.sendPreferenceStr(bean);}
    @Override
    public void release() {

    }
}
