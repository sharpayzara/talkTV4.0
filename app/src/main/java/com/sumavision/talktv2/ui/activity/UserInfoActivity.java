package com.sumavision.talktv2.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.ShareElement;
import com.sumavision.talktv2.http.GlideProxy;
import com.sumavision.talktv2.model.entity.PreferenceBean;
import com.sumavision.talktv2.model.entity.UserInfo;
import com.sumavision.talktv2.presenter.UserInfoPresenter;
import com.sumavision.talktv2.ui.activity.base.CommonHeadPanelActivity;
import com.sumavision.talktv2.ui.iview.IUserInfoView;
import com.sumavision.talktv2.ui.widget.SelectBirthPopupWindow;
import com.sumavision.talktv2.ui.widget.SelectGenderPopupWindow;
import com.sumavision.talktv2.ui.widget.SelectPhotoPopupWindow;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.SelectPicUtil;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.feezu.liuli.timeselector.TimeSelector;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * Created by sharpay on 16-8-23.
 */
public class UserInfoActivity extends CommonHeadPanelActivity<UserInfoPresenter> implements IUserInfoView {
    UserInfoPresenter presenter;
    @BindView(R.id.user_img)
    ImageView userImg;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.user_name_et)
    EditText userNameEt;
    @BindView(R.id.user_sex)
    TextView userSex;
    @BindView(R.id.user_rlt)
    RelativeLayout userRlt;
    @BindView(R.id.preference_icon)
    ImageView preferenceIcon;
    @BindView(R.id.user_preference)
    TextView userPreference;
    @BindView(R.id.preference_rlt)
    RelativeLayout preferenceRlt;
    @BindView(R.id.user_birth)
    TextView userBirth;
    SelectGenderPopupWindow sexPopupWindow;
    SelectPhotoPopupWindow photoPopupWindow;
    public PreferenceBean preferenceBean;
    private UMShareAPI mShareAPI = null;
    private boolean isModify = false; //用户信息是否被更改

    UserInfo tempUserInfo;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_user_edit;
    }

    @Override
    protected void initPresenter() {
        presenter = new UserInfoPresenter(this, this);
        presenter.init();
    }

    @Override
    public void initView() {
        try {
            tempUserInfo = (UserInfo) AppGlobalVars.userInfo.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        initHeadPanel();
        showBackBtn();
        setHeadTitle("我的账户");
        //  GlideProxy.getInstance().loadCircleImage(this, AppGlobalVars.userInfo.getImageUrl(), userImg, R.color.white);
        //  GlideProxy.getInstance().loadCircleResImage2(this,0,userImg,R.color.white);
        userImg.setImageDrawable(ShareElement.userImgDrawable);
        userName.setText(AppGlobalVars.userInfo.getNickName());
        userSex.setText(AppGlobalVars.userInfo.getSex());

        userBirth.setText(AppGlobalVars.userInfo.getBirthday());
        BusProvider.getInstance().register(this);
        showPreference();
        mShareAPI = UMShareAPI.get(this);
    }

    private void showPreference() {
        preferenceBean = presenter.loadPreferenceData();
        if(preferenceBean == null){
            return;
        }
        userPreference.setText(preferenceBean.getPreference());
        if (preferenceBean.getRole() != null && preferenceBean.getRole().equals("男生")) {
            preferenceIcon.setImageDrawable(getResources().getDrawable(R.mipmap.man));
        } else if (preferenceBean.getRole() != null && preferenceBean.getRole().equals("女生")) {
            preferenceIcon.setImageDrawable(getResources().getDrawable(R.mipmap.woman));
        } else if (preferenceBean.getRole() != null && preferenceBean.getRole().equals("小孩")) {
            preferenceIcon.setImageDrawable(getResources().getDrawable(R.mipmap.boy));
        } else if (preferenceBean.getRole() != null && preferenceBean.getRole().equals("老人")) {
            preferenceIcon.setImageDrawable(getResources().getDrawable(R.mipmap.old_man));
        }
        if (AppGlobalVars.userInfo.getTag() == null || !AppGlobalVars.userInfo.getTag().equals(preferenceBean.getPreference() + "," + preferenceBean.getRole())){
            isModify = true;
            AppGlobalVars.userInfo.setTag(preferenceBean.getPreference() + "," + preferenceBean.getRole());
        }

    }

    @OnClick({R.id.user_birth, R.id.login_out, R.id.user_name, R.id.user_sex, R.id.preference_rlt, R.id.user_img})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_birth:
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date curDate = new Date(System.currentTimeMillis());//获取当m前时间
                String str = formatter.format(curDate);
                TimeSelector timeSelector = new TimeSelector(this, new TimeSelector.ResultHandler() {
                    @Override
                    public void handle(String time) {
                        userBirth.setText(time.substring(0,10));
                        isModify = true;
                        AppGlobalVars.userInfo.setBirthday(userBirth.getText().toString());
                    }
                }, "1940-01-01 00:00", str);
                timeSelector.setMode(TimeSelector.MODE.YMD);//只显示 年月日
                timeSelector.show();
                break;
            case R.id.login_out:
                new AlertDialog.Builder(this).setMessage("确认退出登陆？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                BusProvider.getInstance().post("CHANGEIMG","");
                                saveAndUpdateUserInfo(true);
                            }
                        })
                        .setNegativeButton("取消",null)
                        .show();

                break;
            case R.id.user_img:
                photoPopupWindow = new SelectPhotoPopupWindow(this);
                photoPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                break;
            case R.id.user_name:
                userName.setVisibility(View.GONE);
                userNameEt.setVisibility(View.VISIBLE);
                userNameEt.setText(userName.getText().toString());
                userNameEt.setSelection(userName.getText().toString().length());
                userNameEt.setFocusable(true);
                userNameEt.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            case R.id.user_sex:
                sexPopupWindow = new SelectGenderPopupWindow(this);
                sexPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                break;
            case R.id.preference_rlt:
                Intent preferenceIntent = new Intent(UserInfoActivity.this, PreferenceEditActivity.class);
                startActivityForResult(preferenceIntent, AppGlobalConsts.ResultCode.PREFERENCE_CODE);
                break;
        }
    }

    @OnTouch(R.id.user_rlt)
    public boolean onFocusChange(View view, MotionEvent event) {
        userName.setVisibility(View.VISIBLE);
        userNameEt.setVisibility(View.GONE);
        if(!TextUtils.isEmpty(userNameEt.getText().toString()) && !userNameEt.getText().toString().equals(AppGlobalVars.userInfo.getNickName())){
            userName.setText(userNameEt.getText().toString());
            isModify = true;
            AppGlobalVars.userInfo.setNickName(userNameEt.getText().toString());
        }
        userRlt.setFocusable(true);
        userRlt.setFocusableInTouchMode(true);
        userRlt.requestFocus();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = SelectPicUtil.onActivityResult(this,requestCode,resultCode,data);
        if(bitmap != null){
            if (SelectPicUtil.photoUri != null && SelectPicUtil.isCrop == true) {
                presenter.uploadImg(SelectPicUtil.compressFile);
                GlideProxy.getInstance().loadCircleLocalImage(this, SelectPicUtil.photoUri.toString(), userImg, R.color.circle_line);
                BusProvider.getInstance().post("CHANGEIMG",SelectPicUtil.photoUri.toString());
            }
        }
        if(AppGlobalConsts.ResultCode.PREFERENCE_CODE == requestCode) {
            showPreference();
        }
    }

    @Override
    protected void onDestroy() {
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("refreshGender")})
    public void refreshShow(String msg) {
        userSex.setText(msg);
        if(!AppGlobalVars.userInfo.getSex().equals(msg)){
            isModify = true;
            AppGlobalVars.userInfo.setSex(msg);
        }
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("selectPicture")})
    public void slectPicture(String msg) {
        if (msg.equals("byCamera")) {
            SelectPicUtil.getByCamera(this);
        } else {
            SelectPicUtil.getByAlbum(this);
        }
    }
    private UMAuthListener umdelAuthListener = new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            Intent intent = new Intent();
            intent.putExtra("backflag","LOGINOUT");
            setResult(Activity.RESULT_OK,intent);
            finish();
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText( getApplicationContext(), "数据获取失败,请重试", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
        }
    };

    public void loginOut(){
        SHARE_MEDIA platform = null;
        if(AppGlobalVars.userInfo.getOrigin() == null || AppGlobalVars.userInfo.getOrigin().equals("TVFAN")){
            if (presenter.logoutUser()){
                /*Intent intent = new Intent();
                intent.putExtra("backflag","LOGINOUT");
                setResult(Activity.RESULT_OK,intent);*/
                BusProvider.getInstance().post("backflag","LOGINOUT");
                finish();
            }
        }else {
            if (AppGlobalVars.userInfo.getOrigin().equals("QQ")) {
                platform = SHARE_MEDIA.QQ;
            } else if (AppGlobalVars.userInfo.getOrigin().equals("SINA")) {
                platform = SHARE_MEDIA.SINA;
            } else if (AppGlobalVars.userInfo.getOrigin().equals("WEIXIN")) {
                platform = SHARE_MEDIA.WEIXIN;
            }else{
                return;
            }
            if (presenter.logoutUser()){
                mShareAPI.deleteOauth(UserInfoActivity.this, platform, umdelAuthListener);
              /*  Intent intent = new Intent();
                intent.putExtra("backflag","LOGINOUT");
                setResult(Activity.RESULT_OK,intent);*/
                BusProvider.getInstance().post("backflag","LOGINOUT");
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        saveAndUpdateUserInfo(false);
        super.onBackPressed();
    }

    private void saveAndUpdateUserInfo(boolean isExit) {
        if(!TextUtils.isEmpty(userNameEt.getText().toString()) && !userNameEt.getText().toString().equals(AppGlobalVars.userInfo.getNickName())){
            isModify = true;
            AppGlobalVars.userInfo.setNickName(userNameEt.getText().toString());
        }
        if(isModify){
            presenter.saveAndUpdateUserInfo(tempUserInfo,isExit);
        }else if(isExit){
            loginOut();
        }
    }
}
