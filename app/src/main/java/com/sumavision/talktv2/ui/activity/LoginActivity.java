package com.sumavision.talktv2.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jiongbull.jlog.JLog;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.AppKeyResult;
import com.sumavision.talktv2.model.entity.UMUserInfo;
import com.sumavision.talktv2.model.entity.UserInfo;
import com.sumavision.talktv2.presenter.LoginPresenter;
import com.sumavision.talktv2.ui.activity.base.CommonHeadPanelActivity;
import com.sumavision.talktv2.ui.iview.ILoginView;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.sumavision.talktv2.util.BeanToMapUtil;
import com.sumavision.talktv2.util.BusProvider;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by sharpay on 16-5-24.
 */
public class LoginActivity extends CommonHeadPanelActivity<LoginPresenter> implements ILoginView {
    @BindView(R.id.weixing_iv)
    ImageView weixingIv;
    @BindView(R.id.qq_iv)
    ImageView qqIv;
    @BindView(R.id.weibo_iv)
    ImageView weiboIv;
    @BindView(R.id.account_et)
    EditText accountEt;
    @BindView(R.id.password_edit)
    EditText passwordEdit;
    @BindView(R.id.loading_llt)
    LinearLayout loadingLLt;
    private UMShareAPI mShareAPI = null;
    LoginPresenter presenter;
    Context mContext;
    Map platMap;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initPresenter() {
        presenter = new LoginPresenter(this, this);
        presenter.init();
    }

    @Override
    public void initView() {
        initHeadPanel();
        setHeadTitle("登陆");
        showBackBtn();
        mContext = this;
        mShareAPI = UMShareAPI.get(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick({R.id.weixing_iv, R.id.qq_iv, R.id.weibo_iv, R.id.submit, R.id.find_pwd,R.id.acount_editdelete,R.id.password_editdelete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.weixing_iv:
                presenter.loadAppKey(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.qq_iv:
                presenter.loadAppKey(SHARE_MEDIA.QQ);
                break;
            case R.id.weibo_iv:
                presenter.loadAppKey(SHARE_MEDIA.SINA);
                break;
            case R.id.submit:
                if(TextUtils.isEmpty(accountEt.getText().toString()) || TextUtils.isEmpty(passwordEdit.getText().toString())){
                    Toast.makeText(this,"用户名或密码不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                presenter.loginIn(accountEt.getText().toString(),passwordEdit.getText().toString());
                break;
            case R.id.acount_editdelete:
                accountEt.setText("");
                break;
            case R.id.password_editdelete:
                passwordEdit.setText("");
                break;
            case R.id.find_pwd:
                Intent findPwdIntent = new Intent(this, FindPwdActivity.class);
                startActivity(findPwdIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * auth callback interface
     **/
    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            showProgressBar();
            platMap = data;
            mShareAPI.getPlatformInfo(LoginActivity.this, platform, new UMAuthListener() {

                @Override
                public void onComplete(SHARE_MEDIA share_media, int i, final Map<String, String> map) {
                    Toast.makeText(LoginActivity.this,"登陆成功",Toast.LENGTH_SHORT).show();
                    if (AppGlobalConsts.ISFROMDUIBATOLOGIN){
                        AppGlobalConsts.ISLOGINDUIBA = true;
                    }
                    try {
                        map.putAll(platMap);
                        UMUserInfo umuserInfo = (UMUserInfo) BeanToMapUtil.mapToObject(map, UMUserInfo.class);
                        if (umuserInfo.getGender() == null || umuserInfo.getGender().equals("m")
                                || umuserInfo.getGender().equals("1") || umuserInfo.getGender().equals("男")) {
                            umuserInfo.setGender("男");
                        } else {
                            umuserInfo.setGender("女");
                        }
                        if (share_media.toString().equals("SINA") && !TextUtils.isEmpty(umuserInfo.getLocation())) {
                            String locationgArr[] = umuserInfo.getLocation().split(" ");
                            umuserInfo.setProvince(locationgArr[0]);
                            if (locationgArr.length > 1) {
                                umuserInfo.setCity(locationgArr[1]);
                            }
                        }
                        umuserInfo.setOrigin(share_media.toString());
                        UserInfo userInfo = new UserInfo(umuserInfo);
                        AppGlobalVars.userInfo = userInfo;
                        presenter.login3rd();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                    Toast.makeText(getApplicationContext(), "获取数据失败！", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancel(SHARE_MEDIA share_media, int i) {
                    JLog.e("授权取消");
                }
            });
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(getApplicationContext(), "授权失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            //   Toast.makeText(getApplicationContext(), "Authorize cancel", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void returnView() {
        CreditActivity.IS_WAKEUP_LOGIN = true;
        BusProvider.getInstance().post("backflag","LOGININ");
        finish();
    }

    @Override
    public void showProgressBar() {
        if(loadingLLt.getVisibility() != View.VISIBLE){
            loadingLLt.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hiddenProgressBar() {
        loadingLLt.setVisibility(View.GONE);
    }

    @Override
    public void login3rd(SHARE_MEDIA plat, AppKeyResult appKeyResult) {
        try {
            if(appKeyResult.getData().size() > 2){
                if(plat == SHARE_MEDIA.WEIXIN){
                    PlatformConfig.setWeixin("wxcfaa020ee248a2f2", appKeyResult.getData().get(0).getSecret());
                }else if (plat == SHARE_MEDIA.QQ){
                    PlatformConfig.setQQZone("100757629",  appKeyResult.getData().get(1).getSecret()); //"04e1ef24fe1baa6858d1de1f0d70dfb2");
                }else{
                    PlatformConfig.setSinaWeibo("2064721383",appKeyResult.getData().get(2).getSecret());//"eab2b56fc44b8fa36648f77c2b6ebd07"
                }
                Config.REDIRECT_URL="http://www.tvfan.cn";
                mShareAPI.doOauthVerify(LoginActivity.this, plat, umAuthListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
