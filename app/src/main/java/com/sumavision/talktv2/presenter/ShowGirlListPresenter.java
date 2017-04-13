package com.sumavision.talktv2.presenter;

import android.content.Context;
import android.util.Log;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.MediaModel;
import com.sumavision.talktv2.model.ShowGirlModel;
import com.sumavision.talktv2.model.entity.MediaList;
import com.sumavision.talktv2.model.entity.ShowGirlList;
import com.sumavision.talktv2.model.impl.MediaModelImpl;
import com.sumavision.talktv2.model.impl.ShowGirlTopicModelImpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.IMediaCommonView;
import com.sumavision.talktv2.ui.iview.IShowGirlListView;
import com.sumavision.talktv2.util.NetworkUtil;

/**
 * 星秀list的Presenter
 * Created by sharpay on 2016/5/31.
 */
public class ShowGirlListPresenter extends BasePresenter<IShowGirlListView> {
    ShowGirlModel model;
    public ShowGirlListPresenter(Context context, IShowGirlListView iView) {
        super(context, iView);
        model = new ShowGirlTopicModelImpl();
    }

    @Override
    public void release() {
        model.release();
    }

    public void getShowGirlListData(int partId,int start,int offset){
        model.getShowGirlListData(new CallBackListener<ShowGirlList>() {

            @Override
            public void onSuccess(ShowGirlList showGirlList) {
                iView.hideProgressBar();
                iView.fillListData(showGirlList);
            }

            @Override
            public void onFailure(Throwable throwable) {
                if (!NetworkUtil.isConnectedByState(BaseApp.getContext())) {
                    iView.showWifiView();
                }else {
                    iView.showErrorView();
                }
                Log.e("error", throwable.toString());
            }
        },partId,start,offset);
    }
}
