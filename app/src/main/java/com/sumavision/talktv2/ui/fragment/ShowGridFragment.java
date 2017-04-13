package com.sumavision.talktv2.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.ShowGirlTopic;
import com.sumavision.talktv2.presenter.ShowGirlPresenter;
import com.sumavision.talktv2.ui.adapter.ShowGirlPagerAdapter;
import com.sumavision.talktv2.ui.fragment.Base.BaseFragment;
import com.sumavision.talktv2.ui.iview.IShowGirlView;
import com.sumavision.talktv2.ui.widget.LoadingLayout;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.NoDoubleClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by zhoutao on 2016/5/26.
 */
public class ShowGridFragment extends BaseFragment<ShowGirlPresenter> implements IShowGirlView {
    ShowGirlPresenter presenter;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.tab_container)
    ViewPager tabContainer;
    List<String> titleList;
    @BindView(R.id.loading_layout)
    LoadingLayout loadingLayout;
    List<Fragment> fragmentList;
    ShowGirlPagerAdapter pagerAdapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_showgirl;
    }

    @Override
    protected void initPresenter() {
        presenter = new ShowGirlPresenter(this.getContext(), this);
        presenter.init();
    }

    @Override
    public boolean onBackPressed() {
        BusProvider.getInstance().post("returnHome","returnHome");
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    public void initView() {
        pagerAdapter = new ShowGirlPagerAdapter(getChildFragmentManager());
        fragmentList = new ArrayList<>();
        titleList = new ArrayList<>();
        tabContainer.setAdapter(pagerAdapter);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(tabContainer);
        tabLayout.invalidate();
        loadingLayout.setOnRetryClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                presenter.loadShowGirlTopicData();
            }
        });
        presenter.loadShowGirlTopicData();
    }

    @Override
    public void showProgressBar() {
        loadingLayout.showProgressBar();
    }

    @Override
    public void hideProgressBar() {
        loadingLayout.hideProgressBar();
    }

    @Override
    public void showErrorView() {
        loadingLayout.showErrorView();
    }

    @Override
    public void showWifiView() {
        loadingLayout.showWifiView();
    }

    @Override
    public void fillTopicData(ShowGirlTopic topic) {
        fragmentList.clear();
        for(ShowGirlTopic.PartListBean bean : topic.getPartList()){
            ShowGirlListFragment fragment = new ShowGirlListFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("partId",bean.getPartId());
            fragment.setArguments(bundle);
            fragmentList.add(fragment);
            titleList.add(bean.getPartName());
        }
        pagerAdapter.setList(fragmentList,titleList);
        pagerAdapter.notifyDataSetChanged();
    }
}