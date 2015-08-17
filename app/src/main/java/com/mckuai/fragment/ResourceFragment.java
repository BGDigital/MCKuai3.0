package com.mckuai.fragment;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.mckuai.adapter.FragmentAdapter;
import com.mckuai.imc.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResourceFragment extends BaseFragment implements ViewPager.OnPageChangeListener,RadioGroup.OnCheckedChangeListener{
    ViewPager viewPager;
    View view;
    RadioGroup rg_resource;
    RadioButton rb_map;
    RadioButton rb_skin;

    ArrayList<BaseFragment> fragments;
    FragmentAdapter adapter;

    boolean isViewChanged = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (null == view){
            view = inflater.inflate(R.layout.fragment_resource, container, false);
            initView();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()){
            showData();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            showData();
        }
    }

    private void initView(){
        if (null != view && null == viewPager){
            viewPager = (ViewPager) view.findViewById(R.id.vp_resource);
            rg_resource = (RadioGroup) view.findViewById(R.id.rg_resource);
            rb_skin = (RadioButton) view.findViewById(R.id.rb_resource_skin);
            rb_map = (RadioButton) view.findViewById(R.id.rb_resource_map);
            viewPager.setOnPageChangeListener(this);
            rg_resource.setOnCheckedChangeListener(this);
        }
    }


    private void showData(){
        if (null == fragments) {
            fragments = new ArrayList<>(2);
            fragments.add(new MapFragment());
            fragments.add(new SkinFragment());
            adapter = new FragmentAdapter(getChildFragmentManager(),fragments);
            viewPager.setAdapter(adapter);
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        switch (i){
            case 0:
                isViewChanged = true;
//                onCheckedChanged(rg_resource,rb_map.getId());
                rb_map.setChecked(true);
                isViewChanged = false;
                break;

            case 1:
                isViewChanged = true;
//                onCheckedChanged(rg_resource,rb_skin.getId());
                rb_skin.setChecked(true);
                isViewChanged = false;
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        if (!isViewChanged){
            switch (checkedId){
                case R.id.rb_resource_map:
                    viewPager.setCurrentItem(0);
                    break;

                case R.id.rb_resource_skin:
                    viewPager.setCurrentItem(1);
                    break;
            }
        }
    }
}
