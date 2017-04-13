package com.sumavision.talktv2.ui.listener;

import android.app.Activity;

import com.sumavision.talktv2.ui.activity.base.BaseActivity;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

/**
 * Created by sharpay on 16-8-20.
 */
public class QQUIListener implements IUiListener {
    BaseActivity activity;

    public QQUIListener(BaseActivity activity) {
        this.activity = activity;
    }

    protected void doComplete(JSONObject values) {
        activity.onBackPressed();
    }

    @Override
    public void onComplete(Object o) {

    }

    @Override
    public void onError(UiError e) {
    }
    @Override
    public void onCancel() {
    }
}