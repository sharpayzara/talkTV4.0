package com.sumavision.talktv2.model.impl;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.dao.RxDao;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.WatchHistoryModel;
import com.sumavision.talktv2.model.entity.PlayerHistoryBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by zjx on 2016/7/5.
 */
public class WatchHistoryModelImpl implements WatchHistoryModel {
    RxDao rxDao = new RxDao(BaseApp.getContext(), PlayerHistoryBean.class, false);

    public PlayerHistoryBean getPlayHistory(String programId) {
        return (PlayerHistoryBean) rxDao.queryForFirst("programId", programId);
    }


    public void delPlayHistory(Integer programId) {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("id", programId);
        rxDao.deleteByColumnName(map);
    }

    @Override
    public void getAll(CallBackListener callback) {
        Observable.timer(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(i -> {
                    ArrayList<PlayerHistoryBean> beans = new ArrayList<>();
                    List list = rxDao.queryAllByOrder("id", false);
                    if(list != null && list.size() > 0 ){
                        beans.addAll(list);
                    }
                    callback.onSuccess(beans);
                });
    }

    @Override
    public void release() {

    }
}

