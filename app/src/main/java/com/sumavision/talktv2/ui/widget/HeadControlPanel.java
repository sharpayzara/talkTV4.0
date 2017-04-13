package com.sumavision.talktv2.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;

public class HeadControlPanel extends RelativeLayout{
    private Context mContext;
    private TextView mMidleTitle,mRightTitle;
    private LinearLayout mLeftBtn;
    private ImageButton mRightButton;
    private ImageView cornerMark;
    private static final float middle_title_size = 18f;
    private static final float right_title_size = 18f;
    private static final int default_background_color = Color.rgb(255,255,255);

    public HeadControlPanel(Context context) {
        super(context);
    }

    public HeadControlPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeadControlPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        // TODO Auto-generated method stub
        super.onFinishInflate();
        mMidleTitle = (TextView)findViewById(R.id.midle_title);
        mRightTitle= (TextView)findViewById(R.id.right_title);
        mRightButton = (ImageButton) findViewById(R.id.right_Button);
        mLeftBtn = (LinearLayout) findViewById(R.id.left_btn);
        cornerMark = (ImageView) findViewById(R.id.corner_mark);
        setBackgroundColor(default_background_color);
    }
    public void setTitlePanelColor(int color){
        setBackgroundColor(color);
    }
    public void setTitlePanelTextColor(int color){
        mMidleTitle.setTextColor(color);
    }
    public void setMiddleTitle(String s){
        mMidleTitle.setText(s);
        mMidleTitle.setTextSize(middle_title_size);
    }
    public void setMiddleTitle(String s, OnClickListener listener){
        mMidleTitle.setText(s);
        mMidleTitle.setTextSize(middle_title_size);
        mMidleTitle.setOnClickListener(listener);
    }
    public void setRightTitle(String s){
        mRightTitle.setText(s);
        mRightTitle.setTextSize(middle_title_size);
    }
    public void setmRightButton(int iconId){
        mRightButton.setBackgroundResource(iconId);
    }

    public void showCornerMark(boolean bool){
        if(bool){
            cornerMark.setVisibility(VISIBLE);
        }else{
            cornerMark.setVisibility(GONE);
        }
    }

    public LinearLayout getmLeftBtn() {
        return mLeftBtn;
    }

    public void setmLeftBtn(LinearLayout mLeftBtn) {
        this.mLeftBtn = mLeftBtn;
    }
}
