package com.mckuai.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.mckuai.fragment.BaseFragment;
import com.mckuai.fragment.ForumFragment;
import com.mckuai.fragment.GameEditerFragment;
import com.mckuai.fragment.MapFragment;
import com.mckuai.fragment.ResourceFragment;
import com.mckuai.fragment.ServerFragment;
import com.mckuai.fragment.SkinFragment;

import java.util.ArrayList;

public class FragmentAdapter extends FragmentPagerAdapter {

    public BaseFragment currentFragment;
    private ArrayList<BaseFragment> list;


    public FragmentAdapter(FragmentManager fm,ArrayList<BaseFragment> fragments){
        super(fm);
        this.list = fragments;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
	public Fragment getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public int getCount() {
		return list.size();
	}

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        currentFragment = (BaseFragment) object;
        super.setPrimaryItem(container, position, object);
    }

}
