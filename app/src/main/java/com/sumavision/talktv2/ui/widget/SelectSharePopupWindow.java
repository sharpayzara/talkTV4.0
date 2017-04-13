package com.sumavision.talktv2.ui.widget;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.UserModel;
import com.sumavision.talktv2.model.entity.AppKeyResult;
import com.sumavision.talktv2.model.impl.UserModelImpl;
import com.sumavision.talktv2.ui.adapter.ProgramListSwitchAdapter;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.sumavision.talktv2.util.BusProvider;

import java.util.List;
import java.util.Locale;

/**
 * Created by sharpay on 16-6-15.
 */
public class SelectSharePopupWindow extends PopupWindow implements View.OnClickListener {
    private Context mContext;
    private View view;
    private ProgramListSwitchAdapter adapter;
    private LinearLayout weixinLlt, qqLlt, weiboLlt, friendCircleLlt, zoneLlt;

    public SelectSharePopupWindow(Context mContext) {
        this.mContext = mContext;
        this.view = LayoutInflater.from(mContext).inflate(R.layout.select_share_layout, null);
        weixinLlt = (LinearLayout) view.findViewById(R.id.weixin_llt);
        qqLlt = (LinearLayout) view.findViewById(R.id.qq_llt);
        weiboLlt = (LinearLayout) view.findViewById(R.id.weibo_llt);
        friendCircleLlt = (LinearLayout) view.findViewById(R.id.pyq_llt);
        zoneLlt = (LinearLayout) view.findViewById(R.id.zone_llt);
        weixinLlt.setOnClickListener(this);
        qqLlt.setOnClickListener(this);
        weiboLlt.setOnClickListener(this);
        zoneLlt.setOnClickListener(this);
        friendCircleLlt.setOnClickListener(this);
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
        if (AppGlobalVars.appKeyResult != null && AppGlobalVars.appKeyResult.getData().size() > 2) {
            sendShareChannel(v);
        } else {
            UserModel userModel = new UserModelImpl();
            userModel.loadAppKey(new CallBackListener<AppKeyResult>() {
                @Override
                public void onSuccess(AppKeyResult appKey) {
                    AppGlobalVars.appKeyResult = appKey;
                    sendShareChannel(v);
                    userModel.release();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    userModel.release();
                }
            });
        }
        dismiss();
    }

    void sendShareChannel(View v) {
        if (v == weixinLlt) {
            BusProvider.getInstance().post("selectChannel", "WEIXIN");
        } else if (v == qqLlt) {
            BusProvider.getInstance().post("selectChannel", "QQ");
        } else if (v == weiboLlt) {
            if (isWeiboInstalled()) {
                BusProvider.getInstance().post("selectChannel", "WEIBO");
            }else{
                Toast.makeText(mContext,"请先安装微博客户端",Toast.LENGTH_SHORT).show();
            }
        } else if (v == friendCircleLlt) {
            BusProvider.getInstance().post("selectChannel", "CIRCLE");
        } else if (v == zoneLlt) {
            BusProvider.getInstance().post("selectChannel", "ZONE");
        }
    }

    public boolean isWeiboInstalled() {
        PackageManager pm;
        if ((pm = mContext.getApplicationContext().getPackageManager()) == null) {
            return false;
        }
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo info : packages) {
            String name = info.packageName.toLowerCase(Locale.ENGLISH);
            if ("com.sina.weibo".equals(name)) {
                return true;
            }
        }
        return false;

    }
}