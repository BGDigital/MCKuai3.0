package com.mckuai.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mckuai.bean.GameServerInfo;
import com.mckuai.bean.PageInfo;
import com.mckuai.imc.R;

import java.util.ArrayList;

public class ForumFragment extends BaseFragment {

    private View view;
    private PageInfo page;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if( null == view){
            view = inflater.inflate(R.layout.fragment_forum, container, false);
            page = new PageInfo();
        }
        return view;
    }

    private void loadData(){
        if(isLoading){
            return;
        }
    }

}
