package com.sumavision.talktv2.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jiongbull.jlog.JLog;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.ShareElement;
import com.sumavision.talktv2.model.entity.HomeRecommendItem;
import com.sumavision.talktv2.ui.activity.Game37WanActivity;
import com.sumavision.talktv2.ui.activity.ProgramDetailActivity;
import com.sumavision.talktv2.ui.activity.SpecialActivity;
import com.sumavision.talktv2.ui.activity.SpecialDetailActivity;
import com.sumavision.talktv2.ui.activity.WeBADActivity;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.CommonUtil;
import com.sumavision.talktv2.util.NoDoubleClickListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;


/**
*自定义banner
        */
public class MybannerView extends FrameLayout {
    private Context mContext;
    private List<HomeRecommendItem> results;
    private List<MyImageView> imageViewsList;
    private List<ImageView> dotViewsList;
    private LinearLayout mLinearLayout;
    private int  enterCount  =0;
    /**
     * 请求更新显示的View。
     */
    protected  final int MSG_UPDATE_IMAGE  = 1;
    /**
     * 请求暂停轮播。
     */
    protected  final int MSG_KEEP_SILENT   = 2;
    /**
     * 请求恢复轮播。
     */
    protected  final int MSG_BREAK_SILENT  = 3;
    /**
     * 记录最新的页号，当用户手动滑动时需要记录新页号，否则会使轮播的页面出错。
     * 例如当前如果在第一页，本来准备播放的是第二页，而这时候用户滑动到了末页，
     * 则应该播放的是第一页，如果继续按照原来的第二页播放，则逻辑上有问题。
     */
    protected  final int MSG_PAGE_CHANGED  = 4;

    //轮播间隔时间
    protected  final long MSG_DELAY = 4000;
    private ViewPager mViewPager;
    private int currentItem  = 198;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (handler.hasMessages(MSG_UPDATE_IMAGE)){
                handler.removeMessages(MSG_UPDATE_IMAGE);
            }
            switch (msg.what) {
                case MSG_UPDATE_IMAGE:
                    currentItem++;
                    mViewPager .setCurrentItem(currentItem);
                    //准备下次播放
                    handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;
                case MSG_KEEP_SILENT:
                    //只要不发送消息就暂停了
                    break;
                case MSG_BREAK_SILENT:
                    handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                    break;
                case MSG_PAGE_CHANGED:
                    //记录当前的页号，避免播放的时候页面显示不正确。
                    currentItem = msg.arg1;
                    break;
                default:
                    break;
            }
//            mViewPager.setCurrentItem(currentItem);
        }

    };
    public MybannerView(Context context) {
        this(context,null);
        mContext = context;

    }
    public MybannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
        initUI(mContext);
        if(!(results.size()<=0))
        {
            setImageUris(results);
        }
    }
    public MybannerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    private void initUI(Context context){
        imageViewsList = new ArrayList<MyImageView>();
        dotViewsList = new ArrayList<ImageView>();
        results=new ArrayList<HomeRecommendItem>();
        LayoutInflater.from(context).inflate(R.layout.item_rec_mybanner, this, true);
        mLinearLayout=(LinearLayout)findViewById(R.id.linearlayout_rec_mybanner);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_rec_mybanner);
        FrameLayout rlt = (FrameLayout)findViewById(R.id.rlt);
        ViewGroup.LayoutParams params4 = rlt.getLayoutParams();
        params4.height = CommonUtil.screenWidth(mContext)*168/360;
        rlt.setLayoutParams(params4);
        mViewPager.setFocusable(true);
        mViewPager.setAdapter(new MyPagerAdapter());

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            //配合Adapter的currentItem字段进行设置。
            @Override
            public void onPageSelected(int arg0) {
                setImageBackground(arg0);
                if (enterCount >0)
                handler.sendMessage(Message.obtain(handler, MSG_PAGE_CHANGED, arg0, 0));
                enterCount++;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                setImageBackground(arg0);
            }

            //覆写该方法实现轮播效果的暂停和恢复
            @Override
            public void onPageScrollStateChanged(int arg0) {
                switch (arg0) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        handler.sendEmptyMessage(MSG_KEEP_SILENT);
                        break;
                    case ViewPager.SCROLL_STATE_IDLE:
                        handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                        break;
                    default:
                        break;
                }
            }
        });
        mViewPager.setCurrentItem(198);
        //开始轮播效果
        handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
    }

    /**
     * 设置图片地址的集合
     * @param results
     */
    public void setImageUris(final List<HomeRecommendItem> results)
    {
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(CommonUtil.dip2px(mContext,6), CommonUtil.dip2px(mContext,6));
        lp.setMargins(CommonUtil.dip2px(mContext,5),0,0,35);
        lp.gravity= Gravity.BOTTOM;
        for(int i=0;i<results.size();i++){
            MyImageView myImageView=new MyImageView(mContext);
            myImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            //设置myimageview的显示
            myImageView.setItemImageShow(results.get(i).img);
            myImageView.setProgramGrade(results.get(i).prompt);
            myImageView.setProgramTitle(results.get(i).brtxt);
            myImageView.setClickable(true);
            final int finalI = i;
            myImageView.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    MobclickAgent.onEvent(mContext, "4banner", "" + (finalI + 1));
                    Log.i("MybannerView", "index:" + finalI);
                    switch (results.get(finalI).action){
                        case "live":
                            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_ENTER,"直播");
                            BusProvider.getInstance().post("liveplayid",results.get(finalI).id);
                            ShareElement.channelId = results.get(finalI).id;
                            break;

                        case "series":
                            Intent intent = new Intent(mContext, ProgramDetailActivity.class);
                            intent.putExtra("currentType",results.get(finalI).name);
                            intent.putExtra("idStr",results.get(finalI).id);
                            intent.putExtra("cpidStr","");
                            mContext.startActivity(intent);
                            break;
                        case "special":
                            Intent intent4 = new Intent();
                            //这里对id进行处理,l长视频,s短视频
                            String[] ids = results.get(finalI).id.trim().split(",");
                            intent4.putExtra("idStr",ids[0]);
                            if(ids[1].equals("l")){
                                //长视频专题
                                intent4.setClass(mContext, SpecialActivity.class);
                                mContext.startActivity(intent4);
                            }else{
                                //短视频专题
                                intent4.setClass(mContext, SpecialDetailActivity.class);
                                mContext.startActivity(intent4);
                            }
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
                    }

                    JLog.e("banner的条目被点击了");
                }
            });
            imageViewsList.add(myImageView);
            ImageView viewDot =  new ImageView(getContext());
            viewDot.setBackgroundResource(R.drawable.main_dot_light);
            viewDot.setLayoutParams(lp);
            dotViewsList.add(viewDot);
            mLinearLayout.addView(viewDot);
        }
    }
    private void setImageBackground(int selectItems){
        for(int i=0; i<dotViewsList.size(); i++){
            if(i == selectItems % imageViewsList.size()){
                dotViewsList.get(i).setBackgroundResource(R.drawable.main_dot_red);
            }else{
                dotViewsList.get(i).setBackgroundResource(R.drawable.main_dot_light);
            }
        }
    }

    private class MyPagerAdapter  extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        /**
         * 判断是否可以复用条目
         */
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;

        }

        /**
         * 销毁条目
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        /**
         * 初始化条目
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //对ViewPager页号求模取出View列表中要显示的项
            position %= imageViewsList.size();
            if (position<0){
                position = imageViewsList.size()+position;
            }
            MyImageView myImageView = imageViewsList.get(position);
            //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
            ViewParent vp =myImageView.getParent();
            if (vp!=null){
                ViewGroup parent = (ViewGroup)vp;
                parent.removeView(myImageView);
            }
            container.addView(myImageView);
            return myImageView;
        }
    }
}



