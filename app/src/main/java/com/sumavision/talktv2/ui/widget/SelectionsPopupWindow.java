package com.sumavision.talktv2.ui.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.DividerItemDecoration;
import com.sumavision.talktv2.model.entity.SeriesDetail;
import com.sumavision.talktv2.ui.adapter.ArtsSelectionsAdapter;
import com.sumavision.talktv2.util.NoDoubleClickListener;

import java.util.List;

/**
 * Created by sharpay on 16-6-15.
 */
public class SelectionsPopupWindow extends PopupWindow{
    private LinearLayout btn_cancel;
    private View view;
    private DetailVRecyclerView recyclerView;
    private ArtsSelectionsAdapter adapter;
    private int cpState = 1;
    public SelectionsPopupWindow(Context mContext,int height,DetailVRecyclerView.LoadMoreListener listener) {

        this.view = LayoutInflater.from(mContext).inflate(R.layout.arts_selections_layout, null);
        btn_cancel = (LinearLayout) view.findViewById(R.id.btn_back);
        recyclerView = (DetailVRecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLoadMoreListener(listener);
        adapter = new ArtsSelectionsAdapter(mContext);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        // 取消按钮
        btn_cancel.setOnClickListener(new NoDoubleClickListener() {

            public void onNoDoubleClick(View v) {
                // 销毁弹出框
                dismiss();
            }
        });

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

    public void  setSeriesData(List<SeriesDetail.SourceBean> list, int pos){
        adapter.setSeriesData(list, pos);
        adapter.notifyDataSetChanged();
//        recyclerView.scrollToPosition(pos);
    }

    public void setCpState(int cpState) {
        this.cpState = cpState;
    }

    public void changeProgram (int pos) {
        adapter.changeProgram(pos);
        adapter.notifyDataSetChanged();
    }

    public void setSelection (int pos) {
        recyclerView.scrollToPosition(pos);
    }

    public void clearHolders(){
        adapter.clearHolders();
    }
}
