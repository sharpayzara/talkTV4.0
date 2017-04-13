package com.sumavision.talktv2.presenter;

import android.content.Context;
import android.widget.Toast;

import com.jiongbull.jlog.JLog;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.PayModel;
import com.sumavision.talktv2.model.entity.WXOrder;
import com.sumavision.talktv2.model.impl.PayModelImpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.IPayView;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

/**
 * Created by sharpay on 2016/6/6.
 */
public class PayPresenter extends BasePresenter<IPayView> {
    private PayModel model;

    public PayPresenter(Context context, IPayView iView) {
        super(context, iView);
        model = new PayModelImpl();
    }

    public void getWXOrder(){
        Toast.makeText(context, "获取订单中...", Toast.LENGTH_SHORT).show();
        model.getWXOrder(new CallBackListener<WXOrder>() {
            @Override
            public void onSuccess(WXOrder wxOrder) {
                iView.respWXOrder(wxOrder);
            }

            @Override
            public void onFailure(Throwable throwable) {
                JLog.e(throwable.toString());
                Toast.makeText(context, "异常："+throwable.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void release() {
        model.release();
    }
}
