package com.sumavision.talktv2.ui.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sumavision.talktv2.model.entity.MediaTopic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharpay on 16-6-27.
 */
public class MediaPagerAdapter extends FragmentStatePagerAdapter {
    List<Fragment> fragmentList;
    List<MediaTopic.ItemsBean> titleList;
    public MediaPagerAdapter(FragmentManager fm) {
        super(fm);
        this.titleList = new ArrayList<>();
        this.fragmentList = new ArrayList<>();
    }

    public void setList( List<Fragment> fragments,List<MediaTopic.ItemsBean> titleList){
        this.titleList.addAll(titleList);
        fragmentList.addAll(fragments);
    }
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position).getNavName();
    }

}