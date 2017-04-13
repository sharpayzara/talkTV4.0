package com.sumavision.talktv2.ui.activity.base;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.widget.HeadControlPanel;
import com.sumavision.talktv2.ui.widget.MyLinearLayout;
import com.sumavision.talktv2.util.NoDoubleClickListener;

/**
 * Created by sharpay on 16-6-22.
 */
public abstract class CommonHeadPanelActivity<T extends BasePresenter> extends  BaseActivity<T>{

    Button backIcon;
    HeadControlPanel headControlPanel;
    LinearLayout rightBtn;
    MyLinearLayout leftBtn;

    public void initHeadPanel(){
        backIcon = (Button) findViewById(R.id.back_icon);
        headControlPanel = (HeadControlPanel) findViewById(R.id.head_layout);
        rightBtn = (LinearLayout) findViewById(R.id.right_btn);
        leftBtn = (MyLinearLayout) findViewById(R.id.left_btn);
        leftBtn.setOnClickListener(
                new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View view) {
                        onBackPressed();
                    }
                }
        );
        leftBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        backIcon.setBackgroundResource(R.mipmap.nav_right_icon_red);
                        break;
                    case MotionEvent.ACTION_UP:
                        backIcon.setBackgroundResource(R.mipmap.nav_right_icon);
                        break;
                }
                backIcon.setFocusable(true);
                return false;
            }
        });
    }


    public void setHeadTitle(String msg) {
        headControlPanel.setMiddleTitle(msg);
    }
    public void setHeadTitle(String msg, View.OnClickListener listener) {
        headControlPanel.setMiddleTitle(msg,listener);
    }
    public void showBackBtn() {
        headControlPanel.getmLeftBtn().setVisibility(View.VISIBLE);
    }

    public void showCornerMark(boolean bool){
        headControlPanel.showCornerMark(bool);
    }

    /**
     *
     * @param rightTitle 右边按钮的标题
     * @param iconId 右边按钮的图标
     * @param isShow 是否显示
     */
    public void setRightBtn(String rightTitle, int iconId,boolean isShow){
        headControlPanel.setRightTitle(rightTitle);
        headControlPanel.setmRightButton(iconId);
        if(isShow){
            rightBtn.setVisibility(View.VISIBLE);
        }
    }
    public void setRightBtnListener(View.OnClickListener listener){
        rightBtn.setOnClickListener(listener);
    }

}
