package com.sumavision.talktv2.ui.activity;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobad.feeds.NativeResponse;
import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.baidu.mobads.AppActivity;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding.view.RxView;
import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.ShareElement;
import com.sumavision.talktv2.dao.RxDao;
import com.sumavision.talktv2.dao.ormlite.DbCallBack;
import com.sumavision.talktv2.model.entity.CollectBean;
import com.sumavision.talktv2.model.entity.MediaDetail;
import com.sumavision.talktv2.model.entity.MediaPlayComplete;
import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.model.entity.PraiseData;
import com.sumavision.talktv2.model.entity.VideoType;
import com.sumavision.talktv2.presenter.MediaDetailPresenter;
import com.sumavision.talktv2.ui.activity.base.BaseActivity;
import com.sumavision.talktv2.ui.adapter.MediaItemCommonAdapter;
import com.sumavision.talktv2.ui.adapter.MediaSelectionsPagerAdapter;
import com.sumavision.talktv2.ui.iview.IMediaDetailView;
import com.sumavision.talktv2.ui.receiver.TANetChangeObserver;
import com.sumavision.talktv2.ui.receiver.TANetworkStateReceiver;
import com.sumavision.talktv2.ui.widget.LoadingLayout;
import com.sumavision.talktv2.ui.widget.MyLinearLayout;
import com.sumavision.talktv2.ui.widget.NetChangeDialog;
import com.sumavision.talktv2.ui.widget.SelectSharePopupWindow;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.CommonUtil;
import com.sumavision.talktv2.util.NetworkUtil;
import com.sumavision.talktv2.util.NoDoubleClickListener;
import com.sumavision.talktv2.util.TipUtil;
import com.sumavision.talktv2.videoplayer.IPlayerClient;
import com.sumavision.talktv2.videoplayer.PlayBean;
import com.sumavision.talktv2.videoplayer.view.VodPlayerVideoView;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.functions.Action1;

/**
 * Created by sharpay on 16-6-17.
 */
public class MediaDetailActivity extends BaseActivity<MediaDetailPresenter> implements IMediaDetailView,IPlayerClient, VodPlayerVideoView.OnBackListener {
    @BindView(R.id.root_rlt)
    RelativeLayout rootRlt;
    @BindView(R.id.scroll)
    LinearLayout scroll;
    MediaDetailPresenter presenter;
    @BindView(R.id.media_selections_tab)
    TabLayout mediaSelectionsTab;
    @BindView(R.id.media_select_container)
    ViewPager mediaSelectContainer;
    MediaSelectionsPagerAdapter selectionsAdapter;
    @BindView(R.id.playdetail_playtime)
    ImageView playdetailPlaytime;
    @BindView(R.id.viewMoreRlt)
    RelativeLayout viewMoreRlt;
    @BindView(R.id.showDetailRlt)
    RelativeLayout showDetailRlt;
    @BindView(R.id.playerView)
    VodPlayerVideoView videoView;
    @BindView(R.id.play_count)
    TextView playCount;
    @BindView(R.id.praise_checkBox)
    CheckBox praiseCheckBox;
    @BindView(R.id.add_praise_tv)
    TextView addPraiseTv;
    @BindView(R.id.cache_rlt)
    RelativeLayout cacheRlt;
    @BindView(R.id.collect_checkBox)
    CheckBox collectCheckBox;
    @BindView(R.id.info_tv)
    TextView infoTv;
    @BindView(R.id.name_tv)
    TextView nameTv;
    @BindView(R.id.praise_mllt)
    MyLinearLayout praiseMllt;
    PraiseData praiseData;
    @BindView(R.id.moreIcon)
    CheckBox moreIcon;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.loadingLayout)
    LoadingLayout loadingLayout;
    @BindView(R.id.collect_mllt)
    MyLinearLayout collectMllt;
    @BindView(R.id.ad_rlt)
    RelativeLayout ad_rlt;
//    @BindView(R.id.rlTemplate2)
//    RelativeLayout rlTempl2;
    @BindView(R.id.line)
    ImageView line;
    @BindView(R.id.content)
    RelativeLayout content;
    MediaItemCommonAdapter adapter;
    String vid;
    private PlayerHistoryBean playerHistoryBean;
    int sdkType,videoType;
    MediaDetail mediaDetail;
    SelectSharePopupWindow sharePopupWindow;
    private boolean isOnline = true;
    private static RxDao collectDao = new RxDao(BaseApp.getContext(), CollectBean.class);
    private long iBreakTime;
    private int detailHeight;
    private int landscapeState = ShareElement.PORTRAIT;
    private NetChangeDialog netChangeDialog;
    private TANetChangeObserver taNetChangeObserver;

    @OnClick(R.id.viewMoreRlt)
    void clickShowDetailRlt() {
        if (showDetailRlt.getVisibility() == View.GONE) {
            showDetailRlt.setVisibility(View.VISIBLE);
            moreIcon.setChecked(true);
        } else {
            showDetailRlt.setVisibility(View.GONE);
            moreIcon.setChecked(false);
        }
    }
    @OnClick(R.id.collect_mllt)
    void collect(){
        if (mediaDetail == null) {
            Toast.makeText(this, "请稍后", Toast.LENGTH_SHORT).show();
            return;
        }
        final CollectBean bean = new CollectBean();
        bean.setVid(vid);
        bean.setVideoType(VideoType.media);
        bean.setSdkType(sdkType);
        bean.setPlayCount(mediaDetail.getPlayCount() + "次播放");
        bean.setMediaVideoType(videoType);
        bean.setPicurl(mediaDetail.getPicUrl());
        bean.setName(mediaDetail.getName());
        Map<String,String> map = new HashMap<>();
        map.put("vid",bean.getVid());
        collectDao.subscribe();
        collectDao.queryByConditionSync(map, new DbCallBack<List<CollectBean>>() {
            @Override
            public void onComplete(List<CollectBean> list) {
                if(list.size() > 0){
                    collectDao.deleteById(list.get(0).getId());
                    collectCheckBox.setChecked(false);
                    collectDao.unsubscribe();
                }else{
                    collectDao.insertSync(bean, new DbCallBack<Boolean>() {
                        @Override
                        public void onComplete(Boolean bool) {
                            if(bool){
                                collectCheckBox.setChecked(true);
                                TipUtil.showSnackTip(collectMllt,"收藏成功");
                            }else{
                                collectCheckBox.setChecked(false);
                                TipUtil.showSnackTip(collectMllt,"收藏失败");
                            }
                            collectDao.unsubscribe();
                        }
                    });
                }
            }
        });
    }
    @OnClick(R.id.share_rlt)
    void share(View view){
        sharePopupWindow = new SelectSharePopupWindow(this);
        sharePopupWindow .showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addBaiDuAd();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        vid = getIntent().getStringExtra("vid");
        sdkType = getIntent().getIntExtra("sdkType",1);
        videoType = getIntent().getIntExtra("videoType",1);
    }

    public void findCollectBean(){
        Map<String,String> map = new HashMap<>();
        map.put("vid",vid);
        collectDao.subscribe();
        collectDao.queryByConditionSync(map, new DbCallBack<List<CollectBean>>() {
            @Override
            public void onComplete(List<CollectBean> list) {
                if(list != null && list.size() > 0){
                    collectCheckBox.setChecked(true);
                }else{
                    collectCheckBox.setChecked(false);
                }
                collectDao.unsubscribe();
            }
        });

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_media_detail;
    }

    @Override
    protected void initPresenter() {
        presenter = new MediaDetailPresenter(this, this);
        presenter.init();
        netStateChangeListen();
    }

    /**
     * 进行流量管理
     */
    private void netStateChangeListen() {
        if(taNetChangeObserver == null) {
            taNetChangeObserver = new TANetChangeObserver(){
                @Override
                public void onConnect(int type) {
                    if(type == ConnectivityManager.TYPE_MOBILE) {//移动流量
                        if(netChangeDialog == null) {
                            netChangeDialog = new NetChangeDialog(MediaDetailActivity.this, R.style.ExitDialog);
                            netChangeDialog.setCancelable(false);
                        }

                        //忽略网络变化继续观看
                        netChangeDialog.getOkButton().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isOnline){
                                    videoView.registerSensor();
                                    play();
                                }
                                netChangeDialog.dismiss();
                                ShareElement.isIgnoreNetChange = 2;
                            }
                        });
                        //停止观看
                        netChangeDialog.getCancelButton().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                videoView.stop();
                                netChangeDialog.dismiss();
                                videoView.showNetPic(mediaDetail.getPicUrl());
                                ShareElement.isIgnoreNetChange = 1;
                            }
                        });

                        if(ShareElement.isIgnoreNetChange == -1) {
                            if (isOnline){
                                videoView.unRegisterSensor();
                                videoView.stopPreLoading();
                                iBreakTime = videoView.getBreakTime();
                                videoView.stop();
                            }
                            if(!MediaDetailActivity.this.isFinishing())
                                netChangeDialog.show();
                        }

                    }
                    else if(type == ConnectivityManager.TYPE_WIFI) {
                        if(videoView!=null &&
                                (ShareElement.isIgnoreNetChange==1 || (netChangeDialog!=null&&netChangeDialog.isShowing())))
                        {
                            videoView.registerSensor();
                            play();
                        }
                        if(netChangeDialog!=null&&netChangeDialog.isShowing())
                            netChangeDialog.dismiss();
                        ShareElement.isIgnoreNetChange = -1;
                        Toast.makeText(MediaDetailActivity.this.getApplicationContext(),"已连接wifi网络!",Toast.LENGTH_LONG).show();
                    }
                }
            };
        }
        TANetworkStateReceiver.registerObserver(taNetChangeObserver);
    }

    /**
     * 当没有弹出流量提醒框，但是已经不是wifi状态
     */
    private void netStateCheck() {
        videoView.unRegisterSensor();
        if(netChangeDialog == null) {
            netChangeDialog = new NetChangeDialog(this, R.style.ExitDialog);
            netChangeDialog.setCancelable(false);
        }

        netChangeDialog.show();

        //忽略网络变化继续观看
        netChangeDialog.getOkButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.registerSensor();
                netChangeDialog.dismiss();
                play();
                ShareElement.isIgnoreNetChange = 2;
            }
        });
        //停止观看
        netChangeDialog.getCancelButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                netChangeDialog.dismiss();
                videoView.showNetPic(mediaDetail.getPicUrl());
                ShareElement.isIgnoreNetChange = 1;
            }
        });
    }

    private void play() {
        videoView.startPlay(getPlayBean());
        videoView.setHistory(iBreakTime, true);
        iBreakTime = 0;
    }

    PowerManager.WakeLock mWakeLock;
    @Override
    public void initView() {
        UMShareAPI.get(this);
        BusProvider.getInstance().register(this);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "MyTag");
        praiseClick();
    }

    void praiseClick(){
        RxView.clicks(praiseMllt)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if(praiseData!=null){
                            praiseData.setValid(praiseData.getValid()? false:true);
                        }else{
                            praiseData = new PraiseData();
                            praiseData.setValid(true);
                            praiseData.setProgramId(vid);
                            praiseData.setSdkType(sdkType);
                            praiseData.setVideoType(videoType);
                        }
                        praiseCheckBox.setChecked(praiseData.getValid());
                        if(praiseData.getValid()){
                            presenter.sendIntegralLog("like",vid);
                        }
                        if(praiseData.getValid() == true){
                            addPraiseTv.setVisibility(View.VISIBLE);
                            ObjectAnimator oa = ObjectAnimator.ofFloat(addPraiseTv, "alpha", 1.0f,0.0f);
                            ObjectAnimator oa2 = ObjectAnimator.ofFloat(addPraiseTv, "scaleY", 1f,1.2f);
                            ObjectAnimator oa3 = ObjectAnimator.ofFloat(addPraiseTv, "scaleX", 1f,1.2f);
                            ObjectAnimator oa4= ObjectAnimator.ofFloat(addPraiseTv, "Y",addPraiseTv.getY(), -10f);
                            final float y = addPraiseTv.getY();
                            oa4.setInterpolator(new DecelerateInterpolator());
                            AnimatorSet animSet = new AnimatorSet();
                            animSet.play(oa).with(oa2).with(oa3).with(oa4);
                            animSet.setDuration(1000);
                            animSet.start();
                            animSet.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    addPraiseTv.setY(y);
                                    addPraiseTv.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                        }
                        presenter.clickPraise(praiseData);
                    }
                });
    }

    private void animateClose(final View view) {
        int origHeight = view.getHeight();
        ValueAnimator animator = createDropAnimator(view, origHeight, 0);
        animator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    private ValueAnimator createDropAnimator(final View view, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        int value = (Integer) valueAnimator.getAnimatedValue();
                        ViewGroup.LayoutParams layoutParams =
                                view.getLayoutParams();
                        layoutParams.height = value;
                        view.setLayoutParams(layoutParams);
                    }
                });
        return animator;
    }

    @Override
    protected void onResume() {
        super.onResume();
        TANetworkStateReceiver.registerObserver(taNetChangeObserver);
        videoView.removeAllViews();
        String data = getIntent().getDataString();//接收到网页传过来的数据：talktv://tvfan.com/share,vintu5fr5fezyr,1,2,
        try{
            if(!TextUtils.isEmpty(data)){
                String[] split = data.split(",");//以data/切割data字符串
                vid  = split[1];
                sdkType = Integer.parseInt(split[2]);
                videoType = Integer.parseInt(split[3]);
            }
            else{
                vid = getIntent().getStringExtra("vid");
                sdkType = getIntent().getIntExtra("sdkType",1);
                videoType = getIntent().getIntExtra("videoType",1);
            }
        }catch (Exception ex){
            vid = getIntent().getStringExtra("vid");
            sdkType = getIntent().getIntExtra("sdkType",1);
            videoType = getIntent().getIntExtra("videoType",1);
        }
        if((sdkType == -1 || videoType == -1) && videoType != 1){
            loadingLayout.showErrorView();
            isOnline = false;
            TipUtil.showSnackTip(playCount,"对不起，该节目已下线，无法播放！");
            return;
        }
        praiseCheckBox.setChecked(false);
        praiseData =  presenter.loadPraise(vid);
        if(praiseData != null){
            praiseCheckBox.setChecked(praiseData.getValid());
        }
        presenter.enterDetailLog(vid, this);
        presenter.pvLog(vid, this);
        playerHistoryBean = presenter.getPlayHistory(vid);
        List<View> views = new ArrayList<View>();
        int[] guideImages = {R.mipmap.banner, R.mipmap.banner, R.mipmap.banner, R.mipmap.banner, R.mipmap.banner, R.mipmap.banner, R.mipmap.banner, R.mipmap.banner};
        for (int i : guideImages) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(i);
            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            views.add(imageView);
        }
        selectionsAdapter = new MediaSelectionsPagerAdapter(views, this);
        mediaSelectContainer.setAdapter(selectionsAdapter);
        mediaSelectContainer.setOffscreenPageLimit(8);
        mediaSelectionsTab.setTabMode(TabLayout.MODE_SCROLLABLE);
        mediaSelectionsTab.setupWithViewPager(mediaSelectContainer);

        playdetailPlaytime.setFocusable(true);
        playdetailPlaytime.setFocusableInTouchMode(true);
        playdetailPlaytime.requestFocus();
        detailHeight = CommonUtil.dip2px(this, 60);

        adapter = new MediaItemCommonAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(adapter);
        presenter.getMediaDetail(vid);
        findCollectBean();
        loadingLayout.setOnRetryClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                presenter.getMediaDetail(vid);
            }
        });
        if(videoType == 1){
            videoView.init(this,0,2);
            EventBus.getDefault().register(videoView);
        }else{
            videoView.init(this,sdkType,2);
        }
        videoView.isShowNext(false);
        videoView.setOnBackListener(this);
        if(isOnline) {
            videoView.registerSensor();
            mWakeLock.acquire();
        }
        if(videoView.isLandScape()) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            videoView.handleFullHalfScrren(ShareElement.LANDSCAPE);
            hideBottomUIMenu();
        }
    }

    private void animateOpen(final View view) {
        view.setVisibility(View.VISIBLE);
        ValueAnimator animator = createDropAnimator(
                view,
                0,
                detailHeight);
        animator.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        TANetworkStateReceiver.removeRegisterObserver(taNetChangeObserver);
        if (isOnline){
            iBreakTime = videoView.getBreakTime();
            videoView.unRegisterSensor();

            if(videoType == 1){
                EventBus.getDefault().unregister(videoView);
                videoView.stopPreLoading();
            }
            videoView.stop();
            videoView.release();
            if (mWakeLock.isHeld())
                mWakeLock.release();
//            if(videoView.isLandScape())
//                videoView.handleFullHalfScrren(ShareElement.PORTRAIT);
        }
        savePlayHistory();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void showProgressBar() {
        if(loadingLayout != null)
            loadingLayout.showProgressBar();
    }

    @Override
    public void hideProgressBar() {
        if(loadingLayout != null)
            loadingLayout.hideProgressBar();
    }

    @Override
    public void showErrorView() {
        if(loadingLayout != null)
            loadingLayout.showErrorView();
    }

    @Override
    public void showWifiView() {
        if(loadingLayout != null)
            loadingLayout.showWifiView();
    }

    private List<MediaDetail.RelatedBean> recommedDatas = new ArrayList<>();
    @Override
    public void fillDetailValue(MediaDetail mediaDetail) {
        this.mediaDetail = mediaDetail;
        initData();
    }

    private void initData() {
        iBreakTime = 0;
        playCount.setText(mediaDetail.getPlayCount() + "次播放");
        nameTv.setText(mediaDetail.getName());
        infoTv.setText(mediaDetail.getInfo());
        videoView.setProgramName(mediaDetail.getName());
        recommedDatas.clear();
        recommedDatas.addAll(mediaDetail.getRelated());
        adapter.setList(recommedDatas);
        if((netChangeDialog!=null && netChangeDialog.isShowing()) || netCheck()) {
        }
        else {
            play();
            if(playerHistoryBean != null) {
                videoView.setHistory(playerHistoryBean.getPointTime(), true);
            }
        }
    }

    private PlayBean getPlayBean() {
        PlayBean bean = new PlayBean();
        bean.setProgramId(vid);
        bean.setmId(vid);
        if(videoType == 1){
            String url = mediaDetail.getPlayUrl()+"&MovieName="+mediaDetail.getName()+"&MovieId="+vid+"&MovieType=4";
            bean.setUrl(url);
        }else{
            if(sdkType == 2){
                bean.setPptvUrl(mediaDetail.getPlayUrl());
            }else{
                String urlStr = mediaDetail.getPlayUrl();
                String []urlArr = urlStr.split(",");
                try {
                    bean.setSohuAid(Long.parseLong(urlArr[0]));
                }catch (Exception ex){
                    bean.setSohuAid(1000000572765l);
                }
                bean.setSohuVid(Long.parseLong(urlArr[1]));
                bean.setSohuSite(Integer.parseInt(urlArr[2]));
//                return;
            }
        }
        return bean;
    }

    @Override
    public void loadDetailValue(MediaDetail mediaDetail) {
        this.mediaDetail = mediaDetail;
        initData();
    }

    @Override
    public void share3d(SHARE_MEDIA plat) {

    }


    @Override
    public void halfFullScreenSwitch(int landscapeState) {
        if (landscapeState == ShareElement.PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else  if(landscapeState == ShareElement.LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else if(landscapeState == ShareElement.REVERSILANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }
        halfFullScreen(landscapeState);
    }

    private void halfFullScreen(int landscapeState) {
        if (landscapeState == ShareElement.PORTRAIT){
            content.setVisibility(View.VISIBLE);
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(params);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            videoView.unRegisterSensor();
            showBottonUIMenu();
        }
        else  {
            if(this.landscapeState == ShareElement.PORTRAIT) {
                content.setVisibility(View.GONE);
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                getWindow().setAttributes(params);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                videoView.registerSensor();
                hideBottomUIMenu();
            }
        }
        this.landscapeState = landscapeState;
    }

    @Override
    public AudioManager getAudioManager() {
        return (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
        if (isOnline){
            if (videoView != null) {
                EventBus.getDefault().unregister(videoView);
            }
        }
        adView.destroy();
    }

    private void savePlayHistory () {
        if(mediaDetail != null) {
//            presenter.delPlayHistory(vid);
            if(playerHistoryBean == null)
                playerHistoryBean = new PlayerHistoryBean();
            playerHistoryBean.setPointTime(videoView.getBreakTime());

            playerHistoryBean.setProgramId(vid);
            playerHistoryBean.setProgramName(mediaDetail.getName());
            playerHistoryBean.setMediaType(2);
            playerHistoryBean.setPicUrl(mediaDetail.getPicUrl());
            playerHistoryBean.setVideoType(videoType);
            playerHistoryBean.setSdkType(sdkType);
            presenter.insertPlayHistory(playerHistoryBean);
        }
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(AppGlobalConsts.EventType.TAG_A)})
    public void playComplete (MediaPlayComplete mediaPlayComplete) {
        if(recommedDatas==null) {
            videoView.showReplay(true);
            return;
        }
        presenter.delPlayHistory(vid);
        sdkType = recommedDatas.get(0).getSdkType();
        videoType = recommedDatas.get(0).getVideoType();
        vid = recommedDatas.get(0).getCode();
        playerHistoryBean = null;
        playerHistoryBean = presenter.getPlayHistory(vid);
        if(playerHistoryBean != null) {
            videoView.setHistory(playerHistoryBean.getPointTime(), true);
        }
        presenter.loadMediaDetail(vid);
    }

    @Override
    public void onBackPressed() {
        if(isOnline){
            if(videoView != null && videoView.isLock){
                return;
            }
            if(videoView.isLandScape()){
                videoView.handleFullHalfScrren(ShareElement.PORTRAIT);
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public void onBack() {
        finish();
    }

    private boolean netCheck () {
        if(isNetCheck()) {
            netStateCheck();
            return true;
        }
        return false;
    }

    /**
     * 网络未变时是否弹出流量提示框
     * @return
     */
    private boolean isNetCheck() {
        if((ShareElement.isIgnoreNetChange == -1 && NetworkUtil.getCurrentNetworkType(this) == ConnectivityManager.TYPE_MOBILE)
                ||ShareElement.isIgnoreNetChange == 1)
            return true;
        return false;
    }


    private AdView adView;//百度广告的view
    /**
     * 添加百度广告
     */
    private void addBaiDuAd() {
//        Log.i("demo", "NativeOriginActivity.fetchAd");
//        /**
//         * Step 1. 创建BaiduNative对象，参数分别为： 上下文context，广告位ID, BaiduNativeNetworkListener监听（监听广告请求的成功与失败）
//         * 注意：请将YOUR_AD_PALCE_ID替换为自己的广告位ID
//         */
//        BaiduNative baidu = new BaiduNative(this, YOUR_AD_PLACE_ID, new BaiduNative.BaiduNativeNetworkListener() {
//
//            @Override
//            public void onNativeFail(NativeErrorCode arg0) {
//                Log.w("NativeOriginActivity", "onNativeFail reason:" + arg0.name());
//            }
//
//            @Override
//            public void onNativeLoad(List<NativeResponse> arg0) {
//                // 一个广告只允许展现一次，多次展现、点击只会计入一次
//                if (arg0.size() > 0 && arg0.get(0).isAdAvailable(MediaDetailActivity.this)) {
//                    // demo仅简单地显示一条。可将返回的多条广告保存起来备用。
//                    updateView(arg0.get(0));
//                    rlTempl2.setVisibility(View.VISIBLE);
//                    line.setVisibility(View.GONE);
//                }
//
//            }
//
//        });
//
//        /**
//         * Step 2. 创建requestParameters对象，并将其传给baidu.makeRequest来请求广告
//         */
//        // 用户点击下载类广告时，是否弹出提示框让用户选择下载与否
//        RequestParameters requestParameters =
//                new RequestParameters.Builder()
//                        .downloadAppConfirmPolicy(
//                                RequestParameters.DOWNLOAD_APP_CONFIRM_ONLY_MOBILE).build();
//
//        baidu.makeRequest(requestParameters);


        // 设置'广告着陆页'动作栏的颜色主题
        // 目前开放了七大主题：黑色、蓝色、咖啡色、绿色、藏青色、红色、白色(默认) 主题
        AppActivity.setActionBarColorTheme(AppActivity.ActionBarColorTheme.ACTION_BAR_WHITE_THEME);
//        另外，也可设置动作栏中单个元素的颜色, 颜色参数为四段制，0xFF(透明度, 一般填FF)DE(红)DA(绿)DB(蓝)
//        AppActivity.getActionBarColorTheme().set[Background|Title|Progress|Close]Color(0xFFDEDADB);

        // 创建广告View
        String adPlaceId = "2891968"; //  重要：请填上您的广告位ID，代码位错误会导致无法请求到广告
        adView = new AdView(this, adPlaceId);
        // 设置监听器
        adView.setListener(new AdViewListener() {
            public void onAdSwitch() {
                Log.w("2891968", "onAdSwitch");
            }

            public void onAdShow(JSONObject info) {
                // 广告已经渲染出来
                Log.w("2891968", "onAdShow " + info.toString());
            }

            public void onAdReady(AdView adView) {
                // 资源已经缓存完毕，还没有渲染出来
                Log.w("2891968", "onAdReady " + adView);
            }

            public void onAdFailed(String reason) {
                Log.w("2891968", "onAdFailed " + reason);
            }

            public void onAdClick(JSONObject info) {

            }

            @Override
            public void onAdClose(JSONObject arg0) {
                Log.w("", "onAdClose");
            }
        });
        ad_rlt.addView(adView);
    }

    public void updateView(final NativeResponse nativeResponse) {
//        Log.i("demo", "NativeOriginActivity.updateView");
//
//        // use template1
//        AQuery aq = new AQuery(this);
//
//        //use template2。特别提醒：当您选择该模板时，desiredAssets需设置为不需要NativeAdAsset.MAIN_IMAGE，填充会更加充足。
//        aq.id(R.id.iv_title2).text(nativeResponse.getTitle());
//        aq.id(R.id.iv_desc2).text(nativeResponse.getDesc());
//        aq.id(R.id.iv_icon2).image(nativeResponse.getIconUrl());
//        aq.id(R.id.iv_cta).text(nativeResponse.isDownloadApp()? "免费下载" : "查看详情");
//
//        nativeResponse.recordImpression(rlTempl2);// 发送展示日志
//
//        rlTempl2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                nativeResponse.handleClick(v);// 点击响应
//            }
//        });
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("selectChannel")})
    public void selectChannel(String channel) {
        String targetUrl = AppGlobalVars.shareApiHost+"?talktvmedia://tvfan.com/share," + vid+","+sdkType+","+videoType+",";
        if(AppGlobalVars.appKeyResult == null || AppGlobalVars.appKeyResult.getData().size() < 2) {
            Toast.makeText(MediaDetailActivity.this,"分享失败，请稍后重试..",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(mediaDetail.getPicUrl())){
            Toast.makeText(MediaDetailActivity.this,"分享失败，暂不支持分享",Toast.LENGTH_SHORT).show();
            return;
        }
        if(channel.equals("WEIXIN")){
            PlatformConfig.setWeixin("wxcfaa020ee248a2f2", AppGlobalVars.appKeyResult.getData().get(0).getSecret());
            new ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN)
                    .withText(mediaDetail.getName())
                    .withTitle(mediaDetail.getName())
                    .withMedia(new UMImage(this,mediaDetail.getPicUrl()))
                    .withTargetUrl(targetUrl)
                    .setCallback(umShareListener)
                    .share();
        }else if(channel.equals("CIRCLE")){
            PlatformConfig.setWeixin("wxcfaa020ee248a2f2", AppGlobalVars.appKeyResult.getData().get(0).getSecret());
            new ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                    .withText(mediaDetail.getName())
                    .withTitle(mediaDetail.getName())
                    .withMedia(new UMImage(this,mediaDetail.getPicUrl()))
                    .withTargetUrl(targetUrl)
                    .setCallback(umShareListener)
                    .share();
        }
        else if(channel.equals("QQ")){
            PlatformConfig.setQQZone("100757629", AppGlobalVars.appKeyResult.getData().get(1).getSecret()); //"04e1ef24fe1baa6858d1de1f0d70dfb2");
            new ShareAction(this).setPlatform(SHARE_MEDIA.QQ)
                    .withText(mediaDetail.getName())
                    .withMedia(new UMImage(this,mediaDetail.getPicUrl()))
                    .withTargetUrl(targetUrl)
                    .setCallback(umShareListener)
                    .share();
        }else if(channel.equals("ZONE")){
            PlatformConfig.setQQZone("100757629", AppGlobalVars.appKeyResult.getData().get(1).getSecret()); //"04e1ef24fe1baa6858d1de1f0d70dfb2");
            new ShareAction(this).setPlatform(SHARE_MEDIA.QZONE)
                    .withText(mediaDetail.getName())
                    .withMedia(new UMImage(this,mediaDetail.getPicUrl()))
                    .withTargetUrl(targetUrl)
                    .setCallback(umShareListener)
                    .share();
        } else if(channel.equals("WEIBO")){
            PlatformConfig.setSinaWeibo("2064721383", AppGlobalVars.appKeyResult.getData().get(2).getSecret());

            new ShareAction(this).setPlatform(SHARE_MEDIA.SINA)
                    .withText(mediaDetail.getName())
                    .withMedia(new UMImage(this,mediaDetail.getPicUrl()))
                    .withTargetUrl(targetUrl)
                    .setCallback(umShareListener)
                    .share();
        }
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("showNetChangeDialog")})
    public void showNetChangeDialog(String s) {
        if(netCheck()) {
        }
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat","platform"+platform);
            presenter.sendIntegralLog("share",vid);
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
           // Toast.makeText(MediaDetailActivity.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            if(t!=null){
                Log.d("throw","throw:"+t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            //Toast.makeText(MediaDetailActivity.this,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
