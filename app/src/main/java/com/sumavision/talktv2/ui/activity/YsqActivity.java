package com.sumavision.talktv2.ui.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.YsqBean;
import com.sumavision.talktv2.presenter.YsqPresenter;
import com.sumavision.talktv2.ui.activity.base.CommonHeadPanelActivity;
import com.sumavision.talktv2.ui.adapter.YsqItemAdapter;
import com.sumavision.talktv2.ui.iview.IYsqView;
import com.sumavision.talktv2.ui.widget.LMRecyclerView;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.TipUtil;

import butterknife.BindView;

/**
 * Created by sharpay on 16-5-24.
 */
public class YsqActivity extends CommonHeadPanelActivity<YsqPresenter> implements IYsqView,LMRecyclerView.LoadMoreListener,SwipeRefreshLayout.OnRefreshListener {
    YsqPresenter presenter;
    YsqItemAdapter adapter;
    @BindView(R.id.recycler_view)
    LMRecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    int page = 1;
    int size = 20;
    boolean canLoadMore = true;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_ysq;
    }

    @Override
    protected void initPresenter() {
        presenter = new YsqPresenter(this, this);
        presenter.init();
    }

    @Override
    public void initView() {
        BusProvider.getInstance().register(this);
        initHeadPanel();
        showBackBtn();
        setHeadTitle("影视圈");
        adapter = new YsqItemAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(adapter);
        recyclerView.setLoadMoreListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.blue);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                presenter.getYsqData(page,size);
            }
        });
    }

    @Override
    public void fillData(YsqBean ysqBean) {
        adapter.setList(ysqBean.getItems());
        if (getIntent().getFlags()== AppGlobalConsts.ITEMPOSITION){
            String itemid = getIntent().getStringExtra("itemid");
            for (int i = 0;i<ysqBean.getItems().size();i++){
                if (ysqBean.getItems().get(i).getId().equals(itemid) && AppGlobalConsts.ISFROMHOME){
                    recyclerView.scrollToPosition(i);
                    AppGlobalConsts.ISFROMHOME =false;
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void emptyData() {
        canLoadMore = false;
        TipUtil.showSnackTip(recyclerView,"没有更多数据了!");
    }

    @Override
    public void stopRefresh() {
        if (swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void startRefresh() {
        if (!swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    public void loadMore() {
        if(canLoadMore){
            page++;
            presenter.getYsqData(page,size);
        }else{
            TipUtil.showSnackTip(recyclerView,"没有更多数据了!");
        }
    }

    @Override
    public void onRefresh() {
        canLoadMore = true;
        page = 1;
        adapter.setList(null);
        presenter.getYsqData(page,size);
    }
}
