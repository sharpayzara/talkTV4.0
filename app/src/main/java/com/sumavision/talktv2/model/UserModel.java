package com.sumavision.talktv2.model;

import com.sumavision.talktv2.model.entity.AppKeyResult;
import com.sumavision.talktv2.model.entity.LoginResult;
import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.model.entity.ResultData;
import com.sumavision.talktv2.model.entity.UserInfo;
import com.sumavision.talktv2.ui.listener.DownloadProgressListener;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by sharpay on 16-5-26.
 */
public interface UserModel extends BaseModel{
      void saveUserInfo();
      void saveAndUpdateUserInfo(CallBackListener<ResultData> listener);
      void updateUserInfo(CallBackListener listener);
      void loadUserInfo(CallBackListener listener);
      UserInfo getUserInfo();
      boolean logoutUser();
      void loadAppKey(CallBackListener<AppKeyResult> listener);
      void loginIn3rd(CallBackListener<LoginResult> listener);
      void loginIn(String account,String password, CallBackListener<LoginResult> listener);
      void uploadImg(File file, CallBackListener<ResultData> listener);
      void download(CallBackListener listener, String url, File file, DownloadProgressListener progressListener);
      void sendIntegralLog(CallBackListener listener,String event,String userId, String src);
}
