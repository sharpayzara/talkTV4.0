package com.sumavision.talktv2.ui.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.util.Util;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.http.GlideProxy;
import com.sumavision.talktv2.util.CommonUtil;

/**
 * Created by zhoutao on 2016/6/29.
 */
public class MyChildItemVEView extends RelativeLayout {
    private Context mContext;
    ImageView iv_myve_topimg;
    ImageView iv_myve_img;
    TextView tv_myve_title;
    TextView tv_myve_des;
    TextView tv_myve_focus;

    public MyChildItemVEView(Context context) {
        super(context);

    }

    public MyChildItemVEView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        View view = View.inflate(context,R.layout.layout_myveti,this);
        iv_myve_img = (ImageView)view.findViewById(R.id.iv_myve_img);
        iv_myve_topimg = (ImageView)view.findViewById(R.id.iv_myve_topimg);
        iv_myve_topimg.setMaxHeight(CommonUtil.screenWidth(mContext)/3*24/120);
        iv_myve_topimg.setMaxWidth(CommonUtil.screenWidth(mContext)/2*24/180);
        iv_myve_topimg.setMinimumHeight(CommonUtil.screenWidth(mContext)/3*24/120);
        tv_myve_title = (TextView)view.findViewById(R.id.tv_myve_title);
        tv_myve_des = (TextView)view.findViewById(R.id.tv_myve_des);
        tv_myve_focus = (TextView)view.findViewById(R.id.tv_myve_focus);
        FrameLayout flt = (FrameLayout) view.findViewById(R.id.flt);
        ViewGroup.LayoutParams params6 = flt.getLayoutParams();
        params6.height = CommonUtil.screenWidth(mContext)/3*170/120;
        flt.setLayoutParams(params6);
    }



    public void setImgShow(String src){
        if(Util.isOnMainThread()) {
            GlideProxy.getInstance().loadVImage(mContext,src,iv_myve_img);
        }
    }

    public void setTitle(String title){
        tv_myve_title.setText(title);
    }
    public void setDes(String title){
        if(TextUtils.isEmpty(title)){
            tv_myve_des.setVisibility(GONE);
        }else{
            tv_myve_des.setVisibility(VISIBLE);
            tv_myve_des.setText(title);
        }
    }
    public void setFocus(String title){
        tv_myve_focus.setText(title);
    }
    public void setTopImgShow(boolean b){
        if (b){
            iv_myve_topimg.setVisibility(View.VISIBLE);
        }else{
            iv_myve_topimg.setVisibility(View.GONE);
        }
    }
    public void setTopImgResource(int id){
        iv_myve_topimg.setBackgroundResource(id);
    }
}
