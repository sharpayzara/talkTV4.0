package com.sumavision.talktv2.videoplayer.view;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sumavision.cachingwhileplaying.CachingWhilePlayingService;
import com.sumavision.cachingwhileplaying.entity.BufferedPositionInfo;
import com.sumavision.cachingwhileplaying.entity.PreLoadingResultInfo;
import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.ShareElement;
import com.sumavision.talktv2.http.GlideProxy;
import com.sumavision.talktv2.model.entity.MediaPlayComplete;
import com.sumavision.talktv2.model.entity.NextPlay;
import com.sumavision.talktv2.mycrack.CrackResult;
import com.sumavision.talktv2.ui.listener.PlayCompleteListener;
import com.sumavision.talktv2.ui.widget.DefintionChangeDialog;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.DateUtils;
import com.sumavision.talktv2.util.NetworkUtil;
import com.sumavision.talktv2.util.NoDoubleClickListener;
import com.sumavision.talktv2.videoplayer.PlayBean;
import com.sumavision.talktv2.videoplayer.iview.IVodPlayerView;
import com.sumavision.talktv2.videoplayer.player.IPlayer;
import com.sumavision.talktv2.videoplayer.player.impl.IJKPlayer;
import com.sumavision.talktv2.videoplayer.player.impl.PPTVPlayer;
import com.sumavision.talktv2.videoplayer.player.impl.SohuPlayer;
import com.sumavision.talktv2.videoplayer.presenter.VodPlayerPresenter;
import com.sumavision.talktv2.videoplayer.utils.MyOnGestureListner;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by zhangyisu on 2016/6/8.
 */
public class VodPlayerVideoView extends BaseVideoView<VodPlayerPresenter> implements IVodPlayerView, View.OnClickListener, SeekBar.OnSeekBarChangeListener, MyOnGestureListner.OnSingleTapUpListener {

    RelativeLayout controllerLayout;
    //    RelativeLayout gestureProgressLayout;
    private DefintionChangeDialog defintionChangeDialog;
    PlayCompleteListener completeListener;
    @BindView(R.id.play_btn)
    ImageButton playPause;
    @BindView(R.id.player_next_btn)
    ImageButton next;
    @BindView(R.id.time_current)
    TextView curTime;
    @BindView(R.id.media_progress)
    SeekBar seekBar;
    @BindView(R.id.time_total)
    TextView totalTime;
    @BindView(R.id.battery_time)
    RelativeLayout batteryTime;
    @BindView(R.id.tvName)
    TextView programName;
    @BindView(R.id.definition_change)
    TextView definitionChange;
    @BindView(R.id.lock)
    ImageButton lock;
    @BindView(R.id.playerControllerHeader)
    RelativeLayout top;
    @BindView(R.id.vod_controll_bottom)
    RelativeLayout bottom;
    @BindView(R.id.btnBack)
    ImageButton back;
    private ImageView replay;
    TextView adTimeCountDownTextView;
    private PlayBean playBean;
    ObjectAnimator animator;
    RotateAnimation rotateAnimation;

    private boolean isPlayNext, isAd, isStartPlaySDK;
    private boolean isShowNext = true;
    private int defaulDefintion = -1;
    private int currDefintionPos = 0;
    private long iBreakTime;

    private ArrayList<String> urls = new ArrayList<>();
    private ArrayList<String> defintions = new ArrayList<>();


    public VodPlayerVideoView(Context context) {
        super(context);
    }

    public VodPlayerVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VodPlayerVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public boolean isPlaying () {
        return isPlaying;
    }
    public void setProgramName (String name) {
        programName.setText(name);
    }

    public void setProgramNameVisiable(boolean bool){
        if(bool){
            programName.setVisibility(VISIBLE);
        }else{
            programName.setVisibility(GONE);
        }
    }


    public void startPlay(PlayBean playBean) {
        if(adTimeCountDownTextView != null)
            adTimeCountDownTextView.setVisibility(View.INVISIBLE);
        setPlayOrPause(true);
        showReplay(false);
        if(playerType == 2) {
            isStartPlaySDK = true;
        }
        stopPreLoading();
        urls.clear();
        defintions.clear();
        hideNetPic();
        for(int i = 0; i < getChildCount();i++){
            if(getChildAt(i).getTag() != null && getChildAt(i).getTag().toString().equals("fail")){
                removeViewAt(i);
                break;
            }
        }
        this.playBean = playBean;
        super.startPlay(this.playBean);
    }

    @Override
    protected void initPresenter() {
        playerPresenter =  new VodPlayerPresenter(context, this);
    }

    @Override
    protected void addViews() {
        loadingLayout = (RelativeLayout) View.inflate(context, R.layout.loading_live, null);
        ImageView loading = (ImageView) loadingLayout.findViewById(R.id.loading_progress);
        if((android.os.Build.MANUFACTURER).equals("Coolpad"))
            loadingAnimation(loading);
        else
            loadingProperty(loading);
        controllerLayout = (RelativeLayout) View.inflate(context, R.layout.vodplayer_control_layout, null);
        loadingShow(false);
        Log.e("show", "hide  addviews");
        addReplay();
        addView(controllerLayout, 2);
        addView(loadingLayout, 3);
        if (playerType == 2) { // PPTV的显广告时间
            adTimeCountDownTextView = new TextView(context);
            adTimeCountDownTextView.setTextColor(context.getResources().getColor(R.color.white));
            adTimeCountDownTextView.setBackgroundColor(context.getResources().getColor(R.color.black));
            adTimeCountDownTextView.setTextSize(10);
            LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.addRule(ALIGN_PARENT_LEFT);
            lp.setMargins(10, 20, 0, 0);
            addView(adTimeCountDownTextView, -1, lp);
        }
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

    private void addReplay () {
        replay= new ImageView(context);
        replay.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        replay.setImageResource(R.mipmap.replay_xh);
        replay.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                if(playBean != null)
                    startPlay(playBean);
                else if(!TextUtils.isEmpty(url)) {
                    startPlay(url);
                    replay.setVisibility(View.GONE);
                }
            }
        });
        showReplay(false);
        addView(replay, 1);
    }

    public void showReplay(boolean show) {
        if(show)
            replay.setVisibility(View.VISIBLE);
        else
            replay.setVisibility(View.GONE);
    }

    @Override
    protected void initView() {
        playPause.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        playerPresenter.initBatteryAndTime();
        playerPresenter.startP2PService(0);
        myOnGestureListner.setOnSingleTapUpListener(this);
    }

    /**
     * 全半屏切换的监听
     * @param isFullScreen
     */
    @Override
    protected void fullHalfScrrenChange(boolean isFullScreen) {
        if(isFullScreen) {
            batteryTime.setVisibility(View.VISIBLE);
            if(isShowNext && videoType==1)
                next.setVisibility(View.VISIBLE);
            if(defintions.size()>1)
                definitionChange.setVisibility(View.VISIBLE);
            lock.setVisibility(View.VISIBLE);
            small_large_button.setImageResource(R.mipmap.play_zoom_out);
        }
        else {
            batteryTime.setVisibility(View.INVISIBLE);
            next.setVisibility(View.GONE);
            definitionChange.setVisibility(View.GONE);
            lock.setVisibility(View.GONE);
            small_large_button.setImageResource(R.mipmap.playdetail_amplify_btn);
            if(defintionChangeDialog!=null && defintionChangeDialog.isShowing())
                defintionChangeDialog.dismiss();
        }
        playerPresenter.removeControllMsg();
        playerPresenter.sendControllMsg();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.play_btn:
                if (isPlaying) {
                    playOrPause(false);
                } else {
                    playOrPause(true);
                }
                break;
        }
        playerPresenter.removeControllMsg();
        playerPresenter.sendControllMsg();
    }

    public void setBackVisiable (boolean show) {
        if(show)
            back.setVisibility(View.VISIBLE);
        else
            back.setVisibility(View.GONE);
    }

    private void playOrPause (boolean play) {
        if(playState == 0)
            return;
        if(play) {
            playerPresenter.start();
            setPlayOrPause(play);
        }
        else {
            playerPresenter.pause();
            setPlayOrPause(play);
        }
    }

    private void setPlayOrPause(boolean play) {
        if(play) {
            isPlaying = true;
            playPause.setImageResource(R.mipmap.playdetail_pause_btn);
        }
        else {
            isPlaying = false;
            playPause.setImageResource(R.mipmap.playdetail_play_btn);
        }
    }

    @Override
    public void updateSeekbar(String currentTime, int curProgress, int curSecondProgress) {
        curTime.setText(currentTime);
        seekBar.setProgress(curProgress);
//        seekBar.setSecondaryProgress(curSecondProgress);
    }

    @Override
    public void setTotalTime(String totalTime) {
        this.totalTime.setText(totalTime);
    }

    public void setHistory (long iBreakTime, boolean isPlayFromBreak) {
        this.iBreakTime = iBreakTime;
    }

    @Override
    public void onPrepare() {
        super.onPrepare();
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingShow(false);
                setPlayOrPause(true);
                if(playerType==2 && isStartPlaySDK) {
                    isStartPlaySDK = false;
                    playerPresenter.getPPTVDefintion(context, urls, defintions);
                }
                Log.e("show", "hide prepare");
                for(int i = 0; i < getChildCount();i++){
                    if(getChildAt(i).getTag() != null && getChildAt(i).getTag().toString().equals("fail")){
                        removeViewAt(i);
                        break;
                    }
                }
                if (playerType == -1) {
                    playerPresenter.start();
                }
                if(iBreakTime>0) {
                    playerPresenter.playForBreak(iBreakTime);
                    iBreakTime = 0;
                }
                if(defintions.size()>1 && isLandscape)
                    definitionChange.setVisibility(VISIBLE);

                playerPresenter.removeControllMsg();
                playerPresenter.sendControllMsg();
            }
        });

    }

    @Override
    public void stop() {
        super.stop();
        seekBar.setSecondaryProgress(0);
        isPlayNext = false;
        clearSeek();
    }

    @Override
    public void controllDimiss() {
        controllerLayout.setVisibility(View.GONE);
    }

    @Override
    public void getDefaultDefintion(final int defintion) {
        Observable.just("")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        defaulDefintion = defintion;
                        if(playerType==2) {
                            if(defintion != -1) {
                                playerPresenter.changeDefinition(Integer.parseInt(urls.get(defintion)));
                                currDefintionPos = defintion;
                            }
                            else {
                                playerPresenter.changeDefinition(Integer.parseInt(urls.get(0)));
                            }
                        } else {
                            if(urls.size()==0){
                                showError();
                                return;
                            }
                            if(defintion != -1) {
                                startPlay(urls.get(defintion));
                                currDefintionPos = defintion;
                            }
                            else {
                                startPlay(urls.get(0));
                            }
                        }
                        if (currDefintionPos >= defintions.size()) {
                            currDefintionPos = defintions.size() - 1;
                        }
                        definitionChange.setText(defintions.get(currDefintionPos));
                        if(defintions.size()>1 && isLandscape)
                            definitionChange.setVisibility(View.VISIBLE);
                        else
                            definitionChange.setVisibility(View.GONE);
                    }
                });

        if(defintionChangeDialog != null)
            defintionChangeDialog.setDatas(defintion, defintions);
    }

    private String url;
    @Override
    public void startPlay(String url) {
        this.url = url;
        if(playerType == 0) {
            if(type != null && "m3u8".contains(type)) {
                if(isCrack) {
                    startPreLoading(url, playBean.getProgramId(), playBean.getmId());
                }
                else
                    startChangePath(url);
            }
            else {
                super.startPlay(url);
            }
            isCrack = false;
        }
        else {
            super.startPlay(url);
        }

//        super.startPlay(url);
    }

    public int getVideoType() {
        return playerType;
    }

    private void startPreLoading(String url, String programId, String subId) {
        Intent intent = new Intent(context, CachingWhilePlayingService.class);
        intent.putExtra(CachingWhilePlayingService.ACTION_KEY,
                CachingWhilePlayingService.ACTION_PRE_LOADING_START);
        intent.putExtra("url", url);
        intent.putExtra("programId", programId);
        intent.putExtra("subId", subId);
        context.startService(intent);
    }

    private void startChangePath(String url) {
        Intent intent = new Intent(context, CachingWhilePlayingService.class);
        intent.putExtra(
                CachingWhilePlayingService.ACTION_KEY,
                CachingWhilePlayingService.ACTION_PRE_LOADING_CHANGE_SOURCE_START);
        intent.putExtra("url", url);
        context.startService(intent);
    }

    public void stopPreLoading() {
        if(playerType != 0)
            return;
        Intent intent = new Intent(context, CachingWhilePlayingService.class);
        intent.putExtra(CachingWhilePlayingService.ACTION_KEY,
                CachingWhilePlayingService.ACTION_PRE_LOADING_STOP);
        context.startService(intent);
    }

    /**
     * 使用CachingWhilePlaying服务完成后，会回调该方法
     *
     * @param preLoadingResultInfo
     */
    public void onEvent(PreLoadingResultInfo preLoadingResultInfo) {
        super.startPlay(preLoadingResultInfo.path);
    }

    public void onEvent(final BufferedPositionInfo bufferedPositionInfo) {
        Observable.just("")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String> (){

                    @Override
                    public void call(String s) {
                        if(bufferedPositionInfo.getCurBufferedPosition()<=0 || playerPresenter.getTotalTime()<=0)
                            return;
                        int tmp = (int) (bufferedPositionInfo.getCurBufferedPosition()*100/playerPresenter.getTotalTime());
                        if (tmp > 100) {
                            seekBar.setSecondaryProgress(100);
                        } else {
                            seekBar.setSecondaryProgress(tmp);
                        }
                    }
                }) ;
    }

    public void setCompleteListener(PlayCompleteListener completeListener){
        this.completeListener = completeListener;
    }

    private void clearSeek() {
        totalTime.setText("00:00");
        updateSeekbar("00:00", 0, 0);
    }

    @Override
    public int getSeekBarMax() {
        if(seekBar == null)
            return 100;
        return seekBar.getMax();
    }

    @Override
    public void onError(IPlayer mp, int what, int extra, String var) {
        showError();
        sendPlayErrorLog("playfailed");
        if(mp instanceof IJKPlayer){

        }else if(mp instanceof SohuPlayer){

        }else if(mp instanceof PPTVPlayer){

        }
    }

    @Override
    public void isPlayNext(boolean isPlayNext) {
        this. isPlayNext = isPlayNext;
    }

    @Override
    public void sendPlayErrorLog(String errorType) {
        if(playBean != null)
            playerPresenter.playErrorLog(playBean.getProgramId(), playBean.getmId(), errorType);
    }

    private boolean isStopFromNetChange;//是否因为网络变化停止播放

    public void setIsStopFromNetChange (boolean isStopFromNetChange) {
        this.isStopFromNetChange = isStopFromNetChange;
    }
    @Override
    public void onInfo(IPlayer mp, int what, final int extra) {
        switch (what) {
            case IPlayer.MEDIA_INFO_BUFFERING_START:
                if(!isPlayCach)
                    loadingShow(true);
                Log.e("show", "show oninfo");
                break;
            case IPlayer.MEDIA_INFO_BUFFERING_END:
                loadingShow(false);
                Log.e("show", "hide oninfo");
                break;
            case IPlayer.MEDIA_INFO_AD_START:
                Observable.just("").observeOn(AndroidSchedulers.mainThread()).subscribe((s) -> {
                    adTimeCountDownTextView.setVisibility(VISIBLE);
                    loadingShow(false);
                    isAd = true;
                    showAllControll(false);
                });
                break;
            case IPlayer.MEDIA_INFO_AD_FINISH:
                adTimeCountDownTextView.setVisibility(INVISIBLE);
                if(isStopFromNetChange)
                    isStopFromNetChange = false;
                else
                    loadingShow(true);
                isAd = false;
                showAllControll(true);
                break;
            case IPlayer.MEDIA_INFO_AD_COUNTDOWN:
                post(new Runnable() {
                    @Override
                    public void run() {
                        adTimeCountDownTextView.setText("广告 " + extra + " 秒");
                    }
                });
                break;
            case IPlayer.MEDIA_INFO_BUFFERING_PERCENT:
                if (extra == 100) {
                    loadingShow(false);
                    playerPresenter.sendSeekMsg();
                }
                break;
            default:
                break;
        }
    }

    public boolean isAd() {
        return isAd;
    }

    public void isShowDefinitionChange (boolean isShowDefinitionChange) {
        if(isShowDefinitionChange) {
            definitionChange.setVisibility(View.VISIBLE);
        }
        else {
            definitionChange.setVisibility(View.GONE);
        }
    }

    private boolean isCrack;
    private String type;

    @Override
    public void onCrackComplete(CrackResult result) {
        isCrack = true;
        type = result.type;
        removeError();
        playerPresenter.getDefaultDefintion(context, result, urls, defintions);
    }
    @Override
    public void onCrackFailed() {
        /*if(!TextUtils.isEmpty(currUrl) && !currUrl.equals(lastUrl))
            return;*/
        sendPlayErrorLog("crack");
        showError();
    }

    public void removeError(){
        Observable.just("")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        for(int i = 0; i < getChildCount();i++){
                            if(getChildAt(i).getTag() != null && getChildAt(i).getTag().toString().equals("fail")){
                                removeViewAt(i);
                                break;
                            }
                        }
                    }
                });
    }

    public void showError(){
        if(playState == 1)
            return;
        Observable.just("")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        for(int i = 0; i < getChildCount();i++){
                            if(getChildAt(i).getTag() != null && getChildAt(i).getTag().toString().equals("fail")){
                                removeViewAt(i);
                                break;
                            }
                        }
                        loadingShow(false);
                        Log.e("show", "hide error");
                        ImageView iv = new ImageView(context);
                        iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        try {
                            if (!NetworkUtil.isConnectedByState(BaseApp.getContext())) {
                                GlideProxy.getInstance().loadResImage2(context,R.mipmap.wifi_fail,iv);
                            }else {
                                GlideProxy.getInstance().loadResImage2(context,R.mipmap.load_fail,iv);

                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        iv.setTag("fail");
                        iv.setOnClickListener(new NoDoubleClickListener() {
                            @Override
                            public void onNoDoubleClick(View view) {
                                if(playBean != null){
                                    removeViewAt(getChildCount() - 1);
                                    startPlay(playBean);
                                }

                            }
                        });
                        addView(iv);
                    }
                });
    }

    private ImageView netPic;
    private ImageView netPic2;
    /**
     *移动网络时取消观看显示相应的图片
     */
    public void showNetPic (String picUrl) {
        if(netPic == null) {
            OnClickListener onClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    BusProvider.getInstance().post("showNetChangeDialog", "");
                }
            };
            netPic = new ImageView(context);
            netPic.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            GlideProxy.getInstance().loadLetvImage(context, picUrl, netPic);
            netPic.setTag("netchange");

            netPic2 = new ImageView(context);
            RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params2.addRule(RelativeLayout.CENTER_IN_PARENT);
            netPic2.setLayoutParams(params2);
            netPic2.setImageResource(R.mipmap.hot_play_play_btn);
            netPic.setOnClickListener(onClickListener);
            netPic2.setOnClickListener(onClickListener);
        }
        removeView(netPic);
        removeView(netPic2);
        addView(netPic);
        addView(netPic2);
//        netPic.setVisibility(View.VISIBLE);
//        netPic2.setVisibility(View.VISIBLE);
    }

    public void hideNetPic () {
        Observable.just("")
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        if(netPic != null) {
                            removeView(netPic);
                            removeView(netPic2);
//                            netPic.setVisibility(View.GONE);
//                            netPic2.setVisibility(View.GONE);
                        }
                    }
                });
    }

    @Override
    public void onComplation() {
        super.onComplation();
        setPlayOrPause(false);
        playerPresenter.removeSeekMsg();
        Observable.just("")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        clearSeek();
                        playerPresenter.removeControllMsg();
                        playerPresenter.sendControllMsg();
                        if(isPlayNext) {
                            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_A,new NextPlay());
                            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_A, new MediaPlayComplete());
                            if(completeListener != null){
                                completeListener.playComplete();
                            }
                            isPlayNext = false;
                        }
                    }
                });

        if(isPlayCach && completeListener != null) {
            completeListener.playComplete();
        }
    }

    private int progress;
    private boolean fromUser;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        this.progress = progress;
        this.fromUser = fromUser;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        playerPresenter.removeSeekMsg();
        playerPresenter.removeControllMsg();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        playerPresenter.sendSeekMsg();
        playerPresenter.sendControllMsg();
        playerPresenter.onProgressChanged(progress, fromUser);
        if(fromUser && !isPlaying)
            playOrPause(false);
        fromUser = false;
    }

    @OnClick(R.id.lock)
    void lock () {
        lockClick();
        playerPresenter.removeControllMsg();
        playerPresenter.sendControllMsg();
    }
    @OnClick( R.id.definition_change)
    public void definitionChange() {
        if(defintions.size()<=0)
            return;
        initDefintionDialog();
        defintionChangeDialog.show();
        controllerLayout.setVisibility(View.GONE);

        playerPresenter.removeControllMsg();
        playerPresenter.sendControllMsg();
    }

    @OnClick( R.id.btnBack)
    public void back () {
        if(isLandscape) {
            handleFullHalfScrren(ShareElement.PORTRAIT);
            playerPresenter.removeControllMsg();
            playerPresenter.sendControllMsg();
        }
        else {
            if(onBackListener!=null)
                onBackListener.onBack();
        }
    }

    @OnClick(R.id.player_next_btn)
    public void playNext() {
        BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_A,new NextPlay());

        playerPresenter.removeControllMsg();
        playerPresenter.sendControllMsg();
    }

    public void skipAd() {
        playerPresenter.skipAd();
    }

    private void initDefintionDialog () {
        if(defintionChangeDialog == null) {
            defintionChangeDialog = new DefintionChangeDialog(context, R.style.programdialog);
            defintionChangeDialog.setDatas(defaulDefintion, defintions);
            defintionChangeDialog.setOnDefintionChangeListener(new DefintionChangeDialog.OnDefintionChangeListener() {
                @Override
                public void onDefintionChange(int postion) {
                    defintionChangeDialog.dismiss();
                    currDefintionPos = postion;
                    definitionChange.setText(defintions.get(postion));
                    loadingShow(true);
                    if(playerType == 2) {
                        playerPresenter.changeDefinition(Integer.parseInt(urls.get(postion)));
                    }
                    else {
                        stop();
                        if( playerPresenter.getBreakTime()>0)
                            iBreakTime = playerPresenter.getBreakTime();
                        startPlay(urls.get(currDefintionPos));
                    }

                    Log.e("show", "defintionchange");
                }
            });
        }
    }

    private void lockClick () {
        if(isLock) {
            bottom.setVisibility(View.VISIBLE);
            top.setVisibility(View.VISIBLE);
            if(defintions.size()>1)
                definitionChange.setVisibility(View.VISIBLE);
            lock.setImageResource(R.mipmap.unlocked);
        }
        else {
            bottom.setVisibility(View.GONE);
            top.setVisibility(View.GONE);
            definitionChange.setVisibility(View.GONE);
            lock.setImageResource(R.mipmap.lock);
        }
        isLock = !isLock;
        myOnGestureListner.isLocked(isLock);
    }

    /**
     * 获取断点时间
     */
    public long getBreakTime () {
        return playerPresenter.getBreakTime();
    }

    /**
     * onResume调的方法
     */
    public void playResume () {
        startPlay(playBean);
        /*try {
            if(urls.size()>0 && playerType == 0)
                startPlay(urls.get(currDefintionPos));
            else
                startPlay(playBean);
        }catch (Exception ex){
            ex.printStackTrace();
        }*/
    }

    /**
     * 是否显示next按钮
     * @param isShowNext
     */
    public void isShowNext (boolean isShowNext) {
        this.isShowNext = isShowNext;
        if(isLandscape) {
            if(isShowNext)
                next.setVisibility(View.VISIBLE);
            else
                next.setVisibility(View.GONE);
        }
    }

    private boolean isPlayCach;

    // 缓存播放时隐藏控件
    public void setCachePlay(boolean flag) {
        isPlayCach = flag;
        if (flag) {
            next.setVisibility(GONE);
            small_large_button.setVisibility(INVISIBLE);
            definitionChange.setVisibility(GONE);
        } else {
            back.setVisibility(VISIBLE);
            if(isShowNext)
                next.setVisibility(View.VISIBLE);
            small_large_button.setVisibility(INVISIBLE);
            if(defintions.size()>1)
                definitionChange.setVisibility(VISIBLE);
        }
    }
    // 缓存播放时隐藏控件
    public void setBackPlay(boolean flag) {
        if (flag) {
            back.setVisibility(VISIBLE);
        } else {
            back.setVisibility(GONE);
          //  small_large_button.setVisibility(GONE);
        }
    }
    // 缓存播放时隐藏控件
    public void setSLPlay(boolean flag) {
        if (flag) {
            small_large_button.setVisibility(VISIBLE);
        } else {
            small_large_button.setVisibility(GONE);
        }
    }

    private void showAllControll(boolean show) {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(show) {
                    controllerLayout.setVisibility(View.VISIBLE);
                    playerPresenter.sendControllMsg();
                }
                else {
                    controllerLayout.setVisibility(View.GONE);
                    playerPresenter.removeControllMsg();
                    playerPresenter.sendControllMsg();
                }
            }
        });
    }

    @Override
    public void onSingleUp() {
        if(controllerLayout.isShown()){
            showAllControll(false);
        }
        else if(!isAd){
            showAllControll(true);
        }
    }

    @Override
    public void down(MotionEvent e) {
        current = (int) e.getX();
        cp = (int) playerPresenter.getBreakTime();
        totaltime = playerPresenter.getTotalTime();
    }

    private int current;
    private int cp;
    public int seekpos;
    public int totaltime;
    public boolean isSeek;

    @Override
    public void scroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if(playState==0 || isAd)
            return;
        float mOldX = e1.getX();
        int x = (int) e2.getRawX();


        if(Math.abs(distanceX) > Math.abs(distanceY) && seekpos<totaltime-1) {
            gesturelayout.setVisibility(View.VISIBLE);
            if(current<x) {
                gesturelayoutBg.setImageResource(R.mipmap.fastforward);

                if(mOldX>x) {
                    seekpos = (int) (cp - Math.abs((mOldX - x) / 5
                            / getWidth() * totaltime));
                }
                else {
                    seekpos = (int) (cp + Math.abs((mOldX - x) / 5
                            / getWidth() * totaltime));
                }
            }
            else if(current>x) {
                gesturelayoutBg.setImageResource(R.mipmap.fastreverse);
                if(mOldX>x) {
                    seekpos = (int) (cp - Math.abs((mOldX - x) / 5
                            / getWidth() * totaltime));
                }
                else {
                    seekpos = (int) (cp + Math.abs((mOldX - x) / 5
                            / getWidth() * totaltime));
                }
            }
            gestureDegree.setText(DateUtils.formatTime((int) seekpos));
            isSeek = true;
        }

    }

    @Override
    public void onDoubleTap() {
        if(isLock)
            return;
        if (isPlaying) {
            playOrPause(false);
        } else {
            playOrPause(true);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        super.onTouch(v, event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if(isSeek) {
                    gesturelayout.setVisibility(View.GONE);
                    playerPresenter.seekTo(seekpos);
                    updateSeekbar(DateUtils.formatTime((int) seekpos), seekpos * 100/totaltime, 0);
                    isSeek = false;
                }
                break;
        }
        return true;
    }

    @Override
    public void release() {
        super.release();
        if(animator != null)
            animator.cancel();
//        rotateAnimation.cancel();
        stopPreLoading();
    }

    public interface OnBackListener {
        void onBack();
    }

    private OnBackListener onBackListener;
    public void setOnBackListener(OnBackListener onBackListener){
        this.onBackListener = onBackListener;
    }
}
