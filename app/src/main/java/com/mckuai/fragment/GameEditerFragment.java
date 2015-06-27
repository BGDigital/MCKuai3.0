package com.mckuai.fragment;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
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
import com.mckuai.until.MCGameEditer;

import java.util.List;

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
    private MCGameEditer gameEditer;

    private int mode;
    private int time;
    private int viewtype;

    private boolean isShowGameRunning = false;


    public GameEditerFragment(){
        setmTitle("工具");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameEditer = new MCGameEditer();
        getProfileInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (null == view) {
            view = inflater.inflate(R.layout.fragment_game_editer, container, false);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.w(TAG, "onResume");
        if (null == tv_gameMode){
            initView();
        }
        mode = gameEditer.getGameMode();
        showGameMode();
        detectionGameRunning();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.w(TAG, "setUserVisibleHint:" + isVisibleToUser);
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

    private void getProfileInfo(){
        if (gameEditer.hasProfile()){
            mode = gameEditer.getGameMode();
        }
    }

    private void switchGameMode(){
        int temp;
        if (0 == mode){
            temp = 1;
        }
        else {
            temp = 0;
        }
        if (gameEditer.setGameMode(temp)){
            mode = temp;
            showGameMode();
        }
        else {
            showNotification(2, "保存游戏模式失败!", R.id.fl_root);
        }
    }

    private void showGameMode(){
        if (mode == 0){
            tv_gameMode.setText("生存");
            iv_gameMode.setBackgroundResource(R.drawable.icon_mode_live);
        }
        else {
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
        Intent intent = new Intent();
        ComponentName name = new ComponentName("com.mojang.minecraftpe","com.mojang.minecraftpe.MainActivity");
        intent.setComponent(name);
        intent.setAction(Intent.ACTION_VIEW);
        startActivity(intent);
    }

    private void detectionGameRunning(){
        if (!isShowGameRunning){
            ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> run = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo info:run){
                if (info.processName.equalsIgnoreCase("com.mojang.minecraftpe")){
                    showAlert("警告", "检测到我的世界正在运行,此时的修改不会生效,是否结束游戏?", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            killGame();
                        }
                    });
                    break;
                }
            }
        }
        isShowGameRunning = true;
    }

    private void killGame(){
        ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.killBackgroundProcesses("com.mojang.minecraftpe");
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
