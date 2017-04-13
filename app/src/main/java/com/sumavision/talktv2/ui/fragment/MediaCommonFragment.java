package com.sumavision.talktv2.ui.fragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.DividerItemDecoration;
import com.sumavision.talktv2.model.entity.MediaList;
import com.sumavision.talktv2.presenter.MediaCommonPresenter;
import com.sumavision.talktv2.ui.adapter.MediaCommonAdapter;
import com.sumavision.talktv2.ui.fragment.Base.BaseFragment;
import com.sumavision.talktv2.ui.iview.IMediaCommonView;
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
public class MediaCommonFragment extends BaseFragment<MediaCommonPresenter> implements IMediaCommonView, LMRecyclerView.LoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.recycler_view)
    LMRecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    int page = 1;
    int size = 20;
    @BindView(R.id.loading_layout)
    LoadingLayout loading;
    private MediaCommonAdapter adapter;
    boolean canLoadMore = true;
    private MediaCommonPresenter presenter;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_media_common;
    }

    @Override
    protected void initPresenter() {
        presenter = new MediaCommonPresenter(getContext(), this);
        presenter.init();
    }

    @Override
    public boolean onBackPressed() {
        BusProvider.getInstance().post("returnHome","returnHome");
        return true;
    }

    @Override
    public void initView() {
        adapter = new MediaCommonAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(adapter);
        recyclerView.setLoadMoreListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.blue);
        swipeRefreshLayout.setOnRefreshListener(this);
        showProgressBar();
        presenter.getMediaListData(page, size, getArguments().getString("txt"));
        loading.setOnRetryClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                showProgressBar();
                presenter.getMediaListData(page, size, getArguments().getString("txt"));
            }
        });
    }

    @Override
    public void loadMore() {
        if (canLoadMore) {
            if (!swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
            page++;
            presenter.getMediaListData(page, size, getArguments().getString("txt"));
        } else {
            TipUtil.showSnackTip(recyclerView, "没有更多数据了!");
        }
    }

    @Override
    public void onRefresh() {
        canLoadMore = true;
        page = 1;
        adapter.getList().clear();
        adapter.notifyDataSetChanged();
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        presenter.getMediaListData(page, size, getArguments().getString("txt"));
    }

    @Override
    public void fillListData(MediaList list) {
        adapter.setList(list.getItems());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showProgressBar() {
        if(loading != null)
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
        if(loading != null)
            loading.showErrorView();

    }

    @Override
    public void showWifiView() {
        if(loading != null)
            loading.showWifiView();
    }

    @Override
    public void emptyData() {
        canLoadMore = false;
        if(loading != null)
            loading.showEmptyView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView = null;
        swipeRefreshLayout = null;
        adapter = null;
        loading = null;
    }
}
