package com.sumavision.talktv2.presenter;

import android.content.Context;

import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.YsqModel;
import com.sumavision.talktv2.model.entity.YsqBean;
import com.sumavision.talktv2.model.impl.YsqModelmpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.IYsqView;

/**
 * Created by zhoutao on 2016/6/6.
 */
public class YsqPresenter extends BasePresenter<IYsqView> {
    YsqModel model;
    public YsqPresenter(Context context, IYsqView iView) {
        super(context, iView);
        model = new YsqModelmpl();
    }

    public void getYsqData(int page,int size){
        iView.startRefresh();
        model.loadYsqData(new CallBackListener<YsqBean>() {
            @Override
            public void onSuccess(YsqBean ysqBean) {
                iView.stopRefresh();
                if(ysqBean.getItems().size() == 0){
                    iView.emptyData();
                }else{
                    iView.fillData(ysqBean);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                iView.stopRefresh();
            }
        },page,size);
    }
    @Override
    public void release() {
        model.release();
    }
}
