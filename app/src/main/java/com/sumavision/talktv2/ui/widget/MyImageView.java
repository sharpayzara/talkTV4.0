package com.sumavision.talktv2.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sumavision.talktv2.R;

/**
 * Created by zhoutao on 2016/6/29.
 */
public class MyImageView extends RelativeLayout {
    private Context mContext;
    TextView tv_rec_programtitle;
    ImageView item_img;
    TextView tv_rec_programgrade;
    public MyImageView(Context context) {
        super(context);
        mContext = context;
        View view = View.inflate(mContext,R.layout.item_rec_header,this);
        tv_rec_programtitle =(TextView)view. findViewById(R.id.tv_rec_programtitle);
        item_img = (ImageView) view. findViewById(R.id.item_img);
        tv_rec_programgrade = (TextView) view. findViewById(R.id.tv_rec_programgrade);
    }

    public void setProgramTitle(String title){
        tv_rec_programtitle.setText(title);
    }
    public void setProgramGrade(String grade){
        tv_rec_programgrade.setText(grade);
    }
    public void setItemImageShow(String url){
        Glide.with(mContext)
                .load(url)
                .animate(R.anim.image_load)
                .placeholder(R.mipmap.banner_bg)
                .error(R.mipmap.error_banner)
                .crossFade()
                .into(item_img);
    }

    public void setScaleType(ImageView.ScaleType scaleType){
        item_img.setScaleType(scaleType);
    }

    public Drawable getDrawable(){
        return item_img.getDrawable();
    }
}
