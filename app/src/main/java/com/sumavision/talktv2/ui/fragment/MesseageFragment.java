package com.sumavision.talktv2.ui.fragment;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.presenter.MesseageFragmentPresenter;
import com.sumavision.talktv2.ui.fragment.Base.BaseFragment;
import com.sumavision.talktv2.ui.iview.base.IBaseView;
import butterknife.BindView;

/**
 * Created by zhoutao on 2016/6/15.
 */
public class MesseageFragment extends BaseFragment<MesseageFragmentPresenter> implements IBaseView{
    @BindView(R.id.rl_msg_null)
    RelativeLayout rl_msg_null;
    @BindView(R.id.rl_msg_full)
    RelativeLayout rl_msg_full;
    @BindView(R.id.lv_msg)
    ListView lv_msg;

    public MesseageFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_messeage;
    }

    @Override
    protected void initPresenter() {
        presenter = new MesseageFragmentPresenter(getContext(),this);
        presenter.init();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void initView() {
        //获取服务器数据如果没有数据则设置空页面显示

    }
}
