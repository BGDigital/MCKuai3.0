package com.mckuai.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.mckuai.fragment.BaseFragment;
import com.mckuai.fragment.ForumFragment;
import com.mckuai.fragment.GameEditerFragment;
import com.mckuai.fragment.MapFragment;
import com.mckuai.fragment.ResourceFragment;
import com.mckuai.fragment.ServerFragment;
import com.mckuai.fragment.SkinFragment;

public class FragmentAdapter extends FragmentStatePagerAdapter {

    public BaseFragment currentFragment;
    private int type = 0;

	public FragmentAdapter(FragmentManager fm,int type) {
		super(fm);
        this.type = type;
	}

    public FragmentAdapter(FragmentManager fm){
        super(fm);
    }

	@Override
	public Fragment getItem(int arg0) {
		//return (Fragment)list.get(arg0);
        BaseFragment fragment;
        if (0 == type) {
            switch (arg0) {
                case 1:
                    return new ResourceFragment();
                case 2:
                    return new ServerFragment();
                case 3:
                    return new ForumFragment();
                default:
                    return new GameEditerFragment();
            }
        }
        else {
            switch (arg0){
                case 1:
                    return new SkinFragment();
                default:
                    return new MapFragment();
            }
        }
	}

	@Override
	public int getCount() {
//		return list.size();
        switch (type){
            case 1:
                return 2;
            default:
                return 4;
        }
	}

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        currentFragment = (BaseFragment) object;
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (0 != type){
            switch (position){
                case 1:
                    return "地图";
                default: return "皮肤";
            }
        }
        else
        return super.getPageTitle(position);
    }
}
