package com.sumavision.talktv2.presenter;

import android.content.Context;
import android.util.Log;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.ProgramListModel;
import com.sumavision.talktv2.model.entity.ProgramListData;
import com.sumavision.talktv2.model.impl.ProgramListModelImpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.IProgramListCommonView;
import com.sumavision.talktv2.util.NetworkUtil;

import java.util.Map;

/**
 * 自媒体首页界面的Presenter
 * Created by zjx on 2016/5/31.
 */
public class ProgramListCommonPresenter extends BasePresenter<IProgramListCommonView> {
    ProgramListModel model;
    public ProgramListCommonPresenter(Context context, IProgramListCommonView iView) {
        super(context, iView);
        model = new ProgramListModelImpl();
    }
    public  void getProgramListData(String tid,String cname,Integer page,Integer size){
        model.getProgramListData(tid,cname, page, size, new CallBackListener<ProgramListData>() {

            @Override
            public void onSuccess(ProgramListData programListData) {
                iView.hideProgressBar();
                if(programListData.getItems().size() == 0){
                    iView.emptyData();
                }else{
                    iView.fillData(programListData);
                }
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
        });
    }
    @Override
    public void release() {

    }

    public void getProgramListSelectionData(final Map<String ,String > map){
        model.getProgramListSelectionData(map, new CallBackListener<ProgramListData>() {
            @Override
            public void onSuccess(ProgramListData programListData) {
                iView.hideProgressBar();
                if(programListData.getItems().size() == 0){
                    iView.emptyData();
                }else{
                    iView.fillData(programListData);
                }
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
        });
    }

}
