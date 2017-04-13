package com.sumavision.talktv2.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.sumavision.talktv2.BaseApp;
import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.ACache;
import com.sumavision.talktv2.common.DividerGridItemDecoration;
import com.sumavision.talktv2.dao.MyItemTouchCallback;
import com.sumavision.talktv2.dao.OnRecyclerItemClickListener;
import com.sumavision.talktv2.model.entity.HomeRecommend;
import com.sumavision.talktv2.model.entity.decor.DecorEntity;
import com.sumavision.talktv2.model.entity.decor.HomeRecommendData;
import com.sumavision.talktv2.presenter.MyListPresenter;
import com.sumavision.talktv2.ui.adapter.ChangeCardListAdapter;
import com.sumavision.talktv2.ui.adapter.MyListRecyclerAdapter;
import com.sumavision.talktv2.ui.fragment.Base.BaseFragment;
import com.sumavision.talktv2.ui.iview.base.IBaseView;
import com.sumavision.talktv2.ui.widget.NoScrollListview;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.CommonUtil;
import com.sumavision.talktv2.util.VibratorUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/4/12.
 */
public class MyListFragment extends BaseFragment<MyListPresenter> implements IBaseView,MyItemTouchCallback.OnDragListener{
    private MyListPresenter presenter;
    private List<HomeRecommend> fixLists = new ArrayList<>();
    private List<HomeRecommend> changeLists = new ArrayList<>();
    private List<HomeRecommend> recommendList ;
    private String version ;
    @BindView(R.id.rv_myList_fixcard)
    RecyclerView rv_myList_fixcard;
    @BindView(R.id.lv_myList_changecard)
    NoScrollListview lv_myList_changecard;
    public ChangeCardListAdapter changeCardListAdapter;
    public  HomeRecommendData data;
    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_mylist;
    }

    @Override
    protected void initPresenter() {
        presenter = new MyListPresenter(this.getActivity(), this);
        presenter.init();
        //BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_A,"fff");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //ButterKnife.unbind(this);
        BusProvider.getInstance().post("refreshShow","refreshShow");
    }

    @Override
    public boolean onBackPressed() {
        if (data == null){
            data  = new HomeRecommendData();
        }
        data.results = new ArrayList<>();
        data.results.addAll(fixLists);
        data.version = version;
        BaseApp.getACache().put("recommendList",data);
        /*DecorEntity<List<HomeRecommend>> fixDecor = new DecorEntity<>();
        fixDecor.setT(fixLists);*/
        DecorEntity<List<HomeRecommend>> changeDecor = new DecorEntity<>();
        changeDecor.setT(changeLists);

//        ACache.get(this.getContext()).put("fixLists",fixDecor);
        BaseApp.getACache().put("changeLists",changeDecor);

        return false;
    }
    private MyListRecyclerAdapter fixCardListAdapter;
    private  ItemTouchHelper itemTouchHelper;
    @Override
    public void initView() {
        try {
            recommendList =((HomeRecommendData) ACache.get(getContext()).getAsObject("recommendList")).results ;
        }catch (Exception ex){
            recommendList = new ArrayList<>();
        }
        version = ((HomeRecommendData) BaseApp.getACache().getAsObject("recommendList")).version;
            fixLists.addAll(recommendList);
        if (ACache.get(getContext()).getAsObject("changeLists") != null){
            try {
                changeLists =((DecorEntity<List<HomeRecommend>>) BaseApp.getACache().getAsObject("changeLists")).getT() ;
            }catch (Exception ex){
                changeLists = new ArrayList<>();
            }
        }

        fixCardListAdapter = new MyListRecyclerAdapter(R.layout.item_fixcard_list,fixLists);
        rv_myList_fixcard.setSelected(false);
        rv_myList_fixcard.setMinimumHeight(CommonUtil.screenWidth(this.getContext())*40/360);
        rv_myList_fixcard.setHasFixedSize(true);
        rv_myList_fixcard.setAdapter(fixCardListAdapter);
        rv_myList_fixcard.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        rv_myList_fixcard.addItemDecoration(new DividerGridItemDecoration(getActivity()));

        itemTouchHelper = new ItemTouchHelper(new MyItemTouchCallback(fixCardListAdapter).setOnDragListener(this));
        itemTouchHelper.attachToRecyclerView(rv_myList_fixcard);

        rv_myList_fixcard.addOnItemTouchListener(new OnRecyclerItemClickListener(rv_myList_fixcard) {
            @Override
            public void onLongClick(RecyclerView.ViewHolder vh) {
                if (vh.getLayoutPosition()!=fixLists.size() && vh.getLayoutPosition()>2) {
                    itemTouchHelper.startDrag(vh);
                    VibratorUtil.Vibrate(getActivity(), 70);   //震动70ms
                }
            }
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
                if (vh.getLayoutPosition()>2){
                    changeLists.add(fixLists.get(vh.getLayoutPosition()));
                    fixLists.remove(vh.getLayoutPosition());
                    fixCardListAdapter.notifyDataSetChanged();
                    changeCardListAdapter.notifyDataSetChanged();
                }
            }
        });

        changeCardListAdapter = new ChangeCardListAdapter(this.getContext(),changeLists);
        lv_myList_changecard.setAdapter(changeCardListAdapter);
        lv_myList_changecard.setMinimumHeight(CommonUtil.screenWidth(this.getContext())*40/360);
        lv_myList_changecard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fixLists.add(changeLists.get(position));
                changeLists.remove(position);
                changeCardListAdapter.notifyDataSetChanged();
                fixCardListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onFinishDrag() {
    }
}
