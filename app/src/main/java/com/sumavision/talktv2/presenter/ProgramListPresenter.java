package com.sumavision.talktv2.presenter;

import android.content.Context;
import android.util.Log;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.ProgramListModel;
import com.sumavision.talktv2.model.entity.ProgramListTopic;
import com.sumavision.talktv2.model.entity.ProgramSelection;
import com.sumavision.talktv2.model.impl.ProgramListModelImpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.IProgramListView;
import com.sumavision.talktv2.util.NetworkUtil;

import java.util.Map;

/**
 * Created by sharpay on 2016/6/24.
 */
public class ProgramListPresenter extends BasePresenter<IProgramListView> {
    ProgramListModel model;
    public ProgramListPresenter(Context context, IProgramListView iView) {
        super(context, iView);
        model = new ProgramListModelImpl();
    }
    @Override
    public void release() {

    }

    public void getProgramListTopic(final String id){
        iView.showProgressBar();
        model.getProgramListTopic(id, new CallBackListener<ProgramListTopic>() {

            @Override
            public void onSuccess(ProgramListTopic programListTopic) {
                getProgramSelectionData(id,programListTopic);
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

    public void getProgramListSelectionData(final Map<String ,String > map,CallBackListener listener){
        model.getProgramListSelectionData(map, listener);
    }

    public  void getProgramListData(String tid,String cname,Integer page,Integer size,CallBackListener listener){
        model.getProgramListData(tid,cname,page,size, listener);
    }
    public void getProgramSelectionData(String id, final ProgramListTopic programListTopic){
        model.getProgramSelectionData(id, new CallBackListener<ProgramSelection>() {

            @Override
            public void onSuccess(ProgramSelection programSelection) {
                iView.hideProgressBar();
                if(programSelection.getItems().size() == 0){
                    iView.showEmptyView();
                }else{
                    iView.fillTopicAndSeletionData(programSelection,programListTopic);
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
