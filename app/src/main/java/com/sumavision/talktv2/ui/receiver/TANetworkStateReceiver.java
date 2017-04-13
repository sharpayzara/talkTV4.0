package com.sumavision.talktv2.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.sumavision.talktv2.common.ShareElement;
import com.sumavision.talktv2.util.NetworkUtil;

import java.util.ArrayList;

/**
 *网络状态改变的监听
 * Created by zjx on 2016/10/10.
 */
public class TANetworkStateReceiver extends BroadcastReceiver {

    private static Boolean networkAvailable = false;
    private static int netType;
    private static TANetChangeObserver taNetChangeObserver;
    private final static String ANDROID_NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    public final static String TA_ANDROID_NET_CHANGE_ACTION = "ta.android.net.conn.CONNECTIVITY_CHANGE";
    private static BroadcastReceiver receiver;

    private static BroadcastReceiver getReceiver()
    {
        if (receiver == null) {
            synchronized (TANetworkStateReceiver.class) {
                if (receiver == null)
                    receiver = new TANetworkStateReceiver();
            }
        }
        return receiver;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        receiver = TANetworkStateReceiver.this;
        if (intent.getAction().equalsIgnoreCase(ANDROID_NET_CHANGE_ACTION)
                || intent.getAction().equalsIgnoreCase(
                TA_ANDROID_NET_CHANGE_ACTION))
        {
//            TALogger.i(TANetworkStateReceiver.this, "网络状态改变.");
            if (!NetworkUtil.isConnectedByState(context))
            {
//                TALogger.i(TANetworkStateReceiver.this, "没有网络连接.");
                networkAvailable = false;
                notifyObserver();
            } else
            {
//                TALogger.i(TANetworkStateReceiver.this, "网络连接成功.");
                netType = NetworkUtil.getCurrentNetworkType(context);
                networkAvailable = true;
                notifyObserver();
                if(netType == ConnectivityManager.TYPE_WIFI)
                    ShareElement.isIgnoreNetChange = -1;
            }

        }
    }

    /**
     * 注册网络状态广播
     * @param mContext
     */
    public static void registerNetworkStateReceiver(Context mContext)
    {
        IntentFilter filter = new IntentFilter();
        filter.addAction(TA_ANDROID_NET_CHANGE_ACTION);
        filter.addAction(ANDROID_NET_CHANGE_ACTION);
        mContext.getApplicationContext()
                .registerReceiver(getReceiver(), filter);
    }

    /**
     * 检查网络状态
     * @param mContext
     */
    public static void checkNetworkState(Context mContext)
    {
        Intent intent = new Intent();
        intent.setAction(TA_ANDROID_NET_CHANGE_ACTION);
        mContext.sendBroadcast(intent);
    }

    /**
     * 注销网络状态广播
     * @param mContext
     */
    public static void unRegisterNetworkStateReceiver(Context mContext)
    {
        if (receiver != null)
        {
            try
            {
                mContext.getApplicationContext().unregisterReceiver(receiver);
            } catch (Exception e)
            {
//                TALogger.d("TANetworkStateReceiver", e.getMessage());
            }
        }
    }

    /**
     * 获取当前网络状态，true为网络连接成功，否则网络连接失败
     * @return
     */
    public static Boolean isNetworkAvailable()
    {
        return networkAvailable;
    }

    public static int getAPNType()
    {
        return netType;
    }

    private void notifyObserver()
    {
        if (taNetChangeObserver != null )
        {
            if (networkAvailable)
            {
                if(ShareElement.isIgnoreNetChange != 2)//选择继续观看之后不再关心网络变化
                    taNetChangeObserver.onConnect(netType);
            } else
            {
                taNetChangeObserver.onDisConnect();
            }
        }
    }

    /**
     * 注册网络连接观察者
     *
     * @param observer
     */
    public static void registerObserver(TANetChangeObserver observer)
    {
        taNetChangeObserver = observer;
    }

    /**
     * 注销网络连接观察者
     *
     * @param observer
     */
    public static void removeRegisterObserver(TANetChangeObserver observer)
    {
        taNetChangeObserver = null;
    }
}
