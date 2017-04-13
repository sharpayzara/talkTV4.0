package com.sumavision.talktv2.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;

import com.sumavision.talktv2.model.entity.ClassifyItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhoutao on 16-5-27.
 */
public class RecommendPagerAdapter extends FragmentStatePagerAdapter {
    List<Fragment> fragmentList;
    List<ClassifyItem> topicList;
    FragmentManager fm;
    public RecommendPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
        topicList = new ArrayList<>();
        fragmentList = new ArrayList<>();
    }

    public void setList( List<Fragment> fragments, List<ClassifyItem> topicList){
        this.topicList.clear();
        this.topicList.addAll(topicList);
        if(this.fragmentList != null){
            FragmentTransaction ft = fm.beginTransaction();
            for(Fragment f : fragmentList){
                ft.remove(f);
            }
            ft.commitAllowingStateLoss();
            ft=null;
            fm.executePendingTransactions();
        }
        fragmentList.clear();
        fragmentList.addAll(fragments);
        notifyDataSetChanged();
    }

 /*   @Override
    public Fragment getItem(int position) {
        return HomeRecommendFragment.newInstance(,items.get(position).navId,);
    }*/
    public Fragment getItem(int position) {
        Fragment fragment = fragmentList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("nid",topicList.get(position).navId);
        bundle.putString("topicId",topicList.get(position).navId);
        bundle.putString("topicName",topicList.get(position).name);
        bundle.putString("type",topicList.get(position).type);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return topicList.get(position).name;
    }

}
