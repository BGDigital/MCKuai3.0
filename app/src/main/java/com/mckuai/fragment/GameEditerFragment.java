package com.mckuai.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mckuai.imc.R;

public class GameEditerFragment extends BaseFragment {
    private static  final  String TAG = "GameEditerFragment";

    private ImageView iv_map;
    //private Button btn_selectMap;
    private TextView tv_mapName;
   // private ImageButton btn_startGame;
//    private RelativeLayout rl_gameMode;
    private TextView tv_gameMode;
    private TextView tv_gameTime;
    private TextView tv_thirdView;
    private TextView tv_packageItemCount;

    public GameEditerFragment(){
        setmTitle("工具");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.w(TAG,"onCreateView");
        return inflater.inflate(R.layout.fragment_game_editer, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.w(TAG,"onResume");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.w(TAG, "isVisibleToUser="+isVisibleToUser);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.w(TAG, "hidden="+hidden);
    }

    private void initView(){

    }
}
