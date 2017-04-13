 package com.sumavision.talktv2.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
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
import com.sumavision.talktv2.model.entity.NextPlay;
import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.model.entity.PraiseData;
import com.sumavision.talktv2.model.entity.ProgramDetail;
import com.sumavision.talktv2.model.entity.SeriesDetail;
import com.sumavision.talktv2.model.entity.SpecialContentList;
import com.sumavision.talktv2.model.entity.SpecialDetail;
import com.sumavision.talktv2.model.entity.VideoType;
import com.sumavision.talktv2.presenter.SpecialDetailPresenter;
import com.sumavision.talktv2.ui.activity.base.BaseActivity;
import com.sumavision.talktv2.ui.adapter.SpecialItemAdapter;
import com.sumavision.talktv2.ui.iview.ISpecialDetailView;
import com.sumavision.talktv2.ui.receiver.TANetChangeObserver;
import com.sumavision.talktv2.ui.receiver.TANetworkStateReceiver;
import com.sumavision.talktv2.ui.widget.AddCartAnimation;
import com.sumavision.talktv2.ui.widget.LMRecyclerView;
import com.sumavision.talktv2.ui.widget.LoadingLayout;
import com.sumavision.talktv2.ui.widget.MyLinearLayout;
import com.sumavision.talktv2.ui.widget.NetChangeDialog;
import com.sumavision.talktv2.ui.widget.SelectSharePopupWindow;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.sumavision.talktv2.util.BusProvider;
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
public class SpecialDetailActivity extends BaseActivity<SpecialDetailPresenter> implements ISpecialDetailView, SwipeRefreshLayout.OnRefreshListener, LMRecyclerView.LoadMoreListener, IPlayerClient, VodPlayerVideoView.OnBackListener {
    SpecialDetailPresenter presenter;
    @BindView(R.id.playerView)
    VodPlayerVideoView videoView;
    @BindView(R.id.video_layout)
    RelativeLayout videoLayout;
    @BindView(R.id.recycler_view)
    LMRecyclerView recyclerView;
    @BindView(R.id.collect_mllt)
    MyLinearLayout collectMllt;
    @BindView(R.id.loadingLayout)
    LoadingLayout loadingLayout;
    @BindView(R.id.content)
    LinearLayout content;
    @BindView(R.id.ad_rlt)
    RelativeLayout ad_rlt;
    SpecialItemAdapter adapter;
    String idStr;
    String programIdStr;
    @BindView(R.id.brt_tv)
    TextView brtTv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.desc_tv)
    TextView descTv;
    @BindView(R.id.collect_checkBox)
    CheckBox collectCheckBox;
    List<SpecialContentList.ItemsBean> list;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
//    @BindView(R.id.rlTemplate2)
//    RelativeLayout rlTempl2;
    @BindView(R.id.line)
    ImageView line;
    @BindView(R.id.praise_checkBox)
    CheckBox praiseCheckBox;
    @BindView(R.id.add_praise_tv)
    TextView addPraiseTv;
    @BindView(R.id.root_rlt)
    RelativeLayout rootRlt;
    @BindView(R.id.praise_mllt)
    MyLinearLayout praiseMllt;
    boolean canLoadMore = true;
    PlayerHistoryBean playerHistoryBean;
    ProgramDetail programDetail;
    SpecialDetail specialDetail;
    LinearLayoutManager llm;
    public boolean isFirstEnter = true;
    public boolean isAutoPlay = false;
    public boolean isNextPlay = false;
    MyHandler myHandler;
    int page = 1;
    int size = 10;
    int currentPosition = 0;
    PowerManager.WakeLock mWakeLock;
    PraiseData praiseData;
    private static RxDao collectDao = new RxDao(BaseApp.getContext(), CollectBean.class);
    SelectSharePopupWindow sharePopupWindow;
    private AdView adView;//百度广告的view
    private NetChangeDialog netChangeDialog;
    private TANetChangeObserver taNetChangeObserver;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_special_detail;
    }

    @Override
    protected void initPresenter() {
        presenter = new SpecialDetailPresenter(this, this);
        presenter.init();
        netChangeListen();
    }

    /**
     * 进行流量管理
     */
    private void netChangeListen() {
        if(taNetChangeObserver == null) {
            taNetChangeObserver = new TANetChangeObserver(){
                @Override
                public void onConnect(int type) {
                    if(type == ConnectivityManager.TYPE_MOBILE) {//移动流量
                        if(netChangeDialog == null) {
                            netChangeDialog = new NetChangeDialog(SpecialDetailActivity.this, R.style.ExitDialog);
                            netChangeDialog.setCancelable(false);
                        }

                        //忽略网络变化继续观看
                        netChangeDialog.getOkButton().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                netChangeDialog.dismiss();
                                videoView.registerSensor();
                                videoView.startPlay(bean);
                                videoView.setHistory(pointTime, true);
                                pointTime = 0;
                                ShareElement.isIgnoreNetChange = 2;
                            }
                        });
                        //停止观看
                        netChangeDialog.getCancelButton().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                videoView.stop();
                                netChangeDialog.dismiss();
                                videoView.showNetPic(programDetail.getPicurl());
                                ShareElement.isIgnoreNetChange = 1;
                            }
                        });
                        if(ShareElement.isIgnoreNetChange == -1) {
                            pointTime = (int) videoView.getBreakTime();
                            videoView.unRegisterSensor();
                            videoView.stopPreLoading();
                            videoView.stop();
                            if(!SpecialDetailActivity.this.isFinishing())
                                netChangeDialog.show();
                        }
                    }
                    else if(type == ConnectivityManager.TYPE_WIFI) {
                        if(videoView!=null && videoView.isStop()) {
                            videoView.startPlay(bean);
                            videoView.setHistory(pointTime, true);
                            pointTime = 0;
                        }
                        if(netChangeDialog!=null&&netChangeDialog.isShowing())
                            netChangeDialog.dismiss();
                        ShareElement.isIgnoreNetChange = -1;
                        Toast.makeText(SpecialDetailActivity.this.getApplicationContext(),"已连接wifi网络!",Toast.LENGTH_LONG).show();
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
                videoView.startPlay(bean);
                ShareElement.isIgnoreNetChange = 2;
            }
        });
        //停止观看
        netChangeDialog.getCancelButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                netChangeDialog.dismiss();
                videoView.showNetPic(programDetail.getPicurl());
                if(NetworkUtil.getCurrentNetworkType(SpecialDetailActivity.this) == ConnectivityManager.TYPE_MOBILE)
                    ShareElement.isIgnoreNetChange = 1;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addBaiDuAd();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(videoView);
        presenter.release();
        BusProvider.getInstance().unregister(this);
        adView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWakeLock.acquire();
        TANetworkStateReceiver.registerObserver(taNetChangeObserver);
        if((netChangeDialog!= null && netChangeDialog.isShowing()) || netCheck()) {
            return;
        }
        if(bean != null) {
            videoView.startPlay(bean);
            videoView.setHistory(pointTime, true);
        }
    }

    private int pointTime;
    @Override
    protected void onPause() {
        super.onPause();
        TANetworkStateReceiver.removeRegisterObserver(taNetChangeObserver);
        videoView.unRegisterSensor();
        videoView.stopPreLoading();
        videoView.stop();
        if (mWakeLock.isHeld())
            mWakeLock.release();
        pointTime = (int) videoView.getBreakTime();
    }

    @Override
    protected void onStop() {
        super.onStop();
        savePlayHistory();
    }

    @OnClick(R.id.share_btn)
    public void share(View view){
        sharePopupWindow = new SelectSharePopupWindow(this);
        sharePopupWindow .showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void initView() {
        EventBus.getDefault().register(videoView);
        BusProvider.getInstance().register(this);
        videoView.init(this, 0, 1);
        videoView.setOnBackListener(this);
//        presenter.judgeNetwork();
        String data = getIntent().getDataString();
        try{
            if(!TextUtils.isEmpty(data)){
                String[] split = data.split(",");//以data/切割data字符串
                idStr = split[1];
            }
            else{
                idStr = getIntent().getStringExtra("idStr");
            }
        }catch (Exception ex){
            idStr = getIntent().getStringExtra("idStr");
        }
        programIdStr = getIntent().getStringExtra("programIdStr");
        presenter.loadSpecialDetail(idStr);
        presenter.loadSpecialContentList(idStr, page, size);
        list = new ArrayList<>();
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.blue);
        swipeRefreshLayout.setOnRefreshListener(this);
        if (!TextUtils.isEmpty(programIdStr)) {
            adapter = new SpecialItemAdapter(this, list, programIdStr);
        } else {
            adapter = new SpecialItemAdapter(this, list);
        }

        llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
        recyclerView.setLoadMoreListener(this);
        myHandler = new MyHandler();
        adapter.notifyDataSetChanged();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "MyTag");
        praiseClick();
        findCollectBean();
        praiseCheckBox.setChecked(false);
        praiseData =  presenter.loadPraise(idStr);
        if(praiseData != null){
            praiseCheckBox.setChecked(praiseData.getValid());
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        // 从推送跳转过来
        Log.i(TAG, "onNewIntent");
        setIntent(intent);
        idStr = getIntent().getStringExtra("code");
        super.onNewIntent(intent);
    }
    @OnClick(R.id.collect_mllt)
    void collect(){
        if (programDetail == null) {
            Toast.makeText(this, "请稍候", Toast.LENGTH_SHORT).show();
            return;
        }
        final CollectBean bean =new CollectBean();
        bean.setName(titleTv.getText().toString());
        bean.setSid(idStr);
        bean.setVideoType(VideoType.special);
        bean.setPicurl(programDetail.getPicurl());
        Map<String,String> map = new HashMap<>();
        map.put("sid",bean.getSid());
        collectDao.subscribe();
        collectDao.queryByConditionSync(map, new DbCallBack<List<CollectBean>>() {
            @Override
            public void onComplete(List<CollectBean> list) {
                if(list!=null && list.size() > 0){
                    collectDao.deleteById(list.get(0).getId());
                    collectCheckBox.setChecked(false);
                    collectDao.unsubscribe();
                }else{
                    collectDao.insertSync(bean, new DbCallBack<Boolean>() {
                        @Override
                        public void onComplete(Boolean bool) {
                            if(bool){
                                collectCheckBox.setChecked(true);
                                TipUtil.showSnackTip(collectCheckBox,"收藏成功");
                            }else{
                                collectCheckBox.setChecked(false);
                                TipUtil.showSnackTip(collectCheckBox,"收藏失败");
                            }
                            collectDao.unsubscribe();
                        }
                    });
                }

            }
        });
    }

    public void findCollectBean(){
        Map<String,String> map = new HashMap<>();
        map.put("sid",idStr);
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
    public void showProgressBar() {
        loadingLayout.showProgressBar();
    }

    @Override
    public void hideProgressBar() {
        loadingLayout.hideProgressBar();
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void showErrorView() {
        loadingLayout.showErrorView();
    }


    @Override
    public void showWifiView() {
        loadingLayout.showWifiView();
    }

    @Override
    public void showDetailView(SpecialDetail detailData) {
        specialDetail = detailData;
        descTv.setText(detailData.getDesc());
        brtTv.setText("[" + detailData.getBrtxt() + "]");
        titleTv.setText(detailData.getName());
    }

    @Override
    public void fillSpecialList(SpecialContentList specialContentList) {
        if (specialContentList.getItems().size() == 0) {
            canLoadMore = false;
            return;
        }
        list.addAll(specialContentList.getItems());
        adapter.notifyDataSetChanged();
        if (TextUtils.isEmpty(programIdStr)) {
            programIdStr = list.get(0).getId();
        }
        if (isFirstEnter) {
            presenter.loadProgramDetail(programIdStr);
            isFirstEnter = false;
        }
        if (isAutoPlay) {
            llm.scrollToPositionWithOffset(currentPosition, 0);
            myHandler.sendEmptyMessageDelayed(0, 100);
            isAutoPlay = false;
        }
        if(isNextPlay){
            adapter.playedList.add(list.get(currentPosition).getId());
            presenter.loadProgramDetail(list.get(currentPosition).getId());
            isNextPlay = false;
        }
    }

    class MyHandler extends Handler {
        public MyHandler() {
        }

        public MyHandler(Looper L) {
            super(L);
        }

        @Override
        public void handleMessage(Message msg) {
            if(adapter.holders != null && adapter.holders.get(currentPosition) != null ){
                adapter.holders.get(currentPosition).click();
            }
        }
    }

    private  PlayBean bean;
    @Override
    public void fillProgramDetail(ProgramDetail programDetail) {
        playerHistoryBean = presenter.getPlayHistory(programDetail.getId());
        this.programDetail = programDetail;
        videoView.setProgramName(programDetail.getName());
        presenter.getSeriesListValue(programDetail.getId(), programDetail.getCplist().get(0).getCpid(), 1, 20, 0);

        //添加百度联盟广告
        RelativeLayout rl1 = new RelativeLayout(this);
        RelativeLayout.LayoutParams rllp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                adView.getMeasuredHeight());
        rl1.setLayoutParams(rllp1);
        content.addView(rl1);
    }

    /**
     * 获取节目列表数据
     * @param seriesDetail
     */
    @Override
    public void fillSeriesVListValue(SeriesDetail seriesDetail) {
        SeriesDetail.SourceBean sourceBean = seriesDetail.getSource().get(0);
        bean = new PlayBean();
        if(!TextUtils.isEmpty(sourceBean.getPlayUrl())) {
            String url = programDetail.getSrc()+"&MovieName="+programDetail.getName()+"###"+(sourceBean.getName().replace(" ", ""))+"&MovieId="+sourceBean.getId();
            bean.setUrl(url);
        }
        bean.setProgramId(programDetail.getId());
        bean.setmId(sourceBean.getId());
        videoView.stop();
        if(netCheck()) {
        }
        else
            videoView.startPlay(bean);
        presenter.pvLog(bean.getProgramId(), this);
        presenter.enterDetailLog(bean.getProgramId(), this);
        if (playerHistoryBean != null) {
            videoView.setHistory(playerHistoryBean.getPointTime(), true);
        }
    }


    private void savePlayHistory() {
        if (specialDetail != null && programDetail != null) {
            presenter.delPlayHistory(programIdStr);
            if (playerHistoryBean == null)
                playerHistoryBean = new PlayerHistoryBean();
            playerHistoryBean.setPointTime(videoView.getBreakTime());
            playerHistoryBean.setMediaType(3);
            playerHistoryBean.setSpecialId(idStr);
            playerHistoryBean.setProgramId(programIdStr);
            playerHistoryBean.setProgramName(programDetail.getName());
            playerHistoryBean.setPicUrl(specialDetail.getPicture2());
            playerHistoryBean.setVideoType(1);
            playerHistoryBean.setSdkType(1);
            if (videoView.getBreakTime() != 0) {
                presenter.insertPlayHistory(playerHistoryBean);
            }
        }
    }

    @Override
    public void onRefresh() {
        canLoadMore = true;
        page = 1;
        list.clear();
        adapter.notifyDataSetChanged();
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        presenter.loadSpecialContentList(idStr, page, size);
    }

    @Override
    public void loadMore() {
        if (canLoadMore) {
            if (!swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
            page++;
            presenter.loadSpecialContentList(idStr, page, size);
        }
    }

    int landscape = ShareElement.PORTRAIT;
    @Override
    public void halfFullScreenSwitch(int landscapeState) {
        if (landscapeState == ShareElement.PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else if(landscapeState == ShareElement.LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else if(landscapeState == ShareElement.REVERSILANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }
        halfFullScreen(landscapeState);
    }

    private void halfFullScreen(int landscapeState) {

        if (landscapeState == ShareElement.PORTRAIT) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(params);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            llm.scrollToPositionWithOffset(currentPosition, 0);
            if(adapter.holders.get(currentPosition) != null){
                adapter.holders.get(currentPosition).click();
            }
            showBottonUIMenu();
            videoView.unRegisterSensor();
        }
        else {
            if(landscape == ShareElement.PORTRAIT) {
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                getWindow().setAttributes(params);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                hideBottomUIMenu();
                videoView.registerSensor();
            }
        }
        this.landscape = landscapeState;
    }

    @Override
    public AudioManager getAudioManager() {
        return (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("NEXTPROBLEM")})
    public void nextProblem(SpecialItemAdapter.CommonItemHolder holder) {
        //savePlayHistory();
        presenter.delPlayHistory(programIdStr);
        String nextIdStr = holder.bean.getId();
        if (nextIdStr != programIdStr) {
            presenter.loadProgramDetail(nextIdStr);
            if(landscape == ShareElement.PORTRAIT){
                AddCartAnimation.AddToCart(holder.picIv, videoLayout,this, rootRlt, 1);
            }
        }
        programIdStr = nextIdStr;
        currentPosition = holder.bean.getPosition();


    }


    @Override
    public void onBackPressed() {
        savePlayHistory();
        if (videoView.isLandScape()) {
            videoView.handleFullHalfScrren(ShareElement.PORTRAIT);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onBack() {
        if (videoView != null && videoView.isLock) {
            return;
        }
        if (videoView.isLandScape()) {
            videoView.handleFullHalfScrren(ShareElement.PORTRAIT);
            return;
        }
        super.onBackPressed();
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

    private boolean netCheck() {
        if(isNetCheck()) {
            netStateCheck();
            return true;
        }
        return false;
    }

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
//                if (arg0.size() > 0 && arg0.get(0).isAdAvailable(SpecialDetailActivity.this)) {
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
                ad_rlt.setVisibility(View.VISIBLE);
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
//        aq.id(R.id.iv_cta).text(nativeResponse.isDownloadApp() ? "免费下载" : "查看详情");
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
 /**
     * 当前播放完成后进行下一集的选集
     * @param nextPlay
     */
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(AppGlobalConsts.EventType.TAG_A)})
    public void nextPlayForComplete (NextPlay nextPlay) {
        videoView.stop();
        currentPosition++;
        if (list.size() == currentPosition) {
            loadMore();
            isAutoPlay = true;
            return;
        }
        llm.scrollToPositionWithOffset(currentPosition, 0);
        /*recyclerView.getChildAt(currentPosition).performClick();*/
        myHandler.sendEmptyMessageDelayed(0, 100);
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
                            praiseData.setProgramId(idStr);
                        }
                        praiseCheckBox.setChecked(praiseData.getValid());
                        if(praiseData.getValid()){
                            presenter.sendIntegralLog("like",idStr);
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

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("selectChannel")})
    public void shareSpecial(String channel) {
        String targetUrl = AppGlobalVars.shareApiHost+"?talktvspecialdetail://tvfan.com/share," + idStr+",";
        if(AppGlobalVars.appKeyResult == null || AppGlobalVars.appKeyResult.getData().size() < 2){
            Toast.makeText(SpecialDetailActivity.this,"分享失败，请稍后重试..",Toast.LENGTH_SHORT).show();
            return;
        }if(TextUtils.isEmpty(specialDetail.getPicture2())){
            Toast.makeText(SpecialDetailActivity.this,"分享失败，暂不支持分享",Toast.LENGTH_SHORT).show();
            return;
        }
        if(channel.equals("WEIXIN")){
            PlatformConfig.setWeixin("wxcfaa020ee248a2f2",AppGlobalVars.appKeyResult.getData().get(0).getSecret());
            new ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN)
                    .withText(specialDetail.getName())
                    .withTitle(specialDetail.getName())
                    .withMedia(new UMImage(this,specialDetail.getPicture2()))
                    .withTargetUrl(targetUrl)
                    .setCallback(umShareListener)
                    .share();
        }else if(channel.equals("CIRCLE")){
            PlatformConfig.setWeixin("wxcfaa020ee248a2f2", AppGlobalVars.appKeyResult.getData().get(0).getSecret());
            new ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                    .withText(specialDetail.getName())
                    .withTitle(specialDetail.getName())
                    .withMedia(new UMImage(this,specialDetail.getPicture2()))
                    .withTargetUrl(targetUrl)
                    .setCallback(umShareListener)
                    .share();
        }
        else if(channel.equals("QQ")){
            PlatformConfig.setQQZone("100757629", AppGlobalVars.appKeyResult.getData().get(1).getSecret()); //"04e1ef24fe1baa6858d1de1f0d70dfb2");
            new ShareAction(this).setPlatform(SHARE_MEDIA.QQ)
                    .withText(specialDetail.getName())
                    .withMedia(new UMImage(this,specialDetail.getPicture2()))
                    .withTargetUrl(targetUrl)
                    .setCallback(umShareListener)
                    .share();
        }else if(channel.equals("ZONE")){
            PlatformConfig.setQQZone("100757629", AppGlobalVars.appKeyResult.getData().get(1).getSecret()); //"04e1ef24fe1baa6858d1de1f0d70dfb2");
            new ShareAction(this).setPlatform(SHARE_MEDIA.QZONE)
                    .withText(specialDetail.getName())
                    .withMedia(new UMImage(this,specialDetail.getPicture2()))
                    .withTargetUrl(targetUrl)
                    .setCallback(umShareListener)
                    .share();
        } else if(channel.equals("WEIBO")){
            PlatformConfig.setSinaWeibo("2064721383", AppGlobalVars.appKeyResult.getData().get(2).getSecret());//eab2b56fc44b8fa36648f77c2b6ebd07
            new ShareAction(this).setPlatform(SHARE_MEDIA.SINA)
                    .withText(specialDetail.getName())
                    .withMedia(new UMImage(this,specialDetail.getPicture2()))
                    .withTargetUrl(targetUrl)
                    .setCallback(umShareListener)
                    .share();
        }
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat","platform"+platform);
            presenter.sendIntegralLog("share",idStr);

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
           // Toast.makeText(SpecialDetailActivity.this,platform + " 分享失败", Toast.LENGTH_SHORT).show();
            if(t!=null){
                Log.d("throw","throw:"+t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
           // Toast.makeText(SpecialDetailActivity.this,platform + " 分享取消", Toast.LENGTH_SHORT).show();
        }
    };



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("showNetChangeDialog")})
    public void showNetChangeDialog(String s) {
        if(isNetCheck()) {
            netChangeDialog.show();
        }
    }
}