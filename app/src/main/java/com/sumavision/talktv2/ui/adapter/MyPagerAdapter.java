package com.sumavision.talktv2.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class MyPagerAdapter extends FragmentPagerAdapter {
	ArrayList<String> titleList;
	ArrayList<Fragment> fragments;

	public MyPagerAdapter(FragmentManager fm, ArrayList<String> titleList,
						  ArrayList<Fragment> fragments) {
		super(fm);
		this.titleList = titleList;
		this.fragments = fragments;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return this.titleList.get(position);
	}

	@Override
	public int getCount() {
		return this.titleList.size();
	}

	@Override
	public Fragment getItem(int position) {
		return this.fragments.get(position);
	}

}
