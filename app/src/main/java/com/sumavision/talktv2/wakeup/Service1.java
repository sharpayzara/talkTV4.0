package com.sumavision.talktv2.wakeup;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.sumavision.talktv2.util.AppUtil;

import java.util.Calendar;
import java.util.TimeZone;


/**
 * This Service is Persistent Service. Do some what you want to do here.<br/>
 *
 * Created by Mars on 12/24/15.
 */
public class Service1 extends Service{

    ScreenListener screenListener;

    @Override
    public void onCreate() {
        super.onCreate();
        //TODO do some thing what you want..

        screenListener = new ScreenListener(this);
        screenListener.begin(new ScreenListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {

            }

            @Override
            public void onScreenOff() {
                Calendar mCalendar = Calendar.getInstance();
                mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+08"));
                mCalendar.setTimeInMillis(System.currentTimeMillis());
                int mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
                int day = mCalendar.get(Calendar.DAY_OF_WEEK);
                Log.i("Service1", "day:" + day + "; mHour:" + mHour + "; Build.MODEL" + Build.MODEL);
                if (mHour >= 20 && mHour < 22 && (day == 4 || day == 6 || day == 7 || day == 1)) {
                    Intent tmp = new Intent(Service1.this, Main2Activity.class);
                    tmp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(tmp);
                }
            }

            @Override
            public void onUserPresent() {

            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
