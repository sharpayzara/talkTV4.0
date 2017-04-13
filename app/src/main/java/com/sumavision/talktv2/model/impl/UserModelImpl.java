package com.sumavision.talktv2.model.impl;

import android.content.Context;
import android.content.SharedPreferences;

import com.jiongbull.jlog.JLog;
import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.dao.RxDao;
import com.sumavision.talktv2.http.SumaHttpsClient;
import com.sumavision.talktv2.http.UserRetrofit;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.UserModel;
import com.sumavision.talktv2.model.entity.AccountData;
import com.sumavision.talktv2.model.entity.AppKeyResult;
import com.sumavision.talktv2.model.entity.LoginResult;
import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.model.entity.ResultData;
import com.sumavision.talktv2.model.entity.UserInfo;
import com.sumavision.talktv2.ui.listener.DownloadProgressListener;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.sumavision.talktv2.util.CryptTool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import dac3rdparty.org.apache.commons.codec.binary.Base64;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by sharpay on 2016/9/6.
 */
public class UserModelImpl implements UserModel{
    Subscription subscription;
    RxDao rxDao = new RxDao(BaseApp.getContext(), PlayerHistoryBean.class, false);
    private static final String DIGEST_ALGORITHM = "MD5";
    private static final String ENCRYPT_ALGORITHM = "AES";
    private static final String CIPHER = "AES/CBC/PKCS5Padding";
    private static final String ENCODING = "utf-8";
    private static final byte[] iv = {0x01,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
    //used to encrypt client server communication, length must be 16.
    public static final String RELEASE_KEY = "09Z18tvFaNmobILe";

    @Override
    public void saveUserInfo() {
        SharedPreferences preferences = BaseApp.getContext().getSharedPreferences("TVFan", Context.MODE_PRIVATE);
        // 创建字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // 创建对象输出流，并封装字节流
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            // 将对象写入字节流
            oos.writeObject(AppGlobalVars.userInfo);
            // 将字节流编码成base64的字符窜
            String userInfoBase64 = new String(Base64.encodeBase64(baos.toByteArray()));
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("userInfo", userInfoBase64);

            editor.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void release() {
        if(rxDao != null){
            rxDao.unsubscribe();
        }
    }

    public void loadAppKey(final CallBackListener<AppKeyResult> listener){
        subscription = SumaHttpsClient.subscribe(new Callable<Observable<AppKeyResult>>() {
            @Override
            public Observable<AppKeyResult> call() {
                return SumaHttpsClient.getRetrofitInstance(UserRetrofit.class).loadAppkey();
            }
        }, new Action1<AppKeyResult>() {
            @Override
            public void call(AppKeyResult appKeyResult) {
                for(AppKeyResult.DataBean bean: appKeyResult.getData()){
                    try {
                       bean.setSecret(decode(bean.getSecret(),RELEASE_KEY)) ;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                listener.onSuccess(appKeyResult);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },AppKeyResult.class);
    }

    @Override
    public void loginIn(final String account, final String password,final CallBackListener<LoginResult> listener){

        subscription = SumaHttpsClient.subscribe(new Callable<Observable<LoginResult>>() {
            @Override
            public Observable<LoginResult> call() {
                return SumaHttpsClient.getRetrofitInstance(UserRetrofit.class).loginIn(account, CryptTool.md5Digest(password),judgeLoginType(account));
            }
        }, new Action1<LoginResult>() {
            @Override
            public void call(LoginResult loginResult) {
                listener.onSuccess(loginResult);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },LoginResult.class);
    }

    @Override
    public void uploadImg(File file,final CallBackListener<ResultData> listener) {
        //File file = new File(Environment.getExternalStorageDirectory(), "icon.png");
        RequestBody photoRequestBody = RequestBody.create(MediaType.parse("image/png"), file);
        final Map<String,RequestBody> params = new HashMap<>();
        params.put("userId",RequestBody.create(MediaType.parse("text/plain"), AppGlobalVars.userInfo.getUserId()));
        params.put("profile\"; filename=\"" + file.getName(),photoRequestBody);
        subscription = SumaHttpsClient.subscribe(new Callable<Observable<ResultData>>() {
            @Override
            public Observable<ResultData> call() {
                return SumaHttpsClient.getRetrofitInstance(UserRetrofit.class).uploadImg(params);
            }
        }, new Action1<ResultData>() {
            @Override
            public void call(ResultData resultData) {
                listener.onSuccess(resultData);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },LoginResult.class);
    }

    @Override
    public void download(CallBackListener listener, String url, File file, DownloadProgressListener progressListener) {

    }


    public void loginIn3rd(final CallBackListener<LoginResult> listener){
        saveUserInfo();
        subscription = SumaHttpsClient.subscribe(new Callable<Observable<LoginResult>>() {
            @Override
            public Observable<LoginResult> call() {
                return SumaHttpsClient.getRetrofitInstance(UserRetrofit.class).loginIn3rd(AppGlobalVars.userInfo.getOpenid(),
                        AppGlobalVars.userInfo.getAccessToken(),AppGlobalVars.userInfo.getExpiresIn(),AppGlobalVars.userInfo.getOrigin());
            }
        }, new Action1<LoginResult>() {
            @Override
            public void call(LoginResult loginResult) {

                listener.onSuccess(loginResult);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },LoginResult.class);
    }

    @Override
    public void saveAndUpdateUserInfo(final CallBackListener<ResultData> listener) {
        saveUserInfo();
        updateUserInfo(listener);
    }
    public boolean logoutUser(){
        subscription = SumaHttpsClient.subscribe(new Callable<Observable<ResultData>>() {
            @Override
            public Observable<ResultData> call() {
                return SumaHttpsClient.getRetrofitInstance(UserRetrofit.class).loginOut(AppGlobalVars.userInfo.getUserId(),AppGlobalVars.userInfo.getOrigin());
            }
        }, new Action1<ResultData>() {
            @Override
            public void call(ResultData resultData) {
                JLog.e("loginout success");
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                JLog.e(throwable.toString());
            }
        },ResultData.class);

        AppGlobalVars.userInfo = null;
        SharedPreferences preferences = BaseApp.getContext().getSharedPreferences("TVFan", Context.MODE_PRIVATE);
        return preferences.edit().clear().commit();
    }


    @Override
    public void updateUserInfo(final CallBackListener listener) {
        subscription = SumaHttpsClient.subscribe(new Callable<Observable<ResultData>>() {
            @Override
            public Observable<ResultData> call() {
                //网络请求
                return SumaHttpsClient.getRetrofitInstance(UserRetrofit.class).updateUserInfo(AppGlobalVars.userInfo.getUserId()
                        ,AppGlobalVars.userInfo.getNickName(),AppGlobalVars.userInfo.getSex(),AppGlobalVars.userInfo.getBirthday()
                ,AppGlobalVars.userInfo.getCity(),AppGlobalVars.userInfo.getProvince(),AppGlobalVars.userInfo.getTag());
            }
        }, new Action1<ResultData>() {
            @Override
            public void call(ResultData resultData ) {
              listener.onSuccess(resultData);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },ResultData.class);
    }


    @Override
    public void loadUserInfo(final CallBackListener listener) {
        subscription = SumaHttpsClient.subscribe(new Callable<Observable<AccountData>>() {
            @Override
            public Observable<AccountData> call() {
                //网络请求
                return SumaHttpsClient.getRetrofitInstance(UserRetrofit.class).loadUserInfo(AppGlobalVars.userInfo.getUserId(),AppGlobalVars.userInfo.getAccessToken());
            }
        }, new Action1<AccountData>() {
            @Override
            public void call(AccountData accountData) {
                listener.onSuccess(accountData);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },AccountData.class);
    }
    public UserInfo getUserInfo() {
        UserInfo userInfo = null;
        SharedPreferences preferences = BaseApp.getContext().getSharedPreferences("TVFan", Context.MODE_PRIVATE);
        String productBase64 = preferences.getString("userInfo", "");
        //读取字节
        byte[] base64 = Base64.decodeBase64(productBase64.getBytes());
        //封装到字节流
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        try {
            //再次封装
            ObjectInputStream bis = new ObjectInputStream(bais);
            try {
                //读取对象
                userInfo = (UserInfo) bis.readObject();


            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInfo;
    }
    String judgeLoginType(String logintype){
            //String logintype = "admin@tvfan.cn";
            String em = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
            String ph = "^[1][3578]\\d{9}$";
            if (logintype.matches(em)) {
                return "Email";
            } else if (logintype.matches(ph)) {
                return "Phone";
            } else {
                return "UserName";
        }
    }

    @Override
    public void sendIntegralLog(final CallBackListener listener,final String event,final String userId,final String src) {
        subscription = SumaHttpsClient.subscribe(new Callable<Observable<ResultData>>() {
            @Override
            public Observable<ResultData> call() throws Exception {
                return SumaHttpsClient.getRetrofitInstance(UserRetrofit.class).sendOpenAppLog(event,userId,src);
            }
        }, new Action1<ResultData>() {
            @Override
            public void call(ResultData resultData) {
                listener.onSuccess(resultData);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                listener.onFailure(throwable);
            }
        },ResultData.class);
    }

    /** decode */
    public static String decode(String encrypted, String key) throws Exception {

        Cipher cipher = Cipher.getInstance(CIPHER);
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(ENCODING), ENCRYPT_ALGORITHM);
        IvParameterSpec paramSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
        byte[] bytes = decodeBase64String(encrypted);
        return new String(cipher.doFinal(bytes) ,ENCODING);

    }

    private static byte[] decodeBase64String(String s){
        return com.umeng.socialize.net.utils.Base64.decodeBase64(s);
    }
}
