package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.ShareElement;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.entity.PlayerHistoryBean;
import com.sumavision.talktv2.model.entity.SeriesDetail;
import com.sumavision.talktv2.presenter.ProgramDetailPresenter;
import com.sumavision.talktv2.util.AppGlobalConsts;
import com.sumavision.talktv2.util.BusProvider;
import com.sumavision.talktv2.util.OfflineCacheUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by sharpay on 16-5-27.
 */
public class SelectionsPagerAdapter extends PagerAdapter{

    private Context mContext;
    RecyclerView recyclerView;
    ArrayMap<Integer, RecyclerView> recyclerList;
    List<String> seriesArray;
    ProgramDetailPresenter presenter;
    private String id;
    private String cpid;
    private int pageNum;
    private int playPos;
    private int cpState = 1;
    private final int tvPageSize = 30;
    private boolean isFirst = true;
    private String epi;
    private boolean isChangeCp;

    public SelectionsPagerAdapter(Context mContext
            ,ProgramDetailPresenter presenter,String id,String cpid,PlayerHistoryBean playerHistoryBean){
        this.mContext = mContext;
        recyclerList = new ArrayMap<>();
        this.seriesArray = new ArrayList<>();
        this.presenter = presenter;
        this.id = id;
        this.cpid = cpid;
        initPlayPos(playerHistoryBean);
    }
    public void setAdapter(List<String> list){
        seriesArray.clear();
        seriesArray.addAll(list);
    }

    public void setCpState(int cpState) {
        this.cpState = cpState;
    }

    public void isChangeCp (boolean is) {
        isChangeCp = is;
    }
    public void setCpId (String cpId) {
        this.cpid = cpId;
    }


    private void initPlayPos(PlayerHistoryBean playerHistoryBean) {
        if(playerHistoryBean != null) {
            pageNum = playerHistoryBean.getPlayPos()/tvPageSize;
            playPos = playerHistoryBean.getPlayPos() % tvPageSize;
        }
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public void setEpi (String epi) {
        this.epi = epi;
        isFirst = true;
    }

    public void playNext (int pageNum, int playPos) {
        RecyclerView recyclerView = recyclerList.get(pageNum);
        SelectionsRecyclerAdapter adapter = (SelectionsRecyclerAdapter) recyclerView.getAdapter();
        adapter.playNext(playPos);
    }

    public boolean isNextPage(int pageNum, int pos) {
        RecyclerView recyclerView = recyclerList.get(pageNum);
        SelectionsRecyclerAdapter adapter = (SelectionsRecyclerAdapter) recyclerView.getAdapter();
        return adapter.isNextPage(pos);
    }

    @Override
    public int getCount() {
        return seriesArray.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView(recyclerList.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        SelectionsRecyclerAdapter adapter = new SelectionsRecyclerAdapter(R.layout.selections_item_layout,new ArrayList<SeriesDetail.SourceBean>());
        adapter.setCpState(cpState);
        recyclerView= new RecyclerView(mContext);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 5));
        // recyclerView.addItemDecoration(new DividerGridItemDecoration(mContext));
        view.addView(recyclerView);
        adapter.setPageNum(position);
        loadSeriesData(position,adapter);
        recyclerList.put(position, recyclerView);
        return recyclerView;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return seriesArray.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void loadSeriesData(final int position, final SelectionsRecyclerAdapter adapter){
        if(TextUtils.isEmpty(id) || TextUtils.isEmpty(cpid) || TextUtils.isEmpty(seriesArray.get(position)) )
            return;
        presenter.getSeriesGridValue(id,cpid,seriesArray.get(position),new CallBackListener<SeriesDetail>() {

            @Override
            public void onSuccess(SeriesDetail seriesDetail) {
                if(pageNum == position && isFirst) {
                    isFirst = false;
                    if(isChangeCp) {
                        if(TextUtils.isEmpty(epi)) {
                            playPos = seriesDetail.getSource().size()-1;
                        }
                        else {
                            playPos = presenter.getEpiPos(epi, seriesDetail.getSource());
                            epi = "";
                        }
                        isChangeCp = false;
                    }
                    else {
                        BusProvider.getInstance().post("choicePlayer",seriesDetail.getSource().get(0));
                    }
                    BusProvider.getInstance().post("total",seriesDetail.getTotal()+"");
                    if(cpState == 1)
                        BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_A,seriesDetail.getSource().get(playPos));
                    BusProvider.getInstance().post(AppGlobalConsts.EventType.TAG_A,new int[]{pageNum, playPos});
                    adapter.setPlayPos(playPos);

                }
                OfflineCacheUtil.filterCacheInfo(mContext, seriesDetail.getSource(), id);
                adapter.setList(seriesDetail.getSource());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable throwable) {
            }
        });
    }
}