package com.sumavision.talktv2.ui.widget;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TabLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.SeriesDetail;
import com.sumavision.talktv2.presenter.ProgramDetailPresenter;
import com.sumavision.talktv2.ui.adapter.GridCachePagerAdapter;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.CommonUtil;
import com.sumavision.talktv2.util.EdgeAcuity;
import com.sumavision.talktv2.util.NoDoubleClickListener;
import com.sumavision.talktv2.util.OfflineCacheUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharpay on 16-6-15.
 */
public class GridCachePopupWindow extends PopupWindow{
    private Activity mActivity;
    private RelativeLayout btn_cancel;
    private View view;
    private WrapContentHeightViewPager gridCacheContainer;
    private GridCachePagerAdapter adapter;
    private TabLayout gridCacheTab;
    private TextView edgeAcuity;
    private PopupWindow popupwindow;
    private int current = 0;
    private RelativeLayout startRlt;
    private List<SeriesDetail.SourceBean> cacheList;
    private TextView startCacheTxt,selectionTv;
    private String programId;

    String[] defValues = {"standardDef","hightDef","superDef"};

    public GridCachePopupWindow(final Activity mActivity, int height, List<String> seriesArray, ProgramDetailPresenter presenter, String idStr, String cpidStr, final String programName, String picUrl, final View rootView) {
        this.mActivity = mActivity;
        this.programId = idStr;
        cacheList = new ArrayList<>();
        this.view = LayoutInflater.from(mActivity).inflate(R.layout.grid_cache_layout, null);
        btn_cancel = (RelativeLayout) view.findViewById(R.id.btn_back);
        gridCacheTab = (TabLayout) view.findViewById(R.id.grid_cache_tab);
        edgeAcuity = (TextView) view.findViewById(R.id.edge_acuity);
        startRlt = (RelativeLayout) view.findViewById(R.id.start_rlt);
        selectionTv = (TextView) view.findViewById(R.id.selection_tv);
        startCacheTxt = (TextView) view.findViewById(R.id.start_cache_txt);
        gridCacheContainer = (WrapContentHeightViewPager) view.findViewById(R.id.grid_cache_container);
        adapter = new GridCachePagerAdapter(seriesArray,mActivity,presenter,idStr,cpidStr,picUrl);
        gridCacheContainer.setAdapter(adapter);
        gridCacheContainer.setOffscreenPageLimit(8);
        gridCacheTab.setTabMode(TabLayout.MODE_SCROLLABLE);
        gridCacheTab.setupWithViewPager(gridCacheContainer);
        gridCacheTab.invalidate();
        // 取消按钮
        btn_cancel.setOnClickListener(new NoDoubleClickListener() {

            public void onNoDoubleClick(View v) {
                // 销毁弹出框
                dismiss();
            }
        });
        startRlt.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                //  startRlt.setBackground(cache_start_bg);
                OfflineCacheUtil.startCache(mActivity, cacheList, programId, programName, defValues[current]);
                adapter.notifyDataSetChanged();
                adapter.notifyCache();
                cacheList.clear();
                uncheckCacheState(null);
            }
        });
        edgeAcuity.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                if(popupwindow == null){
                    initPopupWindowView();
                }
                final int[] location = new int[2];
                edgeAcuity.getLocationOnScreen(location);
                popupwindow.showAtLocation(rootView, Gravity.NO_GRAVITY, location[0] - popupwindow.getWidth() + edgeAcuity.getWidth(), location[1] + edgeAcuity.getHeight() + CommonUtil.dip2px(mActivity, 5));
            }
        });

        List<String> edgeAcuityList = EdgeAcuity.getInstance(mActivity);
        Object tmp = BaseApp.getACache().getAsObject("currentSelect");
        if (tmp != null) {
            edgeAcuity.setText(edgeAcuityList.get((int) tmp));
            current = (int) tmp;
        } else {
            edgeAcuity.setText(edgeAcuityList.get(0));
            current = 0;
        }

        // 设置外部可点击
        this.setOutsideTouchable(true);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        this.view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

    /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高
        this.setHeight(height);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        // 设置弹出窗体可点击
        this.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置弹出窗体的背景
        this.setBackgroundDrawable(dw);
        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.selections_anim);
    }

    public void initPopupWindowView() {
        final List<String> edgeAcuityList = EdgeAcuity.getInstance(mActivity);
        View customView = mActivity.getLayoutInflater().inflate(R.layout.popupwindow_cp,
                null, false);
        LinearLayout cp_frame_llt = (LinearLayout) customView.findViewById(R.id.cp_frame_llt);
        for (int i = 0; i < 3; i++) {
            RadioButton button = new RadioButton(mActivity);
            button.setText(edgeAcuityList.get(i));
            button.setTag(i);
            button.setButtonDrawable(android.R.color.transparent);
            button.setTextSize(15);
            button.setTextColor(mActivity.getResources().getColor(R.color.cp_color));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(CommonUtil.dip2px(mActivity,15),CommonUtil.dip2px(mActivity,5),CommonUtil.dip2px(mActivity,5),0);
            button.setLayoutParams(params);
            cp_frame_llt.addView(button);
            button.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View view) {
                    edgeAcuity.setText(edgeAcuityList.get((int) view.getTag()));
                    current = (int) view.getTag();
                    popupwindow.dismiss();
                }
            });
        }

        popupwindow = new PopupWindow(customView, CommonUtil.dip2px(mActivity, 60), CommonUtil.dip2px(mActivity, 40 * 3 + 15));
        popupwindow.setOutsideTouchable(true);
        customView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                }
                return false;
            }
        });
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(AppGlobalConsts.EventType.TAG_CACHE)})  //确认选择
    public void checkCacheState(SeriesDetail.SourceBean bean){
        cacheList.add(bean);
        startRlt.setBackgroundColor(mActivity.getResources().getColor(R.color.cache_start_bg));
        startCacheTxt.setTextColor(mActivity.getResources().getColor(R.color.white));
        selectionTv.setText(cacheList.size()+"");
        selectionTv.setBackgroundDrawable(mActivity.getResources().getDrawable(R.mipmap.playdetail_download_selected_btn));
    }


    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(AppGlobalConsts.EventType.TAG_CACHE_CANCEL)}) // 取消选择
    public void uncheckCacheState(SeriesDetail.SourceBean bean){
        cacheList.remove(bean);
        if(cacheList.size() == 0){
            startRlt.setBackgroundColor(mActivity.getResources().getColor(R.color.watch_history_bg));
            startCacheTxt.setTextColor(mActivity.getResources().getColor(R.color.cache_start_txt));
            selectionTv.setBackgroundDrawable(mActivity.getResources().getDrawable(R.mipmap.playdetail_download_nor_btn));
       }
        selectionTv.setText(cacheList.size()+"");
    }

    public GridCachePagerAdapter getAdapter(){
        return adapter;
    }

}
