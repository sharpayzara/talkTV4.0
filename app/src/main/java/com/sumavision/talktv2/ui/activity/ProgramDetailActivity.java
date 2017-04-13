package com.sumavision.talktv2.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.baidu.mobad.feeds.NativeResponse;
import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.baidu.mobads.AppActivity;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding.view.RxView;
import com.jiongbull.jlog.JLog;
import com.sumavision.offlinelibrary.entity.DownloadInfo;
import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.BuildConfig;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.DividerItemDecoration;
import com.sumavision.talktv2.common.ShareElement;
import com.sumavision.talktv2.dao.RxDao;
import com.sumavision.talktv2.dao.ormlite.DbCallBack;
import com.sumavision.talktv2.http.GlideProxy;
import com.sumavision.talktv2.model.entity.ArtsChangeProgramMsg;
import com.sumavision.talktv2.model.entity.CollectBean;
import com.sumavision.talktv2.model.entity.CpDataNet;
import com.sumavision.talktv2.model.entity.DetailRecomendData;
import com.sumavision.talktv2.model.entity.NextPlay;
import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.model.entity.PraiseData;
import com.sumavision.talktv2.model.entity.ProgramDetail;
import com.sumavision.talktv2.model.entity.SeriesDetail;
import com.sumavision.talktv2.model.entity.VideoType;
import com.sumavision.talktv2.mycrack.UpdateCrackDialog;
import com.sumavision.talktv2.presenter.ProgramDetailPresenter;
import com.sumavision.talktv2.ui.activity.base.BaseActivity;
import com.sumavision.talktv2.ui.adapter.ArtsAdapter;
import com.sumavision.talktv2.ui.adapter.RecommandAdapter;
import com.sumavision.talktv2.ui.adapter.SelectionsPagerAdapter;
import com.sumavision.talktv2.ui.iview.IProgranDetailView;
import com.sumavision.talktv2.ui.receiver.TANetChangeObserver;
import com.sumavision.talktv2.ui.receiver.TANetworkStateReceiver;
import com.sumavision.talktv2.ui.widget.DetailHRecyclerView;
import com.sumavision.talktv2.ui.widget.DetailVRecyclerView;
import com.sumavision.talktv2.ui.widget.GridCachePopupWindow;
import com.sumavision.talktv2.ui.widget.ListCachePopupWindow;
import com.sumavision.talktv2.ui.widget.LoadingLayout;
import com.sumavision.talktv2.ui.widget.MyLinearLayout;
import com.sumavision.talktv2.ui.widget.NetChangeDialog;
import com.sumavision.talktv2.ui.widget.SelectSharePopupWindow;
import com.sumavision.talktv2.ui.widget.SelectionsPopupWindow;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.CommonUtil;
import com.sumavision.talktv2.util.NetworkUtil;
import com.sumavision.talktv2.util.NoDoubleClickListener;
import com.sumavision.talktv2.util.OfflineCacheUtil;
import com.sumavision.talktv2.util.TipUtil;
import com.sumavision.talktv2.videoplayer.IPlayerClient;
import com.sumavision.talktv2.videoplayer.PlayBean;
import com.sumavision.talktv2.videoplayer.view.VodPlayerVideoView;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * desc  播放节目详情
 */
public class ProgramDetailActivity extends BaseActivity<ProgramDetailPresenter> implements IProgranDetailView, IPlayerClient, VodPlayerVideoView.OnBackListener {
    @BindView(R.id.scroll)
    LinearLayout scroll;
    @BindView(R.id.all_rlt)
    RelativeLayout all_rlt;
    @BindView(R.id.ad_rlt)
    RelativeLayout ad_rlt;
    @BindView(R.id.video_layout)
    RelativeLayout videoLayout;
    @BindView(R.id.content_layout)
    RelativeLayout contentLayout;
    VodPlayerVideoView videoView;
    VodPlayerVideoView lastVideoView;
    @BindView(R.id.cpllt)
    LinearLayout cpllt;
    @BindView(R.id.showDetailRlt)
    RelativeLayout showDetailRlt;
    @BindView(R.id.program_name_rlt)
    RelativeLayout programNameRlt;
    @BindView(R.id.viewMoreRlt)
    RelativeLayout viewMoreRlt;
    @BindView(R.id.moreIcon)
    CheckBox moreIcon;
    @BindView(R.id.tel_selections_tab)
    TabLayout telSelectionsTab;
    @BindView(R.id.tel_select_container)
    ViewPager teltelSelectContainer; //电视剧选集
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.playdetail_playtime)
    ImageView playdetailPlaytime;
    @BindView(R.id.selector_recycle)
    DetailHRecyclerView artsSelectorRecycle;
    @BindView(R.id.arts_release)
    TextView artsRelease;
    @BindView(R.id.praise_mllt)
    MyLinearLayout praiseMllt;
    @BindView(R.id.arts_selections_tv)
    TextView artsSelectionsTv;
    @BindView(R.id.recommend_recycle)
    RecyclerView recommendRecycle;
    @BindView(R.id.tv_rlt)
    RelativeLayout tvRlt;
    @BindView(R.id.arts_rlt)
    RelativeLayout artsRlt;
    @BindView(R.id.common_tlt)
    TableLayout commonTlt;
    @BindView(R.id.arts_tlt)
    TableLayout artsTlt;
    SelectionsPagerAdapter tvAdapter;
    @BindView(R.id.play_count)
    TextView playCount;
    @BindView(R.id.cache_rlt)
    RelativeLayout cacheRlt;
    @BindView(R.id.collect_mllt)
    MyLinearLayout collectMllt;
    @BindView(R.id.resource)
    ImageView resource;
    @BindView(R.id.program_name)
    TextView programName;
    @BindView(R.id.release)
    TextView release;
    @BindView(R.id.arts_subType)
    TextView artsSubType;
    @BindView(R.id.arts_zone)
    TextView artsZone;
    @BindView(R.id.score)
    TextView score;
    @BindView(R.id.subType)
    TextView subType;
    @BindView(R.id.art_actor)
    TextView artActor;
    @BindView(R.id.zone)
    TextView zone;
    @BindView(R.id.actor)
    TextView actor;
    @BindView(R.id.share_btn)
    ImageView shareBtn;
    @BindView(R.id.detail_info)
    TextView detailInfo;
    @BindView(R.id.tel_selections_rlt)
    RelativeLayout telSelectionsRlt;
    @BindView(R.id.tel_selections_line)
    ImageView telSelectionsLine;
    @BindView(R.id.arts_selections_rlt)
    RelativeLayout artsSelectionsRlt;
    @BindView(R.id.arts_selections_line)
    ImageView artsSelectionsLine;
    @BindView(R.id.main_content)
    LinearLayout mainContent;
    @BindView(R.id.director)
    TextView director;
    @BindView(R.id.directorTr)
    TableRow directorTr;
    @BindView(R.id.loadingLayout)
    LoadingLayout loadingLayout;
    @BindView(R.id.collect_checkBox)
    CheckBox collectCheckBox;
    @BindView(R.id.praise_checkBox)
    CheckBox praiseCheckBox;
    @BindView(R.id.add_praise_tv)
    TextView addPraiseTv;
    @BindView(R.id.recommandrlt)
    RelativeLayout recommandrlt;
    @BindView(R.id.pop_cachepopupwindow_img)
    ImageView popCachePopupWindowImg;
    @BindView(R.id.rlTemplate2)
    RelativeLayout rlTempl2;
    @BindView(R.id.rlt_playcount)
    RelativeLayout rlt_playcount;
    @BindView(R.id.line6)
    ImageView line6;
    private ProgramDetailPresenter presenter;
    private PopupWindow popupwindow;
    private UpdateCrackDialog updateCrackDialog;
    private ArtsAdapter artsAdapter;
    private RecommandAdapter recommendAdapter;
    private AdView adView;//百度广告的view
    private VideoType videoType;
    private ProgramDetail programDetail;
    private PlayerHistoryBean playerHistoryBean;
    private SeriesDetail.SourceBean playBean;
    private int cpChecked = -1;
    private ArrayList<String> seriesArray;
    private int pageSize = 20;
    private final int tvPageSize = 30;
    private int artsLPageNow = 1;
    private int artsFPageNow = 1;
    private int artsPageTotal;
    private int cpState = 1;//cp列表的状态“筛选之后只有乐视SDK---2， 其他---1”
    SelectionsPopupWindow selectionsPopupWindow;
    GridCachePopupWindow gridCachePopupWindow;
    ListCachePopupWindow listCachePopupWindow;
    private String cpidStr = "";
    private String idStr;
    private String epi = "1";
    private int pageNum;
    private int playPos;
    private int totalNum;
    private int artsSerisTotal;
    private boolean artsPlayNext;
    private boolean isChangeArtsPos;
    HashMap<String, DownloadInfo> downloadInfoHashMap;
    HashMap<String, VodPlayerVideoView> videoViewMap = new HashMap<>();
    private SeriesDetail seriesDetail;
    private boolean isFirst = true;//是否刚刚进入
    private boolean isFirstPlay = true;//是否第一次播放
    private boolean isPlay;//是否播放了视频
    private static String YOUR_AD_PLACE_ID = "2891968"; // 双引号中填写自己的广告位ID
    PraiseData praiseData;
    SelectSharePopupWindow sharePopupWindow;
    private boolean isChangeCp;
    private static RxDao collectDao = new RxDao(BaseApp.getContext(), CollectBean.class);

    private NetChangeDialog netChangeDialog;//流量提醒的弹窗
    private TANetChangeObserver taNetChangeObserver;//网络监听器

    @OnClick(R.id.arts_selections_tv)
    void artsSelections() {
        showSelectionsPopuwindows();
    }

    @OnClick(R.id.viewMoreRlt)
    void clickShowDetailRlt() {
        if (showDetailRlt.getVisibility() == View.GONE) {
            showDetailRlt.setVisibility(View.VISIBLE);
            moreIcon.setChecked(true);
        } else {
            showDetailRlt.setVisibility(View.GONE);
            moreIcon.setChecked(false);
        }
    }

    @OnClick(R.id.cache_rlt)
    void cacheRlt(View view) {
        if("cpsdkasd3".equals(cpidStr) || "cppt05".equals(cpidStr) || "cpsdksdf8".equals(cpidStr)) {
            Toast.makeText(this, "由于版权问题，该视频暂不提供缓存功能", Toast.LENGTH_SHORT).show();
            return;
        }
        openCacheWindow(view);
    }

    @OnClick(R.id.cpllt)
    void cpllt() {
        if (popupwindow != null && popupwindow.isShowing()) {
            popupwindow.dismiss();
        } else {
            initPopupWindowView();
        }
    }

    @OnClick(R.id.share_btn)
    void shareClick(View view){
        sharePopupWindow = new SelectSharePopupWindow(this);
        sharePopupWindow .showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }


    @OnClick(R.id.collect_mllt)
    void collect(){
        if (programDetail == null) {
            Toast.makeText(this, "请稍候", Toast.LENGTH_SHORT).show();
            return;
        }
        final CollectBean bean = programDetail.changeCollectBean(new CollectBean());
        bean.setCpid(cpidStr);
        bean.setCurrType(currType);
        bean.setPicurl(programDetail.getPicurl());
        Map<String,String> map = new HashMap<>();
        map.put("sid",bean.getSid());
        collectDao.subscribe();
        collectDao.queryByConditionSync(map, new DbCallBack<List<CollectBean>>() {
            @Override
            public void onComplete(List<CollectBean> list) {
                if(list!=null && list.size() > 0){
                    collectDao.deleteById(list.get(0).getId());
                    collectCheckBox.setChecked(false);
                    collectDao.unsubscribe();
                }else{
                    collectDao.insertSync(bean, new DbCallBack<Boolean>() {
                        @Override
                        public void onComplete(Boolean bool) {
                            if(bool){
                                collectCheckBox.setChecked(true);
                                TipUtil.showSnackTip(collectMllt,"收藏成功");
                            }else{
                                collectCheckBox.setChecked(false);
                                TipUtil.showSnackTip(collectMllt,"收藏失败");
                            }
                            collectDao.unsubscribe();
                        }
                    });
                }

            }
        });
    }

    public void findCollectBean(){
        final CollectBean bean = programDetail.changeCollectBean(new CollectBean());
        Map<String,String> map = new HashMap<>();
        map.put("sid",bean.getSid());
        collectDao.subscribe();
        collectDao.queryByConditionSync(map, new DbCallBack<List<CollectBean>>() {
            @Override
            public void onComplete(List<CollectBean> list) {
                if(list != null && list.size() > 0){
                    collectCheckBox.setChecked(true);
                }else{
                    collectCheckBox.setChecked(false);
                }
                collectDao.unsubscribe();
            }
        });
    }

    private void setPlayName (String name) {
        if(cpState == 1)
            videoView.setProgramName(name);
    }

    private void isShowNext(boolean isShow) {
        if(cpState == 1)
            videoView.isShowNext(isShow);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addBaiDuAd();
    }

    /**
     * 添加百度广告
     */
    private void addBaiDuAd() {
        Log.i("demo", "NativeOriginActivity.fetchAd");


//        /**
//         * Step 1. 创建BaiduNative对象，参数分别为： 上下文context，广告位ID, BaiduNativeNetworkListener监听（监听广告请求的成功与失败）
//         * 注意：请将YOUR_AD_PALCE_ID替换为自己的广告位ID
//         */BaiduNative baidu = new BaiduNative(this, YOUR_AD_PLACE_ID, new BaiduNative.BaiduNativeNetworkListener() {
//
//            @Override
//            public void onNativeFail(NativeErrorCode arg0) {
//                Log.w("NativeOriginActivity", "onNativeFail reason:" + arg0.name());
//
//            }
//
//            @Override
//            public void onNativeLoad(List<NativeResponse> arg0) {
//                // 一个广告只允许展现一次，多次展现、点击只会计入一次
//                if (arg0.size() > 0 && arg0.get(0).isAdAvailable(ProgramDetailActivity.this)) {
//                    // demo仅简单地显示一条。可将返回的多条广告保存起来备用。
//                    updateView(arg0.get(0));
//                    line6.setVisibility(View.GONE);
//                    rlTempl2.setVisibility(View.VISIBLE);
//                }
//
//            }
//
//        });
//
//        /**
//         * Step 2. 创建requestParameters对象，并将其传给baidu.makeRequest来请求广告
//         */
//        // 用户点击下载类广告时，是否弹出提示框让用户选择下载与否
//        RequestParameters requestParameters =
//                new RequestParameters.Builder()
//                        .downloadAppConfirmPolicy(
//                                RequestParameters.DOWNLOAD_APP_CONFIRM_ONLY_MOBILE).build();
//
//        baidu.makeRequest(requestParameters);

        // 代码设置AppSid，此函数必须在AdView实例化前调用
        // AdView.setAppSid("debug");

        // 设置'广告着陆页'动作栏的颜色主题
        // 目前开放了七大主题：黑色、蓝色、咖啡色、绿色、藏青色、红色、白色(默认) 主题
        AppActivity.setActionBarColorTheme(AppActivity.ActionBarColorTheme.ACTION_BAR_WHITE_THEME);
//        另外，也可设置动作栏中单个元素的颜色, 颜色参数为四段制，0xFF(透明度, 一般填FF)DE(红)DA(绿)DB(蓝)
//        AppActivity.getActionBarColorTheme().set[Background|Title|Progress|Close]Color(0xFFDEDADB);

        // 创建广告View
        String adPlaceId = "2891968"; //  重要：请填上您的广告位ID，代码位错误会导致无法请求到广告
        adView = new AdView(this, adPlaceId);
        // 设置监听器
        adView.setListener(new AdViewListener() {
            public void onAdSwitch() {
                Log.w("2891968", "onAdSwitch");
            }

            public void onAdShow(JSONObject info) {
                // 广告已经渲染出来
                Log.w("2891968", "onAdShow " + info.toString());
                ad_rlt.setVisibility(View.VISIBLE);
            }

            public void onAdReady(AdView adView) {
                // 资源已经缓存完毕，还没有渲染出来
                Log.w("2891968", "onAdReady " + adView);
            }

            public void onAdFailed(String reason) {
                Log.w("2891968", "onAdFailed " + reason);
            }

            public void onAdClick(JSONObject info) {

            }

            @Override
            public void onAdClose(JSONObject arg0) {
                Log.w("", "onAdClose");
            }
        });
        ad_rlt.addView(adView);

    }


    public void updateView(final NativeResponse nativeResponse) {
        Log.i("demo", "NativeOriginActivity.updateView");

        // use template1
        AQuery aq = new AQuery(this);

        //use template2。特别提醒：当您选择该模板时，desiredAssets需设置为不需要NativeAdAsset.MAIN_IMAGE，填充会更加充足。
        aq.id(R.id.iv_title2).text(nativeResponse.getTitle());
        aq.id(R.id.iv_desc2).text(nativeResponse.getDesc());
        aq.id(R.id.iv_icon2).image(nativeResponse.getIconUrl());
        aq.id(R.id.iv_cta).text(nativeResponse.isDownloadApp()? "免费下载" : "查看详情");

        nativeResponse.recordImpression(rlTempl2);// 发送展示日志

        rlTempl2.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                nativeResponse.handleClick(v);// 点击响应
            }
        });
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_program_detail;
    }

    @Override
    protected void initPresenter() {
        presenter = new ProgramDetailPresenter(this, this);
        presenter.init();
        netStateChangeListen();
    }

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

    @Override
    public void fillSeriesGridValue(SeriesDetail seriesDetail) {
        this.playBean = seriesDetail.getSource().get(0);
    }

    @Override
    public void fillSeriesVListValue(SeriesDetail seriesDetail) {
        totalNum = seriesDetail.getTotal();
        if(totalNum<=1)
            isShowNext(false);
        if (videoType == VideoType.movie || videoType == VideoType.microcinema) {
            playBean = seriesDetail.getSource().get(0);
            presenter.pvLog(seriesDetail.getSource().get(0).getId(), this);
            if(cpState == 2)
                choiceVideoView();
            else {
                playNetOrLocal(false);
                isPlay = true;
            }
            return;
        }
        artsPageTotal = totalNum/pageSize+1;
        OfflineCacheUtil.filterCacheInfo(this, seriesDetail.getSource(), downloadInfoHashMap);
        if(seriesDetail.getSource().size() == 0){
            TipUtil.showSnackTip(artsSelectorRecycle, getString(R.string.no_more_data));
            return;
        }

        artsSerisTotal += seriesDetail.getSource().size();
        if(!isChangeCp) {
            if(isChangeArtsPos) {//加载前面的数据
                seriesDetail.getSource().addAll(this.seriesDetail.getSource());
                this.seriesDetail = seriesDetail;
                selectionsPopupWindow.clearHolders();
                artsAdapter.clearHolders();
                playPos += 20;
            }
            else {
                if(this.seriesDetail==null) {
                    this.seriesDetail = seriesDetail;
                }
                else
                    this.seriesDetail.getSource().addAll(seriesDetail.getSource());
            }

            selectionsPopupWindow.setSeriesData(this.seriesDetail.getSource(), playPos);
            artsAdapter.setList(this.seriesDetail.getSource());
            artsAdapter.setPlayPos(playPos);
            artsAdapter.notifyDataSetChanged();
            if(artsPlayNext) {
                artsPlayNext = false;
                artsAdapter.notifyDataSetChanged();
                artsSelectorRecycle.scrollToPosition(playPos);
                selectionsPopupWindow.changeProgram(playPos);
                play(this.seriesDetail.getSource().get(playPos));
            }
            else {
                artsAdapter.changeProgram(playPos);
            }
            if(isFirst) {
                if(cpState == 2)
                    choiceVideoView();
                this.playBean = seriesDetail.getSource().get(playPos);
                playNetOrLocal(true);
                artsSelectorRecycle.scrollToPosition(playPos);
                presenter.pvLog(seriesDetail.getSource().get(playPos).getId(), this);
                isFirst = false;
                setPlayName(programDetail.getName()+"  "+seriesDetail.getSource().get(playPos).getEpi());
            }
            if(isChangeArtsPos) {
                selectionsPopupWindow.setSelection(19);
                artsSelectorRecycle.scrollToPosition(19);
                isChangeArtsPos = false;
            }
        }
        else {
            presenter.pvLog(seriesDetail.getSource().get(playPos).getId(), this);
            isChangeCp = false;
            artsAdapter.changeProgram(playPos);
            artsSelectorRecycle.scrollToPosition(0);
            this.playBean = seriesDetail.getSource().get(0);
            this.seriesDetail = seriesDetail;
            selectionsPopupWindow.setSeriesData(this.seriesDetail.getSource(), playPos);
            artsAdapter.setList(this.seriesDetail.getSource());
            artsAdapter.notifyDataSetChanged();
            startPlay(playBean.getPlayUrl());
            setPlayName(programDetail.getName()+" "+playBean.getEpi());
            if(listCachePopupWindow != null){
                listCachePopupWindow.setSeriesData(seriesDetail.getSource());
            }
        }
    }

    @Override
    public void fillRecommendData(DetailRecomendData recomendData) {
        if (recomendData.getItems().size() == 0){
            recommandrlt.setVisibility(View.GONE);
        }
        recommendAdapter.setData((ArrayList<DetailRecomendData.ItemsBean>) recomendData.getItems(), recomendData.getStyle());
        recommendAdapter.notifyDataSetChanged();
    }

    @Override
    public void changeCp(ProgramDetail programDetail) {
        this.programDetail = programDetail;
        cpListFilter();

        if(videoType == VideoType.tv || videoType == VideoType.anim) {
            tvAdapter.setCpId(cpidStr);
            tvAdapter.isChangeCp(true);
            getSeriesArray(true);
        }
        else if(videoType == VideoType.movie || videoType == VideoType.microcinema) {
            startPlay(programDetail.getSrc());
            presenter.getSeriesListValue(idStr, cpidStr, 1, 20, 0);
        }
    }

    /**
     * 获取服务器所以cp的数据，下载cp图片
     * @param cpDataNet
     */
    @Override
    public void fillCpData(CpDataNet cpDataNet) {
        for(final CpDataNet.ItemsBean cpBean : cpDataNet.getItems()) {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/.tvfanfile",
                    cpBean.getCpid()+".jpg");
            if(!file.exists()&&!TextUtils.isEmpty( cpBean.getPicurl()))
                presenter.downloadPic(cpBean.getCpid(), cpBean.getPicurl());
        }
        setCurrCpPic();
    }

    private void cpListFilter() {
        cpState = 1;
        if(programDetail.getCplist().size() > 0) {
            boolean isPPTV = false;
            for(ProgramDetail.CplistBean cplistBean : programDetail.getCplist()) {
                if("cpsdkasd3".equals(cplistBean.getCpid())) {
                    isPPTV = true;
                    break;
                }
            }
            if (isPPTV) {
                int pos = -1;
                int count = programDetail.getCplist().size();
                for(int i=0; i<count; i++) {
                    if("cpsdksdf8".equals(programDetail.getCplist().get(i).getCpid())) {
                        pos = i;
                        break;
                    }
                }
                if(pos!=-1)
                    programDetail.getCplist().remove(pos);
            }
            else {
                for(ProgramDetail.CplistBean cplistBean : programDetail.getCplist()) {
                    if("cpsdksdf8".equals(cplistBean.getCpid())) {
                        programDetail.getCplist().clear();
                        programDetail.getCplist().add(cplistBean);
                        cpState = 2;
                        break;
                    }
                }
            }
        }
    }

    private boolean checkCp(String cpidStr) {
        boolean is = false;
        for(ProgramDetail.CplistBean cplistBean : programDetail.getCplist()) {
            if(cplistBean.getCpid().equals(cpidStr))
                is = true;
        }

        return is;
    }

    @Override
    public void fillDetailValue(ProgramDetail programDetail) {
        if(programDetail.getCplist() == null) {
            programDetail.setCplist(new ArrayList<>());
        }
        presenter.getRecommendData(idStr, programDetail.getType());
        this.programDetail = programDetail;
        cpListFilter();
        programName.setText(programDetail.getName());
        if(TextUtils.isEmpty(programDetail.getPlayCount()))
            rlt_playcount.setVisibility(View.GONE);
        else
            playCount.setText(programDetail.getPlayCount());
        release.setText(programDetail.getRelease());
        score.setText(programDetail.getScore());
        subType.setText(programDetail.getSubType());
        zone.setText(programDetail.getZone());

        if(!TextUtils.isEmpty(programDetail.getPrompt())){
            artsSelectionsTv.setText(programDetail.getPrompt());
        }else{
            artsSelectionsTv.setText("更多");
        }
        getVideoType(programDetail.getType());
        setView();
        if(TextUtils.isEmpty(programDetail.getDirector())){
            directorTr.setVisibility(View.GONE);
            ViewGroup.LayoutParams layoutParams = commonTlt.getLayoutParams();
            layoutParams.height = layoutParams.height-director.getHeight()-CommonUtil.dip2px(this,9);
            commonTlt.setLayoutParams(layoutParams);
        }else{
            director.setText(programDetail.getDirector());
        }
        actor.setText(programDetail.getActor());
        detailInfo.setText(programDetail.getInfo());

        if(TextUtils.isEmpty(cpidStr) || !checkCp(cpidStr))
            cpidStr = programDetail.getCplist().get(0).getCpid();
        if ("cpsdkasd3".equals(cpidStr) || "cppt05".equals(cpidStr) || "cpsdksdf8".equals(cpidStr)) {
            popCachePopupWindowImg.setBackgroundResource(R.mipmap.no_download_icon_nor);
        }
//        if(cpState == 1)
        choiceVideoView();
        if(videoType == VideoType.acts ||videoType == VideoType.other){

            artsZone.setText(programDetail.getZone());
            artsSubType.setText(programDetail.getSubType());
            artActor.setText(programDetail.getActor());
            if(!TextUtils.isEmpty(programDetail.getRelease())){
                artsRelease.setText(programDetail.getRelease());
            }else{
                artsRelease.setText("未知");
            }
            presenter.getSeriesListValue(idStr, cpidStr, artsLPageNow,pageSize,0);
        }
        else if(videoType == VideoType.tv || videoType == VideoType.anim){
            tvAdapter.setCpId(cpidStr);
            getSeriesArray(false);
            setPlayName(programDetail.getName()+"  "+epi);
        }
        else {
            setPlayName(programDetail.getName());
            presenter.getSeriesListValue(idStr, cpidStr, 1, 20, 0);
        }
        findCollectBean();
        chenckCpPic();

//        //添加百度联盟广告
//        RelativeLayout rl1 = new RelativeLayout(this);
//        RelativeLayout.LayoutParams rllp1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
//                adView.getMeasuredHeight());
//        rl1.setLayoutParams(rllp1);
//        scroll.addView(rl1);
    }

    private void choiceVideoView() {
        if(cpState == 1) {
            if(videoLayout.getChildCount()>0) {
                videoView.unRegisterSensor();
                videoView.release();
                lastVideoView = videoView;
                videoLayout.removeViewAt(0);
            }
            if("cpsdkasd3".equals(cpidStr)) {
                if (videoViewMap.get("2") == null) {
                    VodPlayerVideoView videoView2 = new VodPlayerVideoView(this);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    videoView2.setLayoutParams(params);
                    videoView2.init(this, 2,1);
                    videoView2.setOnBackListener(this);
                    videoViewMap.put("2",videoView2);
                }
                videoView = videoViewMap.get("2");
            }
            else {
                if (videoViewMap.get("0") == null) {
                    VodPlayerVideoView videoView = new VodPlayerVideoView(this);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    videoView.setLayoutParams(params);
                    videoView.init(this, 0,1);
                    videoView.setOnBackListener(this);
                    EventBus.getDefault().register(videoView);
                    videoViewMap.put("0",videoView);
                }

                videoView = videoViewMap.get("0");
            }
            videoLayout.addView(videoView);
            videoView.registerSensor();
            initSelectionsPopupWindow(videoView.videoHeight());
        }
        else {
            ImageView imageView = new ImageView(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CommonUtil.screenWidth(this) * 9 / 16);
            imageView.setLayoutParams(params);
            imageView.setClickable(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            String picUrl = "";
            picUrl = programDetail.getPicurl();
            GlideProxy.getInstance().loadLetvImage(this, picUrl, imageView);

            ImageView imageView2 = new ImageView(this);
            RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params2.addRule(RelativeLayout.CENTER_IN_PARENT);
            imageView2.setLayoutParams(params2);
            imageView2.setImageResource(R.mipmap.hot_play_play_btn);

            ImageButton back = new ImageButton(this);
            RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(CommonUtil.dip2px(this, 26), CommonUtil.dip2px(this, 26));
            params3.setMargins(CommonUtil.dip2px(this, 10), CommonUtil.dip2px(this, 6), 0, 0);
            back.setLayoutParams(params3);
            back.setImageResource(R.mipmap.play_right_btn);
            back.setBackgroundColor(Color.TRANSPARENT);
            back.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    finish();
                }
            });

            videoLayout.addView(imageView);
            videoLayout.addView(imageView2);
            videoLayout.addView(back);
            imageView.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    letvClick();
                }
            });
            initSelectionsPopupWindow(imageView.getHeight());
        }
    }

    private void letvClick() {
        if(netCheck()){
            return;
        }
        if(isInstallLetv()) {
            letvPlay();
        }
        else {
            Observable.create(new Observable.OnSubscribe<Boolean>() {
                @Override
                public void call(Subscriber<? super Boolean> subscriber) {
                    subscriber.onStart();
                    subscriber.onNext(slientInstall());
                    subscriber.onCompleted();
                }
            })
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            if (updateCrackDialog != null) {
                                updateCrackDialog.dismiss();
                            }
                            updateCrackDialog = new UpdateCrackDialog(ProgramDetailActivity.this, R.style.UpdateDialog);
                            updateCrackDialog.show();
                            updateCrackDialog.setCancelable(false);
                        }
                    })
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {

                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                        @Override
                        public void onNext(Boolean aBoolean) {
                            if(aBoolean) {
                                updateCrackDialog.dismiss();
//                                    letvPlay();
                            }
                        }
                    });
        }
    }

    /**
     * 是否安装乐视插件
     * @return
     */
    private boolean isInstallLetv () {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo("com.letv.android.client", 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return  packageInfo != null ? true : false;
    }

    /**
     * 静默安装
     *
     * @return
     */
    public boolean slientInstall() {
        createFile(); // 进行资源的转移 将assets下的文件转移到可读写文件目录下
        File file = new File(Environment.getExternalStorageDirectory()
                .getPath() + "/temp.apk");

        boolean result = false;
        Process process = null;
        OutputStream out = null;
        System.out.println(file.getPath());
        if (file.exists()) {
            System.out.println(file.getPath() + "==");
            try {
                process = Runtime.getRuntime().exec("su");
                out = process.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(out);
                dataOutputStream.writeBytes("chmod 777 " + file.getPath()
                        + "\n"); // 获取文件所有权限
                dataOutputStream
                        .writeBytes("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install -r "
                                + file.getPath()); // 进行静默安装命令
                // 提交命令
                dataOutputStream.flush();
                // 关闭流操作
                dataOutputStream.close();
                out.close();
                int value = process.waitFor();

                // 代表成功
                if (value == 0) {
                    Log.e("hao", "安装成功！");
                    result = true;
                } else if (value == 1) { // 失败
                    Log.e("hao", "安装失败！");
                    result = false;
                } else { // 未知情况
                    Log.e("hao", "未知情况！");
                    result = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!result) {
                Log.e("hao", "root权限获取失败，将进行普通安装");
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file),
                        "application/vnd.android.package-archive");
                startActivity(intent);
                result = true;
            }
        }
        return result;
    }

    /**
     * 将assets下的apk复制到sdk下
     */
    public void createFile() {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = ProgramDetailActivity.this.getAssets().open("LetvClient_V1.2_From_6845.apk");
            File file = new File(Environment.getExternalStorageDirectory()
                    .getPath() + "/temp.apk");
            file.createNewFile();
            fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

    }

    private PlayBean playbean;
    private void startPlay(String playUrl) {
        playbean = new PlayBean();
        if("cpsdkasd3".equals(cpidStr)) {
            playbean.setPptvUrl(playUrl);
        }
        else {
            String url = playUrl+"&MovieName="+programDetail.getName()+"###"+(playBean.getName().replace(" ", ""))+"&MovieId="+playBean.getId();
            playbean.setUrl(url);
        }
        playbean.setProgramId(programDetail.getId());
        if(this.playBean != null)
            playbean.setmId(this.playBean.getId());
        if(netCheck()) {
            return;
        }
        if(videoView == null)
            return;
        if(!videoView.isStop())
            videoView.stop();
        videoView.startPlay(playbean);
        setHistory();
    }

    /**
     * 进行流量管理；当网络变化时进行监听
     */
    private void netStateChangeListen() {
        if(taNetChangeObserver == null) {
            taNetChangeObserver = new TANetChangeObserver() {
                @Override
                public void onConnect(int type) {
                    if(type == ConnectivityManager.TYPE_MOBILE) {//移动流量
                        initNetChangeDialog();
                        if(ShareElement.isIgnoreNetChange == -1) {
                            if(videoView!=null) {
                                pointTime = videoView.getBreakTime();
                                videoView.unRegisterSensor();
                                videoView.stop();
                                videoView.stopPreLoading();
                            }
                            if(!ProgramDetailActivity.this.isFinishing())
                                netChangeDialog.show();
                        }

                    }
                    else if(type == ConnectivityManager.TYPE_WIFI) {
                        if(netChangeDialog!=null&&netChangeDialog.isShowing())
                            netChangeDialog.dismiss();
                        if(videoView!=null && cpState == 1 && videoView.isStop()) {
                            videoView.startPlay(playbean);
                            setHistory();
                        }
                        ShareElement.isIgnoreNetChange = -1;
                        Toast.makeText(ProgramDetailActivity.this.getApplicationContext(),"已连接wifi网络!",Toast.LENGTH_LONG).show();
                    }
                }
            };
        }
        TANetworkStateReceiver.registerObserver(taNetChangeObserver);
    }

    /**
     * 当没有弹出流量提醒框，但是已经不是wifi状态;或者点击取消观看，点击其他视频
     */
    private void netStateCheck() {
        if(videoView!=null && cpState == 1) {
            videoView.unRegisterSensor();
            pointTime = videoView.getBreakTime();
            videoView.stopPreLoading();
            videoView.stop();
        }
        initNetChangeDialog();
        netChangeDialog.show();
    }

    /**
     * 初始化NetChangeDialog
     */
    private void initNetChangeDialog () {
        if(netChangeDialog == null) {
            netChangeDialog = new NetChangeDialog(this, R.style.ExitDialog);
            netChangeDialog.setCancelable(false);
            //忽略网络变化继续观看
            netChangeDialog.getOkButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(videoView!=null && cpState == 1) {
                        videoView.registerSensor();
                        videoView.startPlay(playbean);
                        setHistory();
                    }
                    else if (cpState == 2) {
                        letvClick();
                    }
                    netChangeDialog.dismiss();
                    ShareElement.isIgnoreNetChange = 2;
                }
            });
            //停止观看
            netChangeDialog.getCancelButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(videoView!=null && cpState==1) {
                        videoView.stop();
                        videoView.setIsStopFromNetChange(true);
                        videoView.showNetPic(programDetail.getPicurl());
                    }
                    netChangeDialog.dismiss();
                    ShareElement.isIgnoreNetChange = 1;
                }
            });
        }
    }

    /**
     * 播放视频
     * @param isArt
     */
    private void playNetOrLocal (boolean isArt) {
        if(cpState == 2)
            return;
        if(isArt) {
            if(!downloadInfoHashMap.isEmpty()) {
                DownloadInfo downloadInfo = downloadInfoHashMap.get(playBean.getId());
                if(downloadInfo!=null && !TextUtils.isEmpty(downloadInfo.fileLocation)) {
                    videoView.startPlay(downloadInfo.fileLocation);
                    firstPlayHistory();
                    isPlay = true;
                    return;
                }
            }
        }
        else {
            if(!downloadInfoHashMap.isEmpty()) {
                Iterator i =  downloadInfoHashMap.entrySet().iterator();
                while (i.hasNext()) {
                    Map.Entry entry = (Map.Entry) i.next();
                    DownloadInfo downloadInfo = (DownloadInfo) entry.getValue();
                    if (!TextUtils.isEmpty(downloadInfo.fileLocation)) {
                        videoView.startPlay(downloadInfo.fileLocation);
                        firstPlayHistory();
                        isPlay = true;
                        return;
                    }
                }
            }
        }

        if(!TextUtils.isEmpty(programDetail.getSrc())){
            startPlay(playBean.getPlayUrl());
            firstPlayHistory();
            isPlay = true;
        }else{
            TipUtil.showSnackTip(videoView,"对不起，源已失效");
        }
    }

    private String currType;

    public void setView(){
        if (videoType == VideoType.movie || videoType == VideoType.microcinema) {
            commonTlt.setVisibility(View.VISIBLE);
        } else if (videoType == VideoType.tv || videoType == VideoType.anim) {
            commonTlt.setVisibility(View.VISIBLE);
            tvRlt.setVisibility(View.VISIBLE);
            tvAdapter = new SelectionsPagerAdapter(this,presenter,idStr,cpidStr,playerHistoryBean);
            tvAdapter.setCpState(cpState);
            teltelSelectContainer.setAdapter(tvAdapter);
            telSelectionsTab.setTabMode(TabLayout.MODE_SCROLLABLE);
            telSelectionsTab.setupWithViewPager(teltelSelectContainer);
            telSelectionsTab.invalidate();
            if(playerHistoryBean != null) {
                int pos = playerHistoryBean.getPlayPos();
                playPos = pos%tvPageSize ;
                pageNum = pos/tvPageSize;
//                teltelSelectContainer.setCurrentItem(pageNum);
            }
        } else {
            if(playerHistoryBean != null) {
                int pos = playerHistoryBean.getPlayPos();
                playPos = pos%pageSize ;
                pageNum = pos/pageSize;
                artsLPageNow = playerHistoryBean.getPlayPos()/pageSize +1;
                artsFPageNow = playerHistoryBean.getPlayPos()/pageSize +1;
            }
            artsTlt.setVisibility(View.VISIBLE);
            artsRlt.setVisibility(View.VISIBLE);
            //设置布局管理器
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            artsSelectorRecycle.setLayoutManager(linearLayoutManager);
            artsSelectorRecycle.addItemDecoration(new DividerItemDecoration(
                    this, DividerItemDecoration.HORIZONTAL_LIST));
            //设置适配器
            artsAdapter = new ArtsAdapter(this);
            artsSelectorRecycle.setAdapter(artsAdapter);
            artsSelectorRecycle.setLoadMoreListener(new DetailHRecyclerView.LoadMoreListener() {
                @Override
                public void loadMore() {
                    artsLPageNow++;
                    if(artsLPageNow>artsPageTotal)
                        return;
                    presenter.getSeriesListValue(idStr, cpidStr, artsLPageNow,pageSize,0);
                }

                @Override
                public void loadLast() {
                    if(isChangeArtsPos)
                        return;
                    artsFPageNow --;
                    if(artsFPageNow<1)
                        return;
                    pageNum --;
                    isChangeArtsPos = true;
                    presenter.getSeriesListValue(idStr, cpidStr, artsFPageNow,pageSize,0);
                }
            });
        }
    }
    PowerManager.WakeLock mWakeLock;

    @Override
    public void initView() {
        BusProvider.getInstance().register(this);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "MyTag");
        String data = getIntent().getDataString();
        idStr = getIntent().getStringExtra("idStr");
        try{
            if(!TextUtils.isEmpty(data)){
                String[] split = data.split(",");//以data/切割data字符串
                idStr = split[1];
            }
            else{
                idStr = getIntent().getStringExtra("idStr");
            }
        }catch (Exception ex){
            idStr = getIntent().getStringExtra("idStr");
        }
        presenter.enterDetailLog(idStr, this);
        praiseData =  presenter.loadPraise(idStr);
        if(praiseData != null){
            praiseCheckBox.setChecked(praiseData.getValid());
        }
        playerHistoryBean = presenter.getPlayHistory(idStr);
        if(playerHistoryBean != null) {
            cpidStr = playerHistoryBean.getCpId();
            epi = playerHistoryBean.getEpi();
        }
        seriesArray = new ArrayList<>();
        playdetailPlaytime.setFocusable(true);
        playdetailPlaytime.setFocusableInTouchMode(true);
        playdetailPlaytime.requestFocus();
        recommendAdapter = new RecommandAdapter(this);
        recommendRecycle.setAdapter(recommendAdapter);
        LinearLayoutManager recommendManager = new LinearLayoutManager(this);
        recommendManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recommendRecycle.setLayoutManager(recommendManager);
        loadingLayout.setOnRetryClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                if(playerHistoryBean != null)
                    presenter.loadDetailData(idStr,playerHistoryBean.getMid(),cpidStr);//加载详情
                else
                    presenter.loadDetailData(idStr,"",cpidStr);
            }
        });
        if(playerHistoryBean != null)
            presenter.loadDetailData(idStr,playerHistoryBean.getMid(),cpidStr);//加载详情
        else
            presenter.loadDetailData(idStr,"",cpidStr);

        downloadInfoHashMap = OfflineCacheUtil.getDownloadInfoHashMap(this, idStr);
        praiseClick();
    }

    void praiseClick(){
        RxView.clicks(praiseMllt)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if(praiseData!=null){
                            praiseData.setValid(praiseData.getValid()? false:true);
                        }else{
                            praiseData = new PraiseData();
                            praiseData.setValid(true);
                            praiseData.setProgramId(idStr);
                        }
                        praiseCheckBox.setChecked(praiseData.getValid());
                        if(praiseData.getValid()){
                            presenter.sendIntegralLog("like",idStr);
                        }
                        if(praiseData.getValid() == true){
                            addPraiseTv.setVisibility(View.VISIBLE);
                            ObjectAnimator oa = ObjectAnimator.ofFloat(addPraiseTv, "alpha", 1.0f,0.0f);
                            ObjectAnimator oa2 = ObjectAnimator.ofFloat(addPraiseTv, "scaleY", 1f,1.2f);
                            ObjectAnimator oa3 = ObjectAnimator.ofFloat(addPraiseTv, "scaleX", 1f,1.2f);
                            ObjectAnimator oa4= ObjectAnimator.ofFloat(addPraiseTv, "Y",addPraiseTv.getY(), -10f);
                            final float y = addPraiseTv.getY();
                            oa4.setInterpolator(new DecelerateInterpolator());
                            AnimatorSet animSet = new AnimatorSet();
                            animSet.play(oa).with(oa2).with(oa3).with(oa4);
                            animSet.setDuration(1000);
                            animSet.start();
                            animSet.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    addPraiseTv.setY(y);
                                    addPraiseTv.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                        }
                        presenter.clickPraise(praiseData);
                    }
                });
    }


    /**
     * 检测当前节目集所有的源图片本地是否存在
     */
    private void chenckCpPic() {
        for(ProgramDetail.CplistBean  cpBean : programDetail.getCplist()) {
            File file = new File(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS) + "/.tvfanfile", cpBean.getCpid()+".jpg");
            if(!file.exists()) {
                presenter.getCpData();
                return;
            }
        }
        setCurrCpPic();
    }

    void initSelectionsPopupWindow(int height){
        if(selectionsPopupWindow == null){
            selectionsPopupWindow = new SelectionsPopupWindow(this, CommonUtil.screenHeight(this) - height - CommonUtil.getStatueHeight(this), new DetailVRecyclerView.LoadMoreListener() {
                @Override
                public void loadMore() {
                    artsLPageNow++;
                    if(artsLPageNow>artsPageTotal)
                        return;
                    presenter.getSeriesListValue(idStr, cpidStr, artsLPageNow,pageSize,1);
                }

                @Override
                public void loadLast() {
                    if(isChangeArtsPos)
                        return;
                    artsFPageNow --;
                    if(artsFPageNow<1 )
                        return;
                    pageNum --;
                    isChangeArtsPos = true;
                    presenter.getSeriesListValue(idStr, cpidStr, artsFPageNow,pageSize,0);
                }
            });
            selectionsPopupWindow.setCpState(cpState);
        }
    }
    /**
     * 设置当前源的图片
     */
    private void setCurrCpPic () {
        Drawable drawable = Drawable.createFromPath(new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS) + "/.tvfanfile", cpidStr+".jpg").getPath());
        if(drawable != null)
            resource.setImageDrawable(drawable);
        else
            resource.setImageDrawable(getResources().getDrawable(R.mipmap.sohu));
    }

    /**
     *选源列表
     */
    public void initPopupWindowView() {
        if(programDetail!=null && (programDetail.getCplist() == null || programDetail.getCplist().size()==0))
            return;
        cpChecked = -1;
        View customView = getLayoutInflater().inflate(R.layout.popupwindow_cp,
                null, false);
        LinearLayout cp_frame_llt = (LinearLayout) customView.findViewById(R.id.cp_frame_llt);
        if (programDetail == null) {
            return;
        }
        if(BuildConfig.DEBUG){
            JLog.e("cpList-check", TextUtils.isEmpty(programDetail.getId())?"empty":programDetail.getId());
        }
        for(int i = 0; i < programDetail.getCplist().size(); i++) {
            RadioButton button = new RadioButton(this);
            button.setText(" "+programDetail.getCplist().get(i).getCpname());
            if(cpidStr.equals(programDetail.getCplist().get(i).getCpid())) {
                cpChecked = i;
            }
            File file = new File(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS) + "/.tvfanfile", programDetail.getCplist().get(i).getCpid()+".jpg");
            Drawable icon = Drawable.createFromPath(file.getPath());
            if(icon == null)
                icon = getResources().getDrawable(R.mipmap.sohu);
            //icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
            icon.setBounds(0, 0, CommonUtil.dip2px(this, 25), CommonUtil.dip2px(this, 25));
            button.setCompoundDrawables(icon, null, null, null);
            button.setButtonDrawable(null);
            button.setTag(i + "");
            button.setButtonDrawable(android.R.color.transparent);
            button.setTextSize(13);
            button.setPadding(CommonUtil.dip2px(this, 10),0,0,0);
            if (cpChecked == i) {
                button.setTextColor(Color.parseColor("#F02323"));
                button.setChecked(true);
            } else {
                button.setTextColor(getResources().getColor(R.color.cp_color));
            }
            cp_frame_llt.addView(button, i);
            button.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        view.setBackgroundResource(R.color.cp_check_color);
                    } else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                        view.setBackgroundResource(R.color.cp_default_color);
                    }
                    return false;
                }
            });

            button.setOnClickListener(
                    new NoDoubleClickListener() {
                        @Override
                        public void onNoDoubleClick(View view) {
                            popupwindow.dismiss();
//                            if (cpChecked == Integer.parseInt((String) view.getTag()))
//                                return;
                            cpChecked = Integer.parseInt((String) view.getTag());
                            if(cpidStr.equals(programDetail.getCplist().get(cpChecked).getCpid()))
                                return;
                            cpidStr = programDetail.getCplist().get(cpChecked).getCpid();
                            if ("cpsdkasd3".equals(cpidStr) || "cppt05".equals(cpidStr)) {
                                popCachePopupWindowImg.setBackgroundResource(R.mipmap.no_download_icon_nor);
                            } else {
                                popCachePopupWindowImg.setBackgroundResource(R.drawable.cache_selector);
                            }
                            choiceVideoView();
                            setCurrCpPic();

                            isChangeCp = true;
                            /*if (videoType == VideoType.movie || videoType == VideoType.microcinema) {
                                presenter.changeCp(idStr, "", cpidStr);
                                pageNum = 0;
                                playPos = 0;
                            }
                            else */if(videoType == VideoType.tv || videoType == VideoType.anim) {
                                presenter.changeCp(idStr, "", cpidStr);
                            }
                            else {
                                pageNum = 0;
                                playPos = 0;
                                artsSerisTotal = 0;
                                artsFPageNow = 1;
                                artsLPageNow = 1;
                                seriesDetail = null;
                                if (artsAdapter != null)
                                    artsAdapter.clearHolders();
                                selectionsPopupWindow.clearHolders();
                                presenter.getSeriesListValue(idStr, cpidStr, 1, pageSize, 0);
                            }
                        }
                    });

        }
        popupwindow = new PopupWindow(customView, CommonUtil.dip2px(this, 110), CommonUtil.dip2px(this, programDetail.getCplist().size() * 40 + 15));
        popupwindow.setOutsideTouchable(true);

        final int[] location = new int[2];
        cpllt.getLocationOnScreen(location);
        popupwindow.showAtLocation(cpllt, Gravity.NO_GRAVITY, location[0] - popupwindow.getWidth() + cpllt.getWidth(), location[1] + cpllt.getHeight() + CommonUtil.dip2px(this, 5));
        customView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getRawX() > location[0] && event.getRawX() < location[0] + cpllt.getWidth() && event.getRawY() > location[1] && event.getRawY() < location[1] + cpllt.getHeight()) {
                    return false;
                }
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    popupwindow = null;
                }
                return false;
            }
        });
    }

    public void showSelectionsPopuwindows() {
        if (selectionsPopupWindow != null && videoView != null)
            selectionsPopupWindow.showAtLocation(scrollView, Gravity.NO_GRAVITY, 0, (int) videoView.getHeight() + CommonUtil.getStatueHeight(this));
    }

    @Override
    public void onBackPressed() {
        if((videoView != null && videoView.isLock)){
            return;
        }

        if(videoView != null && videoView.isLandScape()){
            videoView.handleFullHalfScrren(ShareElement.PORTRAIT);
            return;
        }
        if (popupwindow != null && popupwindow.isShowing()) {
            popupwindow.dismiss();
            return;
        }
        super.onBackPressed();
    }

    private long pointTime;
    @Override
    protected void onResume() {
        super.onResume();
        mWakeLock.acquire();
        TANetworkStateReceiver.registerObserver(taNetChangeObserver);
        if((netChangeDialog!=null&&netChangeDialog.isShowing()) || netCheck())
            return;
        if(videoView!=null && cpState == 1) {
            videoView.registerSensor();
//            EventBus.getDefault().register(videoView);
//            videoView.startPlay(playbean);
//            videoView.skipAd();
//            setHistory();
            videoView.start();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mWakeLock.release();
        if(netChangeDialog!=null&&netChangeDialog.isShowing())
            return;
        if(videoView!=null){
//            EventBus.getDefault().unregister(videoView);
            if(!videoView.isStop())
                pointTime = videoView.getBreakTime();
            else
                pointTime = 0;
            videoView.unRegisterSensor();
//            videoView.stopPreLoading();
//            videoView.stop();
            videoView.pause();
        }
        savePlayHistory();
    }

    @Override
    protected void onStop() {
        super.onStop();
        TANetworkStateReceiver.removeRegisterObserver(taNetChangeObserver);
    }

    @Override
    protected void onDestroy() {
        recycleMemory();
        if(videoView!=null) {
            videoView.setScreenMode();
            try{
                videoView.release();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        if(videoViewMap.get("0") != null) {
            EventBus.getDefault().unregister(videoViewMap.get("0"));
        }
        if (presenter!=null){
            presenter.release();
        }
        if(gridCachePopupWindow != null){
            BusProvider.getInstance().unregister(gridCachePopupWindow);
        }
        BusProvider.getInstance().unregister(this);
        if(listCachePopupWindow != null){
            BusProvider.getInstance().unregister(listCachePopupWindow);
        }
//        ShareElement.artsAdapterHolder = null;
//        ShareElement.lastHolder = null;
//        ShareElement.artsPopuHolder = null;
        adView.destroy();
        super.onDestroy();
    }

    private void recycleMemory(){
        if(artsAdapter != null){
            ShareElement.artsAdapterHolder = null;
        }
    }

    /**
     * 保存播放记录
     */
    private void savePlayHistory () {
        if(!isPlay)
            return;
        if(programDetail != null && cpState==1) {
//            presenter.delPlayHistory(idStr);
            if(playerHistoryBean == null)
                playerHistoryBean = new PlayerHistoryBean();
            playerHistoryBean.setPointTime(pointTime);
            if (videoType == VideoType.movie || videoType == VideoType.microcinema) {

            } else if (videoType == VideoType.tv || videoType == VideoType.anim) {
                playerHistoryBean.setPlayPos(playPos+(tvPageSize*pageNum));
            }
            else {
                playerHistoryBean.setPlayPos(playPos+(pageSize*pageNum));
            }
            playerHistoryBean.setCpId(cpidStr);
            playerHistoryBean.setProgramId(idStr);
            playerHistoryBean.setProgramName(programDetail.getName());
            playerHistoryBean.setProgramType(currType);
            playerHistoryBean.setPicUrl(programDetail.getPicurl());
            playerHistoryBean.setMediaType(1);
            if(playBean != null) {
                playerHistoryBean.setMid(playBean.getId());
                playerHistoryBean.setEpi(playBean.getEpi());
                playerHistoryBean.setProgramName(programDetail.getName());
            }
            else {
                playerHistoryBean.setProgramName(programDetail.getName());
            }
            presenter.insertPlayHistory(playerHistoryBean);
        }
        else if(cpState == 2) {
            if(playerHistoryBean == null)
                playerHistoryBean = new PlayerHistoryBean();
            playerHistoryBean.setProgramId(idStr);
            playerHistoryBean.setCpId(cpidStr);
            playerHistoryBean.setProgramName(programDetail.getName());
            playerHistoryBean.setPicUrl(programDetail.getPicurl());
            playerHistoryBean.setMediaType(1);
            presenter.insertPlayHistory(playerHistoryBean);
        }
    }

    private View  cacheView;

    /**
     * 动漫和电视剧获取tab
     * @param isChangeCp
     */
    public void getSeriesArray(boolean isChangeCp){
        seriesArray.clear();
        seriesArray.addAll(programDetail.getTabs());
        tvAdapter.setAdapter(seriesArray);

        if(isChangeCp) {
            if(pageNum<seriesArray.size()) {
                tvAdapter.setEpi(epi);
            }
            else {
                pageNum = seriesArray.size()-1;
            }
            tvAdapter.setPageNum(pageNum);
            if(gridCachePopupWindow != null) {
                BusProvider.getInstance().unregister(gridCachePopupWindow);
                gridCachePopupWindow = null;
                gridCachePopupWindow = new GridCachePopupWindow(this, CommonUtil.screenHeight(this) - (int) videoView.getHeight() - CommonUtil.getStatueHeight(this)
                        , seriesArray, presenter, idStr, cpidStr, programName.getText().toString(), programDetail.getPicurl(), cacheView);
                BusProvider.getInstance().register(gridCachePopupWindow);

            }
        }
        tvAdapter.notifyDataSetChanged();
        teltelSelectContainer.setCurrentItem(pageNum);

    }

    /**
     * 缓存的popuWindow
     * @param view
     */
    void openCacheWindow(View view) {
        if (programDetail == null || videoView == null) {
            return;
        }
        if(videoType == VideoType.tv || videoType == VideoType.anim){
            if(gridCachePopupWindow == null){

                gridCachePopupWindow = new GridCachePopupWindow(this,CommonUtil.screenHeight(this) - (int) videoView.getHeight() - CommonUtil.getStatueHeight(this)
                        ,seriesArray,presenter,idStr, cpidStr, programName.getText().toString(), programDetail.getPicurl(), view);
                BusProvider.getInstance().register(gridCachePopupWindow);
            }
            gridCachePopupWindow.showAtLocation(scrollView, Gravity.NO_GRAVITY, 0, (int) videoView.getHeight() + CommonUtil.getStatueHeight(this));

        }else{
            if(listCachePopupWindow == null){
                listCachePopupWindow = new ListCachePopupWindow(this, CommonUtil.screenHeight(this) - (int) videoView.getHeight() - CommonUtil.getStatueHeight(this),presenter,idStr,cpidStr, programName.getText().toString(), programDetail.getPicurl(), view);
                BusProvider.getInstance().register(listCachePopupWindow);
            }
            listCachePopupWindow.showAtLocation(scrollView, Gravity.NO_GRAVITY, 0, (int) videoView.getHeight() + CommonUtil.getStatueHeight(this));
            listCachePopupWindow.setTotalPage(artsPageTotal);
        }
    }

    /**
     * 横竖屏切换
     * @param landscapeState
     */
    @Override
    public void halfFullScreenSwitch(int landscapeState) {
        if (landscapeState == ShareElement.PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            contentLayout.setVisibility(View.VISIBLE);
//            rl.setVisibility(View.VISIBLE);
        }
        else if (landscapeState == ShareElement.LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            contentLayout.setVisibility(View.GONE);
//            rl.setVisibility(View.VISIBLE);
        }
        else if (landscapeState == ShareElement.REVERSILANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            contentLayout.setVisibility(View.GONE);
//            rl.setVisibility(View.VISIBLE);
        }
        halfFullScreen(landscapeState);
    }

    private void dimissPopuWindow(PopupWindow popupwindow) {
        if(popupwindow!=null && popupwindow.isShowing())
            popupwindow.dismiss();
    }

    /**
     * 横竖屏切换的操作
     * @param landscapeState
     */
    private void halfFullScreen(int landscapeState) {
        if (landscapeState == ShareElement.PORTRAIT) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(params);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            showBottonUIMenu();
        }
        else {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(params);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            hideBottomUIMenu();
            dimissPopuWindow(selectionsPopupWindow);
            dimissPopuWindow(gridCachePopupWindow);
            dimissPopuWindow(listCachePopupWindow);
            dimissPopuWindow(popupwindow);
        }
    }

    private boolean netCheck() {
        if(isNetCheck()) {
            netStateCheck();
            return true;
        }
        return false;
    }

    /**
     * 网络未变时是否弹出流量提示框
     * @return
     */
    private boolean isNetCheck() {
        if((ShareElement.isIgnoreNetChange == -1 && NetworkUtil.getCurrentNetworkType(this) == ConnectivityManager.TYPE_MOBILE)
                ||ShareElement.isIgnoreNetChange == 1)
            return true;
        return false;
    }

    @Override
    public AudioManager getAudioManager() {
        return (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    void getVideoType(String type){
        switch (type){
            case "电影":
                videoType = VideoType.movie;
                currType = "1";
                break;
            case "微电影":
                videoType = VideoType.microcinema;
                currType = "1";
                break;
            case "电视剧":
                videoType = VideoType.tv;
                currType = "2";
                break;
            case "动漫":
                videoType = VideoType.anim;
                currType = "2";
                break;
            case "少儿":
                videoType = VideoType.anim;
                currType = "2";
                break;
            case "综艺":
                videoType = VideoType.acts;
                currType = "3";
                break;
            default:
                videoType = VideoType.acts;
                currType = "3";
        }
    }

    /**
     * 第一次播放设置播放断点
     */
    private void firstPlayHistory() {
        if(isFirstPlay) {
            if(playerHistoryBean != null)
                videoView.setHistory(playerHistoryBean.getPointTime(), true);
            isFirstPlay = false;
        }
    }

    private void setHistory () {
        videoView.setHistory(pointTime, true);
        pointTime = 0;
    }

    private void letvPlay () {
        if(playBean == null || TextUtils.isEmpty(playBean.getPlayUrl()) || !playBean.getPlayUrl().contains(",")) {
            Toast.makeText(ProgramDetailActivity.this, "该节目暂时无法播放", Toast.LENGTH_SHORT).show();
            return;
        }
        presenter.pvLog(playBean.getId(), this);
        String aid = playBean.getPlayUrl().substring(0,playBean.getPlayUrl().indexOf(","));
        String vid = playBean.getPlayUrl().substring(playBean.getPlayUrl().indexOf(",")+1, playBean.getPlayUrl().length());
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("liteclient://mliteAction?aid="+aid+"&vid="+vid+"&launchMode=2&packageName=com.sumavision.talktv2&form=&ref=&back=&pcode="));
        try {
            ProgramDetailActivity.this.startActivity(intent);
            isPlay = true;
        }
        catch (Exception e){
            Log.e("letv", e.toString());
        }
    }

    /**
     * 切集后具体播放
     * @param bean
     */
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(AppGlobalConsts.EventType.TAG_A)})
    public void play(SeriesDetail.SourceBean bean){
        if(bean == null)
            return;
        this.playBean = bean;
        if(cpState == 1) {
            isPlay = true;
            presenter.pvLog(bean.getId(), this);
            playbean = new PlayBean();
            if (bean.isCached() && bean.getLocalPath()!=null) { // 播放缓存的本地视频
                playbean.setPlayPath(bean.getLocalPath());
                videoView.isShowDefinitionChange(false);
                videoView.startPlay(playbean);
            }
            else {
                /*if("cpsdkasd3".equals(cpidStr)) {
//                    playbean.setPptvUrl(bean.getPlayUrl());
//                    playbean.setProgramId(programDetail.getId());
//                    playbean.setmId(bean.getId());
                    videoView.stop();
//                    if(isNetCheck()){
//                        netStateCheck();
//                        return;
//                    }
//                    videoView.startPlay(playbean);

                }
                else {
                    try{
//                        String url = bean.getPlayUrl()+"&MovieName="+programDetail.getName()+"###"+(bean.getName().replace(" ", ""))+"&MovieId="+bean.getId();
//                        playbean.setUrl(url);
//                        playbean.setProgramId(programDetail.getId());
//                        if(this.playBean != null)
//                            playbean.setmId(this.playBean.getId());
                        videoView.stop();
//                        if(isNetCheck()){
//                            netStateCheck();
//                            return;
//                        }
//                        videoView.startPlay(playbean);
                        startPlay(bean.getPlayUrl());
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }*/
                startPlay(bean.getPlayUrl());
            }
            firstPlayHistory();
            try{
                setPlayName(programDetail.getName()+"  "+bean.getEpi());
                epi = bean.getEpi();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            if(videoType == VideoType.tv || videoType == VideoType.anim) {
                letvClick();
            }
        }
    }

    /**
     * 获取从剧集列表点击选集的pageNum和playPos
     * @param playPoss
     */
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(AppGlobalConsts.EventType.TAG_A)})
    public void getPlayPos (int[] playPoss) {
        playPos = playPoss[1];
        if(videoType == VideoType.tv || videoType == VideoType.anim) {
            pageNum = playPoss[0];
            if((playPos+1+pageNum*tvPageSize)>=totalNum)
                isShowNext(false);
            else
                isShowNext(true);
        }
        else {
            if((playPos+1+pageNum*pageSize)>=totalNum)
                isShowNext(false);
            else
                isShowNext(true);
        }
    }

    /**
     * 当前播放完成后进行下一集的选集
     * @param nextPlay
     */
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(AppGlobalConsts.EventType.TAG_A)})
    public void nextPlayForComplete (NextPlay nextPlay) {
        if(videoType == VideoType.tv || videoType == VideoType.anim) {//电视剧、动漫
            playPos++;
            if((playPos+1+pageNum*tvPageSize)>totalNum) {
                presenter.delPlayHistory(idStr);
                playPos--;
                showReplay();
                return;
            }
            else if((playPos+1+pageNum*tvPageSize)==totalNum) {
                isShowNext(false);
            }
            else
                isShowNext(true);
            if(tvAdapter.isNextPage(pageNum, playPos)) {
                playPos = 0;
                pageNum++;
                teltelSelectContainer.setCurrentItem(pageNum);
            }
            tvAdapter.playNext(pageNum, playPos);
        }
        else if(videoType == VideoType.movie || videoType == VideoType.microcinema) {
            showReplay();
        }
        else {
            playPos++;
            if((playPos+1+pageNum*pageSize)>totalNum) {
                presenter.delPlayHistory(idStr);
                playPos--;
                showReplay();
                return;
            }
            else if((playPos+1+pageNum*pageSize)==totalNum)
                isShowNext(false);
            else
                isShowNext(true);
            if(playPos>artsSerisTotal-1) {
                artsLPageNow++;
                presenter.getSeriesListValue(idStr, cpidStr, artsLPageNow,pageSize,1);
                artsPlayNext = true;
            }
            else {
                artsAdapter.setPlayPos(playPos);
                artsAdapter.notifyDataSetChanged();
                selectionsPopupWindow.changeProgram(playPos);
                play(seriesDetail.getSource().get(playPos));
            }
        }
    }

    private void showReplay() {
        videoView.showReplay(true);
    }

    /**
     * 综艺是从哪进行切集  1：详情页    2：popuwindow
     * @param msg
     */
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag(AppGlobalConsts.EventType.TAG_A)})
    public void getPlayPos (ArtsChangeProgramMsg msg) {
        playPos = msg.getPso();
        if(msg.getType() == 2){
            artsAdapter.changeProgram(playPos);
            artsSelectorRecycle.scrollToPosition(playPos);
        }
        else {
            selectionsPopupWindow.changeProgram(playPos);
        }
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("total")})
    public void getTvTotal(String total) {
        totalNum = Integer.parseInt(total);
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("choicePlayer")})
    public void loadPic(SeriesDetail.SourceBean bean) {
        hideProgressBar();
        if(cpState == 2) {
            this.playBean = bean;
            choiceVideoView();
        }
    }

    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("showNetChangeDialog")})
    public void showNetChangeDialog(String s) {
        if(cpState == 2)
            return;
        netCheck();
    }

    @Override
    public void onBack() {
        finish();
    }
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("selectChannel")})
    public void shareSpecial(String channel) {
        String targetUrl = AppGlobalVars.shareApiHost+"?talktvprogramdetail://tvfan.com/share," + idStr+",";
        if(AppGlobalVars.appKeyResult == null || AppGlobalVars.appKeyResult.getData().size() < 2){
            Toast.makeText(ProgramDetailActivity.this,"分享失败，请稍后重试..",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(programDetail.getPicurl())){
            Toast.makeText(ProgramDetailActivity.this,"分享失败，暂不支持分享",Toast.LENGTH_SHORT).show();
            return;
        }
        if(channel.equals("WEIXIN")){
            PlatformConfig.setWeixin("wxcfaa020ee248a2f2", AppGlobalVars.appKeyResult.getData().get(0).getSecret());
            new ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN)
                    .withText(programDetail.getName())
                    .withMedia(new UMImage(this,programDetail.getPicurl()))
                    .withTargetUrl(targetUrl)
                    .withTitle(programDetail.getName())
                    .setCallback(umShareListener)
                    .share();
        }else if(channel.equals("CIRCLE")){
            PlatformConfig.setWeixin("wxcfaa020ee248a2f2", AppGlobalVars.appKeyResult.getData().get(0).getSecret());
            new ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                    .withText(programDetail.getName())
                    .withTitle(programDetail.getName())
                    .withMedia(new UMImage(this,programDetail.getPicurl()))
                    .withTargetUrl(targetUrl)
                    .setCallback(umShareListener)
                    .share();
        }
        else if(channel.equals("QQ")){
            PlatformConfig.setQQZone("100757629", AppGlobalVars.appKeyResult.getData().get(1).getSecret()); //"04e1ef24fe1baa6858d1de1f0d70dfb2");
            new ShareAction(this).setPlatform(SHARE_MEDIA.QQ)
                    .withText(programDetail.getName())
                    .withMedia(new UMImage(this,programDetail.getPicurl()))
                    .withTargetUrl(targetUrl)
                    .setCallback(umShareListener)
                    .share();
        }else if(channel.equals("ZONE")){
            PlatformConfig.setQQZone("100757629",AppGlobalVars.appKeyResult.getData().get(1).getSecret()); //"04e1ef24fe1baa6858d1de1f0d70dfb2");
            new ShareAction(this).setPlatform(SHARE_MEDIA.QZONE)
                    .withText( programDetail.getName())
                    .withMedia(new UMImage(this,programDetail.getPicurl()))
                    .withTargetUrl(targetUrl)
                    .setCallback(umShareListener)
                    .share();
        } else if(channel.equals("WEIBO")){
            PlatformConfig.setSinaWeibo("2064721383",AppGlobalVars.appKeyResult.getData().get(2).getSecret());
            Config.REDIRECT_URL="http://www.tvfan.cn";
            new ShareAction(this).setPlatform(SHARE_MEDIA.SINA)
                    .withText(programDetail.getName())
                    .withMedia(new UMImage(this,programDetail.getPicurl()))
                    .withTargetUrl(targetUrl)
                    .setCallback(umShareListener)
                    .share();
        }
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat","platform"+platform);
            presenter.sendIntegralLog("share",idStr);
           // Toast.makeText(ProgramDetailActivity.this, platform + " 分享成功", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(ProgramDetailActivity.this,platform + " 分享失败", Toast.LENGTH_SHORT).show();
            if(t!=null){
                Log.d("throw","throw:"+t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
           // Toast.makeText(ProgramDetailActivity.this,platform + " 分享取消", Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}


