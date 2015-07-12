package com.mckuai.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mckuai.InventorySlot;
import com.mckuai.entity.Player;
import com.mckuai.imc.GamePackageActivity;
import com.mckuai.imc.MCkuai;
import com.mckuai.imc.R;
import com.mckuai.until.GameEditer;
import com.mckuai.until.GameUntil;
import com.mckuai.until.MCGameEditer;
import com.mckuai.until.MCMapManager;

import java.io.File;
import java.util.List;


public class GameEditerFragment extends BaseFragment implements View.OnClickListener {
    private static  final  String TAG = "GameEditerFragment";

    private View view;
    private ImageView iv_map;
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
    private MCMapManager mapManager;
//    private GameEditer editer;//for test

    private int mode;
    private String time;
    private boolean thirdPerson = false;
    private List<InventorySlot> inventorySlots;

    private String mapDir;
    private String mapName;
    private boolean isMapChanged = true;

    private boolean isShowGameRunning = false;
    private boolean isGameInstalled = true;
    private boolean isGameRunning = false;



    public GameEditerFragment(){
        setmTitle("工具");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mapManager = new MCMapManager();
        //editer = new GameEditer();
        Player player;
        File file = new File("/storage/sdcard0/games/com.mojang/minecraftWorlds/My World/db");
        if (null != file && file.exists() && file.isDirectory()){
            player = GameEditer.getPlayer(file);
        }
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

        if (null == mapManager){
           mapManager = MCkuai.getInstance().getMapManager();
        }

       detectionGameInfo();

        if (isGameInstalled){
            getWorldInfo();
            if (gameEditer.hasProfile()) {
                updateWorldInfo();
            }
            else {
                showNotification(2,"未能获取到游戏信息",R.id.fl_root);
            }
        }
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

    private void getWorldInfo(){
        if (!isGameInstalled){
            return;
        }

        if (null == gameEditer){
//            gameEditer = new MCGameEditer(mapDir);//
            gameEditer = new MCGameEditer("/storage/sdcard0/games/com.mojang/minecraftWorlds/My World");
            isMapChanged = false;
        }

        if (isMapChanged){
            gameEditer.setMapDir(mapDir);
            isMapChanged = false;
        }

        if (gameEditer.hasProfile()){
            mode = gameEditer.getGameMode();
            time = gameEditer.getTime();
            mapName = mapManager.getCurrentMapName();
            inventorySlots = gameEditer.getInventory();
            thirdPerson = gameEditer.isThirdPerson();
        }

    }

    private void updateWorldInfo(){
        //游戏模式
        if (mode == 0){
            tv_gameMode.setText("生存");
            iv_gameMode.setBackgroundResource(R.drawable.icon_mode_live);
        }
        else {
            tv_gameMode.setText("创造");
            iv_gameMode.setBackgroundResource(R.drawable.icon_mode_creat);
        }
        //游戏地图名称
        tv_mapName.setText(null == mapName ? "点击\"选择地图\"以选择游戏地图" : mapName);

        //第三人称视角
        if (thirdPerson){
            tv_thirdView.setText("已开启");
            iv_thirdView.setBackgroundResource(R.drawable.icon_thirdview_enable);
        }
        else {
            tv_thirdView.setText("未开启");
            iv_thirdView.setBackgroundResource(R.drawable.icon_thirdview_disable);
        }

        //白天夜晚
        if (null == time){
            time = "白天";
        }
            if (time.equals("白天")) {
                tv_gameTime.setText("白天");
                iv_gameTime.setBackgroundResource(R.drawable.icon_time_day);
            } else {
                tv_gameTime.setText("黑夜");
                iv_gameTime.setBackgroundResource(R.drawable.icon_time_night);
            }

        //背包
        if (null != inventorySlots && !inventorySlots.isEmpty()){
            tv_packageItemCount.setText(inventorySlots.size()+"种");
        }
        else {
            tv_packageItemCount.setText("没有物品");
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
            updateWorldInfo();
        }
        else {
            showNotification(2, "保存游戏模式失败!", R.id.fl_root);
        }
    }


    private void switchGameTime(){
        if (time.equals("白天")){
            gameEditer.setTimeToNight();
            time = "黑夜";
        }
        else {
            gameEditer.setTimeToMorning();
            time = "白天";
        }
        updateWorldInfo();
    }

    private void switchView(){
        thirdPerson = !thirdPerson;
        gameEditer.setThirdPerson(thirdPerson);
        updateWorldInfo();
    }

    private void changePackageItem(){
        Intent intent = new Intent(getActivity(), GamePackageActivity.class);
        MCkuai.getInstance().inventorySlots = inventorySlots;
        startActivity(intent);
    }

    private void startGame(){
        if (!isGameInstalled){
          showNotification(2,"警告：你还未安装游戏！",R.id.fl_root);
            return;
        }

        if (isGameRunning){
            showNotification(3, "游戏已经在运行，无需再次启动！", R.id.fl_root);
            return;
        }

        GameUntil.startGame(getActivity());

    }

    private void detectionGameInfo(){
        isGameInstalled = GameUntil.detectionIsGameInstalled(getActivity());
        if (isGameInstalled){
            isGameRunning = GameUntil.detectionIsGameRunning(getActivity());
            if (isGameRunning){
                showAlert("警告", "检测到我的世界正在运行,此时的修改不会生效,是否结束游戏?", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isGameRunning = !GameUntil.killGameTask(getActivity());
                    }
                });
            }
            mapDir = mapManager.getCurrentMapDir();
            mapName = mapManager.getCurrentMapName();
            isMapChanged = true;
        }
        else {
            showMessage("警告","你还未安装游戏，不能修改游戏内容！");
            return;
        }
        mapManager.closeDB();

    }

    private void selectMap(){
//        Intent intent = new Intent(getActivity(), MymapActivity.class);
//        getActivity().startActivity(intent);
        //showNotification(0,"选择地图",R.id.fl_root);
        //editer.getAllItem();
    }

    @Override
    public void onClick(View v) {
        if (isGameRunning){
            showNotification(3,"游戏正在运行，不能修改当前设置！",R.id.fl_root);
            return;
        }
        switch (v.getId()){
            case R.id.rl_gameMode:
                if (gameEditer.hasProfile()) {
                    switchGameMode();
                }
                else {
                    showNotification(3,"没有地图，不能修改当前设置！",R.id.fl_root);
                }
                break;
            case R.id.rl_gameTime:
                if (gameEditer.hasProfile()) {
                    switchGameTime();
                }
                else {
                    showNotification(3,"没有地图，不能修改当前设置！",R.id.fl_root);
                }

                break;
            case R.id.rl_thirdView:
                if (gameEditer.hasProfile()) {
                    switchView();
                }
                else {
                    showNotification(3,"没有地图，不能修改当前设置！",R.id.fl_root);
                }

                break;
            case R.id.rl_gamePackage:
                if (gameEditer.hasProfile()) {
                    if (null != gameEditer.getInventory()) {
                        changePackageItem();
                    }
                    else {
                        showNotification(3,"背包没有物品！",R.id.fl_root);
                    }
                }
                else {
                    showNotification(3,"没有地图，不能修改当前设置！",R.id.fl_root);
                }

                break;
            case R.id.btn_startGame:
                startGame();
                break;
            case R.id.btn_selectMap:
                if (gameEditer.hasProfile()) {
                    selectMap();
                }
                else {
                    showNotification(3,"没有地图，不能修改当前设置！",R.id.rl_root);
                }

                break;
        }
    }



}
