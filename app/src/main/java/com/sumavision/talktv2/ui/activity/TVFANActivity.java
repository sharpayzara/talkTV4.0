package com.sumavision.talktv2.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.sumavision.offlinelibrary.core.DownloadManager;
import com.sumavision.offlinelibrary.core.DownloadService;
import com.sumavision.offlinelibrary.dao.AccessDownload;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.offlinelibrary.entity.DownloadInfoState;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.ShareElement;
import com.sumavision.talktv2.model.entity.ADInfoItem;
import com.sumavision.talktv2.model.entity.HorVideoData;
import com.sumavision.talktv2.model.entity.UserIntegeInfoItem;
import com.sumavision.talktv2.presenter.HomePresenter;
import com.sumavision.talktv2.ui.activity.base.BaseActivity;
import com.sumavision.talktv2.ui.fragment.LiveFragment;
import com.sumavision.talktv2.ui.fragment.MediaFragment;
import com.sumavision.talktv2.ui.fragment.RecommendFragment;
import com.sumavision.talktv2.ui.fragment.ShowGridFragment;
import com.sumavision.talktv2.ui.fragment.UserFragment;
import com.sumavision.talktv2.ui.iview.IHomeView;
import com.sumavision.talktv2.ui.widget.ExitDialog;
import com.sumavision.talktv2.ui.widget.TabFragmentHost;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.sumavision.talktv2.util.AppUtil;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.DuibaUtill;
import com.sumavision.talktv2.util.NoDoubleClickListener;
import com.sumavision.talktv2.util.TipUtil;
import com.sumavision.talktv2.videoplayer.view.LivePlayerVideoView;
import com.sumavision.talktv2.videoplayer.view.VodPlayerVideoView;
import com.sumavision.talktv2.wakeup.Service1;
import com.tvata.p2p.P2PManager;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;

/**
 * Created by sharpay on 16-5-24.
 */
public class TVFANActivity extends BaseActivity<HomePresenter> implements IHomeView {
    @BindView(R.id.main_content)
    public RelativeLayout main_content;
    @BindView(R.id.tab_host)
    TabFragmentHost mTabHost;
    @BindView(R.id.realtabcontent)
    FrameLayout realtabcontent;
    @BindView(R.id.horVideoView)
    RelativeLayout horVideoView;
    VodPlayerVideoView videoView;
    LivePlayerVideoView livePlayerVideoView;
    boolean landscape;
    //定义FragmentTabHost对象
    private HomePresenter presenter;
    private LayoutInflater layoutInflater;
    private ExitDialog exitdialog;
    private String integeration;//积分
    private boolean isClickOK = false;
    private boolean isClickCancel = false;
    private Class fragmentArray[] = {RecommendFragment.class,
            LiveFragment.class, MediaFragment.class, ShowGridFragment.class,
            UserFragment.class};

    // 定义数组来存放按钮图片
    private int mImageViewArray[] = { R.drawable.selector_home,
            R.drawable.selector_live, R.drawable.selector_hot, R.drawable.selector_bar, R.drawable.selector_user };
    // Tab选项卡的文字
    private String mTextviewArray[] = { "首页", "直播", "热点", "美女", "我的" };

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_tvfan;
}


    @Override
    protected void initPresenter() {
//        int model = 1;
//        try {
//            model = Settings.System.getInt(this.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
//        } catch (Settings.SettingNotFoundException e) {
//            e.printStackTrace();
//        }
//        setBrightnessMode(this,model);
        presenter = new HomePresenter(this,this);
        presenter.init();
        presenter.update();
        presenter.loadScreenData();
        presenter.loadAppKey();
        presenter.judgeNetwork(this);
        presenter.sendOpenAppLog();
    }

    /**
     * 设置
     * @param context
     * @param mode
     */
    private void setBrightnessMode(Context context, int mode) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(context)) {
                    Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, mode);
                } else {
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            } else {
                Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, mode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(AppGlobalConsts.EventType.TAG_ENTER)})
    public void enterOtherFragment(String radioname) {
            switch (radioname){
                case "直播":
                    mTabHost.setCurrentTab(1);
                    break;
                case "美女":
                    mTabHost.setCurrentTab(3);
                    break;
            }
        if (radioname.contains("自媒体")){
            mTabHost.setCurrentTab(2);
            String[] names =radioname.trim().split(",");
            //这里获取到需要跳转到的热点对应cardid上
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    BusProvider.getInstance().post("enterHot",names[1]);
                }
            },400);
        }
        getIntent().putExtra("enterLivePlay","cancel");
    }
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("returnHome")})
    public void returnHomeFrag(String radioname) {
                    mTabHost.setCurrentTab(0);
    }
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("exitApp")})
    public void exitApp(String exitApp) {
//                    exitBy2Click();
        showExitDialog(getCurrentCahingState());
    }

    private boolean getCurrentCahingState(){
        AccessDownload accessDownload = AccessDownload
                .getInstance(this);
        ArrayList<DownloadInfo> downloadInfos = accessDownload
                .queryDownloadInfo(DownloadInfoState.DOWNLOADING);
        if (downloadInfos != null && downloadInfos.size() > 0) {
            return true;
        }else{
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppUtil.wakeUpCheck())
            startService(new Intent(TVFANActivity.this, Service1.class));
    }

    /**
     * 这是弹出退出dialiog的方法
     */
    private void showExitDialog(boolean b) {
       /* if (!b){
            if (exitdialog==null){
                exitdialog = new ExitDialog(this,R.style.ExitDialog);
            }
            setExitDialogShow(exitdialog);
            exitdialog .getOkButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (P2PManager.get() != null)
                        P2PManager.get().stop();
                    finish();
                }
            });
            exitdialog.getCancelButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    exitdialog.dismiss();
                }
            });
            exitdialog.show();
        }else{
            if (cachingExitDialog==null){
                cachingExitDialog = new CachingExitDialog(this,R.style.ExitDialog);
            }
            setExitDialogShow(cachingExitDialog);
            cachingExitDialog .getOkButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //暂停并退出
                    pauseDownload();
                    if (P2PManager.get() != null)
                        P2PManager.get().stop();
                    finish();
                }
            });
            cachingExitDialog.getCancelButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //关闭dialog
                    cachingExitDialog.dismiss();
                }
            });
            cachingExitDialog.getHideButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //后台运行
//                    P2PManager.get().stop();
                    if (P2PManager.get() != null)
                        P2PManager.get().stop();
                    finish();
                }
            });
            cachingExitDialog.show();
        }*/
        presenter.loadADInfo(this,"exit");
        if (exitdialog==null){
            exitdialog = new ExitDialog(this,R.style.ExitDialog);
        }
        exitdialog.setTOPshow(b);
        if (b){
            setExitDialogShow(exitdialog,633,601);
        }else{
            setExitDialogShow(exitdialog,633,601);
        }
        exitdialog .getOkButton().setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (P2PManager.get() != null)
                    P2PManager.get().stop();
                finish();
            }
        });
        exitdialog.getCancelButton().setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                exitdialog.dismiss();
            }
        });
        exitdialog.show();
    }

    private void setExitDialogShow(Dialog exitdialog,int height, int width) {
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();// 获取屏幕宽、高用
        Window dialogWindow = exitdialog.getWindow();
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * height/1280);
        p.width = (int) (d.getWidth() * width/720);
        dialogWindow.setAttributes(p);
    }

    @Override
    public void onBackPressed() {
        if(videoView != null && videoView.isLock){
            return;
        }
        if(videoView != null && landscape){
            videoView.handleFullHalfScrren(ShareElement.PORTRAIT);
            return;
        }
        super.onBackPressed();
        if ("首页".equals(currTabId)){
//            exitBy2Click();
            showExitDialog(getCurrentCahingState());
        }
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(AppGlobalConsts.EventType.TAG_ENTERDUIBA)})
    public void enterDuiba(String redireUrl){
        if (AppGlobalVars.userInfo != null ){
            presenter.getLoginAddress(AppGlobalVars.userInfo.getUserId(), integeration,redireUrl);
        }else{
            presenter.getLoginAddress("not_login","0",redireUrl);
        }
        duibaUrl = redireUrl;
        AppGlobalConsts.ISLOGINDUIBA = false;
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(AppGlobalConsts.EventType.TAG_A)})
    public void mediaShow(HorVideoData data) {
        if(data.isLandscape()){
            landscape = true;
            horVideoView.setVisibility(View.VISIBLE);
            videoView= data.getVideoView();
            horVideoView.addView(videoView);
            videoView.setBackPlay(true);
            videoView.setSLPlay(true);
            videoView.setProgramNameVisiable(true);
        }else{
            landscape = false;
            if(horVideoView.getChildCount()>0)
                horVideoView.removeView(videoView);
            horVideoView.setVisibility(View.GONE);
        }
    }
    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
          TipUtil.showSnackTip(main_content,"再按一次退出程序");
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            if (P2PManager.get() != null)
                P2PManager.get().stop();
            finish();
            System.exit(0);
        }
    }

    private String currTabId;
    PowerManager.WakeLock mWakeLock;
    private boolean wake;
    /**
     * 初始化组件
     */
    private void initFragmentView() {

        // 实例化布局对象
        layoutInflater = LayoutInflater.from(this);
        // 实例化TabHost对象，得到TabHost
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null);
        // 得到fragment的个数
        int count = fragmentArray.length;
        for (int i = 0; i < count; i++) {
            // 为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i])
                    .setIndicator(getTabItemView(i));
            // 将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
        }
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                MobclickAgent.onEvent(TVFANActivity.this, "4dbdh", tabId);
                currTabId = tabId;
                if(("直播".equals(tabId) || "热点".equals(tabId))&& !wake) {
                    mWakeLock.acquire();
                    wake = true;
                }
                else if(wake){
                    wake = false;
                    mWakeLock.release();
                }
            }
        });

        // 从推送跳转过来
        String liveChannelId = getIntent().getStringExtra("liveChannelId");
        duibaUrl = getIntent().getStringExtra("url");
        if(duibaUrl != null){
            AppGlobalConsts.ISLOGINDUIBA = true;
        }
        if (!TextUtils.isEmpty(liveChannelId)) {
            enterOtherFragment("直播");
            ShareElement.channelId = liveChannelId;
        }
    }

    String liveChannelId = null;
    String duibaUrl = null;
    String idStr = null;
    @Override
    protected void onNewIntent(Intent intent) {
        // 从推送跳转过来
        Log.i(TAG, "onNewIntent");
        setIntent(intent);
        liveChannelId = getIntent().getStringExtra("liveChannelId");
        duibaUrl = getIntent().getStringExtra("url");
        if(duibaUrl != null){
            AppGlobalConsts.ISLOGINDUIBA = true;
        }
        idStr = getIntent().getStringExtra("idStr");
        super.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getStringExtra("cardid") != null && AppGlobalConsts.ISFROMGRID){
            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_CARDID,getIntent().getStringExtra("cardid"));
            AppGlobalConsts.ISFROMGRID = false;
        }
        if (!TextUtils.isEmpty(duibaUrl) && AppGlobalConsts.ISLOGINDUIBA ){
            //说明用户是登陆状态,获取当前用户的积分信息

            if(AppGlobalVars.userInfo != null){
                presenter.getUserIntegtion(AppGlobalVars.userInfo.getUserId());
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    enterDuiba(duibaUrl);
                }
            },1000);

        }
        if (!TextUtils.isEmpty(liveChannelId)) {
            enterOtherFragment("直播");
            ShareElement.channelId = liveChannelId;
            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_B, liveChannelId);
            liveChannelId = null;
        }
        if(("直播".equals(currTabId) || "热点".equals(currTabId)) && !wake) {
            mWakeLock.acquire();
            wake = true;
        }
        if (getIntent() != null && "enterLivePlay".equals(getIntent().getStringExtra("enterLivePlay"))){
            enterOtherFragment("直播");
            ShareElement.channelId = getIntent().getStringExtra("id");
            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_B, getIntent().getStringExtra("id"));
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(wake) {
            mWakeLock.release();
            wake = false;
        }
    }


    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);
        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(mTextviewArray[index]);
        return view;
    }

    @Override
    public void initView() {
        BusProvider.getInstance().register(this);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "MyTag");
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
      //  setSupportActionBar(toolbar);
        initFragmentView();
        if (AppGlobalVars.userInfo != null){
            //说明用户是登陆状态,获取当前用户的积分信息
            presenter.getUserIntegtion(AppGlobalVars.userInfo.getUserId());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        BusProvider.getInstance().unregister(this);
        presenter.release();
    }

    public void halfFullScreenSwitch(int landscapeState) {
        if (landscapeState == ShareElement.PORTRAIT){
            mTabHost.setVisibility(View.VISIBLE);
        }
        else  {
            mTabHost.setVisibility(View.GONE);
        }
    }

    /**
     * 暂停下载的方法
     */
    private void pauseDownload() {
        AccessDownload accessDownload = AccessDownload
                .getInstance(TVFANActivity.this);
        ArrayList<DownloadInfo> downloadInfos = accessDownload
                .queryDownloadInfo();
        if (downloadInfos != null && downloadInfos.size() > 0) {
            for (DownloadInfo downloadInfo : downloadInfos) {
                if(downloadInfo.state <= 1){
                    downloadInfo.state = DownloadInfoState.PAUSE;
                    accessDownload.updateDownloadState(downloadInfo);
                }
            }
            DownloadInfo downloadInfo = downloadInfos.get(0);
            downloadInfo.state = DownloadInfoState.PAUSE;
            accessDownload.updateDownloadState(downloadInfo);
            Intent intent = new Intent(TVFANActivity.this,
                    DownloadService.class);
            Bundle bundle = new Bundle();
            bundle.putInt(DownloadService.ACTION_KEY,
                    DownloadService.ACTION_PAUSE);
            intent.putExtra("bundle", bundle);
            intent.putExtra(DownloadService.APPNAME_KEY,
                    getString(R.string.app_name));
            intent.putExtra(DownloadService.APP_EN_NAME_KEY,
                    "tvfanphone");
            intent.putExtra(DownloadManager.extra_loadinfo,
                    downloadInfo);
            intent.putExtra("exit", true);
            startService(intent);

        }
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("sendScanMesseage")})
    public void dismissScan(String resultStr){
        presenter.sendScanData(resultStr);
    }
    @Override
    public void setADInfo(ADInfoItem adInfo) {
        //这里获取到服务器返回的广告信息
        if (adInfo != null){
            exitdialog.setShowImage(adInfo.getObj().getPicurl());
            exitdialog.setADEnter(adInfo.getObj().getAdurl(),adInfo.getObj().getStyle());
        }
    }
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("scanSuccess")})
    public void promtScan(String resultStr){
        TipUtil.showLongSnackTip(main_content,"扫码成功,欢迎使用电视粉高清影视!");
    }
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("scan4G")})
    public void promtScan4G(String resultStr){
        TipUtil.showLongSnackTip(main_content,"扫码失败,请保证电视和手机在同一局域网内!");
    }
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("scanNoNet")})
    public void promtScanNoNet(String resultStr){
        TipUtil.showLongSnackTip(main_content,"扫码失败,您当前未连接网络!");
    }
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("scanFaild")})
    public void promtScanFaild(String resultStr){
        TipUtil.showLongSnackTip(main_content,"扫码失败,本功能仅适用于扫描TV端精品影视二维码!");
    }

    @Override
    public void setUserIntegeInfo(UserIntegeInfoItem userIntegeInfoItem) {
        //获取到用户的积分信息
        integeration = userIntegeInfoItem.getObj()+"";
//        enterDuiba(duibaUrl);
    }

    @Override
    public void setLoginAddress(String url) {
        //跳转到积分商城--兑吧
        if (url != null){
            DuibaUtill.enterDuiba(url,"#0acbc1","#ffffff",getApplicationContext());
        }
    }
}
