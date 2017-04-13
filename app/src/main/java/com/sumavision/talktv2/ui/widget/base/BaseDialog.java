package com.sumavision.talktv2.ui.widget.base;

import android.app.Dialog;
import android.content.Context;

import com.sumavision.talktv2.presenter.base.BasePresenter;

import butterknife.ButterKnife;

/**
 * Created by zjx on 2016/6/20.
 */
public abstract class BaseDialog<T extends BasePresenter> extends Dialog {

    protected T presenter;

    public BaseDialog(Context context, int themeResId) {
        super(context, themeResId);
        setContentView(getLayoutResId());
        ButterKnife.bind(this);
        initPresenter();
    }


    protected abstract int getLayoutResId();

    protected abstract void initPresenter();

}
