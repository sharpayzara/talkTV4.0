package com.sumavision.talktv2.util;

import com.sumavision.talktv2.model.entity.AppKeyResult;
import com.sumavision.talktv2.model.entity.UserInfo;

/**
 *  desc  全局变量
 *  @author  yangjh
 *  created at  16-5-23 上午11:36
 */
public class AppGlobalVars {
    public static String mepgApiHost = "http://mobile.tvfan.cn:8080/mepg-api/";
    public static String globalSoHost = "http://mobile.tvfan.cn:8080/global/";
    public static String upgcApiHost = "http://mobile.tvfan.cn:8080/upgc-api/";
    public static String liveApiHost = "http://tv.tvfan.cn:8080/liveapi/";
    public static String logApiHost = "http://mobile.tvfan.cn:8080/mepg-log/";
    public static String userCenterApiHost ="https://uc.tvfan.cn/userCenter/";
    public static String duibaHost = "https://duiba.tvfan.cn/duiba/";


//    public static String mepgApiHost = "http://172.16.40.87:7180/mepg-api/";
//    public static String globalSoHost = "http://172.16.40.87:7180/global/";
//    public static String upgcApiHost = "http://172.16.40.88:81/upgc-api/";
//    public static String liveApiHost = "http://172.16.40.87:7180/liveapi/";
//    public static String logApiHost = "http://172.16.40.87:7180/mepg-log/";
//    public static String userCenterApiHost = "https://172.16.40.88/userCenter/";
//    public static String duibaHost = "https://172.16.40.88/duiba/";

    //服务器调整测试
//    public static String mepgApiHost = "http://172.16.40.88:8080/mepg-api/";
//    public static String globalSoHost = "http://172.16.40.88:8080/global/";
//    public static String upgcApiHost = "http://172.16.40.88:8080/upgc-api/";
//    public static String liveApiHost = "http://172.16.40.88:8080/liveapi/";
//    public static String logApiHost = "http://172.16.40.88:8080/mepg-log/";
//    public static String userCenterApiHost = "https://172.16.40.88/userCenter/";
//    public static String duibaHost = "https://172.16.40.88/duiba/";
//    public static String shareApiHost = "http://share.tvfan.cn/H5.html";

    public static String showGirlHost = "http://www.kktv1.com";
    public static String pay_order = "http://wxpay.weixin.qq.com";
    public static String shareApiHost = "http://share.tvfan.cn/H5.html";
    public static UserInfo userInfo;
    public static AppKeyResult appKeyResult;

    public static String actionHost = "http://mobile.tvfan.cn:8080";    //正式库
//    public static String actionHost = "http://172.16.40.88:8080/";
}