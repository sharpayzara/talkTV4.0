package com.sumavision.talktv2.presenter;

import android.content.Context;

import com.jiongbull.jlog.JLog;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.HomeModel;
import com.sumavision.talktv2.model.entity.VersionData;
import com.sumavision.talktv2.model.impl.HomeModelmpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.ISettingView;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.AppUtil;

/**
 * Created by zhoutao on 2016/6/24.
 */
public class SettingFragmentPresenter extends BasePresenter<ISettingView> {
    HomeModel homeModel;
    Context mContext ;
    public SettingFragmentPresenter(Context context, ISettingView iView) {
        super(context, iView);
        homeModel = new HomeModelmpl();
        mContext = context;
    }

    @Override
    public void release() {

    }

    public void getVersionData(){
        homeModel.getUpdateInfo(new CallBackListener<VersionData>() {

            @Override
            public void onSuccess(VersionData versionData) {
                if(Integer.parseInt(versionData.versionId ) > AppUtil.getAppVersionCode(mContext)){
                    iView.showUpdateView(AppGlobalConsts.SERVER_CODE_OK,"版本更新",versionData);
                }else{
                    iView.showUpdateView(AppGlobalConsts.SERVER_CODE_ERROR,"",versionData);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                JLog.d("talkTV4.0", "访问异常");
            }
        });
    }
}
