package com.sumavision.talktv2.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.webkit.WebView;
import android.widget.Toast;

import com.sumavision.talktv2.ui.activity.CreditActivity;
import com.sumavision.talktv2.ui.activity.LoginActivity;

/**
 * Created by Administrator on 2016/11/14.
 */

public class DuibaUtill {
    public static void enterDuiba(String url, String navColor, String titleColor, Context context){
        Intent intent = new Intent();
        intent.setClass(context, CreditActivity.class);
        intent.putExtra("navColor", navColor);    //配置导航条的背景颜色，请用#ffffff长格式。
        intent.putExtra("titleColor", titleColor);    //配置导航条标题的颜色，请用#ffffff长格式。
//        intent.putExtra("url", "http://www.duiba.com.cn/test/demoRedirectSAdfjosfdjdsa");    //配置自动登陆地址，每次需服务端动态生成。
        intent.putExtra("url", url);    //配置自动登陆地址，每次需服务端动态生成。
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        CreditActivity.creditsListener = new CreditActivity.CreditsListener() {
            /**
             * 当点击分享按钮被点击
             * @param shareUrl 分享的地址
             * @param shareThumbnail 分享的缩略图
             * @param shareTitle 分享的标题
             * @param shareSubtitle 分享的副标题
             */
            public void onShareClick(WebView webView, String shareUrl, String shareThumbnail, String shareTitle, String shareSubtitle) {
                //当分享按钮被点击时，会调用此处代码。在这里处理分享的业务逻辑。
                new android.app.AlertDialog.Builder(webView.getContext())
                        .setTitle("分享信息")
                        .setItems(new String[] {"标题："+shareTitle,"副标题："+shareSubtitle,"缩略图地址："+shareThumbnail,"链接："+shareUrl}, null)
                        .setNegativeButton("确定", null)
                        .show();
            }

            /**
             * 当点击“请先登录”按钮唤起登录时，会调用此处代码。
             * 用户登录后，需要将CreditsActivity.IS_WAKEUP_LOGIN变量设置为true。
             * @param webView 用于登录成功后返回到当前的webview刷新登录状态。
             * @param currentUrl 当前页面的url
             */
            public void onLoginClick(final WebView webView, final String currentUrl) {
                new android.app.AlertDialog.Builder(webView.getContext())
                        .setTitle("跳转登录")
                        .setMessage("跳转到登录页面？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent loginIntent= new Intent(webView.getContext(), LoginActivity.class);
                                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                webView.getContext().startActivity(loginIntent);
                                AppGlobalConsts.newDuibaUrl = currentUrl;
                                AppGlobalConsts.ISFROMDUIBATOLOGIN = true;
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }

            /**
             * 当点击“复制”按钮时，触发该方法，回调获取到券码code
             * @param webView webview对象。
             * @param code 复制的券码
             */
            public void onCopyCode(WebView webView, String code) {
                //当未登录的用户点击去登录时，会调用此处代码。
                new android.app.AlertDialog.Builder(webView.getContext())
                        .setTitle("复制券码")
                        .setMessage("已复制，券码为："+code)
                        .setPositiveButton("是", null)
                        .setNegativeButton("否", null)
                        .show();
            }

            /**
             * 积分商城返回首页刷新积分时，触发该方法。
             */
            public void onLocalRefresh(WebView mWebView, String credits) {
                //String credits为积分商城返回的最新积分，不保证准确。
                //触发更新本地积分，这里建议用ajax向自己服务器请求积分值，比较准确。
                Toast.makeText(context, "触发本地刷新积分："+credits,Toast.LENGTH_SHORT).show();
            }
        };
    }
}
