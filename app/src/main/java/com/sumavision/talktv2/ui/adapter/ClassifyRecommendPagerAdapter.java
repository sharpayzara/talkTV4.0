package com.sumavision.talktv2.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sumavision.talktv2.model.entity.ClassifyItem;
import com.sumavision.talktv2.ui.fragment.HomeRecommendFragment;

import java.util.List;

/**
 * Created by zhoutao on 16-6-14.
 */
public class ClassifyRecommendPagerAdapter extends FragmentPagerAdapter {

    private List<ClassifyItem> list_Title ;                              //tab名的列表
    public ClassifyRecommendPagerAdapter(FragmentManager fm,List<ClassifyItem> list_Title) {
        super(fm);
        this.list_Title = list_Title;
    }

    @Override
    public Fragment getItem(int position) {
        return HomeRecommendFragment.newInstance(list_Title.get(position).name,list_Title.get(position).navId,list_Title.get(position).name,list_Title.get(position).type);
    }

    @Override
    public int getCount() {
        return list_Title.size();
    }

    //此方法用来显示tab上的名字
    @Override
    public CharSequence getPageTitle(int position) {

        return list_Title.get(position % list_Title.size()).name;
    }
}