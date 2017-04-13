package com.sumavision.talktv2.ui.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.ClassifyItem;
import com.sumavision.talktv2.ui.adapter.ProgramListSwitchAdapter;

import java.util.List;

/**
 * Created by sharpay on 16-6-15.
 */
public class ProgramListSwitchPopupWindow extends PopupWindow{
    private Context mContext;
    private LinearLayout btn_cancel;
    private View view;
    private RecyclerView recyclerView;
    private ProgramListSwitchAdapter adapter;
    public ProgramListSwitchPopupWindow(Context mContext, List<ClassifyItem> list) {
        this.mContext = mContext;
        this.view = LayoutInflater.from(mContext).inflate(R.layout.program_list_switch_layout, null);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        adapter = new ProgramListSwitchAdapter(mContext,list,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 4));
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
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
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

}
