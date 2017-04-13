package com.sumavision.talktv2.ui.widget;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.http.GlideProxy;

/**
 * Created by zhoutao on 2016/6/29.
 */
public class MyADView extends RelativeLayout {
    private Context mContext;
    ImageView iv_ad_img;
    TextView tv_ad_classify;
    TextView tv_ad_info;
    RelativeLayout rl_rec_ad;
    public MyADView(Context context) {
        super(context);
        mContext = context;
        View view = View.inflate(mContext,R.layout.item_rec_ad,this);
        iv_ad_img = (ImageView)view.findViewById(R.id.iv_ad_img);
        tv_ad_classify = (TextView)view. findViewById(R.id.tv_ad_classify);
        tv_ad_info = (TextView)view. findViewById(R.id.tv_ad_info);
        rl_rec_ad = (RelativeLayout) view. findViewById(R.id.rl_rec_ad);
    }


    public void setOnClick(OnClickListener onClickListener){
        rl_rec_ad.setOnClickListener(onClickListener);
    }

    public void setAdClassify(String classify){
        tv_ad_classify.setText(classify);
    }
    public void setAdInfo(String info){
        tv_ad_info.setText(info);
    }
    public void setADImageShow(String url){
        GlideProxy.getInstance().loadHImage(mContext,url,iv_ad_img);
    }
}
