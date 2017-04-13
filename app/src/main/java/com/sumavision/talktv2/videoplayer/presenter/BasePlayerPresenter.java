package com.sumavision.talktv2.videoplayer.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.widget.RelativeLayout;

import com.jiongbull.jlog.JLog;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.mycrack.CrackCompleteListener;
import com.sumavision.talktv2.mycrack.CrackResult;
import com.sumavision.talktv2.mycrack.ParserUtil;
import com.sumavision.talktv2.mycrack.UpdateCrackDialog;
import com.sumavision.talktv2.videoplayer.PlayBean;
import com.sumavision.talktv2.videoplayer.iview.IBasePlayerView;
import com.sumavision.talktv2.videoplayer.player.IPlayer;
import com.sumavision.talktv2.videoplayer.player.impl.IJKPlayer;
import com.sumavision.talktv2.videoplayer.player.impl.PPTVPlayer;
import com.sumavision.talktv2.videoplayer.player.impl.SohuPlayer;
import com.sumavision.talktv2.videoplayer.player.impl.SystemPlayer;
import com.sumavision.talktv2.videoplayer.utils.BatteryReceiver;
import com.tvata.p2p.P2PManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

//import com.sumavision.talktv4.videoplayer.player.impl.PPTVPlayer;

/**
 * Created by zhangyisu on 2016/6/8.
 */
public abstract class BasePlayerPresenter implements CrackCompleteListener, IPlayer.OnPreparedListener, IPlayer.OnInfoListener, IPlayer.OnErrorListener, IPlayer.OnCompletionListener {

    private static final int UPDATE_TIME = 3000;
    private IBasePlayerView playerView;
    protected int playerType;
    IPlayer player;
    public boolean isStop;
    Context context;
    ParserUtil pu;
    UpdateCrackDialog updateCrackDialog;
    String url;
    private BatteryReceiver batteryReceiver;
    private Timer timer = new Timer();
    BasePlayerPresenter(Context context, IBasePlayerView playerView) {
        this.context = context;
        this.playerView = playerView;
    }

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_TIME:
                    setTimeValue();
                    break;
            }
        }
    };

    public void initPlayer(RelativeLayout playerHolder, int playerType) {
        this.playerType = playerType;
        switch (playerType) {
            case -1:
                player = new SystemPlayer(context, playerHolder);
                break;
            case 0:
                player = new IJKPlayer(context, playerHolder);
                JLog.e("baidu player init");
                break;
            case 1:
                player = new SohuPlayer(context, playerHolder);
                JLog.e("sohu player init");
                break;
            case 2:
                player = new PPTVPlayer(context, playerHolder);
                JLog.e("pptv player init");
                break;
//            case 3:
//                player = new IJKPlayer(context, playerHolder);
//                JLog.e("ijk player init");
//                break;

        }
        player.init();
        setPlayerListener();
    }

    public abstract void startPlay(String url);

    public abstract void startPlay(PlayBean playBean);

    public void initBatteryAndTime() {
       // handleBattery();
        startTimer();
    }

    public void stopBatteryAndTime() {
//        try {
//            context.unregisterReceiver(batteryReceiver);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        timer.cancel();
        timetask.cancel();
        if(mHandler != null)
            mHandler.removeMessages(UPDATE_TIME);
    }

    // 电池
    protected void handleBattery() {
        IntentFilter intentFilter = new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED);
        intentFilter.setPriority(Integer.MAX_VALUE);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
//        if(batteryReceiver == null){
//            batteryReceiver = new BatteryReceiver(playerView.getBattery());
//        }
//        try{
//          //  context.registerReceiver(batteryReceiver, intentFilter);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    private TimerTask timetask = new TimerTask() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(UPDATE_TIME);
        }
    };

    // 时间
    protected void startTimer() {
        if (timer != null) {
            try {
                timer.schedule(timetask, 0, 60 * 1000);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    protected void setPlayerListener() {
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnInfoListener(this);
        player.setOnErrorListener(this);
    }

    public void start() {
        player.start();
    }

    public void stop() {
        player.stop();
    }

    public void pause() {
        player.pause();
    }

    public void skipAd() {
        player.skipAd();
    }

    public void release() {
            try{
               // context.unregisterReceiver(batteryReceiver);
            }catch (Exception e){
                e.printStackTrace();
            }
        mHandler.removeCallbacksAndMessages(null);
        player.stop();
        player.release();
        stopBatteryAndTime();
    }

    public void resume() {
        player.resume();
    }

    public long getCurrentPosition() {
        return player.getCurrentPosition();
    }

    public long getDuration() {
        return player.getDuration();
    }

    public void seekTo(long pos) {
        player.seekTo(pos);
    }

    @Override
    public void onJarDownLoading(int process) {
        Observable.just(""+process).observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Action1<String>() {
            @Override
            public void call(String str) {
            updateCrackDialog.onProgressUpdate(str);
            }
        });
    }

    @Override
    public void onCrackComplete(final CrackResult result) {

        if (result.type == null) {
            Log.e("BasePlayerPresenter", "type is null");
            playerView.onCrackFailed();
            return;
        }
        if (result.type.equals("updatePlugins")) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (updateCrackDialog != null) {
                        updateCrackDialog.dismiss();
                    }
                    updateCrackDialog = new UpdateCrackDialog(context, R.style.UpdateDialog);
//            if (!isFinishing()) {
                    updateCrackDialog.show();
                    updateCrackDialog.setCancelable(false);
                }
            });

        } else if (result.type.equals("complete")) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (updateCrackDialog != null) {
                        updateCrackDialog.dismiss();
                    }
                }
            });
//            startPlay(url);
                PlayBean pb = new PlayBean();
                pb.setUrl(url);
                startPlay(pb);

        } else {
            Log.i(this.getClass().getName(), "破解完成，url=" + result.path);

            Observable.just("")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            try {
//                JLog.e("p2p原始地址......."+result.path);
                                if (playType == 1) {
                                    if (!TextUtils.isEmpty(result.path)
                                            && !(Build.MANUFACTURER.equals("Xiaomi") && Build.VERSION.SDK_INT == 23)
                                            && !(Build.MANUFACTURER.equals("LENOVO") || Build.VERSION.SDK_INT == 23) && (result.path.contains("dlive.tvfan.cn") || result.path.contains("dlivec.tvfan.cn"))) {
                                        result.path = getMobilePlayUrl(result.path);
                                        JLog.e("p2p正常工作了......." + result.path);
                                    }
                                    player.setDataSource(result.path);
                                    player.start();
                                } else {
                                    if (result.path.contains("http://dvod.tvfan.cn") || result.path.contains("http://letvvod.tvfan.cn")) {
                                        result.path = getVodP2pPlayUrl(result.path);
                                        Log.i("BasePlayerPresenter", "点播p2p:" + result.path);
                                        player.setDataSource(result.path);
                                        player.start();
                                    } else {
                                        if(isStop != true){
                                            playerView.onCrackComplete(result);
                                        }
                                        isStop = false;
                                    }

                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

        }

    }

    @Override
    public void onCrackFailed(HashMap<String, String> arg0) {
        playerView.onCrackFailed();
    }

    @Override
    public void onCompletion(IPlayer mp) {
        playerView.onComplation();
    }

    @Override
    public boolean onInfo(IPlayer mp, int what, int extra) {
        playerView.onInfo(mp, what, extra);
        return true;
    }

    @Override
    public void onPrepared(IPlayer mp) {
    }

    private void setTimeValue() {
        Time time = new Time();
        time.setToNow();
        int hour = time.hour;
        int minute = time.minute;
        StringBuilder timeValue = new StringBuilder();
        if (hour < 10) {
            timeValue.append(0);
        }
        timeValue.append(hour).append(":");
        if (minute < 10) {
            timeValue.append(0);
        }
        timeValue.append(minute);
        playerView.setTime(timeValue.toString());
    }

//    class MyOnSubscribe implements Observable.OnSubscribe<CrackResult> {
//
//
//        @Override
//        public void call(Subscriber<? super CrackResult> subscriber) {
//            subscriber.onNext();
//        }
//    }

    public static P2PManager p2pManager = null;
    private Handler handler;
    public int playType;//这是标识当前是直播还是点播的常量,0为点播,1为直播

    public void startP2PService(int playType) {
        this.playType = playType;
//        if (Build.MANUFACTURER.equals("Xiaomi") && Build.VERSION.SDK_INT == 23) {
//            return;
//        }
        if (P2PManager.get() == null) {
            //测试用zone
//            String zone = "http://103.244.165.201:4000/p2p/test/webvodpeer.ini";
            String zone = "http://103.244.165.201:4000/p2p/webvodpeer.ini";
       /* //正式用
        String zone = "http://103.244.165.191:4000/p2p/webvodpeer.ini";*/
            String native_dir = context.getCacheDir() + "p_pie_1";

            Log.d("App", "startP2PService at " + native_dir);

            File ndir = new File(native_dir);

            if (ndir.exists() == false){
                ndir.mkdir();
            }else{
                File file = new File(native_dir+"/vodpeer.e");
            }
            handler=new Handler();
            p2pManager = P2PManager.init(native_dir, zone);
            p2pManager.setErrorEvent(new Runnable() {

                @Override
                public void run() {
                    Log.d("App", "p2p 00000000000000000      manager to run error event");

                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            String message = p2pManager.getMessage();

                            // Toast.makeText(TvAppActivity.this,
                            // "Start P2P Failed: " + message, Toast.LENGTH_LONG
                            // ).show();

                        }
                    });
                }

            });

            // manager.setSuccessEvent( new Runnable() {
            //
            // @Override
            // public void run() {
            // //manager.getHttpPort();
            // }} );

            // p2pManager.start();

            Log.d("App", "p2pManager started!");
            p2pManager.start();

            P2PManager.get().set_m3u8_endstring("?");
            P2PManager.get().set_ts_endstring("?");
            P2PManager.get().set_playtype_string("1");
        }else{
            Log.i("BasePlayerPresenter", "p2pManager is not null");
            p2pManager = P2PManager.get();

        }

    }

    public static String getMobilePlayUrl(String url) {
        // if ( nop2p == true) return url;
        Log.d("App" , "use new url : " + url );

        if (p2pManager != null && url!=null) {
//			if (url.contains("http://") == false)
//				return url;
//			if (url.contains("playlist.m3u8") == false)
//				return url;

            // if ( HomeActivity.p2pManager.isRunning() )
            {
                String nurl = p2pManager.getPlayedUrl(url).replace("24188",
                        "14188");
                return nurl;
            }
            // return url;
        }
        return null;
    }

    public String getVodP2pPlayUrl(String url) {
        if (p2pManager != null && url != null) {
            String nurl = p2pManager.getVodPlayedUrl(url).replace("24188", "14188");
            Log.i("BasePlayerPresenter", "getVodP2pPlayUrl: " + nurl);
            return nurl;
        }
        return null;
    }

}
