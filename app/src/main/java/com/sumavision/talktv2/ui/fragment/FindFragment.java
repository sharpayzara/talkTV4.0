package com.sumavision.talktv2.ui.fragment;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.presenter.FindPresenter;
import com.sumavision.talktv2.ui.fragment.Base.BaseFragment;

/**
 * Created by zjx on 2016/5/31.
 */
public class FindFragment extends BaseFragment<FindPresenter> {
    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_find;
    }

    @Override
    protected void initPresenter() {

    }
    @Override
    public boolean onBackPressed() {
        return false;
    }
}
