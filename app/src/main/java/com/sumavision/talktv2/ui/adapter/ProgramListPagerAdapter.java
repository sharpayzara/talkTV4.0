package com.sumavision.talktv2.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;

import com.sumavision.talktv2.model.entity.ProgramListTopic;
import com.sumavision.talktv2.presenter.ProgramListPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharpay on 16-5-27.
 */
public class ProgramListPagerAdapter extends FragmentStatePagerAdapter {
    List<Fragment> fragmentList;
    List<ProgramListTopic.ItemsBean> topicArray;
    ProgramListPresenter presenter;
    LayoutInflater inflater;
    FragmentManager fm;
    public ProgramListPagerAdapter(FragmentManager fm, Context mContext, ProgramListPresenter presenter, View view, String currentType){
        super(fm);
        this.fm = fm;
        this.topicArray = new ArrayList<>();
        this.presenter = presenter;
        inflater = LayoutInflater.from(mContext);
        fragmentList = new ArrayList<>();
    }

    public void setList( List<Fragment> fragments,List<ProgramListTopic.ItemsBean> topicArray){
        this.topicArray.clear();
        this.topicArray.addAll(topicArray);
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
        return topicArray.get(position).getName();
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

}