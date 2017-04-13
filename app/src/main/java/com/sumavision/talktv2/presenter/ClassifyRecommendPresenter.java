package com.sumavision.talktv2.presenter;

import android.content.Context;

import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.IClassifyRecommendView;

/**
 * 1级分类推荐页面的presenter
 * Created by zhoutao on 2016/6/14.
 */
public class ClassifyRecommendPresenter extends BasePresenter<IClassifyRecommendView> {

    public ClassifyRecommendPresenter(Context context, IClassifyRecommendView iView) {
        super(context, iView);

    }

    @Override
    public void release() {

    }
}
