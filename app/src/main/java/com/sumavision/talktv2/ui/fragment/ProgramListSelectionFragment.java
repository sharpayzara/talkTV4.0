package com.sumavision.talktv2.ui.fragment;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.ProgramListData;
import com.sumavision.talktv2.model.entity.ProgramSelection;
import com.sumavision.talktv2.model.entity.SerializableMap;
import com.sumavision.talktv2.presenter.ProgramListCommonPresenter;
import com.sumavision.talktv2.ui.adapter.ProgramListRecyclerAdapter;
import com.sumavision.talktv2.ui.fragment.Base.BaseFragment;
import com.sumavision.talktv2.ui.iview.IProgramListCommonView;
import com.sumavision.talktv2.ui.widget.LMRecyclerView;
import com.sumavision.talktv2.ui.widget.LoadingLayout;
import com.sumavision.talktv2.util.CommonUtil;
import com.sumavision.talktv2.util.NoDoubleClickListener;
import com.sumavision.talktv2.util.TipUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by sharpay on 2016/6/31.
 */
public class ProgramListSelectionFragment extends BaseFragment<ProgramListCommonPresenter> implements IProgramListCommonView, LMRecyclerView.LoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
    ProgramListCommonPresenter presenter;
    @BindView(R.id.lm_recycler_view)
    LMRecyclerView lmRecyclerView;
    @BindView(R.id.swipe_refreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.loading_layout)
    LoadingLayout loadingLayout;
    List<ProgramSelection.ItemsBean> selectionList;
    ProgramListRecyclerAdapter listAdapter;
    Context mContext;
    boolean isSwitch = true; //横图与竖图是否切换
    int page = 1;
    int size = 60;
    boolean canLoadMore = true;
    String tid, currentType;
    Map<String, String> paramMap;
    @BindView(R.id.selection_llt)
    LinearLayout selectionLlt;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_selection_layout;
    }

    @Override
    protected void initPresenter() {
        mContext = this.getContext();
        presenter = new ProgramListCommonPresenter(mContext, this);
        presenter.init();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void initView() {
        isSwitch = true;
        final SerializableMap serializableMap = (SerializableMap) getArguments().get("map");
        paramMap = serializableMap.getMap();
        selectionList = (List<ProgramSelection.ItemsBean>) getArguments().getSerializable("selectionData");
        currentType = getArguments().getString("currentType");
        tid = getArguments().getString("tid");
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.blue);
        loadingLayout.setOnRetryClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                loadingLayout.showProgressBar();
                presenter.getProgramListSelectionData(paramMap);
            }
        });
        lmRecyclerView.setLoadMoreListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        listAdapter = new ProgramListRecyclerAdapter(mContext, currentType);
        lmRecyclerView.setAdapter(listAdapter);
        loadingLayout.showProgressBar();
        presenter.getProgramListSelectionData(paramMap);
        if(selectionList == null){
            return;
        }
        setSelection();
    }

    void setSelection() {
        final int sWrapContent = LinearLayout.LayoutParams.WRAP_CONTENT;
        final int sMatchParent = LinearLayout.LayoutParams.MATCH_PARENT;
        for (int i = 0; i < selectionList.size(); i++) {
            HorizontalScrollView mScrollView = new HorizontalScrollView(mContext);
            mScrollView.setHorizontalScrollBarEnabled(false);
            mScrollView.setLayoutParams(new ViewGroup.LayoutParams(sMatchParent, sWrapContent));
            final RadioGroup group = new RadioGroup(mContext);
            group.setOrientation(RadioGroup.HORIZONTAL);
            group.setLayoutParams(new LinearLayout.LayoutParams(sWrapContent, sWrapContent));
            for (int j = 0; j < selectionList.get(i).getItems().size(); j++) {
                final ProgramSelection.ItemsBean.ItemsBean2 bean = selectionList.get(i).getItems().get(j);
                bean.setParams(selectionList.get(i).getParams());
                RadioButton tempButton = new RadioButton(mContext);
                tempButton.setButtonDrawable(android.R.color.transparent);
                tempButton.setTag(bean);
                tempButton.setSingleLine(true);
                tempButton.setGravity(Gravity.CENTER);
                if (CommonUtil.stringLength(bean.getTitle()) == 4) {
                    tempButton.setBackgroundResource(R.drawable.selection_item_selector2);
                    tempButton.setPadding(0,0,0,0);
                    tempButton.setLayoutParams(new ViewGroup.LayoutParams(CommonUtil.dip2px(mContext, 60), CommonUtil.dip2px(mContext, 45)));
                } else if (CommonUtil.stringLength(bean.getTitle()) == 6) {
                    tempButton.setBackgroundResource(R.drawable.selection_item_selector3);
                    tempButton.setLayoutParams(new ViewGroup.LayoutParams(CommonUtil.dip2px(mContext, 72), CommonUtil.dip2px(mContext, 45)));
                    tempButton.setPadding(0,0,0,0);
                } else {
                    tempButton.setBackgroundResource(R.drawable.selection_item_selector4);
                    tempButton.setLayoutParams(new ViewGroup.LayoutParams(CommonUtil.dip2px(mContext, 85), CommonUtil.dip2px(mContext, 45)));
                    tempButton.setPadding(0,0,0,0);
                }

                tempButton.setText(bean.getTitle());
                group.addView(tempButton);
                if (j == 0) {
                    group.check(tempButton.getId());
                }
                group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        RadioButton button = (RadioButton) group.findViewById(radioGroup.getCheckedRadioButtonId());
                        ProgramSelection.ItemsBean.ItemsBean2 tempBean = (ProgramSelection.ItemsBean.ItemsBean2) button.getTag();
                        umengClick(tempBean);
                        listAdapter.setList(null);
                        listAdapter.notifyDataSetChanged();
                        paramMap.put(tempBean.getParams(),tempBean.getValue());
                        paramMap.put("page","1");
                        canLoadMore = true;
                        presenter.getProgramListSelectionData(paramMap);
                    }
                });
            }
            mScrollView.addView(group);
            selectionLlt.addView(mScrollView);
        }
    }

    private void umengClick(ProgramSelection.ItemsBean.ItemsBean2 tempBean) {
        String name = null;
        for (ProgramSelection.ItemsBean tmp : selectionList) {
            if (tmp.getParams().equals(tempBean.getParams())) {
                name = tmp.getItems().get(0).getTitle();
                if (name != null)
                    MobclickAgent.onEvent(getContext(), "4shx", paramMap.get("cname") + " " + name + " " + tempBean.getTitle());
            }
        }
    }

    @Override
    public void loadMore() {
        if (canLoadMore) {
            if (!swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
            paramMap.put("page",(Integer.parseInt(paramMap.get("page")) + 1 ) +"");
            presenter.getProgramListSelectionData(paramMap);
        } else {
            TipUtil.showSnackTip(lmRecyclerView, "没有更多数据了!");
        }
    }

    @Override
    public void onRefresh() {
        canLoadMore = true;
        paramMap.put("page","1");
        listAdapter.setList(null);
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        presenter.getProgramListSelectionData(paramMap);
    }

    @Override
    public void fillData(ProgramListData programListData) {
        if(programListData.getItems().size() == 0){
            canLoadMore = false;
            TipUtil.showSnackTip(lmRecyclerView, "没有更多数据了!");
            return;
        }
        listAdapter.setStyle(programListData.getStyle());
        listAdapter.setList(programListData.getItems());
        listAdapter.notifyDataSetChanged();
        if (programListData.getStyle().equals("ver") && isSwitch && lmRecyclerView != null) {
            lmRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        } else if (programListData.getStyle().equals("list") && isSwitch && lmRecyclerView != null) {
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
        if (loadingLayout != null) {
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
        if(loadingLayout != null) {
            loadingLayout.showWifiView();
        }
    }

    @Override
    public void emptyData() {
        if(loadingLayout != null){
            canLoadMore = false;
        }
    }

}
