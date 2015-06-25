package com.mckuai.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mckuai.fragment.BaseFragment;

import java.util.List;

public class FragmentAdapter extends FragmentPagerAdapter {
	
	List<BaseFragment> list;

	public FragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	public FragmentAdapter(FragmentManager fm,List<BaseFragment> list) {
		super(fm);
		this.list=list;
	}


	@Override
	public Fragment getItem(int arg0) {
		return (Fragment)list.get(arg0);
	}

	@Override
	public int getCount() {
		return list.size();
	}

}
