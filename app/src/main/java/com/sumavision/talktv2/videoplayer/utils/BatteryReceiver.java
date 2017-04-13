package com.sumavision.talktv2.videoplayer.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.ImageView;

import com.sumavision.talktv2.R;

/**
 * 电量展示广播
 *
 * @author suma-hpb
 */
public class BatteryReceiver extends BroadcastReceiver {
    ImageView batteryImageView;
    AnimationDrawable anim;

    public BatteryReceiver(ImageView batteryImageView) {
        this.batteryImageView = batteryImageView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        int level = intent.getIntExtra("level", 0);
        int status = intent.getIntExtra("status",
                BatteryManager.BATTERY_HEALTH_UNKNOWN);

        if (Intent.ACTION_POWER_DISCONNECTED.equals(intent.getAction())) {
            if (anim != null && anim.isRunning()) {
                Log.i("BatteryReceiver", "amim stop");
                anim.stop();
            }
        }

        if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
            batteryImageView.setBackgroundResource(R.drawable.charging);
            anim = (AnimationDrawable) batteryImageView.getBackground();
            anim.start();
        } else {
            Log.i("BatteryReceiver", "BatteryManager status:" + status);
            if (anim != null && anim.isRunning()) {
                Log.i("BatteryReceiver", "amim stop");
                anim.stop();
            }
            if (level >= 90) {
                batteryImageView.setBackgroundResource(R.mipmap.battery_100);
            } else if (level >= 70 && level < 90) {
                batteryImageView.setBackgroundResource(R.mipmap.battery_80);
            } else if (level >= 40 && level < 70) {
                batteryImageView.setBackgroundResource(R.mipmap.battery_60);
            } else if (level >= 20 && level < 40) {
                batteryImageView.setBackgroundResource(R.mipmap.battery_40);
            } else if (level < 20) {
                batteryImageView.setBackgroundResource(R.mipmap.battery_20);
            }
        }
//		if (!batteryImageView.isShown()) {
//			batteryImageView.setVisibility(View.VISIBLE);
//		}
//        context.unregisterReceiver(this);

    }
}
