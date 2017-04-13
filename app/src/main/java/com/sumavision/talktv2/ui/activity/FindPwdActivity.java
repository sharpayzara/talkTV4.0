package com.sumavision.talktv2.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.presenter.FindPwdPresenter;
import com.sumavision.talktv2.ui.activity.base.CommonHeadPanelActivity;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sharpay on 16-5-24.
 */
public class FindPwdActivity extends CommonHeadPanelActivity<FindPwdPresenter> implements IBaseView {
    FindPwdPresenter presenter;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_find_pwd;
    }

    @Override
    protected void initPresenter() {
        presenter = new FindPwdPresenter(this, this);
        presenter.init();
    }

    @Override
    public void initView() {
        initHeadPanel();
        showBackBtn();
        setHeadTitle("找回密码");

    }

    @OnClick(R.id.call_btn)
    public void onClick() {
        Intent in2 = new Intent();
        in2.setAction(Intent.ACTION_DIAL);
        String tel = "tel:" + "010-58851005";
        in2.setData(Uri.parse(tel));
        startActivity(in2);
    }
}
