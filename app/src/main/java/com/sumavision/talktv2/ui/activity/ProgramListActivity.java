package com.sumavision.talktv2.ui.activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.ClassifyItem;
import com.sumavision.talktv2.model.entity.ProgramListTopic;
import com.sumavision.talktv2.model.entity.ProgramSelection;
import com.sumavision.talktv2.model.entity.SerializableMap;
import com.sumavision.talktv2.model.entity.decor.ClassifyData;
import com.sumavision.talktv2.presenter.ProgramListPresenter;
import com.sumavision.talktv2.ui.activity.base.CommonHeadPanelActivity;
import com.sumavision.talktv2.ui.adapter.ProgramListPagerAdapter;
import com.sumavision.talktv2.ui.fragment.ProgramListCommonFragment;
import com.sumavision.talktv2.ui.fragment.ProgramListSelectionFragment;
import com.sumavision.talktv2.ui.iview.IProgramListView;
import com.sumavision.talktv2.ui.widget.LoadingLayout;
import com.sumavision.talktv2.ui.widget.ProgramListSwitchPopupWindow;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.CommonUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by sharpay on 16-6-27.
 */
public class ProgramListActivity extends CommonHeadPanelActivity<ProgramListPresenter> implements IProgramListView, View.OnClickListener {
    ProgramListPresenter presenter;
    @BindView(R.id.topic_tab)
    TabLayout topicTab;
    @BindView(R.id.view_container)
    ViewPager viewContainer;
    @BindView(R.id.loadingLayout)
    LoadingLayout loadingLayout;
    String idStr,navId;
    ProgramListPagerAdapter adapter;
    ClassifyData classifyData;
    ProgramListSwitchPopupWindow popupWindow;
    String currentType;
    List<ProgramListTopic.ItemsBean> topicArray;
    Map<String,String> paramMap;
    List<Fragment> fragmentList;
    boolean isSwitch = true; //是否显示在筛选页
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_program_list;
    }

    @Override
    protected void initPresenter() {
        presenter = new ProgramListPresenter(this, this);
        presenter.init();
    }

    @Override
    public void initView() {
        BusProvider.getInstance().register(this);
        initHeadPanel();
        showBackBtn();
        showCornerMark(true);
        classifyData = (ClassifyData) BaseApp.getACache().getAsObject("classifydata");
        idStr = getIntent().getStringExtra("idStr");
        navId = getIntent().getStringExtra("navID");
        if (classifyData == null) {
            return;
        }
        for(ClassifyItem item : classifyData.results){
            if(item.navId.equals(idStr)){
                currentType = item.name;
                break;
            }
        }
        /*idStr = "cark3x";
        currentType = "电影";*/
        setHeadTitle(currentType,this);
        fragmentList = new ArrayList<>();
        paramMap = new HashMap<>();
        topicArray = new ArrayList<>();
        setRightBtn("", R.mipmap.index_nav_search2, true);
        setRightBtnListener(this);
        presenter.getProgramListTopic(idStr);
        loadingLayout.setOnRetryClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.getProgramListTopic(idStr);
            }
        });
        //   for(){}
        adapter = new ProgramListPagerAdapter(getSupportFragmentManager(),this,presenter,viewContainer,currentType);
        viewContainer.setAdapter(adapter);
        viewContainer.setOffscreenPageLimit(3);
        topicTab.setTabMode(TabLayout.MODE_SCROLLABLE);
        topicTab.setupWithViewPager(viewContainer);
        topicTab.invalidate();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.midle_title:
                // classifyData
                openPopupWindow(view);
                break;
            case R.id.right_btn:
                Intent intent = new Intent();
                intent.setClass(this, SearchActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void openPopupWindow(View view) {

        if(popupWindow == null && classifyData != null && classifyData.results.size() > 0){
            for(int i = 0;i < classifyData.results.size();i++){
                if(classifyData.results.get(i).name.equals("排行榜") || classifyData.results.get(i).name.equals("推荐")|| classifyData.results.get(i).name.equals("专题")){
                    classifyData.results.remove(i);
                    i--;
                }
            }

            popupWindow = new ProgramListSwitchPopupWindow(this,classifyData.results);
            popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0,CommonUtil.dip2px(this,70));
        }else if(classifyData != null && classifyData.results.size() > 0){
            popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0,CommonUtil.dip2px(this,70));
        }

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
    }

    @Override
    public void showErrorView() {
        loadingLayout.showErrorView();
    }

    @Override
    public void showEmptyView() {
        loadingLayout.showEmptyView();
    }

    @Override
    public void showWifiView() {
        loadingLayout.showWifiView();
    }

    @Override
    public void fillTopicAndSeletionData(ProgramSelection selectionData,ProgramListTopic programListTopic) {
        isSwitch = true;
        topicArray.clear();
        fragmentList.clear();
        ProgramListTopic.ItemsBean itemsBean= new ProgramListTopic.ItemsBean();
        itemsBean.setName("筛选");
        topicArray.add(itemsBean);
        paramMap.clear();
        for(ProgramSelection.ItemsBean bean : selectionData.getItems()){
            if(bean.getItems().size() > 0){
                paramMap.put(bean.getParams(),bean.getItems().get(0).getValue());
            }
        }

        paramMap.put("cid",idStr);
        paramMap.put("page","1");
        paramMap.put("cname",currentType);
        paramMap.put("size","60");
        SerializableMap serializableMap = new SerializableMap();
        serializableMap.setMap(paramMap);
        ProgramListSelectionFragment sfragment = new ProgramListSelectionFragment();
        Bundle sbundle=new Bundle();
        sbundle.putSerializable("selectionData", (Serializable) selectionData.getItems());
        sbundle.putSerializable("map", serializableMap);
        sbundle.putString("currentType",currentType);
        sfragment.setArguments(sbundle);
        fragmentList.add(sfragment);

        for(ProgramListTopic.ItemsBean bean: programListTopic.getItems()){
            ProgramListCommonFragment fragment = new ProgramListCommonFragment();
            Bundle bundle = new Bundle();
            bundle.putString("currentType",currentType);
            bundle.putString("tid",bean.getId());
            fragment.setArguments(bundle);
            fragment.set(currentType,bean.getId());
            fragmentList.add(fragment);
        }
        topicArray.addAll(programListTopic.getItems());
        adapter.setList(fragmentList,topicArray);
        adapter.notifyDataSetChanged();
        if(!TextUtils.isEmpty(navId )){
            for(int i = 0;i < topicArray.size();i++){
                if(topicArray.get(i).getId() != null && topicArray.get(i).getId().equals(navId)){
                    viewContainer.setCurrentItem(i);
                    isSwitch = false;
                    break;
                }
            }
        }
        if(isSwitch){
            viewContainer.setCurrentItem(0);
        }
    }

    @Override
    protected void onDestroy() {
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(AppGlobalConsts.EventType.TAG_A)})
    public void flushProgramList(ClassifyItem item){
        if(item.name == currentType){
            return;
        }
        idStr = item.navId;
        currentType = item.name;
        setHeadTitle(item.name);
        presenter.getProgramListTopic(idStr);
    }
}
