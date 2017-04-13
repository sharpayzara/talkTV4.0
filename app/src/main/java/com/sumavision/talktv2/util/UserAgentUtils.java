package com.sumavision.talktv2.util;

import android.content.Context;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.common.ACache;

/**
 * Created by sharpay on 16-7-28.
 */
public class UserAgentUtils {
    private static String userAgent;
    public static String getUserAgent(){
        if(TextUtils.isEmpty(userAgent)){
            userAgent = BaseApp.getACache().getAsString("userAgent");
        }
        return userAgent;
    }
    public static void setUserAgent(Context mContext){
        WebView webview;
        webview = new WebView(mContext);
        webview.layout(0, 0, 0, 0);
        WebSettings settings = webview.getSettings();
        userAgent = settings.getUserAgentString();
        ACache.get(mContext).put("userAgent",userAgent);
    }
}
