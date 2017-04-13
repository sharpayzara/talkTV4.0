package com.sumavision.talktv2.ui.widget;

/**
 * Created by sharpay on 16-6-6.
 */
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.sumavision.talktv2.R;

public class LoadingLayout extends FrameLayout {

    private int emptyView, wifiView, loadingView,errorView;
    private OnClickListener onRetryClickListener;
    // private static ObjectAnimator animator;

    public LoadingLayout(Context context) {
        this(context, null);
    }

    public LoadingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public LoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LoadingLayout, 0, 0);
        try {
            emptyView = a.getResourceId(R.styleable.LoadingLayout_emptyView, R.layout.empty_view);
            wifiView = a.getResourceId(R.styleable.LoadingLayout_wifiView, R.layout.wifi_view);
            loadingView = a.getResourceId(R.styleable.LoadingLayout_loadingView, R.layout.loading_view);
            errorView = a.getResourceId(R.styleable.LoadingLayout_errorView, R.layout.error_view);

            LayoutInflater inflater = LayoutInflater.from(getContext());
            inflater.inflate(emptyView, this, true);
            inflater.inflate(wifiView, this, true);
            inflater.inflate(loadingView, this, true);
            inflater.inflate(errorView, this, true);
           /* animator = ObjectAnimator.ofFloat(view.findViewById(R.id.loading), "rotation", 0.0F, 360F).setDuration(800);
            animator.setRepeatCount(-1);
            animator.setInterpolator(new LinearInterpolator());*/
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        for (int i = 0; i < getChildCount() - 1; i++) {
            getChildAt(i).setVisibility(GONE);
        }

        findViewById(R.id.btn_empty_retry).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onRetryClickListener) {
                    onRetryClickListener.onClick(v);
                }
            }
        });
        findViewById(R.id.btn_wifi_retry).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onRetryClickListener) {
                    onRetryClickListener.onClick(v);
                }
            }
        });
        findViewById(R.id.btn_error_retry).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onRetryClickListener) {
                    onRetryClickListener.onClick(v);
                }
            }
        });
    }

    public void setOnRetryClickListener(OnClickListener onRetryClickListener) {
        this.onRetryClickListener = onRetryClickListener;
    }

    public void showEmptyView() {
        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            if (i == 0) {
                /*if(animator.isStarted()){
                    animator.end();
                }*/
                child.setVisibility(VISIBLE);
            } else {
                child.setVisibility(GONE);
            }
        }
    }

    public void showWifiView() {
        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            if (i == 1) {
                /*if(animator.isStarted()){
                    animator.end();
                }*/
                child.setVisibility(VISIBLE);
            } else {
                child.setVisibility(GONE);
            }
        }
    }

    public void showProgressBar() {
        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            if (i == 2 && (child.getVisibility() == INVISIBLE || child.getVisibility() == GONE )) {
                //  animator.start();
                child.setVisibility(VISIBLE);
                ((PPTVLoading)((ViewGroup)child).getChildAt(0)).start();
            } else {
                child.setVisibility(GONE);
            }
        }
    }

    public void showErrorView() {
        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            if (i == 3) {
                /*if(animator.isStarted()){
                    animator.end();
                }*/
                child.setVisibility(VISIBLE);
            } else {
                child.setVisibility(GONE);
            }
        }
    }

    public void hideProgressBar() {
        for (int i = 0; i < this.getChildCount(); i++) {
            View child = this.getChildAt(i);
            child.setVisibility(GONE);
            if(i == 2){
                ((PPTVLoading)((ViewGroup)child).getChildAt(0)).stop();
            }
        }
    }
}
