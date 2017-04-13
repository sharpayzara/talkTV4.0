package com.sumavision.talktv2.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.UserIntegeInfoItem;
import com.sumavision.talktv2.presenter.MyIntegralPresenter;
import com.sumavision.talktv2.ui.activity.base.CommonHeadPanelActivity;
import com.sumavision.talktv2.ui.iview.IIntegralView;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.DuibaUtill;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by sharpay on 16-5-24.
 */
public class MyIntegralActivity extends CommonHeadPanelActivity<MyIntegralPresenter> implements IIntegralView {
    MyIntegralPresenter presenter;
    String integral;
    @BindView(R.id.integral_tv)
    TextView integralTv;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_myintegral;
    }

    @Override
    protected void initPresenter() {
        presenter = new MyIntegralPresenter(this, this);
        presenter.init();
    }

    @Override
    public void initView() {
        BusProvider.getInstance().register(this);
        initHeadPanel();
        showBackBtn();
        setHeadTitle("我的积分");
        if(TextUtils.isEmpty(integral)){
            integralTv.setText("0");
        }
    }

    @Override
    protected void onResume() {
        if (AppGlobalVars.userInfo != null && !AppGlobalConsts.ISLOGINDUIBA){
            presenter.getUserIntegtion(AppGlobalVars.userInfo.getUserId());
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    @OnClick({R.id.integral_tv,R.id.tip_tv})
    void click(View view) {
        if (AppGlobalVars.userInfo != null ) {
            presenter.getLoginAddress(AppGlobalVars.userInfo.getUserId(), integral + "",null);
        } else {
            presenter.getLoginAddress("not_login","0",null);
        }
    }
    @Override
    public void enterIntegeteCenter(String url) {
        DuibaUtill.enterDuiba(url,"#0acbc1","#ffffff",this);
    }
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("refreshIntg")})
    public void refreshDuiba(String refreshEdit) {
        presenter.getUserIntegtion(AppGlobalVars.userInfo.getUserId());
    }
    @Override
    public void setUserIntegeInfo(UserIntegeInfoItem userIntegeInfoItem) {
        integral = userIntegeInfoItem.getObj()+"";
        integralTv.setText(integral);
        if (AppGlobalVars.userInfo != null && AppGlobalConsts.ISLOGINDUIBA ){
            presenter.getLoginAddress(AppGlobalVars.userInfo.getUserId(),integral,AppGlobalConsts.newDuibaUrl);
            AppGlobalConsts.ISLOGINDUIBA = false;
        }
    }

}
