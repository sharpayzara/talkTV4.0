package com.sumavision.talktv2.ui.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.DividerItemDecoration;
import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.presenter.WatchHistoryPresenter;
import com.sumavision.talktv2.ui.activity.base.CommonHeadPanelActivity;
import com.sumavision.talktv2.ui.adapter.HistoryRecyclerAdapter;
import com.sumavision.talktv2.ui.iview.IWatchHistoryView;
import com.sumavision.talktv2.ui.iview.base.IBaseView;
import com.sumavision.talktv2.ui.widget.HeightDropAnimator;
import com.sumavision.talktv2.ui.widget.LoadingLayout;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.CommonUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by sharpay on 16-5-24.
 */
public class WatchHistoryActivity extends CommonHeadPanelActivity<WatchHistoryPresenter> implements IWatchHistoryView,View.OnClickListener{
    @BindView(R.id.history_recycle)
    RecyclerView historyRecycle;
    @BindView(R.id.loading_layout)
    LoadingLayout loadingLayout;
    WatchHistoryPresenter presenter;
    HistoryRecyclerAdapter adapter;
    ArrayList<PlayerHistoryBean> list;
    @BindView(R.id.select_btn)
    Button selectBtn;
    @BindView(R.id.options_llt)
    LinearLayout optionsLlt;
    private boolean isSelectAll;
    private boolean isEdit;
    private boolean isHistoryChange;
    private boolean isFirst = true;

    @Override
    protected void onResume() {
        super.onResume();
        presenter.getHistoryData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_watch_history;
    }

    @Override
    protected void initPresenter() {
        presenter = new WatchHistoryPresenter(this, this);
        presenter.init();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void initView() {
        BusProvider.getInstance().register(this);
        initHeadPanel();
        showBackBtn();
        setHeadTitle("观看历史");
        setRightBtn("", R.mipmap.manage_nor_btn, true);
        list = new ArrayList<>();
        adapter = new HistoryRecyclerAdapter(this, list);
        historyRecycle.setAdapter(adapter);
        historyRecycle.setLayoutManager(new LinearLayoutManager(this));
        historyRecycle.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        setRightBtnListener(this);
        loadingLayout.setOnRetryClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.getHistoryData();
            }
        });
    }

    @OnClick({R.id.select_btn, R.id.delete_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.select_btn:
                if(isSelectAll){
                    selectBtn.setText("全选");
                    for(PlayerHistoryBean data : list){
                        data.isChecked = false;
                    }
                    isSelectAll = false;
                }else{
                    selectBtn.setText("全不选");
                    for(PlayerHistoryBean data : list){
                        data.isChecked = true;
                    }
                    isSelectAll = true;
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.delete_btn:
                ArrayList<PlayerHistoryBean> beans = new ArrayList<>();
                beans.addAll(list);
                int count = beans.size();
                for(int i=0; i<count; i++){
                    PlayerHistoryBean data = beans.get(i);
                    if(data.isChecked) {
                        isHistoryChange = true;
                        presenter.delHistory(data.getId());
                        list.remove(data);
                    }
                }
                adapter.notifyDataSetChanged();
                if(list.size() == 0){
                    loadingLayout.showEmptyView();
                }
                break;
            case R.id.right_btn:
                if(!isEdit){
                    new HeightDropAnimator().animateOpen(optionsLlt, CommonUtil.dip2px(this,45));
                    for(PlayerHistoryBean data : list){
                        data.isOpened = true;
                    }
                    isEdit = true;
                }else{
                    new HeightDropAnimator().animateClose(optionsLlt);
                    for(PlayerHistoryBean data : list){
                        data.isOpened = false;
                    }
                    isEdit = false;
                }
                adapter.notifyDataSetChanged();
                break;
        }
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(AppGlobalConsts.EventType.TAG_C)})
    public void setList(String msg){
        PlayerHistoryBean temp = list.get(0);
        for(int i = 0; i < list.size(); i++){
            if(temp.isChecked == list.get(i).isChecked){
                continue;
            }else{
                return;
            }
        }
        if(temp.isChecked){
            selectBtn.setText("全不选");
            isSelectAll = true;
        }else{
            selectBtn.setText("全选");
            isSelectAll = false;
        }
    }


    @Override
    public void showListData(List<PlayerHistoryBean> beanList) {
        list.clear();
        list.addAll(beanList);
        adapter.notifyDataSetChanged();
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
    public void showEmptyView() {
        loadingLayout.showEmptyView();
    }

}
