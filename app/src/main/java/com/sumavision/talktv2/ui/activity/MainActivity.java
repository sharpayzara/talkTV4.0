package com.sumavision.talktv2.ui.activity;
import android.support.v4.widget.SwipeRefreshLayout;
import com.sumavision.talktv2.presenter.MainPresenter;
import com.sumavision.talktv2.ui.activity.base.ToolBarActivity;
import com.sumavision.talktv2.ui.iview.IMainView;
import com.sumavision.talktv2.ui.widget.LMRecyclerView;

/**
 * Created by xybcoder on 2016/3/1.
 */

public class MainActivity extends ToolBarActivity<MainPresenter> implements
        SwipeRefreshLayout.OnRefreshListener, IMainView, LMRecyclerView.LoadMoreListener {

    @Override
    protected int getLayoutResId() {
        return 0;
    }

    @Override
    protected void initPresenter() {

    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showErrorView() {

    }

    @Override
    public void showNoMoreData() {

    }

    @Override
    public void initView() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void onRefresh() {

    }
}
