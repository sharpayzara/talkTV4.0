package com.sumavision.talktv2.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ScrollView;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.jiongbull.jlog.JLog;
import com.sumavision.talktv2.util.CommonUtil;

/**
 * Created by sharpay on 16-8-20.
 */
public class DecroScrollView extends ScrollView {
    View indicatorView;
    int[] location = new int[2];

    public DecroScrollView(Context context) {
        super(context);
    }

    public DecroScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DecroScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

   public void setIndicatorView(View view){
       indicatorView = view;
   }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        indicatorView.getLocationOnScreen(location);
        if(indicatorView != null){

            JLog.e(location[1]+"Y");
            JLog.e(this.getY()+"RawY");
        /*if(indicatorView.getY() < 500){
            return false;
        }else{
            return true;
        }*/
        }
        if(this.getY() == indicatorView.getY()){
            return false;
        }
        return true;
    }
}