package com.sumavision.talktv2.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.http.GlideProxy;
import com.sumavision.talktv2.util.CommonUtil;

/**
 * Created by zhoutao on 2016/6/29.
 */
public class MyChildItemHView extends RelativeLayout {
    private Context mContext;
    ImageView iv_myhori_img;
    ImageView iv_myhori_topimg;
    TextView tv_myhori_title;
    TextView tv_myhori_des;
    TextView tv_myhori_focus;

    private static final int default_background_color = Color.rgb(255,255,255);
    public MyChildItemHView(Context context) {
        super(context);

    }

    public MyChildItemHView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        View view = View.inflate(mContext,R.layout.layout_myhori,this);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,(int)(LayoutParams.WRAP_CONTENT/16*9));
        view.setLayoutParams(params);
        FrameLayout flt = (FrameLayout) view.findViewById(R.id.flt);
        ViewGroup.LayoutParams params6 = flt.getLayoutParams();
        params6.height = CommonUtil.screenWidth(mContext)/2*110/180;
        flt.setLayoutParams(params6);
        iv_myhori_img = (ImageView)view.findViewById(R.id.iv_myhori_img);
        iv_myhori_topimg = (ImageView)view.findViewById(R.id.iv_myhori_topimg);
        iv_myhori_topimg.setMaxHeight(CommonUtil.screenWidth(mContext)/2*24/180);
        iv_myhori_topimg.setMaxWidth(CommonUtil.screenWidth(mContext)/2*24/180);
        iv_myhori_topimg.setMinimumHeight(CommonUtil.screenWidth(mContext)/2*24/180);
        tv_myhori_title = (TextView)view.findViewById(R.id.tv_myhori_title);
        tv_myhori_des = (TextView)view.findViewById(R.id.tv_myhori_des);
        tv_myhori_focus = (TextView)view.findViewById(R.id.tv_myhori_focus);
    }
    public void setImgShow(String src){
        GlideProxy.getInstance().loadHImage(mContext,src,iv_myhori_img);
    }

    public void setTitle(String title){
        if (tv_myhori_title != null){
            tv_myhori_title.setText(title);
        }
    }
    public void setDes(String title){
        if(tv_myhori_des != null){
            if(TextUtils.isEmpty(title)){
                tv_myhori_des.setVisibility(GONE);
            }else{
                tv_myhori_des.setVisibility(VISIBLE);
                tv_myhori_des.setText(title);
            }
        }
    }
    public void setFocus(String title){
        tv_myhori_focus.setText(title);
    }
    public void setTopImgShow(boolean b){
        if (b){
            iv_myhori_topimg.setVisibility(View.VISIBLE);
        }else{
            iv_myhori_topimg.setVisibility(View.GONE);
        }
    }
    public void setTopImgResource(int id){
        iv_myhori_topimg.setBackgroundResource(id);
    }
}
