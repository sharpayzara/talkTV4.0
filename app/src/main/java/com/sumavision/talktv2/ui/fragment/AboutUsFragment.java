package com.sumavision.talktv2.ui.fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.presenter.AboutUsFragmentPresenter;
import com.sumavision.talktv2.ui.fragment.Base.BaseFragment;
import com.sumavision.talktv2.ui.iview.base.IBaseView;
import com.sumavision.talktv2.util.AppUtil;

import butterknife.BindView;

/**
 * Created by zhoutao on 2016/6/17.
 */
public class AboutUsFragment extends BaseFragment<AboutUsFragmentPresenter> implements IBaseView{
    @BindView(R.id.tv_aboutus_versioncode)
    TextView tv_aboutus_versioncode;
    public AboutUsFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_aboutus;
    }

    @Override
    protected void initPresenter() {
        presenter = new AboutUsFragmentPresenter(this.getContext(),this);
        presenter.init();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void initView() {

        setVersionCode();
    }

    private void setVersionCode() {
        tv_aboutus_versioncode.setText("Ver   "+ AppUtil.getAppVersionName(this.getContext()));
    }
}
