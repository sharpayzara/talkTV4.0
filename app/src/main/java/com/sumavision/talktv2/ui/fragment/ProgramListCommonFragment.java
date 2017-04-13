package com.sumavision.talktv2.ui.fragment;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.ProgramListData;
import com.sumavision.talktv2.presenter.ProgramListCommonPresenter;
import com.sumavision.talktv2.ui.adapter.ProgramListRecyclerAdapter;
import com.sumavision.talktv2.ui.fragment.Base.BaseFragment;
import com.sumavision.talktv2.ui.iview.IProgramListCommonView;
import com.sumavision.talktv2.ui.widget.LMRecyclerView;
import com.sumavision.talktv2.ui.widget.LoadingLayout;
import com.sumavision.talktv2.util.NoDoubleClickListener;
import com.sumavision.talktv2.util.TipUtil;

import butterknife.BindView;


/**
 * Created by sharpay on 2016/6/31.
 */
public class ProgramListCommonFragment extends BaseFragment<ProgramListCommonPresenter> implements IProgramListCommonView, LMRecyclerView.LoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    ProgramListCommonPresenter presenter;
    @BindView(R.id.lm_recycler_view)
    LMRecyclerView lmRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.loading_layout)
    LoadingLayout  loadingLayout;
    ProgramListRecyclerAdapter listAdapter;
    Context mContext;
    boolean isSwitch = true; //横图与竖图是否切换
    int page = 1;
    int size = 60;
    boolean canLoadMore = true;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_list_layout;
    }

    @Override
    protected void initPresenter() {
        isSwitch = true;
        mContext = this.getContext();
        presenter = new ProgramListCommonPresenter(mContext,this);
        presenter.init();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void initView() {
        canLoadMore = true;
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.blue);
        loadingLayout.setOnRetryClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                loadingLayout.showProgressBar();
                presenter.getProgramListData(tid,currentType,page,size);
            }
        });
        lmRecyclerView.setLoadMoreListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        listAdapter = new ProgramListRecyclerAdapter(mContext,currentType);
        listAdapter.setList(null);
        lmRecyclerView.setAdapter(listAdapter);
        loadingLayout.showProgressBar();
        presenter.getProgramListData(tid,currentType,page,size);
    }

    @Override
    public void loadMore() {
        if (canLoadMore) {
            if (!swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
            page++;
            presenter.getProgramListData(tid,currentType,page,size);
        } else {
            TipUtil.showSnackTip(lmRecyclerView, "没有更多数据了!");
        }
    }

    @Override
    public void onRefresh() {
        canLoadMore = true;
        page = 1;
        listAdapter.setList(null);
        listAdapter.notifyDataSetChanged();
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        presenter.getProgramListData(tid,currentType,page,size);
    }

    @Override
    public void fillData(ProgramListData programListData) {
        listAdapter.setStyle(programListData.getStyle());
        listAdapter.setList(programListData.getItems());
        listAdapter.notifyDataSetChanged();
        if(programListData.getStyle().equals("ver") && isSwitch && lmRecyclerView != null){
            lmRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        }else if(programListData.getStyle().equals("list") && isSwitch && lmRecyclerView != null){
            lmRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 1));
        }else if(programListData.getStyle().equals("hor") && isSwitch && lmRecyclerView != null){
            lmRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        }
        isSwitch = false;
    }

    @Override
    public void showProgressBar() {
        loadingLayout.showProgressBar();
    }

    @Override
    public void hideProgressBar() {
        if(loadingLayout != null){
            loadingLayout.hideProgressBar();
        }
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void showErrorView() {
        if(loadingLayout != null){
            loadingLayout.showErrorView();
        }

    }

    @Override
    public void showWifiView() {
        if(loadingLayout != null){
            loadingLayout.showWifiView();}
    }

    @Override
    public void emptyData() {
        if(loadingLayout != null){
            canLoadMore = false;
        }
    }
}
