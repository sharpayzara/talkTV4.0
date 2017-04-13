package com.sumavision.talktv2.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.presenter.ISearchMainPresenter;
import com.sumavision.talktv2.ui.adapter.SearchListAdapter;
import com.sumavision.talktv2.ui.fragment.Base.BaseFragment;
import com.sumavision.talktv2.ui.iview.ISearchMainFragmentView;
import com.sumavision.talktv2.ui.widget.LoadingLayout;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.CommonUtil;
import com.sumavision.talktv2.util.NoDoubleClickListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by zhoutao on 2016/6/24.
 */
public class SearchMainFragment extends BaseFragment<ISearchMainPresenter> implements ISearchMainFragmentView{

   @BindView(R.id.ll_search_history)
    LinearLayout ll_search_history;
   @BindView(R.id.tv_search_info1)
    TextView tv_search_info1;
   @BindView(R.id.tv_search_info2)
    TextView tv_search_info2;
   @BindView(R.id.tv_search_info3)
    TextView tv_search_info3;
   @BindView(R.id.tv_search_info4)
    TextView tv_search_info4;
   @BindView(R.id.tv_search_info5)
    TextView tv_search_info5;
   @BindView(R.id.tv_search_info6)
    TextView tv_search_info6;
   @BindView(R.id.lv_search_list)
    ListView lv_search_list;
   @BindView(R.id.iv_search_deletehistory)
    ImageView iv_search_deletehistory;
   @BindView(R.id.search_main_loadlayout)
    LoadingLayout search_main_loadlayout;
   @BindView(R.id.ll_searchhis_1)
    LinearLayout ll_searchhis_1;
   @BindView(R.id.ll_searchhis_2)
    LinearLayout ll_searchhis_2;
   @BindView(R.id.ll_searchhis_3)
    LinearLayout ll_searchhis_3;
   @BindView(R.id.rl_search_hot)
   RelativeLayout rl_search_hot;

    private SearchListAdapter sla;
    private Fragment fragment;
    private String currentStr = "Andrea";
    private List<String> searchHisList;
    public SearchMainFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_search;
    }

    @Override
    protected void initPresenter() {
        presenter = new ISearchMainPresenter(getContext(),this);
        presenter.init();
    }

    @Override
    public boolean onBackPressed() {
        AppGlobalConsts.ISSEARCHBACK = true;
        return false;
    }

    @Override
    public void initView() {
        AppGlobalConsts.EDITSTATE = true;
        searchHisList = new ArrayList<>();
        //获取本地存储的搜索历史数据
        searchHisList = CommonUtil.getSearch(this.getContext());
        setSearchHisShow(searchHisList);
        //设置热门搜索的数据
        presenter.getSearchData();
        sla = new SearchListAdapter(this.getContext());
        lv_search_list.setMinimumHeight(CommonUtil.dip2px(getContext(),40));
        lv_search_list.setAdapter(sla);
        search_main_loadlayout.setOnRetryClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                presenter.getSearchData();
            }
        });
        lv_search_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (AppGlobalConsts.EDITSTATE){
                    BusProvider.getInstance().post("scrolling","scrolling");
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        rl_search_hot.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                if (AppGlobalConsts.EDITSTATE){
                    BusProvider.getInstance().post("scrolling","scrolling");
                }
            }
        });
    }

    @OnClick(R.id.tv_search_info1)
    public void clickHistory1(){
        currentStr =  tv_search_info1.getText().toString();
        sendMsg(currentStr);
    }

    public void sendMsg(String currentStr) {
        //切换到展示搜索结果的Fragment
        BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_A,currentStr);
     /*   fragment = new SearchListInfoFragment();
        this.getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.searchcontent,fragment)
                .commit();
        this.getActivity().getIntent().putExtra("currentStr",currentStr);*/
//        CommonUtil.addSearchListInfo(currentStr,searchHisList,this.getContext());
    }



    @OnClick(R.id.tv_search_info2)
    public void clickHistory2(){
        currentStr =  tv_search_info2.getText().toString();
        sendMsg(currentStr);
    }
    @OnClick(R.id.tv_search_info3)
    public void clickHistory3(){

        currentStr =  tv_search_info3.getText().toString();
        sendMsg(currentStr);
    }
    @OnClick(R.id.tv_search_info4)
    public void clickHistory4(){

        currentStr =  tv_search_info4.getText().toString();
        sendMsg(currentStr);
    }
    @OnClick(R.id.tv_search_info5)
    public void clickHistory5(){

        //获取当前edittext的输入内容
        currentStr =  tv_search_info5.getText().toString();
        sendMsg(currentStr);
    }
    @OnClick(R.id.tv_search_info6)
    public void clickHistory6(){

        //获取当前edittext的输入内容
        currentStr =  tv_search_info6.getText().toString();
        sendMsg(currentStr);
    }

    @Override
    public void showProgressBar() {
            search_main_loadlayout.showProgressBar();
    }

    @Override
    public void hideProgressBar() {
        if(search_main_loadlayout != null){
            search_main_loadlayout.hideProgressBar();
        }
    }

    @Override
    public void showErrorView() {
        search_main_loadlayout.showErrorView();
    }

    @Override
    public void showEmptyView() {
        search_main_loadlayout.showEmptyView();
    }

    @Override
    public void showWifiView() {
        search_main_loadlayout.showWifiView();
    }

    @Override
    public void showHotList(final ArrayList<String> items) {
        sla.setSearchDataList(items);
//        results.addAll(items);
        lv_search_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MobclickAgent.onEvent(getContext(), "4rstj");
                sendMsg(items.get(position));
                if (AppGlobalConsts.EDITSTATE){
                    BusProvider.getInstance().post("scrolling","scrolling");
                }
            }
        });
    }

    @OnClick(R.id.iv_search_deletehistory)
    public void deleteHistory(){
        searchHisList = null;
        CommonUtil.addSearchListInfo("delete",searchHisList,this.getContext());
        ll_search_history.setVisibility(View.GONE);
    }


    public void setSearchHisShow(List<String> searchHisList) {
        switch (searchHisList.size()){
            case 0:
                ll_search_history.setVisibility(View.GONE);
                break;
            case 1:
                if (searchHisList.get(0).equals("")){
                    ll_search_history.setVisibility(View.GONE);
                }else{
                    tv_search_info1.setText(searchHisList.get(0));
                    tv_search_info2.setVisibility(View.INVISIBLE);
                    ll_searchhis_2.setVisibility(View.GONE);
                    ll_searchhis_3.setVisibility(View.GONE);
                }
                break;
            case 2:
                tv_search_info1.setText(searchHisList.get(0));
                tv_search_info2.setText(searchHisList.get(1));
                ll_searchhis_2.setVisibility(View.GONE);
                ll_searchhis_3.setVisibility(View.GONE);
                break;
            case 3:
                tv_search_info1.setText(searchHisList.get(0));
                tv_search_info2.setText(searchHisList.get(1));
                tv_search_info3.setText(searchHisList.get(2));
                tv_search_info4.setVisibility(View.INVISIBLE);
                ll_searchhis_3.setVisibility(View.GONE);
                break;
            case 4:
                tv_search_info1.setText(searchHisList.get(0));
                tv_search_info2.setText(searchHisList.get(1));
                tv_search_info3.setText(searchHisList.get(2));
                tv_search_info4.setText(searchHisList.get(3));
                ll_searchhis_3.setVisibility(View.GONE);
                break;
            case 5:
                tv_search_info1.setText(searchHisList.get(0));
                tv_search_info2.setText(searchHisList.get(1));
                tv_search_info3.setText(searchHisList.get(2));
                tv_search_info4.setText(searchHisList.get(3));
                tv_search_info5.setText(searchHisList.get(4));
                tv_search_info6.setVisibility(View.INVISIBLE);
                break;
            case 6:
                tv_search_info1.setText(searchHisList.get(0));
                tv_search_info2.setText(searchHisList.get(1));
                tv_search_info3.setText(searchHisList.get(2));
                tv_search_info4.setText(searchHisList.get(3));
                tv_search_info5.setText(searchHisList.get(4));
                tv_search_info6.setText(searchHisList.get(5));
                break;
        }
    }
}
