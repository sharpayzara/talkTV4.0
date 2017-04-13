package com.sumavision.talktv2.presenter;

import android.content.Context;

import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.WatchHistoryModel;
import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.model.impl.WatchHistoryModelImpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.IWatchHistoryView;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by zhoutao on 2016/6/6.
 */
public class WatchHistoryPresenter extends BasePresenter<IWatchHistoryView> {

    private WatchHistoryModel model;
    public WatchHistoryPresenter(Context context, IWatchHistoryView iView) {
        super(context, iView);
        model = new WatchHistoryModelImpl();
    }
    public void getHistoryData(){
        iView.showProgressBar();
        model.getAll(new CallBackListener<List<PlayerHistoryBean>>() {
            @Override
            public void onSuccess(List<PlayerHistoryBean> beanList) {
                if(beanList.size() == 0){
                    iView.showEmptyView();
                }else{
                    iView.hideProgressBar();
                }
                iView.showListData(beanList);
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }


    public void delHistory (Integer id) {
        model.delPlayHistory(id);
    }

    @Override
    public void release() {

    }
}
