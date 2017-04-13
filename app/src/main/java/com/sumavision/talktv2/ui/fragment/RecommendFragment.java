package com.sumavision.talktv2.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.bumptech.glide.Glide;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.ACache;
import com.sumavision.talktv2.model.entity.ADInfoItem;
import com.sumavision.talktv2.model.entity.ClassifyItem;
import com.sumavision.talktv2.model.entity.ClassifyUpdataItem;
import com.sumavision.talktv2.model.entity.Gank;
import com.sumavision.talktv2.model.entity.decor.ClassifyData;
import com.sumavision.talktv2.presenter.RecommendPresenter;
import com.sumavision.talktv2.ui.activity.DragSettingActivity;
import com.sumavision.talktv2.ui.activity.Game37WanActivity;
import com.sumavision.talktv2.ui.activity.NewScanActivity;
import com.sumavision.talktv2.ui.activity.SearchActivity;
import com.sumavision.talktv2.ui.activity.WatchHistoryActivity;
import com.sumavision.talktv2.ui.activity.WeBADActivity;
import com.sumavision.talktv2.ui.adapter.RecommendPagerAdapter;
import com.sumavision.talktv2.ui.fragment.Base.BaseFragment;
import com.sumavision.talktv2.ui.iview.IRecommendView;
import com.sumavision.talktv2.ui.widget.LoadingLayout;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.CommonUtil;
import com.sumavision.talktv2.util.NoDoubleClickListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 推荐界面的Fragment
 * Created by zjx on 2016/5/31.
 */
public class RecommendFragment extends BaseFragment<RecommendPresenter> implements IRecommendView,View.OnClickListener {
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.tvfan_search)
    public Button tvfan_search;
    @BindView(R.id.tvfan_history)
    public Button tvfan_history;
    @BindView(R.id.tvfan_more)
    public Button tvfan_more;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.container)
    ViewPager container;
    @BindView(R.id.tvfan_logo)
    ImageView logo;
    @BindView(R.id.loading_layout)
    LoadingLayout loadingLayout;
    @BindView(R.id.spacebtn)
    ImageView spacebtn;
    private PopupWindow window;
    ClassifyData items;
    RecommendPagerAdapter pagerAdapter;
    LinearLayout ll_pop;
    private LinearLayout ll_morewindow_cache, ll_morewindow_qrcode, ll_morewindow_setting;
    List<Fragment> fragmentList;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_recommend;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        BusProvider.getInstance().register(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initPresenter() {
        presenter = new RecommendPresenter(getContext(), this);
        presenter.init();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(AppGlobalConsts.EventType.TAG_CARDID)})
    public void enterOtherFragment(String cardid) {
        items = (ClassifyData) BaseApp.getACache().getAsObject("classifydata");
        for (int i = 0; i < items.results.size(); i++) {
            if (items.results.get(i).navId.equals(cardid)) {
                container.setCurrentItem(i);
            }
        }

    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("refreshShow")})
    public void refreshShow(String refreshShow) {
        initDragData();
    }

    private void initDragData() {
        items = (ClassifyData) ACache.get(getContext()).getAsObject("classifydata");
        if (items == null) {
            items = new ClassifyData();
            items.results = new ArrayList<>();
        }
        fragmentList.clear();
        for (ClassifyItem item : items.results) {
            HomeRecommendFragment fragment = new HomeRecommendFragment();
            fragmentList.add(fragment);
        }
        pagerAdapter.setList(fragmentList, items.results);
    }

    @OnClick(R.id.invigation_btn)
    public void dragBtnClick() {
        enterNext(AppGlobalConsts.EnterType.ENTERGRID);
        MobclickAgent.onEvent(getContext(), "4dbdhpx");
    }

    @Override
    public void showGankList(List<Gank> gankList) {

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
    public void showWifiView() {
        loadingLayout.showWifiView();
    }

    @Override
    public void showErrorView() {
        loadingLayout.showErrorView();
    }

    @Override
    public void showClassifyView(ArrayList<ClassifyItem> classifies) {
        if (items == null) {
            items = new ClassifyData();
            items.results = new ArrayList<>();
        }
        items.results.addAll(classifies);
        //存入缓存
        BaseApp.getACache().put("classifydata", items);
        initDragData();
    }

    private boolean ISADD;

    @Override
    public void updataClassifyView(ArrayList<ClassifyUpdataItem> classifyUpdates) {
        ClassifyData data;
        if (classifyUpdates != null) {
           /* public int isFixed;//是否固定
            public String name;//分类名
            public String navId;//id
            public String picture;//分类图片地址
            public String type;//分类图片地址*/
            //这里获取服务器更新的数据了
            for (int i = 0; i < classifyUpdates.size(); i++) {
                ISADD = true;
                //把ClassifyDpada的数据拿出来
                int isFixed = classifyUpdates.get(i).isFixed;
                String name = classifyUpdates.get(i).name;
                String navId = classifyUpdates.get(i).navId;
                String picture = classifyUpdates.get(i).picture;
                String type = classifyUpdates.get(i).type;
                int status = classifyUpdates.get(i).status;
                if (status == 1) {
                    for (int m = 0; m < items.results.size(); m++) {
                        if (navId.equals(items.results.get(m).navId)) {
                            ISADD = false;
                        }
                    }
                    if (ISADD) {
                        items.results.add(new ClassifyItem(isFixed, name, navId, picture, type));
                    }
                } else {
                    for (int j = 0; j < items.results.size(); j++) {
                        if (items.results.get(j).navId.equals(navId)) {
                            items.results.remove(j);
                        }
                    }
                }
            }
        }
        ACache.get(getContext()).put("classifydata", items);
        //存入缓存
        initDragData();
    }

    @Override
    public void initView() {
        presenter.loadADInfo(getContext(), "flow");
        AppGlobalConsts.NEEDEXITAPP = true;
        //先查询本地数据是否存在
        fragmentList = new ArrayList<>();
        items = (ClassifyData) BaseApp.getACache().getAsObject("classifydata");
        if (items == null) {
            //不存在,那么去请求接口2
            presenter.getClassifyData();
            loadingLayout.setOnRetryClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View view) {
                    presenter.getClassifyData();
                }
            });
        } else {
            //本地数据如果存在
            //请求服务器获取更新分类
            presenter.getClassifyUpdataData();
            loadingLayout.setOnRetryClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View view) {
                    presenter.getClassifyUpdataData();
                }
            });
        }
        tvfan_search.setText("搜全网");
        tvfan_search.setTextColor(Color.parseColor("#777777"));
        tvfan_search.setTextSize(13);
        tvfan_search.setOnClickListener(this);
        tvfan_history.setOnClickListener(this);
        tvfan_more.setOnClickListener(this);
        if (pagerAdapter == null) {
            pagerAdapter = new RecommendPagerAdapter(this.getChildFragmentManager());
            container.setAdapter(pagerAdapter);
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            tabLayout.setupWithViewPager(container);
            tabLayout.invalidate();
            tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(container) {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    int position = tab.getPosition();
                    container.setCurrentItem(position);
                    try {
                        String name = items.results.get(position).name;
                        MobclickAgent.onEvent(getContext(), "4dbtag", name);
                        Log.i("RecommendFragment", "MobclickAgent" + name);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
        presenter.release();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvfan_search:
//                enterClassifyRecommend();
                MobclickAgent.onEvent(getContext(), "4rs");
                enterSearch();
                break;

            case R.id.tvfan_history://播放历史
                MobclickAgent.onEvent(getContext(), "4dbls");
                Intent historyIntent = new Intent(this.getActivity(), WatchHistoryActivity.class);
                startActivity(historyIntent);
                break;

            case R.id.tvfan_more://更多

//                if (window != null && window.isShowing()) {
//                    window.dismiss();
//                    window = null;
//                    return;
//                }
                showPop(tvfan_more);
                break;
            case R.id.ll_morewindow_cache://缓存
                MobclickAgent.onEvent(getContext(), "4dbhc");
                enterNext(AppGlobalConsts.EnterType.ENTERHUANCUN);
                window.dismiss();
                break;
//            case R.id.ll_morewindow_messeage://消息
//                enterNext(AppGlobalConsts.EnterType.ENTERXIAOXI);
//                window.dismiss();
//                break;
            case R.id.ll_morewindow_setting://设置
                MobclickAgent.onEvent(getContext(), "4dbsz");
                enterNext(AppGlobalConsts.EnterType.ENTERSHEZHI);
                window.dismiss();
                break;
            case R.id.ll_morewindow_qrcode://扫一扫
              /*  MobclickAgent.onEvent(getContext(), "4dbqr");
                enterNext(AppGlobalConsts.EnterType.ENTERSHEZHI);*/
                Intent scanIntent = new Intent(this.getActivity(), NewScanActivity.class);
                startActivity(scanIntent);
                window.dismiss();
                break;
            default:
                break;
        }

    }

    private void enterSearch() {
        Intent intent = new Intent();
        intent.setClass(this.getActivity(), SearchActivity.class);
        startActivity(intent);
    }



    private void showPop(View parent) {
        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popupwindow_more, null);
        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
        window = new PopupWindow(view,
                CommonUtil.dip2px(getContext(), 110),
                CommonUtil.dip2px(getContext(), 125));
        window.setAnimationStyle(R.style.mypopwindow_anim_style);
        window.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
        ll_pop = (LinearLayout) view.findViewById(R.id.ll_pop);
       /* window.showAtLocation(toolbar,
                Gravity.NO_GRAVITY, CommonUtil.dip2px(getContext(), 370), CommonUtil.dip2px(getContext(), 68));*/
//        window.showAtLocation(toolbar, Gravity.RIGHT,  CommonUtil.screenWidth(getContext())-window.getWidth(),CommonUtil.dip2px(getContext(), 68));
        window.showAsDropDown(parent);
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
        ll_morewindow_setting = (LinearLayout) view.findViewById(R.id.ll_morewindow_setting);
        ll_morewindow_qrcode= (LinearLayout) view.findViewById(R.id.ll_morewindow_qrcode);
        ll_morewindow_qrcode.setOnClickListener(this);
        ll_morewindow_cache.setOnClickListener(this);
//        ll_morewindow_messeage.setOnClickListener(this);
        ll_morewindow_setting.setOnClickListener(this);
    }

    private void enterNext(String id) {
        Intent intent = new Intent();
        intent.setClass(this.getActivity(), DragSettingActivity.class);
        intent.putExtra("enternext", id);
        startActivity(intent);
    }

    @Override
    public void setADInfo(final ADInfoItem adInfo) {
       /* ViewGroup.LayoutParams lp = spacebtn.getLayoutParams();
        lp.width = CommonUtil.screenWidth(getContext())*130/720;
        lp.height = lp.width;
        spacebtn.setLayoutParams(lp);*/
        if (adInfo != null) {
            //这里获取到了广告信息
            Glide.with(getContext())
                    .load(adInfo.getObj().getPicurl())
                    .animate(R.anim.image_load)
                    .into(spacebtn);
            //设置悬浮按钮
            spacebtn.setOnClickListener(new NoDoubleClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    switch (adInfo.getObj().getStyle()) {
                        case "web":
                            Intent intent = new Intent(getContext(), WeBADActivity.class);
                            intent.putExtra("url", adInfo.getObj().getAdurl());
                            getContext().startActivity(intent);
                            break;
                        case "h5":
                            Intent intent2 = new Intent(getContext(), Game37WanActivity.class);
                            intent2.putExtra("url", adInfo.getObj().getAdurl());
                            getContext().startActivity(intent2);
                            break;
                        case "duiba":
                            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_ENTERDUIBA, adInfo.getObj().getAdurl());
                            AppGlobalConsts.ISLOGINDUIBA = true;
                            break;
                    }
                }
            });
        }
    }
}

   /* @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("searchtolive")})
    public void loadPic(String channelId) {
//        mTabHost.setCurrentTab(1);
        BusProvider.getInstance().post(channelId);
        ShareElement.channelId = channelId;
    }*/
