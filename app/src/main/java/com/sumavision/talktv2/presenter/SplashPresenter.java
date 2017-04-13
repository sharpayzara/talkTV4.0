package com.sumavision.talktv2.presenter;

import android.content.Context;
import android.content.SharedPreferences;

import com.bumptech.glide.Glide;
import com.sumavision.talktv2.model.SplashModel;
import com.sumavision.talktv2.model.impl.SplashModelImpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.ISplashView;
import com.sumavision.talktv2.util.UserAgentUtils;
import com.sumavision.talktv2.videoplayer.player.impl.SystemPlayer;
import com.umeng.socialize.utils.Log;

import java.util.Date;

import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sharpay on 2016/6/6.
 */
public class SplashPresenter extends BasePresenter<ISplashView> {
    Context mContext;
    SplashModel model;
    public SplashPresenter(Context context, ISplashView iView) {
        super(context, iView);
        mContext = context;
        UserAgentUtils.setUserAgent(context);
        model = new SplashModelImpl();
    }
    public void getActionUrl(){
       model.loadActionData(null);
    }


    public void clearCacheData(){
        Observable.just(context.getSharedPreferences("OneWeek", Context.MODE_PRIVATE)
                .getLong("clearCache", System.currentTimeMillis()))
                .filter(beforeTime -> (System.currentTimeMillis() - beforeTime)/1000/60/24 > 6)
                .doOnNext(x -> saveClearCacheLog())
                .observeOn(Schedulers.io())
                .subscribe(x ->  Glide.get(context).clearDiskCache());
    }
    public void saveClearCacheLog(){
        SharedPreferences preferences = context.getSharedPreferences("OneWeek", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("clearCache", System.currentTimeMillis());
        editor.commit();
    }
    @Override
    public void release() {
        model.release();
    }
}
