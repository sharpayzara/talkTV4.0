package com.sumavision.talktv2.videoplayer.view;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.ShareElement;
import com.sumavision.talktv2.videoplayer.IPlayerClient;
import com.sumavision.talktv2.videoplayer.PlayBean;
import com.sumavision.talktv2.videoplayer.iview.IBasePlayerView;
import com.sumavision.talktv2.videoplayer.presenter.BasePlayerPresenter;
import com.sumavision.talktv2.videoplayer.utils.MyOnGestureListner;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhangysiu on 2016/6/8.
 */
public abstract class BaseVideoView<T extends BasePlayerPresenter> extends RelativeLayout implements IBasePlayerView, View.OnTouchListener , View.OnClickListener {

    Context context;
    T playerPresenter;
    protected IPlayerClient iplayerClient;
    protected int videoHeight;
    protected int videoWidth;

    protected RelativeLayout loadingLayout;
    RelativeLayout gesturelayout;
    ImageView gesturelayoutBg;
    TextView gestureDegree;
    TextView fullTime;
//    ImageView fullBattery;
    @BindView(R.id.small_large_button)
    ImageButton small_large_button;
    public boolean isLandscape = false; //调用者的当前屏幕状态
    protected int landscapeState = ShareElement.PORTRAIT;//横竖屏状态  0--竖屏； 1--横屏正向； 2--横屏反向
    protected int screenAngleState = 0;//屏幕角度状态 0--竖屏； 1--横屏正向； 2--横屏反向
    protected boolean sheildScreenAutoRotate;//屏蔽屏幕自动旋转
    protected boolean isPlaying = true; // 当前是否正在播放
    public boolean isLock = false;//是否锁屏
    protected int playState = 0;//播放状态：0--停止、1-- 播放中、2--暂停
    protected final int FULL = 001441;
    protected final int REVERSIFULL = 48547;
    protected final int HALF = 045656;
    public  int playerType; // 播放器类型
    public int videoType; //长视频还是自媒体
    private GestureDetector mGestureDetector;
    private AudioManager audioManager;
    private Window window;
    private float mBrightness;
    private SensorManager sensorManager;
    private Sensor sensor;
    private Handler handler = new Handler();
    private Handler halfOrFullHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case FULL:
                    fullHalfScrrenChange(ShareElement.LANDSCAPE);
                    break;
                case REVERSIFULL:
                    fullHalfScrrenChange(ShareElement.REVERSILANDSCAPE);
                    break;
                case HALF:
                    fullHalfScrrenChange(ShareElement.PORTRAIT);
                    break;
            }
        }
    };

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            int gravity = 0;
            try {
                gravity = Settings.System.getInt(context.getContentResolver(),
                        Settings.System.ACCELEROMETER_ROTATION);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            if (gravity != 1) {
                return;
            }
            if(isLock)
                return;
            int orientation = -1;
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            float magnitude = x*x + y*y;
            if(magnitude*4>=z*z) {
                float OneEightyOverPi = 57.29577957855f;
                float angle = (float)Math.atan2(-y, x) * OneEightyOverPi;
                orientation = 90 - (int)Math.round(angle);

                if (gravity != 1 || sheildScreenAutoRotate) {//手机未开启自动横竖屏
                    if(isLandscape) {
                        if (orientation>80&&orientation<100) {
                            screenAngleState = 1;
                            //横屏正向
                            if(landscapeState!=1)
                                SendHalfOrFullMsg(FULL);
                        }
                        else if(orientation>260&&orientation<280) {
                            screenAngleState = 2;
                            //横屏正向
                            if(landscapeState!=2)
                                SendHalfOrFullMsg(REVERSIFULL);
                        }
                    }
                }
                else {
                    if (orientation>80&&orientation<100) {
                        screenAngleState = 1;
                        //横屏正向
                        if(landscapeState!=1)
                            SendHalfOrFullMsg(FULL);
                    }
                    else if(orientation>260&&orientation<280) {
                        screenAngleState = 2;
                        //横屏反向
                        if(landscapeState!=2)
                            SendHalfOrFullMsg(REVERSIFULL);
                    }
                    else if (((orientation>350&&orientation<360)||(orientation>0&&orientation<10)||(orientation>170&&orientation<190))&&isLandscape){
                        screenAngleState = 0;
                        SendHalfOrFullMsg(HALF);
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    public void SendHalfOrFullMsg(int fullMsg) {
        if(fullMsg == FULL) {
            halfOrFullHandler.sendEmptyMessageDelayed(FULL, 1000L);
        }
        else if(fullMsg == REVERSIFULL){
            halfOrFullHandler.sendEmptyMessageDelayed(REVERSIFULL, 1000L);
        }
        else if(fullMsg == HALF){
            halfOrFullHandler.sendEmptyMessageDelayed(HALF, 1000L);
        }
    }

    public BaseVideoView(Context context) {
        super(context);
        this.context = context;
        initPresenter();
    }

    public BaseVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initPresenter();
    }

    public BaseVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initPresenter();
    }

    protected abstract void initPresenter();

    protected abstract void addViews();

    protected abstract void initView();

    protected abstract void fullHalfScrrenChange(boolean isFullScreen);

    @Override
    public void onPrepare() {
        playState = 1;
    }

    @Override
    public void onComplation() {
        playState = 0;
    }

    public void startPlay(String url) {

        loadingShow(true);
        playerPresenter.startPlay(url);

    }

    public void startPlay(PlayBean playBean) {
        loadingShow(true);
        playerPresenter.startPlay(playBean);
    }

    public  void pause(){
        playerPresenter.pause();
    }

    public void stop() {
        playState = 0;
        playerPresenter.stop();
    }
    public void resume() {
        playerPresenter.resume();
    }

    public void start() {
        playerPresenter.start();
    }

    public boolean isStop () {
        if(playState == 0) {
            return true;
        }
        return false;
    }

    public long getCurrentPosition() {
        return playerPresenter.getCurrentPosition();
    }

    public long getDuration() {
        return playerPresenter.getDuration();
    }

    public void seekTo(long pos) {
        playerPresenter.seekTo(pos);
    }

    protected  MyOnGestureListner myOnGestureListner;
    /**
     * @param tmp        播放器的使用者
     * @param playerType 播放器类型：0 IJK，1搜狐，2PPTV
     * @param videoType 1--长视频  2---自媒体
     */
    public void init(IPlayerClient tmp, int playerType, int videoType) {
        this.videoType = videoType;
        this.iplayerClient = tmp;
        this.playerType = playerType;
        playerPresenter.initPlayer(this, playerType);

        sensorManager = (SensorManager) context.getSystemService(Activity.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // 手势操作
//        View gesture = View.inflate(context, R.layout.gesture_layout, null);
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(CommonUtil.dip2px(context,80), CommonUtil.dip2px(context,50));
//        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
////        gesture.setLayoutParams(paramsGesture);
//        addView(gesture, lp);
        myOnGestureListner = new MyOnGestureListner(this);
        mGestureDetector = new GestureDetector(context, myOnGestureListner);
        mGestureDetector.setIsLongpressEnabled(false);
        audioManager = iplayerClient.getAudioManager();
        window = iplayerClient.getWindow();
        initStartVol();

        addViews();
        ButterKnife.bind(this);
        gesturelayout = (RelativeLayout) View.inflate(context, R.layout.gesture_progress_layout, null);
        gestureDegree = (TextView) gesturelayout.findViewById(R.id.progress_time);
        small_large_button = (ImageButton) findViewById(R.id.small_large_button);
//        battery = (ImageView) findViewById(R.id.battery);
//        fullBattery = (ImageView) findViewById(R.id.fullbattery);
//        time = (TextView) findViewById(R.id.timer);
        fullTime = (TextView) findViewById(R.id.time);
        gesturelayoutBg = (ImageView) gesturelayout.findViewById(R.id.progress_pic);
        small_large_button.setOnClickListener(this);
        addView(gesturelayout);

        // 半屏全屏相关
        getVideoHeight();
        LayoutParams params = (LayoutParams) getLayoutParams();
        params.height = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX, videoHeight, getResources()
                        .getDisplayMetrics());
        setLayoutParams(params);

        initView();
        getScreenMode();
        setOnTouchListener(this);
//        if(handlerThread == null) {
//            handlerThread = new HandlerThread("event handler thread",
//                    Process.THREAD_PRIORITY_BACKGROUND);
//            handlerThread.start();
//            eventHandler = new EventHandler(handlerThread.getLooper());
//        }
//        handleBattery();
    }

    public void registerSensor () {
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public void unRegisterSensor () {
        if(halfOrFullHandler.hasMessages(HALF))
            halfOrFullHandler.removeMessages(HALF);
        if(halfOrFullHandler.hasMessages(FULL))
            halfOrFullHandler.removeMessages(FULL);
        sensorManager.unregisterListener(sensorEventListener);
    }
    private void getVideoHeight() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        if (dm.widthPixels > dm.heightPixels) {
            videoHeight = (int) ((float) dm.heightPixels * 9 / 16);
            videoWidth = dm.heightPixels;
        } else {
            videoHeight = (int) ((float) dm.widthPixels * 9 / 16);
            videoWidth = dm.widthPixels;
        }
        Log.e("msg_videoheight", "" + videoWidth);
        Log.e("msg_videoWidth", "" + videoWidth);
    }

    public boolean isLandScape(){
        return isLandscape;
    }

    public void handleFullHalfScrren(int screenState) {
        sheildScreenAutoRotate = true;
        fullHalfScrrenChange(screenState);
    }

    private void fullHalfScrrenChange(int screenState) {
        if(halfOrFullHandler.hasMessages(HALF)) {
            halfOrFullHandler.removeMessages(HALF);
        }
        if(halfOrFullHandler.hasMessages(FULL)) {
            halfOrFullHandler.removeMessages(FULL);
        }
        if(halfOrFullHandler.hasMessages(REVERSIFULL)) {
            halfOrFullHandler.removeMessages(REVERSIFULL);
        }

        landscapeState = screenState;

        landscapeState = screenState;
        myOnGestureListner.isHalfScreen(screenState);
        iplayerClient.halfFullScreenSwitch(screenState);
        if (screenState == ShareElement.PORTRAIT)
            isLandscape = false;
        else
            isLandscape = true;

        LayoutParams params = (LayoutParams) getLayoutParams();
        if (isLandscape) {
            params.height = LayoutParams.MATCH_PARENT;
            fullHalfScrrenChange(true);
            WindowManager.LayoutParams lp = ((Activity)context).getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            ((Activity)context).getWindow().setAttributes(lp);
            ((Activity)context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            fullHalfScrrenChange(false);
            params.height = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_PX, videoHeight, getResources()
                            .getDisplayMetrics());

            WindowManager.LayoutParams attr = ((Activity)context).getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ((Activity)context).getWindow().setAttributes(attr);
            ((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setLayoutParams(params);
    }

    int mVolume;
    int streamMaxVolume, streamNowVolume;

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    public void onVolumeSlide(float percent) {
        if (mVolume == -1) {
            mVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume < 0)
                mVolume = 0;

        }
        // 显示
        gesturelayoutBg.setImageResource(R.mipmap.sound);
        gesturelayout.setVisibility(View.VISIBLE);

        int index = (int) (percent * streamMaxVolume) + mVolume;
        if (index > streamMaxVolume)
            index = streamMaxVolume;
        else if (index < 0)
            index = 0;
        Log.i("new play er", "index==" + index);
        // 变更声音
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
        float index1 = index;
        gestureDegree.setText((int) (index1 / streamMaxVolume * 100) + "%");
    }

    /**
     * 滑动改变亮度
     *
     * @param percent
     */
    public void onBrightnessSlide(float percent) {
        if (mBrightness < 0) {
            mBrightness = window.getAttributes().screenBrightness;
            if (mBrightness <= 0.00f)
                mBrightness = 0.50f;
            if (mBrightness < 0.01f)
                mBrightness = 0.00f;

        }
        // 显示
        gesturelayout.setVisibility(View.VISIBLE);
        gesturelayoutBg.setImageResource(R.mipmap.light);
        /**
         * private void setCurrentActivityBrightness () {
         WindowManager.LayoutParams lp = getWindow().getAttributes();
         lp.screenBrightness = 0.5;
         getWindow().setAttributes(lp);
         }
         */
        WindowManager.LayoutParams lpa = window
                .getAttributes();
        lpa.screenBrightness = mBrightness + percent;
        if (lpa.screenBrightness > 1.0f)
            lpa.screenBrightness = 1.0f;
        else if (lpa.screenBrightness < 0.01f)
            lpa.screenBrightness = 0.01f;
        window.setAttributes(lpa);
        gestureDegree.setText((int) (lpa.screenBrightness * 100) + "%");
//        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, (int) (percent*255));
    }
    private int screenMode;
    private void getScreenMode() {
        try {
            screenMode = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setScreenMode () {
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,  screenMode);
    }

    public int videoHeight() {
        return videoHeight;
    }

    public int videoWidth() {
        return videoWidth;
    }


    public boolean onTouch(View v, MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        // 处理手势结束
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                endGesture();
                if (gesturelayout != null)
                    gesturelayout.setVisibility(View.GONE);
                break;
            case MotionEvent.ACTION_SCROLL:

                break;
        }

        return true;
    }

    /**
     * 手势结束
     */
    private void endGesture() {
        mVolume = -1;
        mBrightness = -1f;

    }

    private void initStartVol() {

        streamMaxVolume = audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        streamNowVolume = audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        int progress = streamNowVolume * 100 / streamMaxVolume;
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress * streamMaxVolume / 100, 0);
    }

    @Override
    public void setTime(String timeValue) {
//        time.setText(timeValue);
        fullTime.setText(timeValue);
    }

    @Override
    public ImageView getBattery() {
//        return fullBattery;
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.small_large_button:
                sheildScreenAutoRotate = true;
                if(landscapeState == ShareElement.PORTRAIT) {
                    handleFullHalfScrren(ShareElement.LANDSCAPE);
                }
                else
                    handleFullHalfScrren(ShareElement.PORTRAIT);
                break;
        }
    }

    public void release() {
        playerPresenter.release();
        handler.removeCallbacksAndMessages(null);
        halfOrFullHandler.removeCallbacksAndMessages(null);
    }

    protected void loadingShow(final boolean show) {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(show) {
                    loadingLayout.setVisibility(View.VISIBLE);
                }
                else {
                    loadingLayout.setVisibility(View.GONE);
                }
            }
        });
    }
}
