package com.sumavision.talktv2.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.entity.HomeRecommendItem;
import com.sumavision.talktv2.ui.activity.Game37WanActivity;
import com.sumavision.talktv2.ui.activity.WeBADActivity;
import com.sumavision.talktv2.ui.activity.YsqActivity;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.CommonUtil;
import com.sumavision.talktv2.util.NoDoubleClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
*自定义banner
        */
@SuppressLint("HandlerLeak")
public class MyADbannerView extends FrameLayout {
    private Context mContext;
    private final static boolean isAutoPlay = true;
    private List<HomeRecommendItem> results;
    private List<MyADView> imageViewsList;
    private LinearLayout mLinearLayout;

    private ViewPager mViewPager;
    private int currentItem  = 0;
    private ScheduledExecutorService scheduledExecutorService;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mViewPager.setCurrentItem(currentItem);
        }

    };
    public MyADbannerView(Context context) {
        this(context,null);
        mContext = context;

    }
    public MyADbannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
        initUI(mContext);
        if(!(results.size()<=0))
        {
            setImageUris(results);
        }

        if(isAutoPlay){
            startPlay();
        }
    }
    public MyADbannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);


    }
    private void initUI(Context context){
        imageViewsList = new ArrayList<MyADView>();
        results=new ArrayList<HomeRecommendItem>();
        LayoutInflater.from(context).inflate(R.layout.item_rec_myadbanner, this, true);
        mLinearLayout=(LinearLayout)findViewById(R.id.linearlayout_rec_mybanner);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_rec_mybanner);
        RelativeLayout rlt = (RelativeLayout)findViewById(R.id.rlt);
        ViewGroup.LayoutParams params4 = rlt.getLayoutParams();
        params4.height = CommonUtil.dip2px(mContext,48);
        rlt.setLayoutParams(params4);
    }

    /**
     * 设置图片地址的集合
     * @param results
     */
    public void setImageUris(final List<HomeRecommendItem> results)
    {
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(CommonUtil.dip2px(mContext,6), CommonUtil.dip2px(mContext,6));
        lp.setMargins(CommonUtil.dip2px(mContext,5),0,0,0);
        lp.gravity= Gravity.BOTTOM;
        for(int i=0;i<results.size();i++){
            MyADView myADView=new MyADView(mContext);
            myADView.setADImageShow(results.get(i).img);
            myADView.setAdClassify(results.get(i).name);
            myADView.setAdInfo(results.get(i).brtxt);
            myADView.setClickable(true);
            final int finalI = i;
            myADView.setOnClick(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    switch (results.get(finalI).action){
                        case "hub":
                            //跳转到影视圈
                            Intent ysqIntent = new Intent(mContext, YsqActivity.class);
                            ysqIntent.setFlags(AppGlobalConsts.ITEMPOSITION);
                            ysqIntent.putExtra("itemid",results.get(finalI).id);
                            AppGlobalConsts.ISFROMHOME = true;
                            mContext.startActivity(ysqIntent);
                            break;
                        case "ad"://跳转到广告
                            String[] newID =results.get(finalI).id.trim().split(",");
                            switch (newID[0]){
                                case "web":
                                    Intent intent5 = new Intent(mContext, WeBADActivity.class);
                                    intent5.putExtra("url",newID[1]);
                                    mContext.startActivity(intent5);
                                    break;
                                case "h5":
                                    Intent intent6 = new Intent(mContext, Game37WanActivity.class);
                                    intent6.putExtra("url",newID[1]);
                                    mContext.startActivity(intent6);
                                    break;
                                case "duiba":
                                    BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_ENTERDUIBA,newID[1]);
                                    AppGlobalConsts.ISLOGINDUIBA = true;
                                    break;
                            }
                            break;
                        case "活动":
                            break;
                    }
                }
            });
            imageViewsList.add(myADView);
        }
        mViewPager.setFocusable(true);
        mViewPager.setAdapter(new MyPagerAdapter());
        mViewPager.setOnPageChangeListener(new MyPageChangeListener());
    }


    private void startPlay(){
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 1000, 8000, TimeUnit.MILLISECONDS);
    }

    @SuppressWarnings("unused")
    private void stopPlay(){
        scheduledExecutorService.shutdown();
    }


    private class MyPagerAdapter  extends PagerAdapter {

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager)container).removeView(imageViewsList.get(position));

        }

        @Override
        public Object instantiateItem(View container, int position) {


            ((ViewPager)container).addView(imageViewsList.get(position));
            return imageViewsList.get(position);
        }

        @Override
        public int getCount() {
            return imageViewsList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }

        @Override
        public void finishUpdate(View arg0) {

        }

    }

    private class MyPageChangeListener implements OnPageChangeListener {

        boolean isAutoPlay = false;

        @Override
        public void onPageScrollStateChanged(int arg0) {
            switch (arg0) {
                case 1:
                    isAutoPlay = false;
                    break;
                case 2:
                    isAutoPlay = true;
                    break;
                case 0:
                    if (mViewPager.getCurrentItem() == mViewPager.getAdapter().getCount() - 1 && !isAutoPlay) {
                        mViewPager.setCurrentItem(0);
                    }

                    else if (mViewPager.getCurrentItem() == 0 && !isAutoPlay) {
                        mViewPager.setCurrentItem(mViewPager.getAdapter().getCount() - 1);
                    }
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int pos) {

//        	  currentItem = pos;
//              for(int i=0;i < dotViewsList.size();i++){
//                  if(i == pos){
//                      ((ImageView)dotViewsList.get(pos)).setBackgroundResource(R.drawable.dot_black);//R.drawable.main_dot_light
//                  }else {
//                      ((ImageView)dotViewsList.get(i)).setBackgroundResource(R.drawable.dot_white);
//                  }
//              }
        }

    }



    private class SlideShowTask implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            synchronized (mViewPager) {
                currentItem = (currentItem+1)%imageViewsList.size();
                handler.obtainMessage().sendToTarget();
            }
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopPlay();
    }
}



