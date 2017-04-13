package com.sumavision.talktv2.presenter;

import android.content.Context;

import com.jiongbull.jlog.JLog;
import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.dao.RxDao;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.SearchModel;
import com.sumavision.talktv2.model.entity.HttpCache;
import com.sumavision.talktv2.model.entity.decor.SearchInfoData;
import com.sumavision.talktv2.model.impl.SearchModelmpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.ISearchListInfoView;
import com.sumavision.talktv2.util.NetworkUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhoutao on 2016/6/24.
 */
public class ISearchListPresenter extends BasePresenter<ISearchListInfoView> {
    SearchModel searchModel;
    Context mContext ;
    private static Map<String,String> map = new HashMap<String,String>();
    private static RxDao httpCacheDao = new RxDao(BaseApp.getContext(), HttpCache.class);
    public ISearchListPresenter(Context context, ISearchListInfoView iView) {
        super(context, iView);
        searchModel = new SearchModelmpl();
        mContext = context;
    }

    @Override
    public void release() {

    }

    public void getSearchListInfo(String q, final int page) {
        searchModel.loadSearchListInfo(new CallBackListener<SearchInfoData>() {
            @Override
            public void onSuccess(SearchInfoData searchInfoData) {
                iView.hidePPPro();
                iView.hideProgressBar();
                    iView.showSearchList(searchInfoData.results,page);
            }

            @Override
            public void onFailure(Throwable throwable) {
                iView.hidePPPro();
                iView.hideProgressBar();
                if (!NetworkUtil.isConnectedByState(BaseApp.getContext())) {
                    iView.showWifiView();
                } else {
                    iView.showErrorView();
                }
                JLog.d("talkTV4.0", "访问异常");
            }
        }, q,page);
    }
}
