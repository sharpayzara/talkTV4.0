package com.sumavision.talktv2.ui.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.DividerItemDecoration;
import com.sumavision.talktv2.dao.RxDao;
import com.sumavision.talktv2.model.entity.CollectBean;
import com.sumavision.talktv2.presenter.CollectPresenter;
import com.sumavision.talktv2.presenter.WatchHistoryPresenter;
import com.sumavision.talktv2.ui.activity.base.CommonHeadPanelActivity;
import com.sumavision.talktv2.ui.adapter.CollectRecyclerAdapter;
import com.sumavision.talktv2.ui.iview.base.IBaseView;
import com.sumavision.talktv2.ui.widget.HeightDropAnimator;
import com.sumavision.talktv2.ui.widget.LoadingLayout;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.CommonUtil;
import com.sumavision.talktv2.util.NoDoubleClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by sharpay on 16-5-24.
 */
public class CollectActivity extends CommonHeadPanelActivity<CollectPresenter> implements IBaseView,View.OnClickListener{
    @BindView(R.id.history_recycle)
    RecyclerView historyRecycle;
    CollectRecyclerAdapter adapter;
    List<CollectBean> list;
    @BindView(R.id.select_btn)
    Button selectBtn;
    @BindView(R.id.options_llt)
    LinearLayout optionsLlt;
    @BindView(R.id.loading_layout)
    LoadingLayout loadingLayout;
    private boolean isSelectAll;
    private boolean isEdit;
    private static RxDao collectDao = new RxDao(BaseApp.getContext(), CollectBean.class);
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
        presenter = new CollectPresenter(this, this);
        presenter.init();
    }

    @Override
    public void initView() {
        BusProvider.getInstance().register(this);
        initHeadPanel();
        showBackBtn();
        setHeadTitle("我的收藏");
        setRightBtn("", R.mipmap.manage_nor_btn, true);
        list = new ArrayList<>();
        adapter = new CollectRecyclerAdapter(this, list);
        historyRecycle.setAdapter(adapter);
        historyRecycle.setLayoutManager(new LinearLayoutManager(this));
        historyRecycle.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        setRightBtnListener(this);
        findCollectBean();
        loadingLayout.setOnRetryClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                findCollectBean();
            }
        });
    }

    public void findCollectBean(){

        List data = collectDao.queryAllByOrder("id", false);
        if(data.size() == 0){
            loadingLayout.showEmptyView();
        }else{
            loadingLayout.hideProgressBar();
            list.addAll(data);
        }
        adapter.notifyDataSetChanged();
    }

    @OnClick({R.id.select_btn, R.id.delete_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.select_btn:
                if(isSelectAll){
                    selectBtn.setText("全选");
                    for(CollectBean data : list){
                        data.isChecked = false;
                    }
                    isSelectAll = false;
                }else{
                    selectBtn.setText("全不选");
                    for(CollectBean data : list){
                        data.isChecked = true;
                    }
                    isSelectAll = true;
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.delete_btn:

                for (int i = 0; i < list.size(); i++) {
                    if(list.get(i).isChecked()){
                        collectDao.deleteById(list.get(i).getId());
                        list.remove(i);
                        i--;
                    }

                }
                adapter.notifyDataSetChanged();
                if(list.size() == 0){
                    loadingLayout.setVisibility(View.VISIBLE);
                    loadingLayout.showEmptyView();
                }
                break;
            case R.id.right_btn:
                if(list.size() == 0){
                    return;
                }
                if(!isEdit){
                    new HeightDropAnimator().animateOpen(optionsLlt, CommonUtil.dip2px(this,45));
                    for(CollectBean data : list){
                        data.isOpened = true;
                    }
                    isEdit = true;
                }else{
                    new HeightDropAnimator().animateClose(optionsLlt);
                    for(CollectBean data : list){
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
        CollectBean temp = list.get(0);
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
}
