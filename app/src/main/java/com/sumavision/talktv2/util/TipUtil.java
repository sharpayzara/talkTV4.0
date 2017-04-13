package com.sumavision.talktv2.util;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 *  desc  用于显示提示信息用于显示提示信息
 *  @author  yangjh
 *  created at  16-5-24 下午5:33
 */
public class TipUtil {
    /** 之前显示的内容 */
    private static String oldMsg ;
    static Snackbar currSnackbar;
    /** 第一次时间 */
    private static long oneTime = 0 ;
    /** 第二次时间 */
    private static long twoTime = 0 ;
    private TipUtil() {

    }
    public static void showTipWithAction(View view, String tipText, String actionText, View.OnClickListener listener) {
        show(Snackbar.make(view, tipText, Snackbar.LENGTH_INDEFINITE),tipText);
    }

    public static void showTipWithAction(View view, String tipText, String actionText, View.OnClickListener listener,int duration){
        show(Snackbar.make(view, tipText, duration),tipText);
    }

    public static void showSnackTip(View view, String tipText) {
       show(Snackbar.make(view, tipText, Snackbar.LENGTH_SHORT),tipText);
    }
    public static void showLongSnackTip(View view, String tipText) {
       show(Snackbar.make(view, tipText, Snackbar.LENGTH_LONG),tipText);
    }

    public static void show(Snackbar snackbar,String tipText){
        if (currSnackbar == null) {
            currSnackbar = snackbar;
            currSnackbar.show();
            oneTime = System.currentTimeMillis();
        } else{
            twoTime = System.currentTimeMillis();
            if (tipText.equals(oldMsg)) {
                if (twoTime - oneTime > Snackbar.LENGTH_LONG) {
                    snackbar.show();
                }
            } else {
                oldMsg = tipText;
                currSnackbar.setText(tipText);
                currSnackbar.show();
            }
        }
        oneTime = twoTime;
    }
}
