package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.sumavision.talktv2.R;
import com.sumavision.talktv2.common.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhoutao on 16-5-27.
 */
public class MediaSelectionsPagerAdapter extends PagerAdapter{

    private Context mContext;
    private List<View> images;
    RecyclerView recyclerView;
    List<RecyclerView> recyclerList;
    public MediaSelectionsPagerAdapter(List<View> images, Context mContext){
        this.images = images;
        this.mContext = mContext;
        recyclerList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView(images.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        MediaSelectionsRecyclerAdapter adapter = new MediaSelectionsRecyclerAdapter(R.layout.item_media_selections);
        recyclerView= new RecyclerView(mContext);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);


        recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));
        view.addView(recyclerView);
        recyclerList.add(recyclerView);
        return recyclerView;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0){
            return "1-30";
        }else if(position == 1){
            return "31-60";
        }if(position == 2){
            return "61-80";
        }else{
            return "81-100";
        }

    }

}