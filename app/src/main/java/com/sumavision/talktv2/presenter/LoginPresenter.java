package com.sumavision.talktv2.presenter;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import com.jiongbull.jlog.JLog;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.DownloadModel;
import com.sumavision.talktv2.model.PreferenceModel;
import com.sumavision.talktv2.model.UserModel;
import com.sumavision.talktv2.model.entity.AccountData;
import com.sumavision.talktv2.model.entity.AppKeyResult;
import com.sumavision.talktv2.model.entity.LoginResult;
import com.sumavision.talktv2.model.entity.PreferenceBean;
import com.sumavision.talktv2.model.entity.ResultData;
import com.sumavision.talktv2.model.entity.UserInfo;
import com.sumavision.talktv2.model.impl.DownloadModelImpl;
import com.sumavision.talktv2.model.impl.PreferenceModelImpl;
import com.sumavision.talktv2.model.impl.UserModelImpl;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.iview.ILoginView;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.io.File;
import java.io.IOException;

/**
 * Created by sharpay on 2016/6/6.
 */
public class LoginPresenter extends BasePresenter<ILoginView> {
    UserModel model;
    DownloadModel downModel;
    PreferenceModel preferenceModel;

    public LoginPresenter(Context context, ILoginView iView) {
        super(context, iView);
        model =  new UserModelImpl();
        downModel = new DownloadModelImpl();
        preferenceModel = new PreferenceModelImpl();
    }
    /**
     *  desc  登陆操作
     *  @author  yangjh
     *  created at  16-9-6 下午12:02
     */
    public void loginIn(final String userName,final String password){
        iView.showProgressBar();
        model.loginIn(userName,password, new CallBackListener<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if(loginResult.getMsg().equals("success")){
                    if(AppGlobalVars.userInfo == null){
                        AppGlobalVars.userInfo = new UserInfo();
                    }
                    AppGlobalVars.userInfo.setUserId(loginResult.getObj().getUserId());
                    AppGlobalVars.userInfo.setAccessToken(loginResult.getObj().getToken());

                    model.loadUserInfo(new CallBackListener<AccountData>() {
                        @Override
                        public void onSuccess(AccountData accountData) {
                            iView.hiddenProgressBar();
                            AppGlobalVars.userInfo.setNickName(accountData.getObj().getNickName());
                            AppGlobalVars.userInfo.setProvince(accountData.getObj().getProvince());
                            AppGlobalVars.userInfo.setSex(accountData.getObj().getSex());
                            AppGlobalVars.userInfo.setTag(accountData.getObj().getTag());
                            AppGlobalVars.userInfo.setUserId(accountData.getObj().getUserId());
                            AppGlobalVars.userInfo.setBirthday(accountData.getObj().getBirthday());
                            AppGlobalVars.userInfo.setCity(accountData.getObj().getCity());
                            AppGlobalVars.userInfo.setOrigin("TVFAN");
                            AppGlobalVars.userInfo.setImageUrl(accountData.getObj().getProfile());
                            savePreferenceStr();
                            model.saveUserInfo();
                            sendOpenAppLog();
                            iView.returnView();
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            iView.hiddenProgressBar();
                            Toast.makeText(context,"登陆失败，" + throwable.toString(),Toast.LENGTH_SHORT).show();
                        }
                    });

                }else{
                    iView.hiddenProgressBar();
                    Toast.makeText(context,"登陆失败，用户名或密码错误！",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(context,"登陆失败，请稍后重试！",Toast.LENGTH_SHORT).show();
                iView.hiddenProgressBar();
            }
        });
    }
    /**
     *  desc  每天进入一次app的积分
     *  @author  yangjh
     *  created at  16-10-12 下午2:02
     */
    public void sendOpenAppLog(){
        if(AppGlobalVars.userInfo != null && !TextUtils.isEmpty(AppGlobalVars.userInfo.getUserId())){
            model.sendIntegralLog(new CallBackListener<ResultData>() {

                @Override
                public void onSuccess(ResultData resultData) {
                    if(resultData.getMsg() != null && resultData.getMsg().equals("success")){
                        Toast.makeText(context,"欢迎回到电视粉，获得积分+10",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {

                }
            },"login",AppGlobalVars.userInfo.getUserId(),"");
        }
    }
    public void login3rd(){
        iView.showProgressBar();
        model.loginIn3rd(new CallBackListener<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if(loginResult.getMsg().equals("success")){
                    AppGlobalVars.userInfo.setUserId(loginResult.getObj().getUserId());
                    model.saveUserInfo();
                    sendOpenAppLog();
                    if(loginResult.getObj().getIsNew() == 1){ //新用户
                        model.updateUserInfo(new CallBackListener<ResultData>() {
                            @Override
                            public void onSuccess(ResultData resultData) {
                                iView.hiddenProgressBar();
                                if(resultData.getMsg().equals("success")){
                                    iView.returnView();
                                    updateImg();
                                }else{
                                    Toast.makeText(context,"数据提交失败，请重试！",Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                iView.hiddenProgressBar();
                                Toast.makeText(context,throwable.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        model.loadUserInfo(new CallBackListener<AccountData>() {
                               @Override
                               public void onSuccess(AccountData accountData) {
                                   iView.hiddenProgressBar();
                                   if(accountData.getMsg().equals("success")){
                                       AppGlobalVars.userInfo.setNickName(accountData.getObj().getNickName());
                                       AppGlobalVars.userInfo.setProvince(accountData.getObj().getProvince());
                                       AppGlobalVars.userInfo.setSex(accountData.getObj().getSex());
                                       AppGlobalVars.userInfo.setTag(accountData.getObj().getTag());
                                       AppGlobalVars.userInfo.setBirthday(accountData.getObj().getBirthday());
                                       AppGlobalVars.userInfo.setCity(accountData.getObj().getCity());
                                       AppGlobalVars.userInfo.setOrigin("TVFAN");
                                       AppGlobalVars.userInfo.setUserId(accountData.getObj().getUserId());
                                       AppGlobalVars.userInfo.setImageUrl(accountData.getObj().getProfile());
                                       model.saveUserInfo();
                                       savePreferenceStr();
                                   }else{
                                       Toast.makeText(context,"获取用户数据失败", Toast.LENGTH_SHORT).show();
                                   }
                                   iView.returnView();
                               }

                               @Override
                               public void onFailure(Throwable throwable) {
                                   iView.hiddenProgressBar();
                                   Toast.makeText(context,"登陆失败，" + throwable.toString(),Toast.LENGTH_SHORT).show();
                               }});
                    }
                }else{
                    Toast.makeText(context,"数据提交失败，请重试！",Toast.LENGTH_SHORT).show();
                    iView.hiddenProgressBar();
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                iView.hiddenProgressBar();
                Toast.makeText(context,throwable.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void savePreferenceStr(){
        PreferenceBean preferenceBean = new PreferenceBean();
        if(!TextUtils.isEmpty(AppGlobalVars.userInfo.getTag()) && AppGlobalVars.userInfo.getTag().contains(",")){
            int index = AppGlobalVars.userInfo.getTag().lastIndexOf(",");
            preferenceBean.setPreference(AppGlobalVars.userInfo.getTag().substring(0,index));
            preferenceBean.setRole(AppGlobalVars.userInfo.getTag().substring(index+1));
        }else if(!TextUtils.isEmpty(AppGlobalVars.userInfo.getTag()) ){
            preferenceBean.setPreference(AppGlobalVars.userInfo.getTag());
            preferenceBean.setRole("");
        }
        preferenceModel.savePreferenceStr(preferenceBean);
    }
    public UserInfo getUserInfo() {
        return model.getUserInfo();
    }

    public void updateImg(){
        if(TextUtils.isEmpty(AppGlobalVars.userInfo.getImageUrl())){
           return;
        }
        final File file = createPicFile();
        downModel.download(new CallBackListener() {
            @Override
            public void onSuccess(Object o) {
                model.uploadImg(file, new CallBackListener<ResultData>() {
                    @Override
                    public void onSuccess(ResultData resultData) {
                        if(resultData.getMsg().equals("success")){
                            JLog.e("头像上传成功");
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Toast.makeText(context,"头像上传失败！",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(context, "文件下载失败", Toast.LENGTH_SHORT).show();
            }
        }, AppGlobalVars.userInfo.getImageUrl(),file, null);
    }

    @Override
    public void release() {
        model.release();
    }

    public File createPicFile(){
        File picFileDir = new File(Environment.getExternalStorageDirectory(), "/.tvfan");
        if (!picFileDir.exists()) {
            picFileDir.mkdirs();
        } else {
            picFileDir.delete();
            picFileDir.mkdirs();
        }
        String picUrl =  AppGlobalVars.userInfo.getImageUrl();
        String suffix =picUrl.substring(picUrl.lastIndexOf(".") + 1, picUrl.length());

        File picFile  = new File(picFileDir, "temp."+suffix);
        if (!picFile.exists()) {
            try {
                picFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return picFile;
    }

    public void loadAppKey(final SHARE_MEDIA plat){
        if(AppGlobalVars.appKeyResult != null && AppGlobalVars.appKeyResult.getData().size() > 2){
            iView.login3rd(plat,AppGlobalVars.appKeyResult);
            return;
        }
        iView.showProgressBar();
        model.loadAppKey(new CallBackListener<AppKeyResult>() {
            @Override
            public void onSuccess(AppKeyResult appKey) {
                AppGlobalVars.appKeyResult = appKey;
                iView.hiddenProgressBar();
                iView.login3rd(plat,appKey);
            }

            @Override
            public void onFailure(Throwable throwable) {
                iView.hiddenProgressBar();
                Toast.makeText(context,"请求失败,请重试！",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
