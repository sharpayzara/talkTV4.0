package com.sumavision.talktv2.ui.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.ClassifyItem;
import com.sumavision.talktv2.presenter.ClassifyRecommendPresenter;
import com.sumavision.talktv2.ui.adapter.ClassifyRecommendPagerAdapter;
import com.sumavision.talktv2.ui.fragment.Base.BaseFragment;
import com.sumavision.talktv2.ui.iview.IClassifyRecommendView;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;

/**
 * 推荐界面的Fragment
 * Created by zjx on 2016/5/31.
 */
public class ClassifyRecommendFragment extends BaseFragment<ClassifyRecommendPresenter> implements IClassifyRecommendView {

    @BindView(R.id.classify_tab_layout)
    TabLayout classify_tab_layout;
    @BindView(R.id.classify_tab_container)
    ViewPager classify_tab_container;

    List<ClassifyItem> list_title;
    ClassifyRecommendPagerAdapter classifyRecommendPagerAdapter;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_classifyrecomend;
    }

    @Override
    protected void initPresenter() {
        presenter = new ClassifyRecommendPresenter(getContext(), this);
        presenter.init();
    }

    @Override
    public void initView() {
        list_title = new ArrayList<>();
        //获取list的数据
        list_title.add(new ClassifyItem(1,"1234","","",""));
        list_title.add(new ClassifyItem(1,"2234","","",""));
        list_title.add(new ClassifyItem(1,"3234","","",""));
        list_title.add(new ClassifyItem(1,"4234","","",""));
        classifyRecommendPagerAdapter =new ClassifyRecommendPagerAdapter(this.getChildFragmentManager(),list_title);
        classify_tab_container.setAdapter(classifyRecommendPagerAdapter);
        classify_tab_container.setOffscreenPageLimit(3);
        classify_tab_layout.setTabMode(TabLayout.MODE_SCROLLABLE);
        classify_tab_layout.setupWithViewPager(classify_tab_container);
        classify_tab_layout.invalidate();
        classifyRecommendPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.release();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
