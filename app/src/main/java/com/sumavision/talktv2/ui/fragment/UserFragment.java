package com.sumavision.talktv2.ui.fragment;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.ShareElement;
import com.sumavision.talktv2.http.GlideProxy;
import com.sumavision.talktv2.model.entity.AuthResult;
import com.sumavision.talktv2.model.entity.OrderInfo;
import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.model.entity.UserIntegeInfoItem;
import com.sumavision.talktv2.presenter.UserPresenter;
import com.sumavision.talktv2.ui.activity.CollectActivity;
import com.sumavision.talktv2.ui.activity.DragSettingActivity;
import com.sumavision.talktv2.ui.activity.FeedBackActivity;
import com.sumavision.talktv2.ui.activity.Game37WanActivity;
import com.sumavision.talktv2.ui.activity.LoginActivity;
import com.sumavision.talktv2.ui.activity.MyIntegralActivity;
import com.sumavision.talktv2.ui.activity.NewScanActivity;
import com.sumavision.talktv2.ui.activity.PayActivity;
import com.sumavision.talktv2.ui.activity.PreferenceEditActivity;
import com.sumavision.talktv2.ui.activity.UserInfoActivity;
import com.sumavision.talktv2.ui.activity.WatchHistoryActivity;
import com.sumavision.talktv2.ui.activity.YsqActivity;
import com.sumavision.talktv2.ui.adapter.HistoryAdapter;
import com.sumavision.talktv2.ui.fragment.Base.BaseFragment;
import com.sumavision.talktv2.ui.iview.IUserView;
import com.sumavision.talktv2.ui.widget.GlideCircleTransform;
import com.sumavision.talktv2.ui.widget.LMHRecyclerView;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.DuibaUtill;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
//import io.dcloud.WebAppActivity;


//import com.sumavision.talktv2.model.entity.UserIntegeInfoItem;

//import com.alipay.sdk.app.AuthTask;
//import com.alipay.sdk.app.PayTask;

/**
 * 用户中心界面的Fragment
 * Created by zjx on 2016/5/31.
 */
public class UserFragment extends BaseFragment<UserPresenter> implements IUserView{
    /** 支付宝支付业务：入参app_id */
    public static final String APPID = "2016082901818588";

    /** 支付宝账户登录授权业务：入参pid值 */
    public static final String PID = "2088012831017296";
    /** 支付宝账户登录授权业务：入参target_id值 */
    public static final String TARGET_ID = "dianshifen@126.com";

    /** 商户私钥，pkcs8格式 */
    public static final String RSA_PRIVATE ="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAK3q2xANyEZntmSf" +
            "jkxcoTVlldxHFVr7MQV3jdYEVoB35ts8GtPGB1ZWCv24nNVygIJsDUmMgykA2nic" +
            "W1C4hNbNNTivJH011cXfiYqz3fmFWNX9jZFQdDRXv5Oeg2C2CatFsFOlAjywttuK" +
            "5MDCLTsZyhF+pQzTWEVoYl15tsQnAgMBAAECgYBPIav42vyEJE5V7E83qXHkNMuC" +
            "BYnO8rn0TdhoR2MAYw3UNL3UG6dc/htUJDqf19BnBzjofRl0f6Hn/OSDjuFJhhw3" +
            "mSzZ7Sx5adty/0EVEi7o5UG1Uvfan7KGSZ+CUDIPPOtkglettc8VK6a4YCBp4VVc" +
            "IblA7BMbnHIy/6hW8QJBAObfy0BbPtWe6mZEqfVqsxAP4AU32uDLAEz4xfOkWPxo" +
            "U+3QzSv6PHZ6SJUI5qG35DJpOtzWsNQsLndrPdKRHD8CQQDA2DunHizsC6J9yTgM" +
            "dnLtRVzr6C22UKazSvaICweOUtop5PS735t+dLH7EiccaHHNnu4/wIO8CUeUcevp" +
            "XH4ZAkARtYrepeEc+7KZI50x0PxpN/6EB/PHGGsufEqa1Llqqwn9DO4f+HLY16pn" +
            "nCYss3FSmJXGSMWJkNYDnPtCApV7AkEAlCyRiVMSSTyXWfcmbc0FeXJ2d30qwo8t" +
            "x02uJ9HxuFXI86/MrB6gJ4Yay0OjcZx+9PNNNQcDMZQfv9vY3LII4QJAPk5xbDWq" +
            "EiHAnc69uqEE65cBzrH98ATVgYI7FQQXohXS1MdStbGXAbNL4XOLbIOHIqbaodnS" +
            "tEICixQXktZ4Zw==";
    @BindView(R.id.history_recycle)
    LMHRecyclerView historyRecycle;
    @BindView(R.id.user_img)
    ImageView userImg;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.login_desc)
    TextView loginDesc;
    @BindView(R.id.integrate)
    TextView integrate;
    @BindView(R.id.user_rlt)
    ImageView userRlt;
    @BindView(R.id.qrcodeimg)
    ImageView qrcodeimg;
    ArrayList<PlayerHistoryBean> list;
    UserPresenter presenter;
    Context mContext;
    HistoryAdapter adapter;
    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_user;
    }

    @Override
    protected void initPresenter() {
        mContext = this.getContext();
        BusProvider.getInstance().register(this);
        presenter = new UserPresenter(mContext,this);
        presenter.init();
    }

    @Override
    public boolean onBackPressed() {
        BusProvider.getInstance().post("returnHome","returnHome");
        return true ;
    }
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;
    @OnClick({R.id.ysq_rlt, R.id.collect_rlt, R.id.cache_rlt, R.id.history_rlt, R.id.custom_rlt, R.id.interest_rlt,R.id.integraldesc_rlt,
            R.id.setting_rlt, R.id.notice_rlt, R.id.feedback_rlt,R.id.user_name,R.id.user_img,R.id.pay_rlt, R.id.gamecenter_rlt,R.id.integrate_rlt})
    public void onClick(View view) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            switch (view.getId()) {
                case R.id.ysq_rlt:
                    MobclickAgent.onEvent(mContext, "4grzxysq");
                    Intent ysqIntent = new Intent(mContext, YsqActivity.class);
                    AppGlobalConsts.ISFROMHOME = false;
                    startActivity(ysqIntent);
                    break;
             /*   case R.id.scan_rlt:
                    Intent scanIntent = new Intent(mContext, NewScanActivity.class);
                    startActivity(scanIntent);
                    break;*/
                case R.id.collect_rlt:
                    Intent collIntent = new Intent(mContext, CollectActivity.class);
                    startActivity(collIntent);
                    break;
                case R.id.cache_rlt:
                    enterNext(AppGlobalConsts.EnterType.ENTERHUANCUN);
                    break;
                case R.id.history_rlt:
                    Intent historyIntent = new Intent(mContext, WatchHistoryActivity.class);
                    startActivity(historyIntent);
                    break;
                case R.id.custom_rlt:
                    enterNext(AppGlobalConsts.EnterType.ENTERDINGZHI);
                    break;
                case R.id.integraldesc_rlt:
                    Intent myIntegral = new Intent(mContext, MyIntegralActivity.class);
                    myIntegral.putExtra("integral",integrate.getText().toString());
                    startActivity(myIntegral);
                    break;
                case R.id.feedback_rlt:
                    Intent feedBackIntent = new Intent(mContext, FeedBackActivity.class);
                    startActivity(feedBackIntent);
                   /*Intent feedBackIntent = new Intent(mContext, FeedbackWebViewActivity.class);
                    this.startActivity(feedBackIntent);*/
                    break;
                case R.id.interest_rlt:
                    Intent interestIntent = new Intent(mContext, PreferenceEditActivity.class);
                    mContext.startActivity(interestIntent);
                    break;
                case R.id.setting_rlt:
                    enterNext(AppGlobalConsts.EnterType.ENTERSHEZHI);
                    break;
                case R.id.notice_rlt:
                    enterNext(AppGlobalConsts.EnterType.ENTERTONGGAO);
                    break;
                case R.id.user_img:
                    if(AppGlobalVars.userInfo != null){
                        Intent userInfoIntent= new Intent(mContext, UserInfoActivity.class);
                        ShareElement.userImgDrawable = userImg.getDrawable();
                        startActivityForResult(userInfoIntent, AppGlobalConsts.ResultCode.USER_CODE);
                    }else{
                        Intent loginIntent= new Intent(mContext, LoginActivity.class);
                        startActivityForResult(loginIntent, AppGlobalConsts.ResultCode.USER_CODE);
                        if (AppGlobalConsts.ISFROMDUIBATOLOGIN){
                            AppGlobalConsts.ISFROMDUIBATOLOGIN = false;
                        }
                    }

                    break;
                case R.id.pay_rlt:
                    //payV2();
                    //authV2();
                    startActivity(new Intent(mContext, PayActivity.class));
                    break;
                case R.id.integrate_rlt:
                    enterDuiba(null);
                    break;
                case R.id.gamecenter_rlt:
                    //跳转到游戏中心--37wan
                    Intent intent2 = new Intent(mContext, Game37WanActivity.class);
                    intent2.putExtra("url","http://37.com.cn/dsf");
                    mContext.startActivity(intent2);
                    break;
            }
        }

    }

    @Override
    public void initView() {
        list = new ArrayList<>();
        presenter.getHistoryData();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        historyRecycle.setLayoutManager(linearLayoutManager);
        userImg.setImageResource(R.mipmap.head_sculpture);
        if(AppGlobalVars.userInfo != null){
            showOnline();
        }else{
            showOffline();
        }
        //设置适配器
        adapter = new HistoryAdapter(mContext,list);
        historyRecycle.setAdapter(adapter);
        if(list.size() > 0 ){
            historyRecycle.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden){
            presenter.getHistoryData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.getHistoryData();
        //这里需要判断当前用户的登录状态
        if (AppGlobalVars.userInfo != null){
            //说明此时用户是已经登录的状态了
            //根据用户id去请求用户积分数据
            presenter.getUserIntegtion(AppGlobalVars.userInfo.getUserId());
        }else{
            //此时用户不在登录状态
            //隐藏用户的积分
            integrate.setVisibility(View.GONE);
        }
    }
    private void enterNext(String id) {
        Intent intent = new Intent();
        intent.setClass(this.getActivity(), DragSettingActivity.class);
        intent.putExtra("enternext",id);
        startActivity(intent);
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("backflag")})
    public void refreshShow(String backMsg){
        if(backMsg.equals("LOGININ")){
            showOnline();
        }else if(backMsg.equals("LOGINOUT")){
            showOffline();
        }
    }

    /**
     * 这是进入兑吧积分商城的方法
     * @param url
     */
    public void enterDuiba(String url){
        if (AppGlobalVars.userInfo != null ){
                presenter.getLoginAddress(AppGlobalVars.userInfo.getUserId(), integrate.getText().toString(),url);
        }else{
            presenter.getLoginAddress("not_login","0",null);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       /* if(requestCode == AppGlobalConsts.ResultCode.USER_CODE) {
            if (data != null && "LOGININ".equals(data.getStringExtra("backflag"))) {

            } else if(data != null && "LOGINOUT".equals(data.getStringExtra("backflag"))) {

            }
        }*/
    }

    public Bitmap blurBitmap(Bitmap bitmap){

        //Let's create an empty bitmap with the same size of the bitmap we want to blur
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        //Instantiate a new Renderscript
        RenderScript rs = RenderScript.create(this.getContext());

        //Create an Intrinsic Blur Script using the Renderscript
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        //Create the Allocations (in/out) with the Renderscript and the in/out bitmaps
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

        //Set the radius of the blur
        blurScript.setRadius(25.f);

        //Perform the Renderscript
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);

        //Copy the final bitmap created by the out Allocation to the outBitmap
        allOut.copyTo(outBitmap);

        //recycle the original bitmap
       // bitmap.recycle();

        //After finishing everything, we destroy the Renderscript.
        rs.destroy();

        return outBitmap;
    }

    public void showOffline(){
        userName.setText("点击登陆");
        loginDesc.setVisibility(View.VISIBLE);
        userImg.setImageResource(R.mipmap.head_sculpture);
    }
    public void showOnline(){
        AppGlobalVars.userInfo = presenter.getUserInfo();

        Glide.with(mContext)
                .load(AppGlobalVars.userInfo.getImageUrl())
                .animate(R.anim.image_load)
                .placeholder(R.mipmap.head_sculpture)
                .error(R.mipmap.head_sculpture)
                .crossFade()
                .transform(new GlideCircleTransform(mContext,mContext.getResources().getColor(R.color.white)))
                // .into(imageView);
                .into(new SimpleTarget(250, 250) {
                    @Override
                    public void onResourceReady(Object resource, GlideAnimation glideAnimation) {
                        if (resource instanceof GlideBitmapDrawable)
                            userImg.setImageBitmap(GlideBitmapDrawable.class.cast(resource).getBitmap());
                            setBlurImage(GlideBitmapDrawable.class.cast(resource).getBitmap());

                    }
                });


       /* GlideProxy.getInstance().loadCircleImage(mContext,AppGlobalVars.userInfo.getImageUrl(), userImg,R.color.white);
        userRlt.setBackground(new BitmapDrawable(blurBitmap(((BitmapDrawable)userImg.getDrawable()).getBitmap())));*/
        userName.setText(AppGlobalVars.userInfo.getNickName());
        loginDesc.setVisibility(View.GONE);
    }

    void setBlurImage(Bitmap bitmap){
        userRlt.setImageBitmap(blurBitmap(bitmap));
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f,
                1.2f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f,
                1.2f);
        ObjectAnimator animator= ObjectAnimator.ofPropertyValuesHolder(userRlt, pvhY,pvhZ);
        animator.setDuration(10).start();
    }

    public void payV2() {
        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         */
        Map<String, String> params = OrderInfo.buildOrderParamMap(APPID);
        String orderParam = OrderInfo.buildOrderParam(params);
        String sign = OrderInfo.getSign(params, RSA_PRIVATE);
        final String orderInfo = orderParam + "&" + sign;

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
//                PayTask alipay = new PayTask(getActivity());
//                Map<String, String> result = alipay.payV2(orderInfo, true);
//                Log.i("msp", result.toString());
//                Message msg = new Message();
//                msg.obj = result;
//                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
        /*    @SuppressWarnings("unchecked")
            PayResult payResult = new PayResult((Map<String, String>) msg.obj);
            *//**
             对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
             *//*
            String resultInfo = payResult.getResult();// 同步返回需要验证的信息
            String resultStatus = payResult.getResultStatus();
            // 判断resultStatus 为9000则代表支付成功
            if (TextUtils.equals(resultStatus, "9000")) {
                // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                Toast.makeText(mContext, "支付成功", Toast.LENGTH_SHORT).show();
            } else {
                // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                Toast.makeText(mContext, "支付失败", Toast.LENGTH_SHORT).show();
            }*/
            AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
            String resultStatus = authResult.getResultStatus();

            // 判断resultStatus 为“9000”且result_code
            // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
            if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                // 获取alipay_open_id，调支付时作为参数extern_token 的value
                // 传入，则支付账户为该授权账户
                Toast.makeText(mContext,
                        "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
                        .show();
            } else {
                // 其他状态值则为授权失败
                Toast.makeText(mContext,
                        "授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();

            }
        }
    };

    public void authV2() {
        if (TextUtils.isEmpty(PID) || TextUtils.isEmpty(APPID) || TextUtils.isEmpty(RSA_PRIVATE)
                || TextUtils.isEmpty(TARGET_ID)) {
            new AlertDialog.Builder(mContext).setTitle("警告").setMessage("需要配置PARTNER |APP_ID| RSA_PRIVATE| TARGET_ID")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                        }
                    }).show();
            return;
        }

        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * authInfo的获取必须来自服务端；
         */
        Map<String, String> authInfoMap = OrderInfo.buildAuthInfoMap(PID, APPID, TARGET_ID);
        String info = OrderInfo.buildOrderParam(authInfoMap);
        String sign = OrderInfo.getSign(authInfoMap, RSA_PRIVATE);
        final String authInfo = info + "&" + sign;
        Runnable authRunnable = new Runnable() {

            @Override
            public void run() {
//                // 构造AuthTask 对象
//                AuthTask authTask = new AuthTask(getActivity());
//                // 调用授权接口，获取授权结果
//                Map<String, String> result = authTask.authV2(authInfo, true);
//
//                Message msg = new Message();
//                msg.obj = result;
//                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread authThread = new Thread(authRunnable);
        authThread.start();
    }
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("CHANGENAME")})
    public void refreshUserName(String msg){
        userName.setText(msg);
    }
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("CHANGEIMG")})
    public void refreshUserImg(String photoUrl){
        if(!TextUtils.isEmpty(photoUrl)){
            Glide.with(mContext)
                    .load(photoUrl)
                    .animate(R.anim.image_load)
                    .placeholder(R.mipmap.head_sculpture)
                    .error(R.mipmap.head_sculpture)
                    .crossFade()
                    .transform(new GlideCircleTransform(mContext,mContext.getResources().getColor(R.color.white)))
                    // .into(imageView);
                    .into(new SimpleTarget(250, 250) {
                        @Override
                        public void onResourceReady(Object resource, GlideAnimation glideAnimation) {
                            if (resource instanceof GlideBitmapDrawable)
                                userImg.setImageBitmap(GlideBitmapDrawable.class.cast(resource).getBitmap());
                            setBlurImage(GlideBitmapDrawable.class.cast(resource).getBitmap());

                        }
                    });
            //GlideProxy.getInstance().loadCircleLocalImage(mContext, photoUrl, userImg, R.color.circle_line);

        }else{
            GlideProxy.getInstance().loadResImage(mContext, R.mipmap.head_sculpture, userImg, R.color.circle_line);
            userRlt.setImageResource(R.mipmap.user_bg);
            userRlt.invalidate();
            userImg.invalidate();
        }
    }
    int credits;
    String loginUrl;
    @Override
    public void setUserIntegeInfo(UserIntegeInfoItem userIntegeInfoItem) {
        integrate.setVisibility(View.VISIBLE);
        credits = userIntegeInfoItem.getObj();
        integrate.setText(credits+"");
        if (AppGlobalVars.userInfo != null && AppGlobalConsts.ISLOGINDUIBA ){
            presenter.getLoginAddress(AppGlobalVars.userInfo.getUserId(),credits+"",AppGlobalConsts.newDuibaUrl);
            AppGlobalConsts.ISLOGINDUIBA = false;
        }
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("refreshIntg")})
    public void refreshDuiba(String refreshEdit) {
            presenter.getUserIntegtion(AppGlobalVars.userInfo.getUserId());
    }

    @Override
    public void setLoginAddress(String url) {
        loginUrl = url;
        //跳转到积分商城--兑吧
        if (loginUrl != null){
            DuibaUtill.enterDuiba(loginUrl,"#0acbc1","#ffffff",mContext);
        }
    }

    @Override
    public void showListData(List<PlayerHistoryBean> beanList) {
        list.clear();
        list.addAll(beanList);
        adapter.notifyDataSetChanged();
        if(list.size() == 0){
            historyRecycle.setVisibility(View.GONE);
        }else{
            historyRecycle.setVisibility(View.VISIBLE);
        }
    }
    @OnClick(R.id.qrcodeimg)
    public void openScan(){
            Intent scanIntent = new Intent(this.getActivity(), NewScanActivity.class);
            startActivity(scanIntent);
    }
}
