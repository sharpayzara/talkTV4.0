package com.sumavision.talktv2.wakeup;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.sumavision.talktv2.R;
import com.umeng.analytics.MobclickAgent;

public class Main2Activity extends Activity {

    private ScreenListener l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_capture);

        Log.i("whiteActivity2", "我已经启动了！！！！！！！！！！！！！！！");
        //you have to start the service once.
        //需要一个随即的存活时间
        l = new ScreenListener(this);
        l.begin(new ScreenListener.ScreenStateListener() {

            @Override
            public void onUserPresent() {
                Log.e("onUserPresent", "onUserPresent");
//                l.unregisterListener();
//                MobclickAgent.onKillProcess(whiteActivity2.this);
//                finish();
//                android.os.Process.killProcess(android.os.Process.myPid());
            }

            @Override
            public void onScreenOn() {
                Log.e("onScreenOn", "onScreenOn");
                finish();
            }

            @Override
            public void onScreenOff() {


            }
        });

    }

    public void onResume() {
        Log.i("whiteActivity2", "调用 onResume()！！！！！！！！！！！！！！！");
        super.onResume();
//        MobclickAgent.setDebugMode(true);
        MobclickAgent.onResume(this);
    }

    public void onPause() {

        Log.i("whiteActivity2", "调用 onPause()！！！！！！！！！！！！！！！");

        super.onPause();
        MobclickAgent.onPause(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        l.unregisterListener();
    }
}
