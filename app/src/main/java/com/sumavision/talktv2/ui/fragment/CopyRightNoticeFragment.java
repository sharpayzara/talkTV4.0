package com.sumavision.talktv2.ui.fragment;

import android.os.Bundle;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.presenter.CopyRightNoticeFragmentPresenter;
import com.sumavision.talktv2.ui.fragment.Base.BaseFragment;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

/**
 * Created by zhoutao on 2016/6/16.
 */
public class CopyRightNoticeFragment extends BaseFragment<CopyRightNoticeFragmentPresenter> implements IBaseView{
    public CopyRightNoticeFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_copyrightnotice;
    }

    @Override
    protected void initPresenter() {
        presenter = new CopyRightNoticeFragmentPresenter(this.getContext(),this);
        presenter.init();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void initView() {

    }
}
