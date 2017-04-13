package com.sumavision.talktv2.ui.fragment;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.ShareElement;
import com.sumavision.talktv2.model.entity.HorVideoData;
import com.sumavision.talktv2.model.entity.MediaList;
import com.sumavision.talktv2.model.entity.MediaRecommand;
import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.presenter.MediaRecommandPresenter;
import com.sumavision.talktv2.ui.activity.TVFANActivity;
import com.sumavision.talktv2.ui.adapter.MediaRecommandAdapter;
import com.sumavision.talktv2.ui.fragment.Base.BaseFragment;
import com.sumavision.talktv2.ui.iview.IMediaRecommandView;
import com.sumavision.talktv2.ui.receiver.TANetChangeObserver;
import com.sumavision.talktv2.ui.receiver.TANetworkStateReceiver;
import com.sumavision.talktv2.ui.widget.LMRecyclerView;
import com.sumavision.talktv2.ui.widget.LoadingLayout;
import com.sumavision.talktv2.ui.widget.NetChangeDialog;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.NetworkUtil;
import com.sumavision.talktv2.util.NoDoubleClickListener;
import com.sumavision.talktv2.util.TipUtil;
import com.sumavision.talktv2.videoplayer.IPlayerClient;
import com.sumavision.talktv2.videoplayer.PlayBean;
import com.umeng.analytics.MobclickAgent;
import com.waynell.videolist.visibility.calculator.SingleListViewItemActiveCalculator;
import com.waynell.videolist.visibility.scroll.RecyclerViewItemPositionGetter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 自媒体首页界面的Fragment
 * Created by sharpay on 2016/6/31.
 */
public class MediaRecommendFragment extends BaseFragment<MediaRecommandPresenter> implements IMediaRecommandView,LMRecyclerView.LoadMoreListener, SwipeRefreshLayout.OnRefreshListener, IPlayerClient {
    boolean canLoadMore = true;
    @BindView(R.id.recycler_view)
    LMRecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.loading_layout)
    LoadingLayout loadingLayout;
    MediaRecommandPresenter presenter;
    MediaRecommandAdapter adapter;
    List<MediaRecommand> list;
    int page = 1;
    int size = 20;
    private SingleListViewItemActiveCalculator mCalculator;
    private int mScrollState;
    private boolean isVertical = true;
    private boolean isHidden = false;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_media_recommand2;
    }

    @Override
    protected void initPresenter() {
        presenter = new MediaRecommandPresenter(this.getContext(),this);
        presenter.init();
        netStateChangeListen();
    }

    private NetChangeDialog netChangeDialog;
    private TANetChangeObserver taNetChangeObserver;
    private int iBreakTime;
    private void netStateChangeListen() {
        if(taNetChangeObserver == null) {
            taNetChangeObserver = new TANetChangeObserver(){
                @Override
                public void onConnect(int type) {
                    if(isHidden)
                        return;
                    if(type == ConnectivityManager.TYPE_MOBILE) {//移动流量
                        if(netChangeDialog == null) {
                            netChangeDialog = new NetChangeDialog(getContext(), R.style.ExitDialog);
                            netChangeDialog.setCancelable(false);
                        }

                        //忽略网络变化继续观看
                        netChangeDialog.getOkButton().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                netChangeDialog.dismiss();
                                ShareElement.isIgnoreNetChange = 2;
                                if(adapter==null || adapter.currVideo == null)
                                    return;

                                play();
                                //18910559587
                                adapter.currVideo.setHistory(iBreakTime, true);
                                iBreakTime = 0;
                            }
                        });
                        //停止观看
                        netChangeDialog.getCancelButton().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                netChangeDialog.dismiss();
                                ShareElement.isIgnoreNetChange = 1;
                            }
                        });
                        if(ShareElement.isIgnoreNetChange == -1) {
                            netChangeDialog.show();
                            if(adapter==null || adapter.currVideo == null)
                                return;
                            iBreakTime = (int) adapter.currVideo.getBreakTime();
                            stop();
                        }

                    }
                    else if(type == ConnectivityManager.TYPE_WIFI) {
                        if(netChangeDialog!=null&&netChangeDialog.isShowing())
                            netChangeDialog.dismiss();
                        if (adapter != null && adapter.currVideo != null && adapter.currVideo.isStop()){
                            play();
                        }
                        ShareElement.isIgnoreNetChange = -1;
                        Toast.makeText(getContext().getApplicationContext(),"已连接wifi网络!",Toast.LENGTH_LONG).show();
                    }
                }
            };
        }
        registerOrRemoveObserver(true);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isHidden = hidden;
        if(hidden) {
            registerOrRemoveObserver(false);
            Log.e("TANetworkStateReceiver", "hidden");
        }
        else {
            registerOrRemoveObserver(true);
            Log.e("TANetworkStateReceiver", "show");
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void initView() {
        BusProvider.getInstance().register(this);
        list = new ArrayList<>();
        adapter = new MediaRecommandAdapter(list,getContext(), recyclerView, this, presenter);
        adapter.setOnNetSateListener(new MediaRecommandAdapter.OnNetSateListener() {
            @Override
            public void netSateListener() {
                netStateCheck();
            }
        });
        adapter.setOnClickPlayListener(new MediaRecommandAdapter.OnClickPlayListener() {
            @Override
            public void onClickPlay() {
                netStateCheck();
            }
        });
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
        recyclerView.setLoadMoreListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.blue);
        swipeRefreshLayout.setOnRefreshListener(this);
        mCalculator = new SingleListViewItemActiveCalculator(adapter,
                new RecyclerViewItemPositionGetter(llm, recyclerView));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                mScrollState = newState;
                if(newState == RecyclerView.SCROLL_STATE_IDLE && adapter.getItemCount() > 0 && isVertical) {
                    mCalculator.onScrollStateIdle();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(isVertical){
                    mCalculator.onScrolled(mScrollState);
                }
            }
        });
        showProgressBar();
        presenter.getMediaListData(page,size,getArguments().getString("txt"));
//        presenter.judgeNetwork();
    }

    /**
     * 当没有弹出流量提醒框，但是已经不是wifi状态
     */
    private void netStateCheck() {
        if(netChangeDialog == null) {
            netChangeDialog = new NetChangeDialog(this.getContext(), R.style.ExitDialog);
            netChangeDialog.setCancelable(false);
        }

        netChangeDialog.show();

        //忽略网络变化继续观看
        netChangeDialog.getOkButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareElement.isIgnoreNetChange = 2;
                netChangeDialog.dismiss();
                play();
            }
        });
        //停止观看
        netChangeDialog.getCancelButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                netChangeDialog.dismiss();
                ShareElement.isIgnoreNetChange = 1;
            }
        });
    }

    @Override
    public void loadMore() {
        if (canLoadMore) {
            if (!swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
            page++;
            presenter.getMediaListData(page, size, getArguments().getString("txt"));
        } else {
            TipUtil.showSnackTip(recyclerView, "没有更多数据了!");
        }
    }

    @Override
    public void onRefresh() {
        MobclickAgent.onEvent(getContext(), "4rdxlsx");
        canLoadMore = true;
        page = 1;
        list.clear();
        adapter.notifyDataSetChanged();
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        presenter.getMediaListData(page, size, getArguments().getString("txt"));
    }


    @Override
    public void halfFullScreenSwitch(int landscapeState) {
        if (landscapeState == ShareElement.PORTRAIT) {
            HorVideoData data = new HorVideoData();
            data.setLandscape(false);
            BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_A,data);
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if( adapter.currVideo.getParent() != null){
                ((RelativeLayout)adapter.currVideo.getParent()).removeViewAt(0);
            }
            adapter.currVideo.setProgramNameVisiable(false);
            adapter.currVideo.setBackVisiable(false);
            adapter.currRlt.addView(adapter.currVideo);
            isVertical = true;
            adapter.currVideo.unRegisterSensor();
            ((TVFANActivity)getActivity()).showBottonUIMenu();
        } else if(landscapeState == ShareElement.LANDSCAPE){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            sendLandscapeMsgToActivity();
        }
        else if(landscapeState == ShareElement.REVERSILANDSCAPE){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            sendLandscapeMsgToActivity();
        }
    }

    private void sendLandscapeMsgToActivity () {
        if(!isVertical)
            return;
        HorVideoData data = new HorVideoData();
        isVertical = false;
        if( adapter.currVideo.getParent() != null){
            ((RelativeLayout)adapter.currVideo.getParent()).removeViewAt(1);
        }
        data.setLandscape(true);
        data.setVideoView(adapter.currVideo);
        BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_A,data);
        ((TVFANActivity)getActivity()).hideBottomUIMenu();
        adapter.currVideo.registerSensor();
    }
    @Override
    public AudioManager getAudioManager() {
        return (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public Window getWindow() {
        if(getActivity() != null)
            return getActivity().getWindow();
        else
            return getParentFragment().getActivity().getWindow();

    }

    @Override
    public void fillListData(MediaList mediaList) {
        isHidden = false;
        for(MediaList.ItemsBean bean : mediaList.getItems()){
            MediaRecommand tmp = new MediaRecommand();
            tmp.setCode(bean.getCode());
            tmp.setName(bean.getName());
            tmp.setDuration(bean.getDuration());
            tmp.setPlayCount(bean.getPlayCount());onPause();
            tmp.setVideoType(bean.getVideoType());
            tmp.setSdkType(bean.getSdkType());
            tmp.setPicUrl(bean.getPicUrl());
            PlayBean pb = new PlayBean();
            pb.setProgramId(bean.getCode());
            if(bean.getVideoType() == 1){  //本地破解
                pb.setUrl(bean.getPlayUrl());
                tmp.setPlayerType(0);
            }else{
                if(bean.getSdkType() == 2){   //pptv
                    pb.setPptvUrl(bean.getPlayUrl());
                    tmp.setPlayerType(2);
                }else{                          //搜狐
                    String urlStr = bean.getPlayUrl();
                    String []urlArr = urlStr.split(",");
                    try {
                        pb.setSohuAid(Long.parseLong(urlArr[0]));
                    }catch (Exception ex){
                        pb.setSohuAid(1000000572765l);
                    }
                    pb.setSohuVid(Long.parseLong(urlArr[1]));
                    pb.setSohuSite(Integer.parseInt(urlArr[2]));
                    tmp.setPlayerType(1);
                }
            }
            tmp.setPlayBean(pb);
            list.add(tmp);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showProgressBar() {
        loadingLayout.showProgressBar();
    }

    @Override
    public void hideProgressBar() {
        if(loadingLayout != null){
            loadingLayout.hideProgressBar();
        }
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!isHidden) {
            registerOrRemoveObserver(true);
            Log.e("TANetworkStateReceiver", "onResume");
        }

    }

    public void registerOrRemoveObserver(boolean register){
        if(register)
            TANetworkStateReceiver.registerObserver(taNetChangeObserver);
        else
            TANetworkStateReceiver.removeRegisterObserver(taNetChangeObserver);
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(AppGlobalConsts.EventType.TAG_A)})
    public void refreshShow(Boolean bool){
        if(isHidden )
            return;
        if(bool){
            play();
        }else{
            stop();
        }
    }

    /**
     *
     */
    private void play() {
//        if(adapter.currVideo.playerType == 2){
//            adapter.currentHolder.setActive(null,adapter.currentHolder.position);
//        }else if(adapter.currVideo.playerType == 1) {
//            adapter.currentHolder.sohu4Resume();
//        }else{
//            EventBus.getDefault().register(adapter.currVideo);
//            adapter.currVideo.startPlay(adapter.currentBean);
//            presenter.pvLog(adapter.currentBean.getProgramId(), getContext());
//        }

        if((netChangeDialog!=null&&netChangeDialog.isShowing()))
            return;
        if (isNetCheck()) {
            netStateCheck();
            return;
        }
        if (adapter != null && adapter.currVideo != null) {
            if(adapter.currVideo.playerType == 0)
                EventBus.getDefault().register(adapter.currVideo);
            adapter.currVideo.startPlay(adapter.currentBean);
            adapter.turnPicVideoView(false);
        }
    }

    private void stop() {
//        if (adapter != null && adapter.currVideo != null){
//            if(adapter.currVideo.playerType == 0) {
//                EventBus.getDefault().unregister(adapter.currVideo);
//                adapter.currVideo.stopPreLoading();
//            }
//            pointTime = (int) adapter.currVideo.getBreakTime();
//            adapter.currVideo.stop();
//
//        }
        if (adapter != null && adapter.currVideo != null) {
            if(adapter.currVideo.playerType == 0)
                EventBus.getDefault().unregister(adapter.currVideo);
            adapter.currVideo.stop();
            adapter.currVideo.stopPreLoading();
            adapter.turnPicVideoView(true);
        }
    }

    /**
     * 网络未变时是否弹出流量提示框
     * @return
     */
    private boolean isNetCheck() {
        if((ShareElement.isIgnoreNetChange == -1 && NetworkUtil.getCurrentNetworkType(BaseApp.getContext()) == ConnectivityManager.TYPE_MOBILE)
                ||ShareElement.isIgnoreNetChange == 1)
            return true;
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("TANetworkStateReceiver", "onPause");
        if(!isHidden) {
            registerOrRemoveObserver(false);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        isHidden = !isVisibleToUser;
        if(!isVisibleToUser){
            if (adapter != null && adapter.currVideo != null){
//                if(adapter.currVideo.playerType == 0) {
//                    adapter.currVideo.stopPreLoading();
//                }
//                adapter.currVideo.stop();
                stop();
                adapter.currVideo.setScreenMode();
                adapter.currRlt.removeView(adapter.currVideo);
                setScreenLig();
            }

        }else{
            if (adapter != null && adapter.currVideo != null && adapter.isComplete == false){
//                if(adapter.currVideo.getParent() != null){
//                    ((ViewGroup)adapter.currVideo.getParent()).removeView(adapter.currVideo);
//                }
//                adapter.currRlt.addView(adapter.currVideo);
//                if(adapter.currVideo.playerType == 2){
//                    adapter.currentHolder.setActive(null,adapter.currentHolder.position);
//                }else{
//                    adapter.currVideo.startPlay(adapter.currentBean);
//                    presenter.pvLog(adapter.currentBean.getProgramId(), getContext());
//                }
                play();
            }

        }
    }

    private void setScreenLig() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        getWindow().setAttributes(lp);
    }

    @Override
    public void showErrorView() {
        loadingLayout.showErrorView();
    }

    @Override
    public void showWifiView() {
        loadingLayout.showWifiView();
    }

    @Override
    public void emptyData() {
        canLoadMore = false;
        loadingLayout.showEmptyView();
    }

    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }
}
