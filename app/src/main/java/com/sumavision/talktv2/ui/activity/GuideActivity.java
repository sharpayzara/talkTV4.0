package com.sumavision.talktv2.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.presenter.GuidePresenter;
import com.sumavision.talktv2.ui.activity.base.BaseActivity;
import com.sumavision.talktv2.ui.adapter.GuidePagerAdapter;
import com.sumavision.talktv2.ui.fragment.PicFragment;
import com.sumavision.talktv2.ui.fragment.PreferenceFragment;
import com.sumavision.talktv2.ui.iview.base.IBaseView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by sharpay on 16-6-27.
 */
public class GuideActivity extends BaseActivity<GuidePresenter> implements IBaseView{
    GuidePresenter presenter;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    GuidePagerAdapter adapter;
    List<Fragment> list;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_guide;
    }

    @Override
    protected void initPresenter() {
        presenter = new GuidePresenter(this,this);
        presenter.init();
    }

    @Override
    public void initView() {
        list = new ArrayList<>();
        for(int i = 0 ;i<3; i++){
            PicFragment fragment = new PicFragment();
            Bundle bundle = new Bundle();
            bundle.putString("pic",""+i);
            fragment.setArguments(bundle);
            list.add(fragment);
        }
        list.add(new PreferenceFragment());
        adapter = new GuidePagerAdapter(getSupportFragmentManager(),list);
        viewPager.setAdapter(adapter);
    }
}
