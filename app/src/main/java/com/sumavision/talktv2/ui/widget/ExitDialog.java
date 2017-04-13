package com.sumavision.talktv2.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.ui.activity.Game37WanActivity;
import com.sumavision.talktv2.ui.activity.WeBADActivity;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.NoDoubleClickListener;

/**
 * Created by zhoutao on 2016/7/17.
 */
public class ExitDialog extends Dialog{
    private Context mContext;
    private Button btn_exit_cancel,btn_exit_ok;
    private ImageView exit_ad;
    private RelativeLayout exit_top;
//    private RelativeLayout background;
    public ExitDialog(Context context) {
        super(context);
    }

    public ExitDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
        initView();
    }

    public ExitDialog(Context context, boolean cancelable, OnCancelListener cancelListener, Context mContext) {
        super(context, cancelable, cancelListener);
        this.mContext = mContext;
    }

    private void initView() {
        setContentView(R.layout.dialog_exit);
        btn_exit_cancel = (Button) findViewById(R.id.btn_exit_cancel);
        btn_exit_ok = (Button) findViewById(R.id.btn_exit_ok);
        exit_top = (RelativeLayout) findViewById(R.id.exit_top);
        exit_ad = (ImageView) findViewById(R.id.exit_ad);
//        background = (RelativeLayout) findViewById(R.id.exit_backgroud);
//        background.setBackground(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.about));
    }

    public Button getOkButton(){
        return btn_exit_ok;
    }
    public Button getCancelButton(){
        return btn_exit_cancel;
    }
    public void setTOPshow(boolean ishow){
        if (ishow){
            exit_top.setVisibility(View.VISIBLE);
        }else{
            exit_top.setVisibility(View.GONE);

        }
    }
    public void setADEnter(String url,String style){
        if (!TextUtils.isEmpty(url)){
            exit_ad.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    switch (style){
                        case "web":
                            //如果当前是弹出状态,执行跳转
                            Intent intent = new Intent(getContext(), WeBADActivity.class);
                            intent.putExtra("url",url);
                            getContext().startActivity(intent);
                            break;
                        case "h5":
                            Intent intent2 = new Intent(getContext(), Game37WanActivity.class);
                            intent2.putExtra("url",url);
                            getContext().startActivity(intent2);
                            break;
                        case "duiba":
                            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_ENTERDUIBA,url);
                            AppGlobalConsts.ISLOGINDUIBA = true;
                            break;
                    }
                }
            });
            /*if ("h5".equals(style)){
                exit_ad.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, Game37WanActivity.class);
                        intent.putExtra("url",url);
                        mContext.startActivity(intent);
                    }
                });
            }else{
                exit_ad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), WeBADActivity.class);
                        intent.putExtra("url",url);
                        getContext().startActivity(intent);
                    }
                });
            }*/
        }
    }
    public void setShowImage(String url){
        Glide.with(mContext)
                .load(url)
                .animate(R.anim.image_load)
                .placeholder(R.drawable.exit_bg)
                .error(R.mipmap.error_horizontal)
                .crossFade()
                .into(exit_ad);
    }
}
