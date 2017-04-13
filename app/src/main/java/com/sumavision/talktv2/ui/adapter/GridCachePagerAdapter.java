package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.model.CallBackListener;
import com.sumavision.talktv2.model.entity.SeriesDetail;
import com.sumavision.talktv2.presenter.ProgramDetailPresenter;
import com.sumavision.talktv2.util.OfflineCacheUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharpay on 16-5-27.
 */
public class GridCachePagerAdapter extends PagerAdapter{

    private Context mContext;
    RecyclerView recyclerView;
    List<RecyclerView> recyclerList;
    List<String> seriesArray;
    ProgramDetailPresenter presenter;
    private String id;
    private String cpid;
    private final String picUrl;
    public GridCachePagerAdapter(List<String> seriesArray, Context mContext, ProgramDetailPresenter presenter, String id, String cpid, String picUrl){
        this.mContext = mContext;
        recyclerList = new ArrayList<>();
        this.seriesArray = seriesArray;
        this.presenter = presenter;
        this.id = id;
        this.cpid = cpid;
        this.picUrl = picUrl;
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

    public void notifyCache() {
        for (RecyclerView tmp : recyclerList) {
            tmp.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        GridCacheRecyclerAdapter adapter = new GridCacheRecyclerAdapter(R.layout.grid_cache_item_layout,new ArrayList<SeriesDetail.SourceBean>());
        recyclerView= new RecyclerView(mContext);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 5));
       // recyclerView.addItemDecoration(new DividerGridItemDecoration(mContext));
        view.addView(recyclerView);
        loadSeriesData(position,adapter);
        recyclerList.add(recyclerView);
        return recyclerView;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return seriesArray.get(position);
    }

    public void loadSeriesData(int position, final GridCacheRecyclerAdapter adapter){
        presenter.getSeriesGridValue(id,cpid,seriesArray.get(position),new CallBackListener<SeriesDetail>() {

            @Override
            public void onSuccess(SeriesDetail seriesDetail) {
                for (SeriesDetail.SourceBean tmp : seriesDetail.getSource()) {
                    tmp.setPicUrl(picUrl);
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