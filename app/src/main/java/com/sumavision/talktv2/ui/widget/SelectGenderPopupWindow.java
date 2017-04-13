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
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.ClassifyItem;
import com.sumavision.talktv2.ui.adapter.ProgramListSwitchAdapter;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;

import java.util.List;

/**
 * Created by sharpay on 16-6-15.
 */
public class SelectGenderPopupWindow extends PopupWindow implements View.OnClickListener{
    private Context mContext;
    private LinearLayout btn_cancel;
    private View view;
    private RecyclerView recyclerView;
    private ProgramListSwitchAdapter adapter;
    private TextView manTv,womanTv;
    public SelectGenderPopupWindow(Context mContext) {
        this.mContext = mContext;
        this.view = LayoutInflater.from(mContext).inflate(R.layout.select_gender_layout, null);
        manTv = (TextView) view.findViewById(R.id.man_tv);
        womanTv = (TextView) view.findViewById(R.id.woman_tv);
        manTv.setOnClickListener(this);
        womanTv.setOnClickListener(this);
        // 设置外部可点击
        this.setOutsideTouchable(true);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        this.view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                dismiss();
                return true;
            }
        });

    /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高
        this.setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        // 设置弹出窗体可点击
        this.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置弹出窗体的背景
        this.setBackgroundDrawable(dw);
    }

    @Override
    public void onClick(View v) {
        if(v == manTv){
            BusProvider.getInstance().post("refreshGender","男");
        }else{
            BusProvider.getInstance().post("refreshGender","女");
        }
        dismiss();
    }
}
