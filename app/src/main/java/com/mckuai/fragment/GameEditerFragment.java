package com.mckuai.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mckuai.InventorySlot;
import com.mckuai.adapter.WorldAdapter;
import com.mckuai.bean.WorldInfo;
import com.mckuai.imc.GamePackageActivity;
import com.mckuai.imc.MCkuai;
import com.mckuai.imc.R;
import com.mckuai.until.GameUntil;
import com.mckuai.until.MCGameEditer;
import com.mckuai.until.OptionUntil;

import java.util.ArrayList;
import java.util.List;


public class GameEditerFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String TAG = "GameEditerFragment";

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

    private ListView lv_mapList;

    private MCGameEditer gameEditer;
    private int mode;//地图模式
    private long size;//地图大小
    private String time;//白天黑夜
    private String viewName; //地图名字
    private boolean thirdPerson = false; //是否启用第三人称
    private ArrayList<WorldInfo> worldInfos;
    private int inventoryTypeCount;         //背包中物品种类数
    private int curWorldIndex;//当前显示的世界的索引


    private boolean isShowGameRunning = false;
    private boolean isGameInstalled = true;
    private boolean isGameRunning = false;

    private WorldAdapter adapter;

    public GameEditerFragment() {
        setmTitle("工具");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detectionGameInfo();
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
        if (null == tv_gameMode) {
            initView();
        }

        if (isGameInstalled && null == worldInfos) {
            gameEditer = new MCGameEditer(new MCGameEditer.OnWorldLoadListener() {
                @Override
                public void OnComplete(ArrayList<WorldInfo> worldInfos, boolean isThirdView) {
                    Log.e(TAG, "地图数目：" + worldInfos.size());
                    setData(worldInfos, isThirdView);
                }
            }, false);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.w(TAG, "setUserVisibleHint:" + isVisibleToUser);
    }


    private void initView() {
        tv_gameMode = (TextView) view.findViewById(R.id.tv_gameMode);
        tv_gameTime = (TextView) view.findViewById(R.id.tv_gameTime);
        tv_packageItemCount = (TextView) view.findViewById(R.id.tv_curItemCount);
        tv_thirdView = (TextView) view.findViewById(R.id.tv_curView);
        tv_mapName = (TextView) view.findViewById(R.id.tv_mapName);
        iv_map = (ImageView) view.findViewById(R.id.iv_map);
        iv_gameMode = (ImageView) view.findViewById(R.id.iv_gameMode);
        iv_gameTime = (ImageView) view.findViewById(R.id.iv_gameTime);
        iv_thirdView = (ImageView) view.findViewById(R.id.iv_thirdView);
        iv_packageItem = (ImageView) view.findViewById(R.id.iv_gamePackage);
        lv_mapList = (ListView) view.findViewById(R.id.lv_mapList);

        view.findViewById(R.id.rl_gameMode).setOnClickListener(this);
        view.findViewById(R.id.rl_gameTime).setOnClickListener(this);
        view.findViewById(R.id.rl_thirdView).setOnClickListener(this);
        view.findViewById(R.id.rl_gamePackage).setOnClickListener(this);
        view.findViewById(R.id.btn_startGame).setOnClickListener(this);
        view.findViewById(R.id.btn_selectMap).setOnClickListener(this);
        lv_mapList.setOnItemClickListener(this);
    }

    private void setData(ArrayList<WorldInfo> worldList, boolean isThirdViewEnable) {
        this.worldInfos = worldList;
        this.thirdPerson = isThirdViewEnable;

        if (!worldList.isEmpty()) {
            curWorldIndex = 0;
            getWorldInfo();
        }
    }

    private void getWorldInfo() {
        WorldInfo world = worldInfos.get(curWorldIndex);
        if (!isGameInstalled) {
            return;
        }

        if (null != world && null != world.getLevel()) {
            if (null == world.getPlayer()) {
                world.setPlayer(gameEditer.getPlayer(world.getDir()));
            }

            mode = world.getLevel().getGameType();
            viewName = world.getLevel().getLevelName();
            time = world.getTime();
            inventoryTypeCount = world.getInventoryTypeCount();
        } else {
            mode = 0;
            viewName = null;
            time = "白天";
            inventoryTypeCount = 0;
        }
        updateWorldInfo();
    }

    private void updateWorldInfo() {
        //游戏模式
        if (mode == 0) {
            tv_gameMode.setText("生存");
            iv_gameMode.setBackgroundResource(R.drawable.icon_mode_live);
        } else {
            tv_gameMode.setText("创造");
            iv_gameMode.setBackgroundResource(R.drawable.icon_mode_creat);
        }
        //游戏地图名称
        tv_mapName.setText(null == viewName ? "点击\"选择地图\"以选择游戏地图" : viewName);

        //第三人称视角
        if (thirdPerson) {
            tv_thirdView.setText("已开启");
            iv_thirdView.setBackgroundResource(R.drawable.icon_thirdview_enable);
        } else {
            tv_thirdView.setText("未开启");
            iv_thirdView.setBackgroundResource(R.drawable.icon_thirdview_disable);
        }


        if (time.equals("白天")) {
            tv_gameTime.setText("白天");
            iv_gameTime.setBackgroundResource(R.drawable.icon_time_day);
        } else {
            tv_gameTime.setText("黑夜");
            iv_gameTime.setBackgroundResource(R.drawable.icon_time_night);
        }

        //背包
        if (0 != inventoryTypeCount) {
            tv_packageItemCount.setText(inventoryTypeCount + "种");
        } else {
            tv_packageItemCount.setText("没有物品");
        }
    }

    private void switchGameMode() {
        mode = Math.abs(mode - 1);

        if (!worldInfos.get(curWorldIndex).setIsCreative(1 == mode)) {
            mode = Math.abs(mode - 1);
        }

        updateWorldInfo();
    }


    private void switchGameTime() {
        if (time.equals("白天")) {
            if (worldInfos.get(curWorldIndex).setIsDay(false)) {
                time = "黑夜";
            }
        } else {
            if (worldInfos.get(curWorldIndex).setIsDay(true)) {
                time = "白天";
            }
        }
        updateWorldInfo();
    }

    private void switchView() {
        if (OptionUntil.setThirdPerson(!thirdPerson)) {
            thirdPerson = !thirdPerson;
        }
        updateWorldInfo();
    }

    private void changePackageItem() {
        Intent intent = new Intent(getActivity(), GamePackageActivity.class);
        MCkuai.getInstance().world = worldInfos.get(curWorldIndex);
        startActivityForResult(intent, 999);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            WorldInfo world = MCkuai.getInstance().world;
            if (null != world) {
                worldInfos.get(curWorldIndex).setInventory(world.getInventory());
                updateWorldInfo();
            }
        }
    }

    private void startGame() {
        if (!isGameInstalled) {
            showNotification(2, "警告：你还未安装游戏！", R.id.fl_root);
            return;
        }

        if (isGameRunning) {
            showNotification(3, "游戏已经在运行，无需再次启动！", R.id.fl_root);
            return;
        }

        GameUntil.startGame(getActivity());

    }

    private void detectionGameInfo() {
        isGameInstalled = GameUntil.detectionIsGameInstalled(getActivity());
        if (isGameInstalled) {
            isGameRunning = GameUntil.detectionIsGameRunning(getActivity());
            if (isGameRunning) {
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
        } else {
            showMessage("警告", "你还未安装游戏，不能修改游戏内容！");
            return;
        }

    }

    private void selectMap() {
        if (1 < worldInfos.size()) {
            if (null == adapter) {
                adapter = new WorldAdapter(getActivity());
                lv_mapList.setAdapter(adapter);
            }
            adapter.setData(worldInfos);
            lv_mapList.setVisibility(View.VISIBLE);
        } else {
            showNotification(1, "当前只有一张地图！", R.id.fl_root);
        }
    }

    @Override
    public void onClick(View v) {
        if (isGameRunning) {
            showNotification(3, "游戏正在运行，不能修改当前设置！", R.id.fl_root);
            return;
        }
        switch (v.getId()) {
            case R.id.rl_gameMode:
                if (worldInfos.get(curWorldIndex).getLevel() != null) {
                    switchGameMode();
                } else {
                    showNotification(3, "没有地图，不能修改当前设置！", R.id.fl_root);
                }
                break;
            case R.id.rl_gameTime:
                if (worldInfos.get(curWorldIndex).getLevel() != null) {
                    switchGameTime();
                } else {
                    showNotification(3, "没有地图，不能修改当前设置！", R.id.fl_root);
                }

                break;
            case R.id.rl_thirdView:
                if (worldInfos.get(curWorldIndex).getLevel() != null) {
                    switchView();
                } else {
                    showNotification(3, "没有地图，不能修改当前设置！", R.id.fl_root);
                }

                break;
            case R.id.rl_gamePackage:
                changePackageItem();
                break;
            case R.id.btn_startGame:
                startGame();
                break;
            case R.id.btn_selectMap:
                if (null != worldInfos && !worldInfos.isEmpty()) {
                    if (View.VISIBLE == lv_mapList.getVisibility()) {
                        lv_mapList.setVisibility(View.GONE);
                    } else {
                        selectMap();
                    }
                } else {
                    showNotification(3, "没有地图，不能修改当前设置！", R.id.rl_root);
                }

                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        lv_mapList.setVisibility(View.GONE);
        curWorldIndex = (int) id;
        getWorldInfo();
    }
}
