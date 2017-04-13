package com.sumavision.talktv2.wakeup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.sumavision.talktv2.R;

/**
 *
 * Created by Mars on 12/24/15.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_capture);

        //you have to start the service once.
        startService(new Intent(MainActivity.this, Service1.class));
    }
}
