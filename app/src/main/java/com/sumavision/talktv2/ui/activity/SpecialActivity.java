package com.sumavision.talktv2.ui.activity;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.http.GlideProxy;
import com.sumavision.talktv2.model.entity.PraiseData;
import com.sumavision.talktv2.model.entity.SpecialContentList;
import com.sumavision.talktv2.model.entity.SpecialDetail;
import com.sumavision.talktv2.presenter.SpecialPresenter;
import com.sumavision.talktv2.ui.activity.base.BaseActivity;
import com.sumavision.talktv2.ui.adapter.SplecialListAdapter;
import com.sumavision.talktv2.ui.iview.ISpecialView;
import com.sumavision.talktv2.ui.widget.LMRecyclerView;
import com.sumavision.talktv2.ui.widget.LoadingLayout;
import com.sumavision.talktv2.ui.widget.MyGifView;
import com.sumavision.talktv2.ui.widget.SelectSharePopupWindow;
import com.sumavision.talktv2.ui.widget.SpecialPopupWindow;
import com.sumavision.talktv2.util.AppGlobalVars;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.NoDoubleClickListener;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by sharpay on 16-5-24.
 */
public class SpecialActivity extends BaseActivity<SpecialPresenter> implements ISpecialView,LMRecyclerView.LoadMoreListener {

    SpecialPresenter presenter;
    @BindView(R.id.img_iv)
    ImageView imgIv;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.recycler_view)
    LMRecyclerView recyclerView;
    @BindView(R.id.up_index)
    MyGifView up_index;
    @BindView(R.id.loading_layout)
    LoadingLayout loadingLayout;
    SplecialListAdapter adapter;
    @BindView(R.id.detail_tv)
    TextView detailTv;
    @BindView(R.id.br_tv)
    TextView brTv;
    SpecialPopupWindow popupWindow;
    @BindView(R.id.more_option_btn)
    Button moreOptionBtn;
    String idStr;
    int page = 1;
    int size = 20;
    String picture,titleName;
    PraiseData praiseData;
    List<SpecialContentList.ItemsBean> list;
    SelectSharePopupWindow sharePopupWindow;
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_special;
    }

    @Override
    protected void initPresenter() {
        presenter = new SpecialPresenter(this, this);
        presenter.init();
    }

    @Override
    public void initView() {
        toolbar.setNavigationOnClickListener(new NoDoubleClickListener(){
            public void onNoDoubleClick(View view){
                finish();
            }
        });
        list = new ArrayList<>();
        LinearLayoutManager llm = new LinearLayoutManager(this);
        adapter = new SplecialListAdapter(this,list);
        recyclerView.setHasFixedSize(true);
        llm.setSmoothScrollbarEnabled(true);
        llm.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        toolbar.setNavigationOnClickListener(new NoDoubleClickListener() {
            public void onNoDoubleClick(View view) {
                finish();
            }
        });
        up_index.setMovieResource(R.raw.up_index);
        idStr = getIntent().getStringExtra("idStr");
        String data = getIntent().getDataString();
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
        praiseData =  presenter.loadPraise(idStr);
        presenter.loadSpecialDetail(idStr);
        presenter.loadSpecialContentList(idStr,page,size);
        loadingLayout.setOnRetryClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                presenter.loadSpecialContentList(idStr,page,size);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.more_option_btn)
    void expandOption(){
        if(popupWindow == null){
            popupWindow = new SpecialPopupWindow(this,idStr,picture,titleName,praiseData);
        }
        popupWindow.showPopupWindow(moreOptionBtn);

    }
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("openShare")})
    public void share(String args){
        sharePopupWindow = new SelectSharePopupWindow(this);
        sharePopupWindow .showAtLocation(imgIv, Gravity.BOTTOM, 0, 0);
    }
    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("savePraise")})
    public void savePraise(PraiseData praiseData){
        presenter.clickPraise(praiseData);
        if(praiseData.getValid()){
            presenter.sendIntegralLog("like",idStr);
        }
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
    public void showWifiView() {
        loadingLayout.showWifiView();
    }

    @Override
    public void showDetailView(SpecialDetail detailData) {
        detailTv.setText(detailData.getDesc());
        brTv.setText(detailData.getBrtxt());
        titleName = detailData.getName();
        collapsingToolbar.setTitle(detailData.getName());
        picture = detailData.getPicture2();
        moreOptionBtn.setVisibility(View.VISIBLE);
        GlideProxy.getInstance().loadImage(this,detailData.getPicture2(),imgIv,R.mipmap.zhuanti_bg);
    }

    @Override
    public void fillSpecialList(SpecialContentList specialContentList) {
        list.addAll(specialContentList.getItems());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void loadMore() {
        page++;
        presenter.loadSpecialContentList(idStr,page,size);
    }


    @Subscribe(
            thread = EventThread.MAIN_THREAD,
            tags = {@Tag("selectChannel")})
    public void shareSpecial(String channel) {
        String targetUrl = AppGlobalVars.shareApiHost+"?talktvspecial://tvfan.com/share," + idStr+",";
        if(AppGlobalVars.appKeyResult == null || AppGlobalVars.appKeyResult.getData().size() < 2){
            Toast.makeText(SpecialActivity.this,"分享失败，请稍后重试..",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(picture)){
            Toast.makeText(SpecialActivity.this,"分享失败，暂不支持分享",Toast.LENGTH_SHORT).show();
            return;
        }
        if(channel.equals("WEIXIN")){
            PlatformConfig.setWeixin("wxcfaa020ee248a2f2", AppGlobalVars.appKeyResult.getData().get(0).getSecret());
            new ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN)
                    .withText(titleName)
                    .withTitle(titleName)
                    .withMedia(new UMImage(this,picture))
                    .withTargetUrl(targetUrl)
                    .setCallback(umShareListener)
                    .share();
        }else if(channel.equals("CIRCLE")){
            PlatformConfig.setWeixin("wxcfaa020ee248a2f2", AppGlobalVars.appKeyResult.getData().get(0).getSecret());
            new ShareAction(this).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                    .withText(titleName)
                    .withTitle(titleName)
                    .withMedia(new UMImage(this,picture))
                    .withTargetUrl(targetUrl)
                    .setCallback(umShareListener)
                    .share();
        }
        else if(channel.equals("QQ")){
            PlatformConfig.setQQZone("100757629", AppGlobalVars.appKeyResult.getData().get(1).getSecret()); //"04e1ef24fe1baa6858d1de1f0d70dfb2");
            new ShareAction(this).setPlatform(SHARE_MEDIA.QQ)
                    .withText(titleName)
                    .withMedia(new UMImage(this,picture))
                    .withTargetUrl(targetUrl)
                    .setCallback(umShareListener)
                    .share();
        }else if(channel.equals("ZONE")){
            PlatformConfig.setQQZone("100757629",AppGlobalVars.appKeyResult.getData().get(1).getSecret()); //"04e1ef24fe1baa6858d1de1f0d70dfb2");
            new ShareAction(this).setPlatform(SHARE_MEDIA.QZONE)
                    .withText(titleName)
                    .withMedia(new UMImage(this,picture))
                    .withTargetUrl(targetUrl)
                    .setCallback(umShareListener)
                    .share();
        } else if(channel.equals("WEIBO")){
            PlatformConfig.setSinaWeibo("2064721383", AppGlobalVars.appKeyResult.getData().get(2).getSecret());
            new ShareAction(this).setPlatform(SHARE_MEDIA.SINA)
                    .withText(titleName)
                    .withMedia(new UMImage(this,picture))
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
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            if(t!=null){
                Log.d("throw","throw:"+t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
