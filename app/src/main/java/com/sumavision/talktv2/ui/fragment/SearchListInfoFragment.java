package com.sumavision.talktv2.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jiongbull.jlog.JLog;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.http.GlideProxy;
import com.sumavision.talktv2.model.entity.decor.SearchInfoItem;
import com.sumavision.talktv2.presenter.ISearchListPresenter;
import com.sumavision.talktv2.ui.activity.TVFANActivity;
import com.sumavision.talktv2.ui.adapter.SearchListInfoAdapter;
import com.sumavision.talktv2.ui.adapter.SearchListInfoHotAdapter;
import com.sumavision.talktv2.ui.adapter.SearchListInfoLivePlayAdapter;
import com.sumavision.talktv2.ui.fragment.Base.BaseFragment;
import com.sumavision.talktv2.ui.iview.ISearchListInfoView;
import com.sumavision.talktv2.ui.widget.FullyLinearLayoutManager;
import com.sumavision.talktv2.ui.widget.LoadingLayout;
import com.sumavision.talktv2.ui.widget.MyScrollView;
import com.sumavision.talktv2.ui.widget.MySwipeRefreshLayout;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.CommonUtil;
import com.sumavision.talktv2.util.NoDoubleClickListener;
import com.sumavision.talktv2.util.TipUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

//import static com.sumavision.talktv2.R.id.ll_item_foot;

/**
 * Created by zhoutao on 2016/8/25.
 */
public class SearchListInfoFragment extends BaseFragment<ISearchListPresenter> implements ISearchListInfoView,SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.swipe_refresh_layout)
    MySwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.lmrv_searchinfo_list_liveplay)
    RecyclerView lmrv_searchinfo_list_liveplay;
    @BindView(R.id.lmrv_searchinfo_list_play)
    RecyclerView lmrv_searchinfo_list_play;
    @BindView(R.id.lmrv_searchinfo_list_hot)
    RecyclerView lmrv_searchinfo_list_hot;
    @BindView(R.id.loadingLayout)
    LoadingLayout loadingLayout;
    @BindView(R.id.rl_ppb)
    RelativeLayout rl_ppb;
    @BindView(R.id.searchinfolist_liveplay_btn)
    RelativeLayout searchinfolist_liveplay_btn;
    @BindView(R.id.view_3d)
    View view_3d;
    @BindView(R.id.view_2d)
    View view_2d;
    @BindView(R.id.iv_search_liveplay_item_img1)
    ImageView iv_search_liveplay_item_img1;
    @BindView(R.id.iv_search_liveplay_item_img2)
    ImageView iv_search_liveplay_item_img2;
    @BindView(R.id.iv_search_liveplay_item_title1)
    TextView iv_search_liveplay_item_title1;
    @BindView(R.id.iv_search_liveplay_item_title2)
    TextView iv_search_liveplay_item_title2;
    @BindView(R.id.searchinfolist_liveplay_toptwo)
    LinearLayout searchinfolist_liveplay_toptwo;
    @BindView(R.id.searchinfolist_liveplay_top)
    LinearLayout searchinfolist_liveplay_top;
    @BindView(R.id.searchinfolist_liveplay_item1)
    RelativeLayout searchinfolist_liveplay_item1;
    @BindView(R.id.searchinfolist_liveplay_item2)
    RelativeLayout searchinfolist_liveplay_item2;
    @BindView(R.id.searchinfolist_scrollview)
    MyScrollView searchinfolist_scrollview;
    @BindView(R.id.searchinfolist_liveplay_more)
    TextView searchinfolist_liveplay_more;
    @BindView(R.id.searchinfolist_liveplay_more_img)
    ImageView searchinfolist_liveplay_more_img;
    /*@BindView(R.id.ll_item_foot)
    RelativeLayout ll_item_foot;*/
    boolean canLoadMore = true;
    private SearchListInfoAdapter sla;
    private SearchListInfoLivePlayAdapter livesla;
    private SearchListInfoHotAdapter hotsla;
    private ArrayList<SearchInfoItem> results,liveResultes,playResultes,hotResultes,liveFinalResultes;
    private String str;
    private int page = 1;
    private int index = 0;
    private boolean ISMORE = false;
    public SearchListInfoFragment(){
    }
    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(this);
        presenter.release();
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed() {
        AppGlobalConsts.ISSEARCHBACK = true;
        return false;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_searchinfolist;
    }

    @Override
    protected void initPresenter() {
        presenter = new ISearchListPresenter(getContext(),this);
        presenter.init();
    }
    public void showEditLayout2(String currentStr){
        //1.获取搜索后返回的数据
        presenter.getSearchListInfo(currentStr,page);
        str = currentStr;
        JLog.e("开始搜索!");
    }
    @Override
    public void initView() {
        BusProvider.getInstance().register(this);
        results = new ArrayList<>();
        liveResultes = new ArrayList<>();
        hotResultes = new ArrayList<>();
        playResultes = new ArrayList<>();
        liveFinalResultes = new ArrayList<>();
        canLoadMore = true;
        searchinfolist_scrollview.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN :

                        break;
                    case MotionEvent.ACTION_MOVE :
                        index++;
                        break;
                    default :
                        break;
                }
                if (event.getAction() == MotionEvent.ACTION_UP &&  index > 0) {
                    index = 0;
                    View view = ((ScrollView) v).getChildAt(0);
                    if (view.getMeasuredHeight() <= v.getScrollY() + v.getHeight()) {
                        //加载数据代码
                        loadMore();
                    }
                }
                return false;
            }
        });

        loadingLayout.setOnRetryClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                showEditLayout2(str);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.blue);
        swipeRefreshLayout.setOnRefreshListener(this);
        searchinfolist_liveplay_toptwo.setVisibility(View.GONE);
            //设置直播列表显示数据
            livesla = new SearchListInfoLivePlayAdapter(this.getContext());
            lmrv_searchinfo_list_liveplay.setAdapter(livesla);
//        livesla.setOnLiveClick(new SearchListInfoLivePlayAdapter.OnLiveClick() {
//            @Override
//            public void liveClick(String id) {
//                BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_ENTER, "直播");
//                BusProvider.getInstance().post("searchtolive", id);
//            }
//        });
        FullyLinearLayoutManager liverecommendManager  = new FullyLinearLayoutManager(this.getContext());
            liverecommendManager.setOrientation(LinearLayoutManager.VERTICAL);
            lmrv_searchinfo_list_liveplay.setLayoutManager(liverecommendManager);
            lmrv_searchinfo_list_liveplay.setMinimumHeight(CommonUtil.dip2px(getContext(),47));
            //设置长视频列表显示数据
            sla = new SearchListInfoAdapter(this.getContext());
            lmrv_searchinfo_list_play.setAdapter(sla);
            FullyLinearLayoutManager playrecommendManager  = new FullyLinearLayoutManager(this.getContext());
            playrecommendManager.setOrientation(LinearLayoutManager.VERTICAL);
            lmrv_searchinfo_list_play.setLayoutManager(playrecommendManager);
            lmrv_searchinfo_list_play.setMinimumHeight(CommonUtil.dip2px(getContext(),170));
            //设置短视频列表显示数据
            hotsla = new SearchListInfoHotAdapter(this.getContext());
            lmrv_searchinfo_list_hot.setAdapter(hotsla);
             FullyLinearLayoutManager hotrecommendManager  = new FullyLinearLayoutManager(this.getContext());
             hotrecommendManager.setOrientation(LinearLayoutManager.VERTICAL);
            lmrv_searchinfo_list_hot.setLayoutManager(hotrecommendManager);
            lmrv_searchinfo_list_hot.setMinimumHeight(CommonUtil.dip2px(getContext(),60));
        showProgressBar();
        str =this.getActivity().getIntent().getStringExtra("currentStr");
        showEditLayout2(str);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private boolean PBSHOWSTATE = false;
    @Override
    public void showProgressBar() {
        loadingLayout.showProgressBar();
        PBSHOWSTATE = true;
    }

    @Override
    public void hideProgressBar() {
        if (loadingLayout != null){
            loadingLayout.hideProgressBar();
            PBSHOWSTATE = false;
        }
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    @Override
    public void hidePPPro(){
        if (rl_ppb.getVisibility() == View.VISIBLE){
            rl_ppb.setVisibility(View.GONE);
        }
    }
    public void showPPPro(){
        if (PBSHOWSTATE == false ){
            rl_ppb.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void showErrorView() {
        loadingLayout.showErrorView();
        PBSHOWSTATE = true;
    }

    @Override
    public void showEmptyView() {
            TipUtil.showSnackTip(swipeRefreshLayout, "没有更多数据了!");
        PBSHOWSTATE = true;
    }

    @Override
    public void showWifiView() {
        loadingLayout.showWifiView();
        PBSHOWSTATE = true;
    }
    private String code1,code2;
    @Override
    public void showSearchList(ArrayList<SearchInfoItem> searchInfoItems,int page) {
        view_3d.setVisibility(View.VISIBLE);
        view_2d.setVisibility(View.VISIBLE);
//        ll_item_foot.setVisibility(View.VISIBLE);
        lmrv_searchinfo_list_hot.setVisibility(View.VISIBLE);
        lmrv_searchinfo_list_play.setVisibility(View.VISIBLE);
        if(searchInfoItems.size() == 0){
            if (page == 1 && results.size() == 0){
                    loadingLayout.showEmptyView();
            }else{
                showEmptyView();
                canLoadMore = false;
                return;
            }
        }else{
            canLoadMore = true;
        }
        results.addAll(searchInfoItems);
        for (int i= 0 ;i<searchInfoItems.size();i++){
            if (searchInfoItems.get(i).type.equals("S")){
                //短视频
                hotResultes.add(searchInfoItems.get(i));
            }else if (searchInfoItems.get(i).type.equals("L")){
                //长视频
                playResultes.add(searchInfoItems.get(i));
            }else{
                //直播
                liveResultes.add(searchInfoItems.get(i));
            }
        }

        //设置搜索结果页前两条显示
        if(liveResultes.size()==0 && page == 1){
            searchinfolist_liveplay_toptwo.setVisibility(View.GONE);
        }

        if (liveResultes.size()==1 && page == 1){
            searchinfolist_liveplay_toptwo.setVisibility(View.VISIBLE);
            searchinfolist_liveplay_item1.setVisibility(View.VISIBLE);
            searchinfolist_liveplay_btn.setVisibility(View.GONE);
            searchinfolist_liveplay_item2.setVisibility(View.GONE);
            code1 = liveResultes.get(0).code;
            GlideProxy.getInstance().loadHImage(getContext(),"http://tvfan.cn/photo/channel/image/android"+code1+".png",iv_search_liveplay_item_img1);
            iv_search_liveplay_item_title1.setText(liveResultes.get(0).name);
        }
        if (liveResultes.size()==2 && page == 1){
            searchinfolist_liveplay_toptwo.setVisibility(View.VISIBLE);
            searchinfolist_liveplay_item1.setVisibility(View.VISIBLE);
            searchinfolist_liveplay_item2.setVisibility(View.VISIBLE);
            searchinfolist_liveplay_btn.setVisibility(View.GONE);
            code1 = liveResultes.get(0).code;
            code2 = liveResultes.get(1).code;
            GlideProxy.getInstance().loadHImage(getContext(),"http://tvfan.cn/photo/channel/image/android"+code1+".png",iv_search_liveplay_item_img1);
            GlideProxy.getInstance().loadHImage(getContext(), "http://tvfan.cn/photo/channel/image/android"+code2+".png", iv_search_liveplay_item_img2);
            iv_search_liveplay_item_title1.setText(liveResultes.get(0).name);
            iv_search_liveplay_item_title2.setText(liveResultes.get(1).name);
        }
        if (liveResultes.size()>2 && page == 1) {
            code1 = liveResultes.get(0).code;
            code2 = liveResultes.get(1).code;
            searchinfolist_liveplay_toptwo.setVisibility(View.VISIBLE);
            GlideProxy.getInstance().loadHImage(getContext(),"http://tvfan.cn/photo/channel/image/android"+code1+".png",iv_search_liveplay_item_img1);
            GlideProxy.getInstance().loadHImage(getContext(), "http://tvfan.cn/photo/channel/image/android"+code2+".png", iv_search_liveplay_item_img2);
            iv_search_liveplay_item_title1.setText(liveResultes.get(0).name);
            iv_search_liveplay_item_title2.setText(liveResultes.get(1).name);
            liveResultes.remove(0);
            liveResultes.remove(0);
            liveFinalResultes.addAll(liveResultes);
        }
        if (hotResultes.size()==0){
            lmrv_searchinfo_list_hot.setVisibility(View.GONE);
            view_3d.setVisibility(View.GONE);
        }
        if (playResultes.size()==0){
            lmrv_searchinfo_list_play.setVisibility(View.GONE);
            view_2d.setVisibility(View.GONE);
        }
        if (sla != null){
            sla.setListData(playResultes);
        }
        if(livesla != null){
            livesla.setListData(liveFinalResultes);
        }
        if(hotsla != null){
            hotsla.setListData(hotResultes);
        }
    }

    public void loadMore() {
        if (canLoadMore) {
            showPPPro();
            page++;
            presenter.getSearchListInfo(str,page);
        } else {
            hidePPPro();
            TipUtil.showSnackTip(lmrv_searchinfo_list_hot, "没有更多数据了!");
        }
    }

    @Override
    public void onRefresh() {
        page = 1;
        results.clear();
        liveResultes .clear();
        hotResultes .clear();
        playResultes .clear();
        liveFinalResultes .clear();
//        ll_item_foot.setVisibility(View.GONE);
        view_3d.setVisibility(View.GONE);
        if (sla != null){
            sla.setListData(playResultes);
            sla.notifyDataSetChanged();
        }
        if(livesla != null){
            livesla.setListData(liveFinalResultes);
            livesla.notifyDataSetChanged();
        }
        if(hotsla != null){
            hotsla.setListData(hotResultes);
            hotsla.notifyDataSetChanged();
        }
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
        presenter.getSearchListInfo(str,page);
    }
    @OnClick(R.id.searchinfolist_liveplay_btn)
    public void clickMore(){
        ISMORE=!ISMORE;
        if (ISMORE){
            searchinfolist_liveplay_more.setText("收起");
            searchinfolist_liveplay_more_img.setBackgroundResource(R.mipmap.playdetail_more_btn_pressed);
            lmrv_searchinfo_list_liveplay.setVisibility(View.VISIBLE);
        }else{
            searchinfolist_liveplay_more.setText("查看更多");
            searchinfolist_liveplay_more_img.setBackgroundResource(R.mipmap.playdetail_more_btn_nor);
            lmrv_searchinfo_list_liveplay.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.searchinfolist_liveplay_item1)
    public void enterItemLivePlay(){
        Intent intent  = new Intent(getContext(), TVFANActivity.class);
        Bundle b = new Bundle();
        intent.putExtra("enterLivePlay","enterLivePlay");
        intent.putExtra("id",code1);
        startActivity(intent);
        getActivity().finish();
//        BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_ENTER, "直播");
//        BusProvider.getInstance().post("searchtolive", code1);
    }
    @OnClick(R.id.searchinfolist_liveplay_item2)
    public void enterItem2LivePlay(){
        Intent intent  = new Intent(getContext(), TVFANActivity.class);
        Bundle b = new Bundle();
        intent.putExtra("enterLivePlay","enterLivePlay");
        intent.putExtra("id",code2);
        startActivity(intent);
        getActivity().finish();
//        BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_ENTER, "直播");
//        BusProvider.getInstance().post("searchtolive", code2);
    }
   /* @OnClick(R.id.ll_item_foot)
    public void enterKanDapian(){
        //通过内部webview打开
        Intent intent  = new Intent(getContext(), WebVedioActivity.class);
        intent.putExtra("url","http://www.dianshifen.net/search.php?searchword="+str);
//        intent.putExtra("url","http://www.kandapian.net/movie/index40023.html");
        startActivity(intent);

     *//*   //通过外部浏览器打开
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse("http://www.dianshifen.net/search.php?searchword="+str);
        intent.setData(content_url);
        startActivity(intent);*//*
    }*/

}
