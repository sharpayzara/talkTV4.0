package com.sumavision.talktv2.ui.receiver;


/**
 * Created by zjx on 2016/10/10.
 */
public abstract class TANetChangeObserver {

    /**
     * 网络连接连接时调用
     */
    public abstract void onConnect(int type);


    /**
     * 当前没有网络连接
     */
    public void onDisConnect()
    {
    }
}
