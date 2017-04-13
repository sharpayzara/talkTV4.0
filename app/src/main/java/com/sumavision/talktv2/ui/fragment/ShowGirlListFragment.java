package com.sumavision.talktv2.ui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.DividerItemDecoration;
import com.sumavision.talktv2.model.entity.ShowGirlList;
import com.sumavision.talktv2.presenter.ShowGirlListPresenter;
import com.sumavision.talktv2.ui.adapter.ShowGirlListAdapter;
import com.sumavision.talktv2.ui.fragment.Base.BaseFragment;
import com.sumavision.talktv2.ui.iview.IShowGirlListView;
import com.sumavision.talktv2.ui.widget.LMRecyclerView;
import com.sumavision.talktv2.ui.widget.LoadingLayout;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.NoDoubleClickListener;
import com.sumavision.talktv2.util.TipUtil;

import butterknife.BindView;

/**
 * 自媒体首页界面的Fragment
 * Created by sharpay on 2016/6/31.
 */
public class ShowGirlListFragment extends BaseFragment<ShowGirlListPresenter> implements IShowGirlListView, LMRecyclerView.LoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.recycler_view)
    LMRecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.loading_layout)
    LoadingLayout loading;
    private ShowGirlListAdapter adapter;
    boolean canLoadMore = true;
    private ShowGirlListPresenter presenter;
    private int partId,start = 1,offset = 20;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_showgirl_list;
    }

    @Override
    protected void initPresenter() {
        presenter = new ShowGirlListPresenter(this.getActivity(), this);
        presenter.init();
    }

    @Override
    public boolean onBackPressed() {
        BusProvider.getInstance().post("returnHome","returnHome");
        return true;
    }

    @Override
    public void initView() {
        Bundle bundle = getArguments();
        partId = bundle.getInt("partId");
        adapter = new ShowGirlListAdapter(this.getActivity());
        recyclerView.addItemDecoration(new DividerItemDecoration(this.getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(), 2));
        recyclerView.setAdapter(adapter);
        recyclerView.setLoadMoreListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.blue);
        swipeRefreshLayout.setOnRefreshListener(this);
        showProgressBar();
        presenter.getShowGirlListData(partId,start,offset);
        loading.setOnRetryClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                showProgressBar();
                presenter.getShowGirlListData(partId,start,offset);
            }
        });
    }

    @Override
    public void loadMore() {
        if (canLoadMore) {
            if (!swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
            start++;
            presenter.getShowGirlListData(partId,start,offset);
        } else {
            TipUtil.showSnackTip(recyclerView, "没有更多数据了!");
        }
    }

    @Override
    public void onRefresh() {
        canLoadMore = true;
        start = 1;
        adapter.getList().clear();
        adapter.notifyDataSetChanged();
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        presenter.getShowGirlListData(partId,start,offset);
    }

    @Override
    public void fillListData(ShowGirlList list) {
        adapter.setPathPrefix(list.getPathPrefix());
        adapter.setList(list.getRoomList());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showProgressBar() {
       loading.showProgressBar();
    }

    @Override
    public void hideProgressBar() {
        if(loading != null){
            loading.hideProgressBar();
        }
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void showErrorView() {
        loading.showErrorView();

    }

    @Override
    public void showWifiView() {
        loading.showWifiView();
    }

    @Override
    public void emptyData() {
        canLoadMore = false;
        loading.showEmptyView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView = null;
        swipeRefreshLayout = null;
        loading = null;
        adapter = null;
    }
}
