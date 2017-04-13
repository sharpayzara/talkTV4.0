package com.sumavision.talktv2.presenter;

import android.content.Context;

import com.jiongbull.jlog.JLog;
import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.SearchModel;
import com.sumavision.talktv2.model.entity.decor.SearchData;
import com.sumavision.talktv2.model.impl.SearchModelmpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.ISearchMainFragmentView;
import com.sumavision.talktv2.util.NetworkUtil;

/**
 * Created by zhoutao on 2016/6/26.
 */
public class ISearchMainPresenter extends BasePresenter<ISearchMainFragmentView> {

    SearchModel searchModel;
    Context mContext ;
    public ISearchMainPresenter(Context context, ISearchMainFragmentView iView) {
        super(context, iView);
        searchModel = new SearchModelmpl();
        mContext = context;
    }

    @Override
    public void release() {

    }
    public void getSearchData(){
        iView.showProgressBar();
        searchModel.loadSearchHotData(new CallBackListener<SearchData>(){
            @Override
            public void onSuccess(SearchData searchData) {
                if(searchData != null) {
                    iView.hideProgressBar();
                    if (searchData.results.size()>0){
                        iView.showHotList(searchData.results);
                    }else{
                        iView.showEmptyView();
                    }
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
                JLog.d("talkTV4.0", "访问异常");
            }
        });
    }
}
