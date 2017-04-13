package com.sumavision;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;

import com.sumavision.talktv2.ui.activity.LoginActivity;

/**
 * Created by sharpay on 16-10-18.
 */
public class TestSubject extends ActivityInstrumentationTestCase2<LoginActivity> {  //ApplicationTestCase
    private static final String LOG_TAG = "test";
    private LoginActivity loginActivity;
    private Context ctx;

    public TestSubject() {
        super(LoginActivity.class);
    }
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ctx = getActivity().getApplicationContext();
        loginActivity = getActivity();
    }

    public void testShow(){
        assertEquals("Hello Android", "Hello Android");
    }
}
