package com.sumavision.talktv2.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.ShareElement;
import com.sumavision.talktv2.model.entity.decor.LiveData;
import com.sumavision.talktv2.presenter.LivePresenter;
import com.sumavision.talktv2.ui.activity.LiveDetailActivity;
import com.sumavision.talktv2.ui.activity.TVFANActivity;
import com.sumavision.talktv2.ui.adapter.ChannelListAdapter;
import com.sumavision.talktv2.ui.adapter.ChannelTypeListAdapter;
import com.sumavision.talktv2.ui.fragment.Base.BaseFragment;
import com.sumavision.talktv2.ui.iview.ILiveView;
import com.sumavision.talktv2.ui.receiver.TANetChangeObserver;
import com.sumavision.talktv2.ui.receiver.TANetworkStateReceiver;
import com.sumavision.talktv2.ui.widget.LoadingLayout;
import com.sumavision.talktv2.ui.widget.NetChangeDialog;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.NetworkUtil;
import com.sumavision.talktv2.util.NoDoubleClickListener;
import com.sumavision.talktv2.videoplayer.IPlayerClient;
import com.sumavision.talktv2.videoplayer.view.LivePlayerVideoView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 直播首页界面的Fragment
 * Created by zjx on 2016/5/31.
 */
public class LiveFragment extends BaseFragment<LivePresenter> implements ILiveView, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, AbsListView.OnScrollListener, IPlayerClient {
    @BindView(R.id.list_category)
    ListView channelTypeList;
    @BindView(R.id.channel_list)
    ListView channelList;
    @BindView(R.id.loadingLayout)
    LoadingLayout loadingLayout;
    @BindView(R.id.talktv_videoview)
    LivePlayerVideoView videoView;
    @BindView(R.id.video_layout)
    RelativeLayout video_layout;

    private ChannelTypeListAdapter channelTypeAdapter;
    private ChannelListAdapter channelListAdapter;

    private ArrayList<String> channelTypeNameDatas = new ArrayList<>();
    private ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> channelDatasFormNet = new ArrayList<>();
    private ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> channelDatas = new ArrayList<>();
    private ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> channelDatasForCollect = new ArrayList<>();

    private boolean isFirst = true;
    private int lanscapeState = ShareElement.PORTRAIT;
    private boolean isFromRecommend;//是否第一次进入直播界面
    private String currChannelId;
    private final static String TAG = "LiveFragment";


//    protected NetStateChangeReceiver netStateChangeReceiver;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_live;
    }

    @Override
    protected void initPresenter() {
        presenter = new LivePresenter(getContext(), this);
        presenter.init();
        videoView.setLiveFragment(this);
        netStateChangeListen();
    }

    private NetChangeDialog netChangeDialog;
    private TANetChangeObserver taNetChangeObserver;

    /**
     * 网络变化
     */
    private void netStateChangeListen () {
        if(taNetChangeObserver == null) {
            taNetChangeObserver = new TANetChangeObserver(){
                @Override
                public void onConnect(int type) {
                    if(isHidden())
                        return;
                    if(type == ConnectivityManager.TYPE_MOBILE) {//移动流量
                        initNetChangeDialog();
                    }
                    else if(type == ConnectivityManager.TYPE_WIFI) {
                        if(videoView!=null && videoView.isStop() && !videoView.isCopyRightImageShow())
                            videoView.changeChannel(channelBean);
                        if(netChangeDialog!=null&&netChangeDialog.isShowing())
                            netChangeDialog.dismiss();
                        Toast.makeText(getContext().getApplicationContext(),"已连接wifi网络!",Toast.LENGTH_LONG).show();
                        ShareElement.isIgnoreNetChange = -1;
                    }
                }
            };
        }
        TANetworkStateReceiver.registerObserver(taNetChangeObserver);
    }

    /**
     * 进入当前界面进行网络监察
     */
    private boolean netCheck () {
        if(isMobileNet()) {
            initNetChangeDialog();
            netChangeDialog.show();
            videoView.stop();
            return true;
        }
        return false;
    }

    /**
     * 初始化流量监察dialog
     */
    private void initNetChangeDialog() {
        if(netChangeDialog == null) {
            netChangeDialog = new NetChangeDialog(getContext(), R.style.ExitDialog);
            netChangeDialog.setCancelable(false);
        }
        //忽略网络变化继续观看
        netChangeDialog.getOkButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.changeChannel(channelBean);
                netChangeDialog.dismiss();
                ShareElement.isIgnoreNetChange = 2;
            }
        });

        //停止观看
        netChangeDialog.getCancelButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                netChangeDialog.dismiss();
                videoView.showNetPic();
                ShareElement.isIgnoreNetChange = 1;
            }
        });
        videoView.stop();
        netChangeDialog.show();
    }

    LiveData.ContentBean.TypeBean.ChannelBean channelBean;

    /**
     * 获取直播type和list数据
     * @param contentBean
     */
    @Override
    public void getLiveData(LiveData.ContentBean contentBean) {
        hidden = false;
        initData(contentBean);
        currChannelId = ShareElement.channelId;
        if(TextUtils.isEmpty(currChannelId)) {
            currChannelId = channelDatasFormNet.get(0).getId();
            channelBean = channelDatasFormNet.get(0);
        }
        else {
            for(LiveData.ContentBean.TypeBean.ChannelBean bean : channelDatasFormNet) {
                if(currChannelId.equals(bean.getId())) {
                    channelBean = bean;
                    break;
                }
            }
            if(channelBean == null) {
                currChannelId = channelDatasFormNet.get(0).getId();
                channelBean = channelDatasFormNet.get(0);
                Toast.makeText(getContext(), "该频道暂时无法播放！", Toast.LENGTH_SHORT).show();
            }
        }
        presenter.queryAllCollect(channelDatasFormNet, channelTypeNameDatas);
        videoView.setTypeDatas(channelTypeNameDatas);
        videoView.setChannelDatas(channelDatasFormNet);
        videoView.setOnFavChangeLisenter(new LivePlayerVideoView.OnFavChangeLisenter() {
            @Override
            public void onFavChange(boolean fav) {
                favChange();
                presenter.favChangeForVideo(fav, channelTypeNameDatas, channelDatasForCollect);
            }
        });
        videoView.setOnNetSateListener(new LivePlayerVideoView.OnNetSateListener() {
            @Override
            public void netSateListener() {
                videoView.unRegisterSensor();
                if(netChangeDialog == null) {
                    netChangeDialog = new NetChangeDialog(getContext(), R.style.ExitDialog);
                    netChangeDialog.setCancelable(false);
                }
                netChangeDialog.show();

                //忽略网络变化继续观看
                netChangeDialog.getOkButton().setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        videoView.registerSensor();
                        videoView.play(0);
                        netChangeDialog.dismiss();
                        ShareElement.isIgnoreNetChange = 2;
                    }
                });
                //停止观看
                netChangeDialog.getCancelButton().setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        netChangeDialog.dismiss();
                        if(NetworkUtil.getCurrentNetworkType(getContext()) == ConnectivityManager.TYPE_MOBILE)
                            ShareElement.isIgnoreNetChange = 1;
                    }
                });
            }
        });
        if(netCheck())
            return;
        videoView.changeChannel(channelBean);
        videoView.setCollectDatas(channelDatasForCollect);
    }

    /**
     * 收藏数据改变
     */
    private void favChange() {
        channelDatas.clear();
        channelDatas.addAll(channelDatasForCollect);
        channelDatas.addAll(channelDatasFormNet);
        channelListAdapter.notifyDataSetChanged();
    }

    private void initChannelList() {
        channelListAdapter = new ChannelListAdapter(getContext(), channelDatas, channelDatasForCollect);
        channelListAdapter.setCurrId(currChannelId);
        channelList.setAdapter(channelListAdapter);
        channelList.setSelection(presenter.getPos(currChannelId, channelDatas));
        channelList.setOnScrollListener(this);
        initLyoutClick();
    }

    /**
     * 频道列表的播放、收藏、进入详情等控件的点击监听
     */
    private void initLyoutClick() {

        channelListAdapter.setOnToPlayListener(new ChannelListAdapter.OnToPlayListener() {
            @Override
            public void onToPlay(int position) {
//                if(ShareElement.isIgnoreNetChange == 1) {
//
//                    return;
//                }
                if(channelBean.getId().equals(channelDatas.get(position).getId())) {
                    channelBean = channelDatas.get(position);
                    return;
                }
                videoView.setShowCopyRight(true);
                channelBean = channelDatas.get(position);
                videoView.stop();
                currChannelId = channelBean.getId();
                videoView.changeChannel(channelDatas.get(position));
                channelListAdapter.setCurrId(channelBean.getId());
                channelListAdapter.notifyDataSetChanged();
            }
        });

        channelListAdapter.setOnCollectListener(new ChannelListAdapter.OnCollectListener() {
            @Override
            public void onCollect(int position, boolean isFav) {
                if(isFav) {
                    presenter.cacelFav(channelDatas.get(position).getId(), channelDatasForCollect, channelTypeNameDatas);
                }
                else {
                    presenter.collectChannel(channelDatas.get(position).getId(), channelDatasForCollect,
                            channelDatasFormNet, position, channelTypeNameDatas);
                }
                videoView.isFav(!isFav);
            }
        });

        channelListAdapter.setOnToDetialListener(new ChannelListAdapter.OnToDetialListener() {
            @Override
            public void onToDetail(int postion, boolean isFav) {
                Intent intent = new Intent(getContext(), LiveDetailActivity.class);
                intent.putExtra("channelId", channelDatas.get(postion).getId());
                intent.putExtra("isFav", isFav);
                intent.putExtra("channelName", channelDatas.get(postion).getName());
                intent.putExtra("pic", "http://tvfan.cn/photo/channel/image/android" + channelDatas.get(postion).getId() + ".png");
                intent.putExtra("program", channelDatas.get(postion).getCpName());
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1&&resultCode== Activity.RESULT_OK&&data.getBooleanExtra("change", false)) {
            presenter.queryAllCollect(channelDatasFormNet, channelTypeNameDatas);
        }
        else if(requestCode==2&&resultCode== Activity.RESULT_OK) {
            videoView.play2();
        }
    }

    private void initChannelTypeList() {
        channelTypeAdapter = new ChannelTypeListAdapter(getContext(), channelTypeNameDatas);
        if(channelBean != null)
            channelTypeAdapter.getSelector(channelBean.getChannelType());
        else
            channelTypeAdapter.getSelector("推荐");
        channelTypeList.setAdapter(channelTypeAdapter);
        channelTypeList.setOnItemClickListener(this);
        channelTypeList.setSelection(channelTypeAdapter.getSelector());
    }

    private void initData(LiveData.ContentBean contentBean) {
        int count = contentBean.getTypeCount();
        List<LiveData.ContentBean.TypeBean> list = contentBean.getType();
        for (int i=0; i<count; i++) {
            String channelType = list.get(i).getName();
            channelTypeNameDatas.add(channelType);
            List<LiveData.ContentBean.TypeBean.ChannelBean> list1 = list.get(i).getChannel();
            int count1 = list1.size();
            for(int j=0; j<count1; j++ ){
                list1.get(j).setChannelType(channelType);
            }
            channelDatasFormNet.addAll(list1);
        }
        channelDatas.addAll(channelDatasForCollect);
        channelDatas.addAll(channelDatasFormNet);
    }

    /**
     * 网络未变时是否弹出流量提示框
     * @return
     */
    private boolean isMobileNet() {
        if((ShareElement.isIgnoreNetChange == -1 && NetworkUtil.getCurrentNetworkType(getContext()) == ConnectivityManager.TYPE_MOBILE)
                ||ShareElement.isIgnoreNetChange == 1)
            return true;
        return false;
    }

    /**
     * 显示加载框
     */
    @Override
    public void showProgressBar() {
        loadingLayout.showProgressBar();
    }

    @Override
    public void hideProgressBar() {
        loadingLayout.hideProgressBar();
    }

    @Override
    public void showErrorView() {
        loadingLayout.showErrorView();
    }

    @Override
    public void showEmptyView() {
        loadingLayout.showEmptyView();
    }

    @Override
    public void showWifiView() {
        loadingLayout.showWifiView();
    }

    /**
     *
     * @param channelTypeList
     * @param channelList
     */
    @Override
    public void collectSuccess(ArrayList<String> channelTypeList, ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> channelList) {
        if(channelTypeNameDatas.size() != channelTypeList.size()) {
            channelTypeNameDatas.clear();
            channelTypeNameDatas.addAll(channelTypeList);
            channelTypeAdapter.notifyDataSetChanged();
        }
        favChange();
    }

    /**
     * 取消收藏成功
     */
    @Override
    public void cancelCollect() {
        favChange();
        if(!channelTypeNameDatas.get(0).equals("收藏"))
            channelTypeAdapter.notifyDataSetChanged();
    }

    @Override
    public void queryAllCollect(ArrayList<String> channelTypeList, ArrayList<LiveData.ContentBean.TypeBean.ChannelBean> channelList) {
        channelTypeNameDatas.clear();
        channelTypeNameDatas.addAll(channelTypeList);
        channelDatas.clear();
        channelDatas.addAll(channelList);
        channelDatas.addAll(channelDatasFormNet);

        channelDatasForCollect.clear();
        channelDatasForCollect.addAll(channelList);

        if(isFirst) {
            initChannelTypeList();
            initChannelList();
            isFirst = false;
        }
        else {
            channelTypeAdapter.notifyDataSetChanged();
            channelListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void initView () {
        AppGlobalConsts.ISSEARCHBACK = false;
        BusProvider.getInstance().register(this);
        presenter.getLiveData();
        loadingLayout.setOnRetryClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                presenter.getLiveData();
            }
        });

        initVideo();
    }


    private void initVideo() {
        videoView.init(this, 0, 0);
    }

    /**
     * 下拉刷新的监听
     */
    @Override
    public void onRefresh() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public boolean onBackPressed() {
        if(videoView != null && videoView.isLock){
            return true;
        }
        if(lanscapeState != ShareElement.PORTRAIT)
            videoView.handleFullHalfScrren(ShareElement.PORTRAIT);
        else{
            BusProvider.getInstance().post("returnHome","returnHome");
        }
        return true;
    }

    /**
     * 频道分类列表点击事件
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String name = channelTypeNameDatas.get(position);
        MobclickAgent.onEvent(getContext(), "4zbzc", name);
        int count = channelDatas.size();
        for (int i = 0; i < count; i++) {
            if (name.equals(channelDatas.get(i).getChannelType())) {
                channelList.setSelection(i);
                break;
            }
        }
        channelTypeAdapter.setSelector(position);
        channelTypeAdapter.notifyDataSetChanged();
    }

    /**
     * 频道列表滚动状态监听（设置type列表的选中）
     * @param view
     * @param scrollState
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {
            int firstVisibleItem = view.getFirstVisiblePosition();
            int lastVisibleItem = view.getLastVisiblePosition();
            int size = 0;
            if (channelDatas != null) {
                size = channelDatas.size();
            }
            if (size > 0) {
                if (lastVisibleItem == view.getCount() - 1) {
                    channelTypeAdapter.setSelector(channelTypeNameDatas.size()-1);
                    channelTypeAdapter.notifyDataSetInvalidated();
                    channelTypeList.setSelection(channelTypeNameDatas.size() - 1);
                    return;
                }
                if (firstVisibleItem < size && firstVisibleItem != 0) {
                    LiveData.ContentBean.TypeBean.ChannelBean data = channelDatas.get(firstVisibleItem - 1);
                    String name = data.getChannelType();
                    for (int i = 0; i < channelTypeNameDatas.size(); i++) {
                        if (name.equals(channelTypeNameDatas.get(i))) {
                            channelTypeAdapter.setSelector(i);
                            channelTypeAdapter.notifyDataSetInvalidated();
                            channelTypeList.setSelection(i);
                        }
                    }
                } else if (firstVisibleItem < size && firstVisibleItem == 0) {
                    channelTypeAdapter.setSelector(0);
                    channelTypeAdapter.notifyDataSetInvalidated();
                    channelTypeList.setSelection(0);
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if(AppGlobalConsts.ISSEARCHBACK && getActivity().getResources().getConfiguration().orientation == 1){
            BusProvider.getInstance().post("returnHome","returnHome");
        }
        if(channelBean != null && !hidden) {
            videoView.setShowCopyRight(false);
            TANetworkStateReceiver.registerObserver(taNetChangeObserver);
            if(( netChangeDialog!=null && netChangeDialog.isShowing()) ||
                    netCheck() || videoView.isCopyRightImageShow())
                return;
//                videoView.changeChannel(channelBean);
            if(videoView.isStop())
                videoView.changeChannel(channelBean);
            else
                videoView.start();
//            videoView.registerSensor();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(!hidden) {
            TANetworkStateReceiver.removeRegisterObserver(taNetChangeObserver);
            if(videoView.isCopyRightImageShow())
                return;
//            videoView.stop();
            videoView.pause();
            videoView.setScreenMode();
            videoView.removeMsg();
//            videoView.unRegisterSensor();
        }
    }

    private void addOrRemoveVideo (boolean add) {
        if(video_layout!=null && videoView!=null) {
            if(add && video_layout.getChildCount()==0)
                video_layout.addView(videoView);
            else if(!add)
                video_layout.removeView(videoView);
        }
    }

    private void setScreenLig() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();

        lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;

        getWindow().setAttributes(lp);
    }

    private boolean hidden = true;
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if(hidden) {
            addOrRemoveVideo(false);
            TANetworkStateReceiver.removeRegisterObserver(taNetChangeObserver);
            if(videoView.isCopyRightImageShow())
                return;
//            videoView.stop();
            videoView.pause();
            videoView.setScreenMode();
            videoView.removeMsg();
            setScreenLig();
//            videoView.unRegisterSensor();
        }
        else{
            addOrRemoveVideo(true);
            TANetworkStateReceiver.registerObserver(taNetChangeObserver);
            if(videoView.isCopyRightImageShow())
                return;
            if(!isFromRecommend) {
                videoView.setShowCopyRight(false);
//                videoView.changeChannel(channelBean);
                if(netCheck())
                    return;
                if(videoView.isStop())
                    videoView.changeChannel(channelBean);
                else
                    videoView.start();
            }
            isFromRecommend = false;
//            videoView.registerSensor();
        }
    }

    @Override
    public void halfFullScreenSwitch(int landscapeState) {
        if (getActivity() != null){
            if (landscapeState == ShareElement.PORTRAIT){
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                ((TVFANActivity) getActivity()).halfFullScreenSwitch(landscapeState);
            }
            else if(landscapeState == ShareElement.LANDSCAPE){
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                ((TVFANActivity) getActivity()).halfFullScreenSwitch(landscapeState);
            }
            else if(landscapeState == ShareElement.REVERSILANDSCAPE) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                ((TVFANActivity) getActivity()).halfFullScreenSwitch(landscapeState);
            }
        }
        halfFullScreen(landscapeState);
        this.lanscapeState = landscapeState;
    }

    private void halfFullScreen(int landscapeState) {
        if (landscapeState == ShareElement.PORTRAIT){
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(params);
            videoView.unRegisterSensor();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            ((TVFANActivity)getActivity()).showBottonUIMenu();
//            getActivity().setTheme(R.style.TransTheme);
        }
        else  {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(params);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            videoView.registerSensor();
            ((TVFANActivity)getActivity()).hideBottomUIMenu();
//            getActivity().setTheme(R.style.BlackTheme);
        }
    }

    public LivePresenter getLivePresenter() {
        return  presenter;
    }

    @Override
    public AudioManager getAudioManager() {
        return (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
    }

    private Activity activity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    @Override
    public Window getWindow() {
       return activity.getWindow();
    }

    /**
     * 其他界面直接跳到直播界面进行播放相应的台，传过来channelId
     * @param channelId
     */
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(AppGlobalConsts.EventType.TAG_B)})
    public void getPlayId (String channelId) {
        videoView.setShowCopyRight(false);
        isFromRecommend = true;
        if(channelListAdapter != null) {
            for(LiveData.ContentBean.TypeBean.ChannelBean bean : channelDatas) {
                if(channelId.equals(bean.getId())) {
                    channelBean = bean;
                    videoView.changeChannel(bean);
                    break;
                }
            }
            if(!channelId.equals(channelBean.getId())) {
                channelBean = channelDatasFormNet.get(0);
                videoView.changeChannel(channelBean);
                Toast.makeText(getContext(), "该频道暂时无法播放！", Toast.LENGTH_SHORT).show();
            }
            this.currChannelId = channelBean.getId();
            channelListAdapter.setCurrId(currChannelId);
            channelListAdapter.notifyDataSetChanged();
            channelList.setSelection(presenter.getPos(currChannelId, channelDatas));
            channelTypeAdapter.getSelector(channelBean.getChannelType());
            channelTypeAdapter.notifyDataSetChanged();
            channelTypeList.setSelection(channelTypeAdapter.getSelector());
        }
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("changeChannelFromVideo")})
    public void changeChannel(LiveData.ContentBean.TypeBean.ChannelBean bean) {
        channelBean = bean;
        currChannelId = bean.getId();
        channelListAdapter.setCurrId(bean.getId());
        channelListAdapter.notifyDataSetChanged();
        channelList.setSelection(presenter.getPos(bean.getId(), channelDatas));
        channelTypeAdapter.getSelector(bean.getChannelType());
        channelTypeAdapter.notifyDataSetChanged();
        channelTypeList.setSelection(channelTypeAdapter.getSelector());
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("showNetChangeDialog")})
    public void showNetChangeDialog(String s) {
        if(isMobileNet()) {
            netChangeDialog.show();
        }
    }
}
