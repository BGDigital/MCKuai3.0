package com.mckuai.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mckuai.imc.GamePackageActivity;
import com.mckuai.imc.R;

public class GameEditerFragment extends BaseFragment implements View.OnClickListener {
    private static  final  String TAG = "GameEditerFragment";

    private View view;
    private ImageView iv_map;
    //private Button btn_selectMap;
    private TextView tv_mapName;
    private TextView tv_gameMode;
    private TextView tv_gameTime;
    private TextView tv_thirdView;
    private TextView tv_packageItemCount;

    private ImageView iv_gameMode;
    private ImageView iv_gameTime;
    private ImageView iv_thirdView;
    private ImageView iv_packageItem;

    public GameEditerFragment(){
        setmTitle("工具");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.w(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_game_editer, container, false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.w(TAG, "onResume");
        if (null == tv_gameMode){
            initView();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.w(TAG, "isVisibleToUser=" + isVisibleToUser);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.w(TAG, "hidden=" + hidden);
    }

    private void initView(){
        tv_gameMode = (TextView)view.findViewById(R.id.tv_gameMode) ;
        tv_gameTime = (TextView)view.findViewById(R.id.tv_gameTime) ;
        tv_packageItemCount = (TextView)view.findViewById(R.id.tv_curItemCount) ;
        tv_thirdView = (TextView)view.findViewById(R.id.tv_curView) ;
        tv_mapName = (TextView)view.findViewById(R.id.tv_mapName) ;
        iv_map = (ImageView)view.findViewById(R.id.iv_map);
        iv_gameMode = (ImageView)view.findViewById(R.id.iv_gameMode);
        iv_gameTime = (ImageView)view.findViewById(R.id.iv_gameTime);
        iv_thirdView = (ImageView)view.findViewById(R.id.iv_thirdView);
        iv_packageItem = (ImageView)view.findViewById(R.id.iv_gamePackage);

        view.findViewById(R.id.rl_gameMode).setOnClickListener(this);
        view.findViewById(R.id.rl_gameTime).setOnClickListener(this);
        view.findViewById(R.id.rl_thirdView).setOnClickListener(this);
        view.findViewById(R.id.rl_gamePackage).setOnClickListener(this);
        view.findViewById(R.id.btn_startGame).setOnClickListener(this);
        view.findViewById(R.id.btn_selectMap).setOnClickListener(this);
    }

    private void switchGameMode(){
        if (tv_gameMode.getText().equals("创造")){
            tv_gameMode.setText("生存");
            iv_gameMode.setBackgroundResource(R.drawable.icon_mode_live);
        }
        else{
            tv_gameMode.setText("创造");
            iv_gameMode.setBackgroundResource(R.drawable.icon_mode_creat);
        }
    }

    private void switchGameTime(){
        if (tv_gameTime.getText().equals("白天")){
            tv_gameTime.setText("黑夜");
            iv_gameTime.setBackgroundResource(R.drawable.icon_time_night);
        }
        else {
            tv_gameTime.setText("白天");
            iv_gameTime.setBackgroundResource(R.drawable.icon_time_day);
        }
    }

    private void switchView(){
        if (tv_thirdView.getText().equals("未开启")){
            tv_thirdView.setText("已开启");
            iv_thirdView.setBackgroundResource(R.drawable.icon_thirdview_enable);
        }
        else {
            tv_thirdView.setText("未开启");
            iv_thirdView.setBackgroundResource(R.drawable.icon_thirdview_disable);
        }
    }

    private void changePackageItem(){
        Intent intent = new Intent(getActivity(), GamePackageActivity.class);
        startActivity(intent);
    }

    private void startGame(){
        showNotification(0,"启动游戏",R.id.fl_root);
    }

    private void selectMap(){
        showNotification(0,"选择地图",R.id.fl_root);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_gameMode:
                switchGameMode();
                break;
            case R.id.rl_gameTime:
                switchGameTime();
                break;
            case R.id.rl_thirdView:
                switchView();
                break;
            case R.id.rl_gamePackage:
                changePackageItem();
                break;
            case R.id.btn_startGame:
                startGame();
                break;
            case R.id.btn_selectMap:
                selectMap();
                break;
        }
    }
}
