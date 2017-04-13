package com.sumavision.talktv2.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.sumavision.offlinelibrary.entity.InternalExternalPathInfo;
import com.sumavision.offlinelibrary.util.CommonUtils;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.CacheDeleteSeleteEvent;
import com.sumavision.talktv2.model.entity.CacheTabStyle;
import com.sumavision.talktv2.presenter.OwnCacheFragmentPresenter;
import com.sumavision.talktv2.ui.adapter.MyPagerAdapter;
import com.sumavision.talktv2.ui.fragment.Base.BaseFragment;
import com.sumavision.talktv2.ui.iview.base.IBaseView;
import com.sumavision.talktv2.ui.widget.ChangeCachePathDialog;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.CommonUtil;
import com.sumavision.talktv2.util.PreferencesUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by zhoutao on 2016/6/20.
 */
public class OwnCacheFragment extends BaseFragment<OwnCacheFragmentPresenter> implements IBaseView, ViewPager.OnPageChangeListener {
    @BindView(R.id.tabs)
    com.sumavision.talktv2.ui.widget.PagerSlidingTabStrip tabs;
    @BindView(R.id.vp_cache_viewpager)
    android.support.v4.view.ViewPager vp_cache_viewpager;
    @BindView(R.id.spaceSize)
    TextView spaceSize;
    @BindView(R.id.iv_space)
    ImageView iv_space;
    @BindView(R.id.fl_owncache)
    FrameLayout fl_owncache;
    @BindView(R.id.ll_cache_viewgroup)
    LinearLayout ll_cache_viewgroup;
    @BindView(R.id.rl_owncache)
    RelativeLayout rl_owncache;
    @BindView(R.id.btn_caching_all)
    Button btn_caching_all;
    @BindView(R.id.btn_caching_delete)
    Button btn_caching_delete;

    protected CachingFragment cachingFragment;
    protected CachedFragment cachedFragment;
    private ArrayList<Fragment> fragments;
    private boolean selectAll;
    public OwnCacheFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_owncache;
    }

    @Override
    protected void initPresenter() {
        presenter = new OwnCacheFragmentPresenter(getContext(), this);
        presenter.init();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }


    @Override
    public void initView() {
        fragments = new ArrayList<Fragment>();
        cachingFragment = new CachingFragment();
        cachedFragment = new CachedFragment();
        fragments.add(cachedFragment);
        fragments.add(cachingFragment);
        ArrayList<String> titles = new ArrayList<String>();
        titles.add("已完成");
        titles.add("未完成");
        MyPagerAdapter adapter = new MyPagerAdapter(
                this.getActivity().getSupportFragmentManager(), titles, fragments);
        vp_cache_viewpager.setAdapter(adapter);
        tabs.setOnPageChangeListener(this);

        tabs.setViewPager(vp_cache_viewpager, -1);

        setTabsValue();
        refreshBottomShow();

        fl_owncache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeCachePathDialog();
            }
        });
    }

    ChangeCachePathDialog changeCachePathDialog;
    private void showChangeCachePathDialog() {
        if (changeCachePathDialog == null)
            changeCachePathDialog = new ChangeCachePathDialog(getActivity(), R.style.ExitDialog);
        changeCachePathDialog.show();
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(AppGlobalConsts.EventType.CHANGE_PATH)})
    public void cachePathSelected(ChangeCachePathDialog.PathData data) {
        spaceSize.setText((data.pathType == 0 ? "手机存储" : "SD卡") + data.showInfo);
    }

    private void refreshBottomShow() {
        String space = "";
//        int width = 0;
//        double totalSize = CommonUtil.getTotalExternalMemorySize() / 1024 / 1024 / 1024.0;
//        double availableSize = CommonUtil.getAvailableExternalMemorySize() / 1024 / 1024 / 1024;
//        java.text.DecimalFormat df = new java.text.DecimalFormat("#.#");
//        width = (int) (availableSize / totalSize * (this.getContext().getResources().getDisplayMetrics().widthPixels));
//        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, this.getContext().getResources().getDisplayMetrics().heightPixels);
//        iv_space.setLayoutParams(lp);
//        iv_space.setAlpha((float) 0.1);
//        space = String.format("手机存储  可用：" + df.format(availableSize) + " G / 共：" + df.format(totalSize) + "G", "0", "0");
        InternalExternalPathInfo internalExternalPathInfo = CommonUtils
                .getInternalExternalPath(getContext());
        int tmp = PreferencesUtils.getInt(getContext(), null, "cache_path_type");
        if (internalExternalPathInfo.emulatedSDcard != null || internalExternalPathInfo.removableSDcard != null)
            space = ChangeCachePathDialog.getLeftSpaceInfo(tmp == 0 ? internalExternalPathInfo.emulatedSDcard: internalExternalPathInfo.removableSDcard);
        else
            space = "0";
        spaceSize.setText((tmp == 0 ? "手机存储 ": "SD卡 ") + space);
        iv_space.invalidate();
        spaceSize.invalidate();
    }

    private void setTabsValue() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        CacheTabStyle style = new CacheTabStyle();
        style.selectedTextColorRes = R.color.cache_start_bg;
        style.textColorRes = R.color.light_black;
        style.textSize = 14;
        tabs.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, style.textSize, dm));
        tabs.setTextColorResource(style.textColorRes);
        tabs.setSelectedTextColorResource(style.selectedTextColorRes);
        tabs.setTabBackground(0);
        tabs.setUnderlineColor(Color.parseColor("#ededed"));
        tabs.setDividerColor(Color.parseColor("#999999"));
        tabs.setBackgroundResource(style.backgroundRes > 0 ? style.backgroundRes
                : R.color.white);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //如果tab滑动完成取消编辑按钮的状态
        if (AppGlobalConsts.ISEDITSTATE){
            showbackEditLayout("backEdit");
            AppGlobalConsts.ISEDITSTATE = false;
        }

    }

    @Override
    public void onPageSelected(int position) {


        Log.i("OwnCacheFragment", "onPageSelected");
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        Log.i("OwnCacheFragment", "onPageScrollStateChanged");
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            switch (vp_cache_viewpager.getCurrentItem()) {
                case 0:
                    ((CachedFragment) fragments.get(0)).showEditButton();
                    break;
                case 1:
                    ((CachingFragment) fragments.get(1)).showEditButton();
            }
        }
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(AppGlobalConsts.EventType.TAG_C)})
    public void showEditLayout(String refreshEdit) {
        ll_cache_viewgroup.setVisibility(View.VISIBLE);
        fl_owncache.setVisibility(View.GONE);
        tabs.setVisibility(View.GONE);
        rl_owncache.invalidate();
        switch (vp_cache_viewpager.getCurrentItem()) {
            case 0:
                ((CachedFragment) fragments.get(0)).showEditLayout1("");
                break;
            case 1:
                ((CachingFragment) fragments.get(1)).showEditLayout1("");
        }
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(AppGlobalConsts.EventType.TAG_D)})
    public void showbackEditLayout(String backEdit) {
        ll_cache_viewgroup.setVisibility(View.INVISIBLE);
        fl_owncache.setVisibility(View.VISIBLE);
        tabs.setVisibility(View.VISIBLE);
        rl_owncache.invalidate();
        CacheDeleteSeleteEvent tmp = new CacheDeleteSeleteEvent(0, true);
        changeDeleteSelectState(tmp);
        switch (vp_cache_viewpager.getCurrentItem()) {
            case 0:
                ((CachedFragment) fragments.get(0)).showbackEditLayout2("");
                break;
            case 1:
                ((CachingFragment) fragments.get(1)).showbackEditLayout2("");
        }
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(AppGlobalConsts.EventType.CHANGE_SELECT_ALL_STATE)})
    public void changeDeleteSelectState(CacheDeleteSeleteEvent tmp) {
        if (tmp.deleteCount > 0) {
            btn_caching_delete.setTextColor(Color.RED);
            btn_caching_delete.setText("删除(" + tmp.deleteCount + ")");
        } else {
            btn_caching_delete.setTextColor(Color.GRAY);
            btn_caching_delete.setText("删除");
        }
        if (tmp.selectAll) {
            btn_caching_all.setText("全选");
            selectAll = false;
        } else {
            btn_caching_all.setText("取消全选");
            selectAll = true;
        }
    }

    @OnClick(R.id.btn_caching_all)
    public void clickAllBtn() {
//        btn_caching_all.setTextColor(Color.RED);
        btn_caching_delete.setTextColor(Color.GRAY);
//        BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_E, fragments.get(vp_cache_viewpager.getCurrentItem()).getClass().getName());
        selectAll = !selectAll;
        if (selectAll) {
            btn_caching_all.setText("取消全选");
        } else {
            btn_caching_all.setText("全选");
            btn_caching_delete.setTextColor(Color.GRAY);
            btn_caching_delete.setText("删除");
        }
        switch (vp_cache_viewpager.getCurrentItem()) {
            case 0:
                ((CachedFragment) fragments.get(0)).clickallbtn(selectAll);
                break;
            case 1:
                ((CachingFragment) fragments.get(1)).clickallbtn(selectAll);
                break;
        }
    }

    @OnClick(R.id.btn_caching_delete)
    public void clickDeleteBtn() {
        btn_caching_delete.setText("删除");
        btn_caching_delete.setTextColor(Color.GRAY);
//        btn_caching_all.setTextColor(Color.GRAY);
//        BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_F, fragments.get(vp_cache_viewpager.getCurrentItem()).getClass().getName());
        switch (vp_cache_viewpager.getCurrentItem()) {
            case 0:
                ((CachedFragment) fragments.get(0)).clickdeletebtn("");
                break;
            case 1:
                ((CachingFragment) fragments.get(1)).clickdeletebtn("");
                break;
        }
    }

    public int getCurrentItem() {
        return vp_cache_viewpager.getCurrentItem();
    }
}
