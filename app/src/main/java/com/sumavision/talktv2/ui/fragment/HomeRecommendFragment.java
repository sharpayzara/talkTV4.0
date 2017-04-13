package com.sumavision.talktv2.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.ACache;
import com.sumavision.talktv2.common.ShareElement;
import com.sumavision.talktv2.model.entity.ClassifyItem;
import com.sumavision.talktv2.model.entity.HomeRecommend;
import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.model.entity.SpecialListData;
import com.sumavision.talktv2.model.entity.decor.DecorEntity;
import com.sumavision.talktv2.model.entity.decor.HomeRecommendData;
import com.sumavision.talktv2.model.entity.decor.HomeRecommendUpdateData;
import com.sumavision.talktv2.presenter.HomeFragmentPresenter;
import com.sumavision.talktv2.ui.activity.ProgramDetailActivity;
import com.sumavision.talktv2.ui.activity.ProgramListActivity;
import com.sumavision.talktv2.ui.adapter.HomeRecommandAdapter;
import com.sumavision.talktv2.ui.adapter.SpecialListAdapter;
import com.sumavision.talktv2.ui.fragment.Base.BaseFragment;
import com.sumavision.talktv2.ui.iview.IHomeRecommendFragmentView;
import com.sumavision.talktv2.ui.widget.HeightDropAnimator;
import com.sumavision.talktv2.ui.widget.LMRecyclerView;
import com.sumavision.talktv2.ui.widget.LoadingLayout;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.CommonUtil;
import com.sumavision.talktv2.util.NoDoubleClickListener;
import com.sumavision.talktv2.util.TipUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * @author yangjh
 *         created at  16-5-24 下午9:01
 */
public class HomeRecommendFragment extends BaseFragment<HomeFragmentPresenter> implements IHomeRecommendFragmentView,SwipeRefreshLayout.OnRefreshListener,LMRecyclerView.LoadMoreListener {
    private static final String NID = "nid";
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    private String nid;
    private static ArrayList<ClassifyItem> lists;
    @BindView(R.id.fragment_rec_loadinglayout)
    LoadingLayout fragment_rec_loadinglayout;
    @BindView(R.id.title_tv)
    TextView titleTv;
    private HomeRecommendData homeData;
    private boolean hasMore = false;
    private List<HomeRecommend> recommendList;
    private List<SpecialListData.ItemsBean> specialList;
    private String topicId, card_id;
    private String topicName,type;
    @BindView(R.id.rlt)
    RelativeLayout rlt;
    @BindView(R.id.more_rlt)
    RelativeLayout moreRlt;
    @BindView(R.id.recycler_view)
    LMRecyclerView recyclerView;
    RelativeLayout history_rlt;
    HomeRecommandAdapter adapter;
    private PlayerHistoryBean playerHistoryBean;
    boolean canLoadMore = true;
    SpecialListAdapter specialListAdapter;
    int page = 1;
    int size = 20;
    @OnClick({R.id.more_rlt,R.id.title_tv})
    void Onclick(View view) {
                Intent intent2 = new Intent();
                intent2.setClass(getActivity(), ProgramListActivity.class);
                intent2.putExtra("idStr", topicId);
                startActivity(intent2);
    }

    public HomeRecommendFragment() {

    }

    public static HomeRecommendFragment newInstance(String nid, String topicId, String topicName,String type) {
        HomeRecommendFragment fragment = new HomeRecommendFragment();
        Bundle args = new Bundle();
        args.putString(NID, nid);
        args.putString("topicId", topicId);
        args.putString("topicName", topicName);
        args.putString("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nid = getArguments().getString(NID);
            topicId = getArguments().getString("topicId");
            topicName = getArguments().getString("topicName");
            type = getArguments().getString("type");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.sendOrMoveHistoryHideMsg(false);
        rlt.removeView(history_rlt);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_tvfan;
    }

    @Override
    protected void initPresenter() {
        presenter = new HomeFragmentPresenter(getContext(), this);
        presenter.init();
    }

    @Override
    public void showProgressBar() {
        fragment_rec_loadinglayout.showProgressBar();
    }

    @Override
    public void hideProgressBar() {
        fragment_rec_loadinglayout.hideProgressBar();
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void showErrorView() {
        fragment_rec_loadinglayout.showErrorView();
    }

    @Override
    public void showEmptyView() {
        fragment_rec_loadinglayout.showEmptyView();
    }

    @Override
    public void showWifiView() {
        fragment_rec_loadinglayout.showWifiView();
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("refreshHomeShow")})
    public void refreshHomeShow(String refreshHomeShow) {
        showProgressBar();
    }

    @Override
    public void initView() {
        if (type.equals("1") || type.equals("4") || type.equals("3")) {
            hasMore = false;
        } else {
            hasMore = true;
            titleTv.setText("查看全部" + topicName);
        }
        recommendList = new ArrayList<>();
        if(type.equals("3")){
            specialList = new ArrayList<>();
            showProgressBar();
            presenter.loadSpecialListData(page,size);
        }else if (!nid.equals("rehp3t")) {
            presenter.loadHomeRecommendData(nid);
        } else {
            homeData = (HomeRecommendData) BaseApp.getACache().getAsObject("recommendList");
            if(ShareElement.isFirst) {
                presenter.getHistoryData();
                ShareElement.isFirst = false;
            }
            if (homeData != null) {
                presenter.loadHomeRecommendUpdateData("rehp3t", homeData.version);
            } else {
                presenter.loadHomeRecommendData("rehp3t");
            }
        }
        fragment_rec_loadinglayout.setOnRetryClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(type.equals("3")){
                    showProgressBar();
                    presenter.loadSpecialListData(page,size);
                } else if (!nid.equals("rehp3t")) {
                    presenter.loadHomeRecommendData(nid);
                } else {
                    homeData = (HomeRecommendData) ACache.get(getActivity()).getAsObject("recommendList");
                    if (homeData != null) {
                        presenter.loadHomeRecommendUpdateData("rehp3t", homeData.version);
                    } else {
                        presenter.loadHomeRecommendData("rehp3t");
                    }
                }
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.blue);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if(type.equals("3")){
            specialListAdapter = new SpecialListAdapter(getContext(),specialList);
            recyclerView.setAdapter(specialListAdapter);
            recyclerView.setBackgroundColor(getResources().getColor(R.color.bg_color));
        }else{
            adapter = new HomeRecommandAdapter(getContext(), recommendList, nid, topicId, card_id,topicName,type);
            recyclerView.setAdapter(adapter);
        }
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    openPopupWindow();

                } else {
                    closePopupWindow();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        if(type.equals("3")){
            recyclerView.setLoadMoreListener(this);
        }

    }
    @Override
    public void showListView(HomeRecommendData homeRecommendData, String nid) {
        /*if (homeRecommendData.results.size() == 0){
            showEmptyView();
        }*/
        homeData = (HomeRecommendData) BaseApp.getACache().getAsObject("recommendList");

        if (nid.equals("rehp3t")) {
            if (homeData == null) {
                //说明这是第一次进入应用的情况
                homeData = new HomeRecommendData();
                homeData.results = new ArrayList<>();
                homeData.version = homeRecommendData.version;
                homeData.results.addAll(homeRecommendData.results);
            } else {
                //不是第一次进入应用
                for (int i = 0; i < homeRecommendData.results.size(); i++) {
                    for (int j = 0; j < homeData.results.size(); j++) {
                        //对比服务器的数据和本地存储的数据,找到需要显示的卡片并且更新卡片内容
                        if (homeData.results.get(j).card_id.equals(homeRecommendData.results.get(i).card_id)) {
                            homeData.results.remove(j);
                            homeData.results.add(homeRecommendData.results.get(i));
                            break;
                        }
                    }
                }
                try {
                    changeLists =((DecorEntity<List<HomeRecommend>>) ACache.get(getContext()).getAsObject("changeLists")).getT() ;
                }catch (Exception ex){
                    changeLists = new ArrayList<>();
                }
                for (int m=0;m<changeLists.size();m++){
                    for (int n=0;n<homeRecommendData.results.size();n++){
                        //存在就替换
                        if (homeRecommendData.results.get(n).card_id.equals(changeLists.get(m).card_id)){
                            changeLists.get(m).action = homeRecommendData.results.get(n).action;
                            changeLists.get(m).card_id = homeRecommendData.results.get(n).card_id;
                            changeLists.get(m).card_name = homeRecommendData.results.get(n).card_name;
                            changeLists.get(m).card_type = homeRecommendData.results.get(n).card_type;
                            changeLists.get(m).card_style = homeRecommendData.results.get(n).card_style;
                            changeLists.get(m).hasChange = homeRecommendData.results.get(n).hasChange;
                            changeLists.get(m).hasMore = homeRecommendData.results.get(n).hasMore;
                            changeLists.get(m).items = homeRecommendData.results.get(n).items;
                            changeLists.get(m).style = homeRecommendData.results.get(n).style;
                            break;
                        }
                    }
                }
                DecorEntity<List<HomeRecommend>> changeDecor = new DecorEntity<>();
                changeDecor.setT(changeLists);
                BaseApp.getACache().put("changeLists",changeDecor);

            }
            recommendList.addAll(homeData.results);
            ACache.get(getContext()).put("recommendList", homeData);
        } else {
            recommendList.addAll(homeRecommendData.results);
        }
        adapter.notifyDataSetChanged();
    }
    private List<HomeRecommend>changeLists;
    private boolean ISSTOP1;
    private boolean ISSTOP2;
    @Override
    public void updateListView(HomeRecommendUpdateData homeRecommendUpdateData, String nid, String v) {
        homeData = (HomeRecommendData) BaseApp.getACache().getAsObject("recommendList");
        try {
            changeLists =((DecorEntity<List<HomeRecommend>>) ACache.get(getContext()).getAsObject("changeLists")).getT() ;
        }catch (Exception ex){
            changeLists = new ArrayList<>();
        }

        //版本号不同，说明有更新
        if (!homeData.version.equals(homeRecommendUpdateData.version)) {
            homeData.version = homeRecommendUpdateData.version;
            if (homeRecommendUpdateData.results != null) {
                for (int i = 0; i < homeRecommendUpdateData.results.size(); i++) {
                    ISSTOP1= true;
                    ISSTOP2= true;
                    if (homeRecommendUpdateData.results.get(i).status == 0) {
                        //此时是删减card
                        for (int j = 0; j < homeData.results.size(); j++) {
                            if (homeRecommendUpdateData.results.get(i).card_id.equals(homeData.results.get(j).card_id)) {
                                homeData.results.remove(j);
                                break;
                            }
                        }
                        for (int k = 0 ;k<changeLists.size();k++){
                            if (homeRecommendUpdateData.results.get(i).card_id.equals(changeLists.get(k).card_id)){
                                changeLists.remove(k);
                                break;
                            }
                        }
                    } else {
                        //增加card
                        //首先判断增加的这个card是否在隐藏队列中存在,如果存在就替换数据,并且给ISSTOP1赋值为false
                        for (int m=0;m<changeLists.size();m++){
                            //存在就替换
                            if (homeRecommendUpdateData.results.get(i).card_id.equals(changeLists.get(m).card_id)){
                                changeLists.get(m).action = homeRecommendUpdateData.results.get(i).action;
                                changeLists.get(m).card_id = homeRecommendUpdateData.results.get(i).card_id;
                                changeLists.get(m).card_name = homeRecommendUpdateData.results.get(i).card_name;
                                changeLists.get(m).card_type = homeRecommendUpdateData.results.get(i).card_type;
                                changeLists.get(m).card_style = homeRecommendUpdateData.results.get(i).card_style;
                                changeLists.get(m).hasChange = homeRecommendUpdateData.results.get(i).hasChange;
                                changeLists.get(m).hasMore = homeRecommendUpdateData.results.get(i).hasMore;
                                changeLists.get(m).items = homeRecommendUpdateData.results.get(i).items;
                                changeLists.get(m).style = homeRecommendUpdateData.results.get(i).style;
                                ISSTOP1 = false;
                                break;
                            }
                        }
                        //在判断是否在显示队列中存在,如果存在就替换数据,并且给ISSTOP2赋值为false
                        for (int z = 0;z<homeData.results.size();z++){
                            if (homeRecommendUpdateData.results.get(i).card_id.equals(homeData.results.get(z).card_id)){
                                homeData.results.get(z).action = homeRecommendUpdateData.results.get(i).action;
                                homeData.results.get(z).card_id = homeRecommendUpdateData.results.get(i).card_id;
                                homeData.results.get(z).card_name = homeRecommendUpdateData.results.get(i).card_name;
                                homeData.results.get(z).card_type = homeRecommendUpdateData.results.get(i).card_type;
                                homeData.results.get(z).card_style = homeRecommendUpdateData.results.get(i).card_style;
                                homeData.results.get(z).hasChange = homeRecommendUpdateData.results.get(i).hasChange;
                                homeData.results.get(z).hasMore = homeRecommendUpdateData.results.get(i).hasMore;
                                homeData.results.get(z).items = homeRecommendUpdateData.results.get(i).items;
                                homeData.results.get(z).style = homeRecommendUpdateData.results.get(i).style;
                                ISSTOP2 = false;
                                break;
                            }
                        }
                        if (ISSTOP1 && ISSTOP2){
                            //当所有队列中都不存在时
                            addNewCard(homeData,homeRecommendUpdateData,i);
                        }
                    }
                }
            }
            BaseApp.getACache().put("recommendList", homeData);
            DecorEntity<List<HomeRecommend>> changeDecor = new DecorEntity<>();
            changeDecor.setT(changeLists);
            ACache.get(this.getContext()).put("changeLists",changeDecor);
            presenter.loadHomeRecommendData(nid);

        } else {
            //说明没有更新
            recommendList.addAll(homeData.results);
            //setRecommendShow(recommendList, nid);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showHistory(final PlayerHistoryBean history) {
        playerHistoryBean = history;
        Observable.just("")
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        if(history != null){
                            history_rlt = (RelativeLayout)View.inflate(getContext(), R.layout.histroy_show, null);
                            TextView content = (TextView) history_rlt.findViewById(R.id.history_content);
                            content.setText(history.getProgramName());
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CommonUtil.dip2px(getContext(), 40.0f));
                            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                            history_rlt.setLayoutParams(params);
                            rlt.addView(history_rlt);
                            history_rlt.findViewById(R.id.hide_history_rlt).setOnClickListener(new NoDoubleClickListener() {
                                @Override
                                public void onNoDoubleClick(View v) {
                                    presenter.sendOrMoveHistoryHideMsg(false);
                                    hideHistory();
                                }
                            });
                            content.setOnClickListener(new NoDoubleClickListener() {
                                @Override
                                public void onNoDoubleClick(View v) {
                                    presenter.sendOrMoveHistoryHideMsg(false);
                                    hideHistory();
                                    Intent intent3 = new Intent();
                                    intent3.setClass(getActivity(), ProgramDetailActivity.class);
                                    intent3.putExtra("idStr", playerHistoryBean.getProgramId());
                                    startActivity(intent3);
                                }
                            });
                            presenter.sendOrMoveHistoryHideMsg(true);
                        }
                    }
                });
    }

    @Override
    public void hideHistory() {
        final ObjectAnimator animator = ObjectAnimator.ofFloat(history_rlt, "translationY", CommonUtil.dip2px(getActivity(), 40.0f));
        animator.setDuration(500L);
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animator.cancel();
                if (rlt != null)
                    rlt.removeView(history_rlt);
                history_rlt = null;
            }
        });
    }

    @Override
    public void showSpecialView(SpecialListData specialListData) {
        if(specialListData.getItems().size() != 0 ){
            specialList.addAll(specialListData.getItems());
            specialListAdapter.notifyDataSetChanged();
        }else{
            canLoadMore = false;
        }
    }


    private void addNewCard(HomeRecommendData homeData,HomeRecommendUpdateData homeRecommendUpdateData, int i) {
        HomeRecommend homeRecommend = new HomeRecommend();
        homeRecommend.action = homeRecommendUpdateData.results.get(i).action;
        homeRecommend.card_id = homeRecommendUpdateData.results.get(i).card_id;
        homeRecommend.card_name = homeRecommendUpdateData.results.get(i).card_name;
        homeRecommend.card_type = homeRecommendUpdateData.results.get(i).card_type;
        homeRecommend.card_style = homeRecommendUpdateData.results.get(i).card_style;
        homeRecommend.hasChange = homeRecommendUpdateData.results.get(i).hasChange;
        homeRecommend.hasMore = homeRecommendUpdateData.results.get(i).hasMore;
        homeRecommend.items = homeRecommendUpdateData.results.get(i).items;
        homeRecommend.style = homeRecommendUpdateData.results.get(i).style;
        homeData.results.add(homeRecommend);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.release();
        recyclerView = null;
        swipeRefreshLayout = null;
        fragment_rec_loadinglayout = null;
        moreRlt = null;
        titleTv = null;
        adapter = null;
        rlt = null;
        recommendList.clear();
        recommendList = null;
        try {
            homeData.results.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        homeData = null;
    }

    @Override
    public boolean onBackPressed() {
        BusProvider.getInstance().post("exitApp", "exitApp");
        return true;
    }


    private void openPopupWindow() {
        if (hasMore) {
            moreRlt.setVisibility(View.VISIBLE);
            if (moreRlt.getHeight() == 0) {
                new HeightDropAnimator().animateOpen(moreRlt, CommonUtil.dip2px(getContext(), 35));
            }
        }

    }

    private void closePopupWindow() {
        if (hasMore) {
            if (moreRlt.getHeight() == CommonUtil.dip2px(getContext(), 35)) {
                new HeightDropAnimator().animateClose(moreRlt);
            }
        }
    }

    @Override
    public void onRefresh() {
        recommendList.clear();
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        if(type.equals("3")){
            page = 1;
            canLoadMore = true;
            specialList.clear();
            showProgressBar();
            presenter.loadSpecialListData(page,size);
        }else if (!nid.equals("rehp3t")) {
            presenter.loadHomeRecommendData(nid);
        } else {
            homeData = (HomeRecommendData) BaseApp.getACache().getAsObject("recommendList");
            if (homeData != null) {
                presenter.loadHomeRecommendUpdateData("rehp3t", homeData.version);
            } else {
                presenter.loadHomeRecommendData("rehp3t");
            }
        }
    }

    @Override
    public void loadMore() {
        if (canLoadMore) {
            if (!swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
            page++;
            presenter.loadSpecialListData(page,size);
        } else {
            TipUtil.showSnackTip(recyclerView, "没有更多数据了!");
        }
    }
}
