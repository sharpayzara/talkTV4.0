package com.sumavision.talktv2.presenter;

import android.content.Context;

import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.SearchModel;
import com.sumavision.talktv2.model.entity.HintData;
import com.sumavision.talktv2.model.impl.SearchModelmpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.ISearchView;

import rx.Subscription;

/**
 * Created by zhoutao on 2016/6/24.
 */
public class SearchPresenter extends BasePresenter<ISearchView> {
    Context mContext ;
    Subscription subscription;
    SearchModel model;
    public SearchPresenter(Context context, ISearchView iView) {
        super(context, iView);
        mContext = context;
        model = new SearchModelmpl();
    }

    @Override
    public void release() {
        model.release();
    }
    public void getHintData(String key){
        model.loadHintData(new CallBackListener<HintData>() {
            @Override
            public void onSuccess(HintData hintData) {
                iView.fillHintData(hintData);
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        },key);
    }
}
