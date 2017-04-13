package com.sumavision.talktv2.ui.listener;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sumavision.talktv2.ui.activity.LoginActivity;
import com.sumavision.talktv2.ui.activity.base.BaseActivity;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

/**
 * Created by sharpay on 16-8-20.
 */
public class WeiBoUIListener implements WeiboAuthListener {
    LoginActivity activity;
    private Oauth2AccessToken mAccessToken;
    String nickname;
    public WeiBoUIListener(LoginActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onCancel() {
        // TODO Auto-generated method stub
        Toast.makeText(activity, "取消授权", Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onComplete(Bundle values) {
        // TODO Auto-generated method stub
        // 从 Bundle 中解析 Token
        mAccessToken = Oauth2AccessToken.parseAccessToken(values);
        if (mAccessToken.isSessionValid()) {
            nickname = "用户名："
                    + String.valueOf(values
                    .get("com.sina.weibo.intent.extra.NICK_NAME"));
            // 显示 Token

            // 保存 Token 到 SharedPreferences
/*            AccessTokenKeeper.writeAccessToken(activity,
                    mAccessToken);*/
            Toast.makeText(activity, "授权成功", Toast.LENGTH_SHORT)
                    .show();
            // Toast.makeText(
            // activity,
            // "头像地址:"
            // + String.valueOf(values
            // .get("com.sina.weibo.intent.extra.USER_ICON")),
            // Toast.LENGTH_LONG).show();

            Toast.makeText(activity, nickname, Toast.LENGTH_LONG)
                    .show();

        } else {
            // 以下几种情况，您会收到 Code：
            // 1. 当您未在平台上注册的应用程序的包名与签名时；
            // 2. 当您注册的应用程序包名与签名不正确时；
            // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
            String code = values.getString("code");
            String message = "授权失败";
            if (!TextUtils.isEmpty(code)) {
                message = message + "\nObtained the code: " + code;
            }
            Toast.makeText(activity, message, Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onWeiboException(WeiboException e) {
        // TODO Auto-generated method stub
        Toast.makeText(activity,
                "Auth exception : " + e.getMessage(), Toast.LENGTH_LONG)
                .show();
    }

}