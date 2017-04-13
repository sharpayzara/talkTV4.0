package com.sumavision.talktv2.videoplayer.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.ShareElement;
import com.sumavision.talktv2.http.GlideProxy;
import com.sumavision.talktv2.model.entity.decor.LiveData;
import com.sumavision.talktv2.model.entity.decor.LiveDetailData;
import com.sumavision.talktv2.mycrack.CrackResult;
import com.sumavision.talktv2.ui.activity.LiveCopyrightActivity;
import com.sumavision.talktv2.ui.fragment.LiveFragment;
import com.sumavision.talktv2.ui.iview.ILivePlayerVideoView;
import com.sumavision.talktv2.ui.widget.LiveChanneListDialog;
import com.sumavision.talktv2.ui.widget.LiveProgramListDialog;
import com.sumavision.talktv2.ui.widget.LiveSourceDialog;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.NetworkUtil;
import com.sumavision.talktv2.util.NoDoubleClickListener;
import com.sumavision.talktv2.videoplayer.PlayBean;
import com.sumavision.talktv2.videoplayer.player.IPlayer;
import com.sumavision.talktv2.videoplayer.player.impl.IJKPlayer;
import com.sumavision.talktv2.videoplayer.player.impl.PPTVPlayer;
import com.sumavision.talktv2.videoplayer.player.impl.SohuPlayer;
import com.sumavision.talktv2.videoplayer.presenter.LivePlayerPresenter;
import com.sumavision.talktv2.videoplayer.utils.MyOnGestureListner;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 直播播放器
 * Created by Administrator on 2016/6/13.
 */
public class LivePlayerVideoView extends BaseVideoView<LivePlayerPresenter> implements ILivePlayerVideoView, MyOnGestureListner.OnSingleTapUpListener {
    private RelativeLayout fullControll;
    private RelativeLayout halfControll;
    PlayBean playBean;
    @BindView(R.id.tvName)
    TextView channelName;
    @BindView(R.id.channel_name)
    TextView channel_Name;
    @BindView(R.id.fav)
    ImageButton fav;
    @BindView(R.id.lock)
    ImageButton lock;
    @BindView(R.id.channel_change_bg)
    Button channelChange;
    @BindView(R.id.source_change_bg)
    Button sourceChange;
    @BindView( R.id.program_bg)
    Button program;
    @BindView(R.id.ll_controll_right)
    LinearLayout rightControll;
    @BindView(R.id.playerControllerHeader)
    RelativeLayout topLayout;
    @BindView(R.id.bottom_control)
    RelativeLayout bottomLayout;
    @BindView(R.id.btnBack)
    ImageButton back;
    @BindView(R.id.battery_time)
    RelativeLayout battery_time;
    private LiveFragment liveFragment;
    private ImageView liveAd;
    private ImageView toLiveCopyRightActivity;
    private LiveChanneListDialog liveChanneListDialog;//频道列表dialog
    private LiveProgramListDialog liveProgramListDialog;//节目单dialog
    private LiveSourceDialog liveSourceDialog;
    private LiveData.ContentBean.TypeBean.ChannelBean channelBean;

    private ArrayList<String> channelTypeDatas = new ArrayList<>();//频道类型数据
    private HashMap<Integer, ArrayList<LiveData.ContentBean.TypeBean.ChannelBean>> channelDatas = new HashMap<>();
    private ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> collectDatas;
    private ArrayList<LiveDetailData.ContentBean.PlayBean> playBeens = new ArrayList<>();
    private LiveDetailData liveDetailData;

    private boolean isFav;
    private boolean isFull;
    private boolean isGetDetail = false;//是否获取了详情数据
    private boolean showCopyRight = true;//是否需要进入官网
    private int currSourcePos;
    private int SHOWN_TIME = 10*1000;

    RotateAnimation rotateAnimation;
    ObjectAnimator animator;

    private Handler hd = new Handler();

    public LivePlayerVideoView(Context context) {
        super(context);
    }

    public LivePlayerVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LivePlayerVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onPrepare() {
        super.onPrepare();
    }

    @Override
    protected void initPresenter() {
        playerPresenter = new LivePlayerPresenter(context, this, this);
    }

    /**
     * 添加相应的view
     */
    @Override
    protected void addViews() {
        loadingLayout = (RelativeLayout) View.inflate(context, R.layout.loading_live, null);
        fullControll = (RelativeLayout) View.inflate(context, R.layout.live_full_controll, null);
        halfControll = (RelativeLayout) View.inflate(context, R.layout.liveplayer_control_layout, null);
        initCopyRightButton();
        ImageView loading = (ImageView) loadingLayout.findViewById(R.id.loading_progress);
        loadingShow(false);
        if((android.os.Build.MANUFACTURER).equals("Coolpad"))
            loadingAnimation(loading);
        else
            loadingProperty(loading);
        fullControll.setVisibility(View.GONE);
        addAd();
        addView(halfControll, 2);
        addView(fullControll, 2);
        addView(loadingLayout, 3);
        addView(toLiveCopyRightActivity, 4);
        hd.postDelayed(halfControllerRunnable, SHOWN_TIME);
    }

    /**
     * 属性动画
     * @param loading
     */
    private void loadingProperty(ImageView loading) {
        animator = ObjectAnimator.ofFloat(loading, "rotation", 0.0F, 360F).setDuration(1000);
        animator.setRepeatCount(-1);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    /**
     * 补间动画
     * @param loading
     */
    private void loadingAnimation (ImageView loading) {
        rotateAnimation = new RotateAnimation(0.0f, 360.0f ,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(-1);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.start();
        loading.setAnimation(rotateAnimation);
    }

    /**
     * 初始化进入直播官网的点击按钮
     */
    private void initCopyRightButton() {
        toLiveCopyRightActivity = new ImageView(context);
        toLiveCopyRightActivity.setImageResource(R.mipmap.livetv_goto_bg);
        toLiveCopyRightActivity.setVisibility(View.GONE);
        toLiveCopyRightActivity.setScaleType(ImageView.ScaleType.FIT_XY);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        toLiveCopyRightActivity.setClickable(true);
        toLiveCopyRightActivity.setLayoutParams(params2);
        toLiveCopyRightActivity.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Intent intent = new Intent(context, LiveCopyrightActivity.class);
                intent.putExtra("liveurl", liveDetailData.getJumpUrl());
                intent.putExtra("channelName", channelBean.getName());
                liveFragment.startActivityForResult(intent, 2);
            }
        });
    }

    private void addAd() {
        liveAd = new ImageView(context);
        liveAd.setVisibility(View.GONE);
        RelativeLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        liveAd.setLayoutParams(params);
        addView(liveAd, 1);
    }

    @Override
    protected void initView() {
//        startP2PService();
        playerPresenter.startP2PService(1);
        myOnGestureListner.setOnSingleTapUpListener(this);
        playerPresenter.initBatteryAndTime();
    }

    /**
     * 全屏半屏切换
     * @param isFullScreen
     */
    @Override
    protected void fullHalfScrrenChange(boolean isFullScreen) {
        isFull = isFullScreen;
        hd.removeCallbacks(halfControllerRunnable);
        if(isFullScreen) {
            controllerLayoutShow(false);
            fullControll.setVisibility(View.VISIBLE);
            halfControll.setVisibility(View.GONE);
            fav.setVisibility(View.VISIBLE);
            battery_time.setVisibility(View.VISIBLE);
            back.setVisibility(View.VISIBLE);
        }
        else {
            controllerLayoutShow(true);
            halfControll.setVisibility(View.VISIBLE);
            fullControll.setVisibility(View.GONE);
            hd.postDelayed(halfControllerRunnable, SHOWN_TIME);
            if(liveChanneListDialog!=null)
                liveChanneListDialog.dismiss();
            if(liveProgramListDialog!=null)
                liveProgramListDialog.dismiss();
            if(liveSourceDialog!=null)
                liveSourceDialog.dismiss();
        }
    }

    /**
     * 频道切换
     * @param channelBean
     */
    public void changeChannel(LiveData.ContentBean.TypeBean.ChannelBean channelBean) {
        if(channelBean != null) {
            this.channelBean = channelBean;
            channelName.setText(channelBean.getName());
            channel_Name.setText(channelBean.getName());
            showAd(false);
            playerPresenter.getProgramData(channelBean.getId());
            if(ShareElement.isIgnoreNetChange == 2)
                loadingShow(true);
            playerPresenter.isFav(channelBean.getId());

            int typePos = playerPresenter.getCurrType(channelTypeDatas, channelBean.getChannelType());
            liveChanneListDialog.select(typePos, channelBean.getId());
        }
    }

    /**
     * 获取频道类型数据
     * @param channelTypeDatas
     */
    public void setTypeDatas (ArrayList<String> channelTypeDatas) {
        this.channelTypeDatas.addAll(channelTypeDatas);
        if("收藏".equals(channelTypeDatas.get(0)))
            this.channelTypeDatas.remove(0);
    }

    /**
     * 获取频道列表数据
     * @param channeldatas
     */
    public void setChannelDatas(ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> channeldatas) {
        ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> channelData = new ArrayList<>();
        int pos = 0;
        int count = channeldatas.size();
        for(int i=0; i<count; i++) {
            LiveData.ContentBean.TypeBean.ChannelBean channelBean = channeldatas.get(i);
            channelData.add(channelBean);
            if (i == count-1) {
                channelDatas.put(pos, channelData);
            }
            else if(!channelBean.getChannelType().equals(channeldatas.get(i+1).getChannelType())) {
                channelDatas.put(pos, channelData);
                channelData = new ArrayList<>();
                pos++ ;
            }
        }
        initChannelListDialog();
    }

    /**
     * 获取收藏数据
     * @param collectDatas
     */
    public void setCollectDatas(ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> collectDatas) {
        this.collectDatas = collectDatas;
        playerPresenter.isFav(channelBean.getId());
        liveChanneListDialog.setCollectDatas(collectDatas);
    }

    public void setLiveFragment (LiveFragment liveFragment) {
        this.liveFragment = liveFragment;
    }

    /**
     *初始化频道列表Dialog
     */
    private void initChannelListDialog () {
        if(liveChanneListDialog == null) {
            int windowHeight = getWidth();
            liveChanneListDialog = new LiveChanneListDialog(context, R.style.programdialog, windowHeight);
            liveChanneListDialog.setTypeDatas(channelTypeDatas);
            liveChanneListDialog.setChannelDatas(channelDatas);
            liveChanneListDialog.setOnChangeChannelListener(new LiveChanneListDialog.OnChangeChannelListener() {
                @Override
                public void onChangeChannel(LiveData.ContentBean.TypeBean.ChannelBean channelBean) {
                    if(LivePlayerVideoView.this.channelBean.getId().equals(channelBean.getId())) {
                        LivePlayerVideoView.this.channelBean = channelBean;
                        return;
                    }
                    showCopyRight = true;
                    LivePlayerVideoView.this.channelBean = channelBean;
                    channelName.setText(channelBean.getName());
                    channel_Name.setText(channelBean.getName());
                    playerPresenter.getProgramData(channelBean.getId());
                    playerPresenter.isFav(channelBean.getId());
                    BusProvider.getInstance().post("changeChannelFromVideo", channelBean);
                }
            });
            liveChanneListDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    channelChange.setBackgroundResource(R.mipmap.play_btn_bg_selected);
                }
            });
            liveChanneListDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    channelChange.setBackgroundResource(R.mipmap.play_btn_bg_nor);
                }
            });
        }
    }

    /**
     * 初始化节目单列表Dialog
     */
    private void initProgramListDialog () {
        if(liveDetailData == null)
            return;
        if(liveProgramListDialog == null) {
            int windowHeight = getHeight();
            liveProgramListDialog = new LiveProgramListDialog(context, R.style.programdialog, windowHeight);
            liveProgramListDialog.setData(liveDetailData.getContent().getDay());
            if(channelBean!=null)
                liveProgramListDialog.setChannelName(channelBean.getName());
            liveProgramListDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    if(program!=null)
                    program.setBackgroundResource(R.mipmap.play_btn_bg_selected);
                }
            });
            liveProgramListDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if(program!=null)
                    program.setBackgroundResource(R.mipmap.play_btn_bg_nor);
                }
            });
        }
    }

    /**
     * 初始化换源dialog
     */
    private void initLiveSourceDialog () {
        if(liveSourceDialog == null) {
            int windowHeight = getHeight();
            liveSourceDialog = new LiveSourceDialog(context, R.style.programdialog, windowHeight);
            liveSourceDialog.setSourceDatas(playBeens.size());
            liveSourceDialog.setOnSourceChangeListener(new LiveSourceDialog.OnSourceChangeListener() {
                @Override
                public void onSourceChange(int pos) {
                    playerPresenter.removeChangeSourceMsg();
                    playerPresenter.log("remove  "+"sourceChange");
                    if(ShareElement.isIgnoreNetChange == 1)
                        return;
                    play(pos);
                }
            });
            liveSourceDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    sourceChange.setBackgroundResource(R.mipmap.play_btn_bg_selected);
                }
            });
            liveSourceDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    sourceChange.setBackgroundResource(R.mipmap.play_btn_bg_nor);
                }
            });
        }
    }


    public void setShowCopyRight(boolean showCopyRight) {
        this.showCopyRight = showCopyRight;
    }

    public boolean isCopyRightImageShow() {
        return  (toLiveCopyRightActivity.getVisibility()==View.VISIBLE);
    }

    /**
     * 播放
     * @param pos
     */
    public void play(int pos) {
        showAd(false);
        currSourcePos = pos;
        hideNetPic();
        toLiveCopyRightActivity.setVisibility(View.GONE);
        if(isGetDetail && !TextUtils.isEmpty(liveDetailData.getJumpUrl()) && showCopyRight) {
            small_large_button.setClickable(false);
            toLiveCopyRightActivity.setVisibility(View.VISIBLE);
            loadingShow(false);
            playerPresenter.removeChangeSourceMsg();
        }
        else {
            play2();
        }
        isGetDetail = false;
    }

    public void play2() {
        toLiveCopyRightActivity.setVisibility(View.GONE);
//        halfControll.setVisibility(View.VISIBLE);
        small_large_button.setClickable(true);
//        if(isFirst) {
//            isFirst = false;
//            play();
//        }
//        else if(!isPlaying){
//            playerPresenter.stop();
//            play();
//        }
//        else {
//            isPlayAfterStop = true;
//            playerPresenter.stop();
//        }
        play();
        loadingShow(true);
    }


    private void play() {
//        isPlaying = true;
        playerPresenter.log("send  "+"play");
        playerPresenter.removeChangeSourceMsg();
        playerPresenter.sendChangeSourceMsg();
        playBean = new PlayBean();
        playBean.setPlayPath(liveDetailData.getContent().getPlay().get(currSourcePos).getVideoPath());
        playBean.setUrl(liveDetailData.getContent().getPlay().get(currSourcePos).getUrl());
        startPlay(playBean);
        playerPresenter.livePlayLog(channelBean.getId(), liveDetailData.getContent().getPlay().get(currSourcePos).getId()+"");
    }

    /**
     * View的点击事件
     * @param v
     */
    @OnClick({R.id.btnBack, R.id.fav, R.id.lock, R.id.channel_change_bg, R.id.source_change_bg, R.id.program_bg, R.id.small_button})
    public void onclick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
            case R.id.small_button:
                handleFullHalfScrren(ShareElement.PORTRAIT);
                break;
            case R.id.fav:
                favClick();
                break;
            case R.id.lock:
                if(isLock) {
                    isLock = false;
                    rightControll.setVisibility(View.VISIBLE);
                    topLayout.setVisibility(View.VISIBLE);
                    bottomLayout.setVisibility(View.VISIBLE);
                    lock.setImageResource(R.mipmap.unlocked);
                    hd.removeCallbacks(controllerRunnable);
                    hd.postDelayed(controllerRunnable, SHOWN_TIME);
                }
                else {
                    isLock = true;
                    rightControll.setVisibility(View.GONE);
                    topLayout.setVisibility(View.GONE);
                    bottomLayout.setVisibility(View.GONE);
                    lock.setImageResource(R.mipmap.lock);
                    hd.removeCallbacks(lockRunnable);
                    hd.postDelayed(lockRunnable, SHOWN_TIME);
                }
                myOnGestureListner.isLocked(isLock);
                break;
            case R.id.channel_change_bg:
                showDialog();
                if(liveChanneListDialog != null)
                    liveChanneListDialog.show();
                break;
            case R.id.source_change_bg:
                showDialog();
                initLiveSourceDialog();
                if(liveSourceDialog!=null)
                    liveSourceDialog.show();
                break;
            case R.id.program_bg:
                showDialog();
                initProgramListDialog();
                if(liveProgramListDialog!=null)
                    liveProgramListDialog.show();
                break;
        }
    }

    /**
     * 点击收藏按钮
     */
    private void favClick(){
        if(isFav) {
            playerPresenter.cancelFav(collectDatas, channelBean.getId());
            fav.setImageResource(R.mipmap.play_collect_nor_btn);
            if(onFavChangeLisenter != null)
                onFavChangeLisenter.onFavChange(false);
        }
        else {
            playerPresenter.favChannel(collectDatas, channelBean);
            fav.setImageResource(R.mipmap.play_collect_selected_btn);
            onFavChangeLisenter.onFavChange(true);
        }
        isFav = !isFav;

    }

    /**
     * dialog点击显示进行的相关操作
     */
    private void showDialog() {
        lock.setVisibility(View.GONE);
        topLayout.setVisibility(View.GONE);
        bottomLayout.setVisibility(View.GONE);
        hd.removeCallbacks(controllerRunnable);
    }

    /**
     * 点击屏幕相关view的显示和隐藏
     * @param show
     */
    private void controllerLayoutShow(boolean show) {
        if(show) {
            rightControll.setVisibility(View.GONE);
            lock.setVisibility(View.GONE);
            topLayout.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.GONE);
            hd.removeCallbacks(controllerRunnable);
        }
        else {
            rightControll.setVisibility(View.VISIBLE);
            lock.setVisibility(View.VISIBLE);
            topLayout.setVisibility(View.VISIBLE);
            bottomLayout.setVisibility(View.VISIBLE);
            hd.removeCallbacks(controllerRunnable);
            hd.postDelayed(controllerRunnable, SHOWN_TIME);
        }
    }

    /**
     * lock的显示
     */
    private Runnable lockRunnable = new Runnable() {
        @Override
        public void run() {
            lock.setVisibility(View.GONE);
        }
    };

    /**
     * fullcontrollerLayout的显示
     */
    private Runnable controllerRunnable = new Runnable() {
        @Override
        public void run() {
            hd.removeCallbacks(controllerRunnable);
            controllerLayoutShow(true);
        }
    };

    /**
     * halfcontroller的显示
     */
    private Runnable halfControllerRunnable = new Runnable() {
        @Override
        public void run() {
            hd.removeCallbacks(halfControllerRunnable);
            halfControll.setVisibility(View.GONE);
        }
    };

    public void pause(){
        playerPresenter.pause();
    }

    public void start() {
        playerPresenter.start();
    }

    public void resume () {
        playerPresenter.resume();
    }

    public void removeMsg() {
        hd.removeCallbacksAndMessages(null);
        playerPresenter.removeChangeSourceMsg();
    }

    /**
     * 获取频道详情界面
     * @param liveDetailData
     */
    @Override
    public void getDetailSucess(LiveDetailData liveDetailData) {
        this.liveDetailData = liveDetailData;
        if("success".equals(liveDetailData.getMsg())) {
            isGetDetail = true;
            int count = liveDetailData.getContent().getPlay().size();
            playBeens.clear();
            for(int i=0; i<count; i++) {
                playBeens.add(liveDetailData.getContent().getPlay().get(i));
                if(i==4)
                    break;
            }
            if(liveProgramListDialog != null) {
                liveProgramListDialog.setData(liveDetailData.getContent().getDay());
                liveProgramListDialog.setChannelName(channelBean.getName());
            }
            if(liveSourceDialog != null)
                liveSourceDialog.setSourceDatas(playBeens.size());

//            if(ShareElement.isIgnoreNetChange == 1)
//                return;
//            else
            if((ShareElement.isIgnoreNetChange == -1 && NetworkUtil.getCurrentNetworkType(context) == ConnectivityManager.TYPE_MOBILE)||(ShareElement.isIgnoreNetChange == 1)) {
                if(onNetSateListener != null)
                    onNetSateListener.netSateListener();
                loadingShow(false);
                return;
            }
            play(0);
        }
    }

    @Override
    public void getDetailFail(Throwable throwable) {}

    /**
     * 当前播放的频道是否被收藏
     * @param isFav
     */
    @Override
    public void isFav(final boolean isFav) {
        this.isFav = isFav;
        Observable.just("")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        if(isFav)
                            fav.setImageResource(R.mipmap.play_collect_selected_btn);
                        else
                            fav.setImageResource(R.mipmap.play_collect_nor_btn);
                    }
                });

    }

    /**
     * 自动切源
     */
    @Override
    public void changeSourceAuto() {
        int sourceCount = playBeens.size()-1;
        int pos = currSourcePos+1;
        if(pos > sourceCount)
            pos = 0;
        play(pos);

        if(liveSourceDialog != null) {
            liveSourceDialog.changeSourceAuto(pos);
            liveSourceDialog.setPos(pos);
        }
    }

    @Override
    public void prepare() {
        Observable.just("")
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        showAd(true);
                    }
                });

        loadingShow(false);
    }

    @Override
    public void onError(IPlayer mp, int what, int extra, String var) {
        showError();
        playerPresenter.playErrorLog(channelBean.getId(), liveDetailData.getContent().getPlay().get(currSourcePos).getId(), "playfailed");
        if(mp instanceof IJKPlayer){
        }else if(mp instanceof SohuPlayer){

        }else if(mp instanceof PPTVPlayer){

        }
    }

    public void showError(){
        if (!NetworkUtil.isConnectedByState(BaseApp.getContext())) {
            Observable.just("")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            if (getChildCount() > 0 && getChildAt(getChildCount() - 1).getTag() != null && getChildAt(getChildCount() - 1).getTag().toString().equals("fail")) {
                                return;
                            }
                            loadingShow(false);
                            ImageView iv = new ImageView(context);
                            iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                            try {
                                GlideProxy.getInstance().loadResImage2(context, R.mipmap.wifi_fail, iv);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            iv.setTag("fail");
                            iv.setOnClickListener(new NoDoubleClickListener() {
                                @Override
                                public void onNoDoubleClick(View view) {
                                    removeViewAt(getChildCount() - 1);
                                    startPlay(playBean);
                                }
                            });
                            addView(iv);
                        }
                    });
        }
    }

    private ImageView netPic;
    private ImageView netPic2;
    /**
     *
     */
    public void showNetPic () {
        if(netPic == null) {
            OnClickListener onClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    BusProvider.getInstance().post("showNetChangeDialog", "");
                }
            };
            netPic = new ImageView(context);
            netPic.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            netPic.setImageResource(R.mipmap.play_bg);
            netPic.setTag("netchange");

            netPic2 = new ImageView(context);
            RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params2.addRule(RelativeLayout.CENTER_IN_PARENT);
            netPic2.setLayoutParams(params2);
            netPic2.setImageResource(R.mipmap.hot_play_play_btn);
            netPic.setOnClickListener(onClickListener);
            netPic2.setOnClickListener(onClickListener);
            addView(netPic);
            addView(netPic2);
        }
        netPic.setVisibility(View.VISIBLE);
        netPic2.setVisibility(View.VISIBLE);
    }

    /**
     *
     */
    public void hideNetPic () {
        Observable.just("")
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        if(netPic != null) {
                            netPic.setVisibility(View.GONE);
                            netPic2.setVisibility(View.GONE);
                        }
                    }
                });
    }

    @Override
    public void onInfo(IPlayer mp, int what, int extra) {
        switch (what) {
            case IPlayer.MEDIA_INFO_BUFFERING_START:
                loadingShow(true);
                playerPresenter.sendChangeSourceMsg();
                playerPresenter.log("send  "+"info");
                break;
            case IPlayer.MEDIA_INFO_BUFFERING_END:
                loadingShow(false);
                playerPresenter.removeChangeSourceMsg();
                playerPresenter.log("remove  "+"info");
                break;
            case IPlayer.MEDIA_INFO_BUFFERING_PERCENT:
                if (extra == 100) {
                    loadingShow(false);
                    playerPresenter.removeChangeSourceMsg();
                    playerPresenter.log("remove  "+"info");
                }
            default:
                break;
        }

    }

    @Override
    public void onCrackComplete(CrackResult result) {
    }

    @Override
    public void onCrackFailed() {
        playerPresenter.playErrorLog(channelBean.getId(), liveDetailData.getContent().getPlay().get(currSourcePos).getId(), "crack");
        showError();
    }

    @Override
    public void stop() {
        super.stop();

    }

    @Override
    public void onComplation() {
        super.onComplation();
        if (!NetworkUtil.isConnectedByState(BaseApp.getContext())) {
            Observable.just("")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            if(getChildCount() > 0 && getChildAt(getChildCount()-1).getTag() !=null && getChildAt(getChildCount()-1).getTag().toString().equals("fail")){
                                return;
                            }
                            loadingShow(false);
                            ImageView iv = new ImageView(context);
                            iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                            GlideProxy.getInstance().loadResImage2(context, R.mipmap.wifi_fail, iv);
                            iv.setTag("fail");
                            iv.setOnClickListener(new NoDoubleClickListener() {
                                @Override
                                public void onNoDoubleClick(View view) {
                                    removeViewAt(getChildCount() - 1);
                                    startPlay(playBean);
                                }
                            });
                        }
                    });
        }
    }

    @Override
    public void onSingleUp() {
        if(isFull) {
            if (isLock) {
                if(lock.isShown()) {
                    lock.setVisibility(View.GONE);
                }
                else {
                    lock.setVisibility(View.VISIBLE);
                }
            }
            else if(rightControll.isShown()) {
                controllerLayoutShow(true);
            }
            else {
                controllerLayoutShow(false);
            }
        }
        else {
            hd.removeCallbacks(halfControllerRunnable);
            if(halfControll.isShown()) {
                halfControll.setVisibility(View.GONE);
            }
            else {
                halfControll.setVisibility(View.VISIBLE);
                hd.postDelayed(halfControllerRunnable, SHOWN_TIME);
            }
        }
    }

    @Override
    public void down(MotionEvent e) {}

    @Override
    public void scroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {}

    @Override
    public void onDoubleTap() {

    }

    private void showAd(boolean show) {
        if(show && !TextUtils.isEmpty(liveDetailData.getAdUrl())) {
            GlideProxy.getInstance().loadLiveAd(context, liveDetailData.getAdUrl(), liveAd);
            liveAd.setVisibility(View.VISIBLE);
        }
        else if(!show && liveAd.isShown()){
            liveAd.setVisibility(View.GONE);
        }
    }

    public interface OnFavChangeLisenter {
        void onFavChange(boolean fav);
    }
    private OnFavChangeLisenter onFavChangeLisenter;
    public void setOnFavChangeLisenter (OnFavChangeLisenter onFavChangeLisenter) {
        this.onFavChangeLisenter = onFavChangeLisenter;
    }

    public interface OnNetSateListener {
        void netSateListener();
    }
    private OnNetSateListener onNetSateListener;
    public void setOnNetSateListener (OnNetSateListener onNetSateListener) {
        this.onNetSateListener = onNetSateListener;
    }

    @Override
    public void release() {
        if(animator != null)
            animator.cancel();
//        rotateAnimation.cancel();
        super.release();
    }
}
