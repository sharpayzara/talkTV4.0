package com.sumavision.talktv2.dao.ormlite;

/**
 *  desc  数据库操作回调
 *  @author  yangjh
 *  created at  16-5-24 下午5:30
 */
public interface DbCallBack<T> {

    void onComplete(T data);


}
