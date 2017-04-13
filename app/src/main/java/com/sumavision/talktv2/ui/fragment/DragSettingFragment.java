package com.sumavision.talktv2.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.presenter.DragSettingFragmentPresenter;
import com.sumavision.talktv2.ui.activity.DragSettingActivity;
import com.sumavision.talktv2.ui.fragment.Base.BaseFragment;
import com.sumavision.talktv2.ui.iview.base.IBaseView;
import com.sumavision.talktv2.util.AppGlobalConsts;

import butterknife.BindView;

/**
 * Created by zhoutao on 2016/5/26.
 */
public class DragSettingFragment extends BaseFragment implements View.OnClickListener,IBaseView{
    private static final String TYPE = "type";
    private String type;
    @BindView(R.id.dragsetting_btn_list)
    RelativeLayout dragsetting_btn_list;
    @BindView(R.id.dragsetting_btn_grid)
    RelativeLayout dragsetting_btn_grid;

    public DragSettingFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(TYPE);
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_dragsetting;
    }

    @Override
    protected void initPresenter() {
        presenter = new DragSettingFragmentPresenter(getContext(),this);
        presenter.init();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onClick(View v) {
        ((View.OnClickListener)getActivity()).onClick(v);
    }

    @Override
    public void initView() {
        dragsetting_btn_list.setOnClickListener(this);
        dragsetting_btn_grid.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        AppGlobalConsts.TITLE_TXT=0;
        ((DragSettingActivity)getActivity()).refreshToolbar();
        super.onResume();
    }

}
