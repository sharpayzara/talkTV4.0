package com.sumavision.talktv2.videoplayer.test;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.ShareElement;
import com.sumavision.talktv2.presenter.base.BasePresenter;
import com.sumavision.talktv2.ui.activity.base.BaseActivity;
import com.sumavision.talktv2.ui.iview.base.IBaseView;
import com.sumavision.talktv2.videoplayer.IPlayerClient;

import butterknife.BindView;


/**
 * Created by zhangyisu on 2016/6/1.
 */

public class PlayerActivity extends BaseActivity<BasePresenter> implements IBaseView, IPlayerClient {

    //    @Bind(R.id.talktv_videoview)
//    VodPlayerVideoView videoView;
//    @BindView(R.id.listView)
//    ListView listView;
    @BindView(R.id.video_view)
    VideoView vv;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_player;
    }

    @Override
    protected void initPresenter() {
        presenter = new BasePresenter(this, this) {
            @Override
            public void release() {

            }
        };
        presenter.init();
    }

    @Override
    public void initView() {

        /* 获取MediaController对象，控制媒体播放 */
        MediaController mc = new MediaController(this);
        vv.setMediaController(mc);

        /* 设置URI ， 指定数据 */
        vv.setVideoPath("http://61.240.149.17/vbigts.tc.qq.com/OOtxiW65x_RrvXSoxa7_mgQB5XZIZduGJuZ6GkJ3rhAKfMwhrm7stTMcjq2oD19rmSZYUQTvIkTPJeu8KKseFs5LQG1nwBp_hM9fCpIVd_4TRqSXlazx-NdniQLbQznUlod8cnxpss56676K0nqC2Q/g0019sw4gaa.320086.ts.m3u8?ver=4, orignUrl=http://v.qq.com/cover/w/wnko4ss6g1f5vgg.html?vid=g0019sw4gaa");

        /* 开始播放视频 */
        vv.start();

        /*  请求获取焦点 */
        vv.requestFocus();

//        videoView.init(this, 2);
//        List<DownloadInfo> results = new ArrayList<DownloadInfo>();
//        results.add(new DownloadInfo());
//        results.add(new DownloadInfo());
//        TestAdapter testAdapter = new TestAdapter(this, results);
//        listView.setAdapter(testAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        videoView.startPlay("http://www.le.com/ptv/vplay/25520328.html"); // 点播破解
//        videoView.startPlay("Letv://cctv5_1300"); // 直播
//        videoView.startPlay("http://172.16.16.181/1/cachingwhileplaying.m3u8"); // 直接可以播放

//        PlayBean pb = new PlayBean();
////        pb.setPptvUrl("pptv://code=ePEtsrad1c8Lxd09zWkifXa9joz7AQy9&key=7");
//        pb.setPptvUrl("pptv://code=XpwEVemoPe4qa/6bZTWr0w==&key=6");
//        videoView.startPlay(pb);
    }

    @Override
    public void halfFullScreenSwitch(int landscapeState) {
        if (landscapeState == ShareElement.PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else if(landscapeState == ShareElement.LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else if(landscapeState == ShareElement.REVERSILANDSCAPE)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);

    }

    @Override
    public AudioManager getAudioManager() {
        return (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (videoView != null)
//            videoView.release();
    }
}
