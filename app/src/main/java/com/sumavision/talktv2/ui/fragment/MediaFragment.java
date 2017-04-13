package com.sumavision.talktv2.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.MediaTopic;
import com.sumavision.talktv2.presenter.MediaPresenter;
import com.sumavision.talktv2.ui.activity.DragSettingActivity;
import com.sumavision.talktv2.ui.activity.SearchActivity;
import com.sumavision.talktv2.ui.activity.WatchHistoryActivity;
import com.sumavision.talktv2.ui.adapter.MediaPagerAdapter;
import com.sumavision.talktv2.ui.fragment.Base.BaseFragment;
import com.sumavision.talktv2.ui.iview.IMediaView;
import com.sumavision.talktv2.ui.widget.LoadingLayout;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.CommonUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 自媒体首页界面的Fragment
 * Created by zjx on 2016/5/31.
 */
public class MediaFragment extends BaseFragment<MediaPresenter> implements IMediaView,View.OnClickListener {
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.tvfan_search)
    public Button tvfan_search;
    @BindView(R.id.tvfan_history)
    public Button tvfan_history;
    @BindView(R.id.tvfan_more)
    public Button tvfan_more;
    private PopupWindow window;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.tab_container)
    ViewPager tabContainer;
    @BindView(R.id.loading_layout)
    LoadingLayout loadingLayout;
    MediaPagerAdapter pagerAdapter;
    MediaPresenter presenter;
    List<Fragment> fragmentList;
    List<MediaTopic.ItemsBean> titleList;
    LinearLayout ll_pop;
    MediaRecommendFragment recommendFragment;
    private LinearLayout ll_morewindow_cache,ll_morewindow_messeage,ll_morewindow_setting;
    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_media;
    }

    @Override
    protected void initPresenter() {
        presenter = new MediaPresenter(this.getContext(),this);
        presenter.init();
    }

    @Override
    public boolean onBackPressed() {
        BusProvider.getInstance().post("returnHome","returnHome");
        return true;
    }
    @Override
    public void onDestroyView() {
        BusProvider.getInstance().unregister(this);
        super.onDestroyView();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden){
            if(tabContainer != null && tabContainer.getCurrentItem() == 0){
                if(recommendFragment != null){
                  recommendFragment.setUserVisibleHint(!hidden);
                }
            }
        }else{
            if(recommendFragment != null){
                recommendFragment.setUserVisibleHint(!hidden);
            }
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    public void onResume() {
        super.onResume();
        if(tabLayout.getSelectedTabPosition() == 0){
            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_A,true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(tabLayout.getSelectedTabPosition() == 0){
            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_A,false);
        }
    }


    public void initView() {
        BusProvider.getInstance().register(this);
        fragmentList = new ArrayList<>();
        pagerAdapter = new MediaPagerAdapter(this.getChildFragmentManager());
        tabContainer.setAdapter(pagerAdapter);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(tabContainer);
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(tabContainer) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabContainer.setCurrentItem(tab.getPosition());
                try {
                    String name = titleList.get(tab.getPosition()).getNavName();
                    MobclickAgent.onEvent(getContext(), "4rddbtag", name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.invalidate();
        presenter.getMediaTopicData();
        loadingLayout.setOnRetryClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.getMediaTopicData();
            }
        });
        tvfan_search.setOnClickListener(this);
        tvfan_history.setOnClickListener(this);
        tvfan_more.setOnClickListener(this);
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("enterHot")})
    public void enterOtherFragment(String cardid) {
        int position = 0;
        if(titleList != null){
            for (int i = 0;i<titleList.size();i++){
                if (cardid.equals(titleList.get(i).getNavCode())){
                    position = i;
                }
            }
        }
        tabContainer.setCurrentItem(position);
    }

    @Override
    public void fillTopicData(MediaTopic mediaTopic) {
        fragmentList.clear();
        recommendFragment= new MediaRecommendFragment();
        Bundle recommendBundle = new Bundle();
        recommendBundle.putString("txt",mediaTopic.getItems().get(0).getValue());
        recommendFragment.setArguments(recommendBundle);
        fragmentList.add(recommendFragment);
        titleList = new ArrayList<>();
        List<MediaTopic.ItemsBean> list= mediaTopic.getItems();
        titleList.add(mediaTopic.getItems().get(0));
        list.remove(0);
        for(MediaTopic.ItemsBean bean : list){
            MediaCommonFragment fragment = new MediaCommonFragment();
            Bundle bundle = new Bundle();
            bundle.putString("txt",bean.getValue());
            fragment.setArguments(bundle);
            fragmentList.add(fragment);
        }
        titleList.addAll(list);
        pagerAdapter.setList(fragmentList,titleList);
        pagerAdapter.notifyDataSetChanged();
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tvfan_search:
//                enterClassifyRecommend();
                enterSearch();
                break;

            case R.id.tvfan_history://播放历史
                Intent historyIntent = new Intent(this.getActivity(), WatchHistoryActivity.class);
                startActivity(historyIntent);
                /*//个人定制
                enterNext(001);*/
                break;

            case R.id.tvfan_more://更多
//                if (window != null && window.isShowing()) {
//                    window.dismiss();
//                    window = null;
//                    return;
//                }
                showPop();
                break;
            case R.id.ll_morewindow_cache://缓存
                enterNext(AppGlobalConsts.EnterType.ENTERHUANCUN);
                window.dismiss();
                break;
//            case R.id.ll_morewindow_messeage://消息
//                enterNext(3);
//                window.dismiss();
//                break;
            case R.id.ll_morewindow_setting://设置
                enterNext(AppGlobalConsts.EnterType.ENTERSHEZHI);
                window.dismiss();
                break;
            default:
                break;
        }
    }
    private void showPop() {
        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popupwindow_more, null);
        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
        window = new PopupWindow(view,
                CommonUtil.dip2px(getContext(),110),
                CommonUtil.dip2px(getContext(),125));
        window.setAnimationStyle(R.style.mypopwindow_anim_style);
        window.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
        ll_pop = (LinearLayout) view.findViewById(R.id.ll_pop);
        window.showAtLocation(toolbar,
                Gravity.NO_GRAVITY, CommonUtil.dip2px(getContext(),330),  CommonUtil.dip2px(getContext(),68));
        ll_pop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (window != null && window.isShowing()) {
                    window.dismiss();
                    window = null;
                }
                return true;
            }
        });
        ll_morewindow_cache = (LinearLayout) view.findViewById(R.id.ll_morewindow_cache);
//        ll_morewindow_messeage= (LinearLayout)view. findViewById(R.id.ll_morewindow_messeage);
        ll_morewindow_setting = (LinearLayout)view. findViewById(R.id.ll_morewindow_setting);
        ll_morewindow_cache.setOnClickListener(this);
//        ll_morewindow_messeage.setOnClickListener(this);
        ll_morewindow_setting.setOnClickListener(this);
    }
    public boolean dismissWindow(){
        if (window != null && window.isShowing()){
            window.dismiss();
            return true;
        }
        return false;
    }
    private void enterNext(String id) {
        Intent intent = new Intent();
        intent.setClass(this.getActivity(), DragSettingActivity.class);
        intent.putExtra("enternext",id);
        startActivity(intent);
    }
    private void enterSearch() {
        Intent intent = new Intent();
        intent.setClass(this.getActivity(), SearchActivity.class);
        startActivity(intent);
    }
}
