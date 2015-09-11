package com.mckuai.fragment;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.service.carrier.CarrierService;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mckuai.adapter.WorldAdapter;
import com.mckuai.mctools.item.GameItem;
import com.mckuai.mctools.item.WorldItem;
import com.mckuai.imc.GamePackageActivity;
import com.mckuai.imc.MCkuai;
import com.mckuai.imc.R;
import com.mckuai.mctools.WorldUtil.GameUntil;
import com.mckuai.mctools.WorldUtil.MCWorldUtil;
import com.mckuai.mctools.WorldUtil.OptionUntil;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.DLTaskListener;


public class GameEditerFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener,CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "GameEditerFragment";

    private View view;
    private ImageView iv_map;
    private TextView tv_mapName;
    private TextView tv_gameMode;
    private TextView tv_gameTime;
    private TextView tv_thirdView;
    private TextView tv_packageItemCount;
    private TextView tv_notification;
    private RelativeLayout rl_notification;
    private ProgressBar progressBar;

    private RadioButton iv_gameMode;
    private RadioButton iv_gameTime;
    private RadioButton iv_thirdView;
    private RadioButton iv_packageItem;
    private ListView lv_mapList;


    private MCWorldUtil gameEditer;
    private int mode;//地图模式
    private String time;//白天黑夜
    private String viewName; //地图名字
    private boolean thirdPerson = false; //是否启用第三人称
    private ArrayList<WorldItem> worldItems;//世界
    private ArrayList<GameItem> gameItems;//游戏版本
    private int inventoryTypeCount;         //背包中物品种类数
    private int curWorldIndex;//当前显示的世界的索引
    private Integer[] res_Map = {R.drawable.background_map_0, R.drawable.background_map_1, R.drawable.background_map_2, R.drawable.background_map_3, R.drawable.background_map_4, R.drawable.background_map_5, R.drawable.background_map_6, R.drawable.background_map_7, R.drawable.background_map_8, R.drawable.background_map_9};


    private boolean isShowUninstallAlert = true;
    private boolean isGameInstalled = false;
    private boolean isGameRunning = false;
    private boolean isDownloadGame = false;

    private int width;
    private int height;

    private WorldAdapter adapter;

    public GameEditerFragment() {
        setmTitle("工具");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == view) {
            view = inflater.inflate(R.layout.fragment_game_editer, container, false);
        }
        setmTitle("修改器");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null == tv_gameMode) {
            initView();
        }
        if (!isGameInstalled) {
            detectionGameInfo();
        }
        if (isGameInstalled && null == worldItems) {
            gameEditer = new MCWorldUtil(new MCWorldUtil.OnWorldLoadListener() {
                @Override
                public void OnComplete(ArrayList<WorldItem> worldItems, boolean isThirdView) {
                    //Log.e(TAG, "地图数目：" + (null == worldItems ? 0 : worldItems.size()));
                    /*Collections.sort(worldItems, new Comparator<WorldItem>() {
                        @Override
                        public int compare(WorldItem lhs, WorldItem rhs) {

                            int result = (int) (lhs.getLevel().getLastPlayed() - rhs.getLevel().getLastPlayed());
                            Log.e(TAG, lhs.getLevel().getLevelName() + "-" + rhs.getLevel().getLevelName() + ":" + result);
                            return result;
                        }
                    });*/
                    setData(worldItems, isThirdView);
                }
            }, false);
        }
    }


    private void initView() {
        tv_gameMode = (TextView) view.findViewById(R.id.tv_mod_value);
        tv_gameTime = (TextView) view.findViewById(R.id.tv_time_value);
        tv_packageItemCount = (TextView) view.findViewById(R.id.tv_backpack_value);
        tv_thirdView = (TextView) view.findViewById(R.id.tv_thirdperson_value);
        tv_mapName = (TextView) view.findViewById(R.id.tv_mapName);
        tv_notification = (TextView) view.findViewById(R.id.tv_notificationTitle);
        iv_map = (ImageView) view.findViewById(R.id.iv_map);
        iv_gameMode = (RadioButton) view.findViewById(R.id.btn_mod);
        iv_gameTime = (RadioButton) view.findViewById(R.id.btn_time);
        iv_thirdView = (RadioButton) view.findViewById(R.id.btn_thirdpersonvisual);
        iv_packageItem = (RadioButton) view.findViewById(R.id.btn_backpack);
        lv_mapList = (ListView) view.findViewById(R.id.lv_mapList);
        rl_notification = (RelativeLayout) view.findViewById(R.id.rl_notificationDownloadProgress);
        progressBar = (ProgressBar) view.findViewById(R.id.pb_downloadProgress);

        iv_gameMode.setOnCheckedChangeListener(this);
        iv_gameTime.setOnCheckedChangeListener(this);
        iv_thirdView.setOnCheckedChangeListener(this);
        iv_packageItem.setOnCheckedChangeListener(this);
        view.findViewById(R.id.btn_startGame).setOnClickListener(this);
        view.findViewById(R.id.btn_selectMap).setOnClickListener(this);
        lv_mapList.setOnItemClickListener(this);
        rl_notification.setOnClickListener(this);

        Display display =getActivity().getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
    }


    private void setData(ArrayList<WorldItem> worldList, boolean isThirdViewEnable) {
        this.worldItems = worldList;
        this.thirdPerson = isThirdViewEnable;
        if (null != worldList && !worldList.isEmpty()) {
            curWorldIndex = 0;
            getWorldInfo();
        } else {
            showNotification(2, "提示：未检测到地图，点击地图获取更多精彩地图！", R.id.fl_root);
        }
    }

    private void getWorldInfo() {
        WorldItem world = worldItems.get(curWorldIndex);
        if (!isGameInstalled) {
            return;
        }

        if (null != world && null != world.getLevel()) {
            if (null == world.getPlayer()) {
                world.setPlayer(gameEditer.getPlayer(world.getDir()));
            }
            world.resetLastPlayTime();

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
//            iv_gameMode.setBackgroundResource(R.drawable.icon_mode_live);
        } else {
            tv_gameMode.setText("创造");
//            iv_gameMode.setBackgroundResource(R.drawable.icon_mode_creat);
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
        //背影
        iv_map.setBackgroundResource(res_Map[curWorldIndex % 10]);
    }

    private void switchGameMode() {
        mode = Math.abs(mode - 1);
        if (!worldItems.get(curWorldIndex).setGameMod(1 == mode)) {
            mode = Math.abs(mode - 1);
        }
    }


    private void switchGameTime() {
        if (time.equals("白天")) {
            if (worldItems.get(curWorldIndex).setIsDay(false)) {
                time = "黑夜";
            }
        } else {
            if (worldItems.get(curWorldIndex).setIsDay(true)) {
                time = "白天";
            }
        }
    }

    private void switchView() {
        if (OptionUntil.setThirdPerson(!thirdPerson)) {
            thirdPerson = !thirdPerson;
        }
    }

    private void changePackageItem() {
        Intent intent = new Intent(getActivity(), GamePackageActivity.class);
        MCkuai.getInstance().world = worldItems.get(curWorldIndex);
        startActivityForResult(intent, 999);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            WorldItem world = worldItems.get(curWorldIndex);
            if (null != world && null != world.getPlayer()) {
                worldItems.get(curWorldIndex).getPlayer().setInventory(world.getInventory());
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
        if (MCkuai.getInstance().fragmentIndex != 0) {
            return;
        }
        final ArrayList<GameItem> games = GameUntil.detectionGame(getActivity());
        isGameInstalled = GameUntil.detectionIsGameInstalled(getActivity());
        if (null != games && !games.isEmpty()) {
            isGameInstalled = true;
            for (GameItem game : games) {
                if (game.isRunning()) {
                    isGameRunning = true;
                    break;
                }
            }
        }
        if (isGameInstalled) {
            if (isGameRunning) {
                showAlert("警告", "检测到我的世界正在运行,此时的修改不会生效,是否结束游戏?", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isGameRunning = !GameUntil.killGameTask(getActivity());
                        isGameRunning = !GameUntil.killGameTask(getActivity(), games);
                    }
                });
            }
        } else {
            showDownloadGame(false);
            return;
        }

    }

    private void selectMap() {
        worldItems = gameEditer.getAllWorlds();
        if (1 < worldItems.size()) {
            if (null == adapter) {
                adapter = new WorldAdapter(getActivity());
                lv_mapList.setAdapter(adapter);
            }
            adapter.setData(worldItems);
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

        if (!isGameInstalled && rl_notification.getVisibility() != View.VISIBLE) {
            showDownloadGame(true);
            return;
        }
        switch (v.getId()) {
            case R.id.rl_notificationDownloadProgress:
                //安装游戏
                MobclickAgent.onEvent(getActivity(), "installGame");
                String file = (String) v.getTag();
                if (null != file) {
                    if (file.equals("false")) {
                        rl_notification.setVisibility(View.INVISIBLE);
                    } else {
                        installGame((String) v.getTag());
                    }
                }
                break;
            case R.id.btn_startGame:
                //运行游戏
                MobclickAgent.onEvent(getActivity(), "startGame_tool");
                if (rl_notification.getVisibility() == View.VISIBLE) {
                    showNotification(3, "正在下载游戏，请稍候！", R.id.fl_root);
                    return;
                }
                if (isGameInstalled) {
                    startGame();
                }
                break;
            case R.id.btn_selectMap:
                //选择地图
                MobclickAgent.onEvent(getActivity(), "selectMap");
                if (rl_notification.getVisibility() == View.VISIBLE) {
                    showNotification(3, "正在下载游戏，请稍候！", R.id.fl_root);
                    return;
                }
                if (!checkGameInstall()) {
                    return;
                }
                if (null != worldItems && !worldItems.isEmpty()) {
                    if (View.VISIBLE == lv_mapList.getVisibility()) {
                        lv_mapList.setVisibility(View.GONE);
                    } else {
                        selectMap();
                    }
                } else {
                    showNotification(3, "提示：未检测到地图，点击地图获取更多精彩地图！", R.id.fl_root);
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.btn_mod:
                MobclickAgent.onEvent(getActivity(), "switchGameMode");
                if (!checkGameInstall()) {
                    return;
                }
                if (null != worldItems && worldItems.get(curWorldIndex).getLevel() != null) {
                    switchGameMode();
                } else {
                    showNotification(2, "提示：未检测到地图，点击地图获取更多精彩地图！", R.id.fl_root);
                }
                break;
            case R.id.btn_time:
                MobclickAgent.onEvent(getActivity(), "switchTime");
                if (!checkGameInstall()) {
                    return;
                }
                if (null != worldItems && worldItems.get(curWorldIndex).getLevel() != null) {
                    switchGameTime();
                } else {
                    showNotification(3, "提示：未检测到地图，点击地图获取更多精彩地图！", R.id.fl_root);
                }
                break;
            case R.id.btn_thirdpersonvisual:
                MobclickAgent.onEvent(getActivity(), "switchView");
                //切换第三人称和第一人称
                if (null != worldItems && !worldItems.isEmpty() && worldItems.get(curWorldIndex).getLevel() != null) {
                    switchView();
                } else {
                    showNotification(3, "提示：未检测到地图，点击地图获取更多精彩地图！", R.id.fl_root);
                }
                break;
            case R.id.btn_backpack:
                MobclickAgent.onEvent(getActivity(), "showPackage");
                if (!checkGameInstall()) {
                    return;
                }
                if (null != worldItems && null != worldItems.get(curWorldIndex)) {
                    changePackageItem();
                } else {
                    showNotification(3, "提示：未检测到地图，点击地图获取更多精彩地图！", R.id.fl_root);
                }
                break;
        }
        updateWorldInfo();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        lv_mapList.setVisibility(View.GONE);
        curWorldIndex = (int) id;
        getWorldInfo();
    }

    private boolean checkGameInstall() {
        if (!isGameInstalled) {
            showDownloadGame(true);
        }
        return isGameInstalled;
    }

    private void showDownloadGame(boolean showAlert) {
        if (!showAlert && (isDownloadGame || !isShowUninstallAlert)) {
            return;
        }
        isShowUninstallAlert = false;
        MobclickAgent.onEvent(getActivity(), "showDownloadGame");
        showAlert("提示", "为了更好的体验游戏，请下载《我的世界》\n是否立即下载游戏？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "downloadGame");
                String url = "http://softdown.mckuai.com:8081/mcpe0.11.1.apk";
                final String downloadDir = MCkuai.getInstance().getGameDownloadDir();
                File file = new File(downloadDir);
                if (null != file && (!file.exists() || !file.isDirectory())) {
                    file.mkdirs();
                }

                DLManager.getInstance(getActivity()).dlStart(url,downloadDir,new DLTaskListener(){
                    @Override
                    public void onStart(String fileName, String url) {
                        super.onStart(fileName, url);
                        isDownloadGame = true;
                    }

                    @Override
                    public boolean onConnect(int type, String msg) {
                        return super.onConnect(type, msg);
                    }

                    @Override
                    public void onProgress(final int progress) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e(TAG,"progress="+progress);
                                progressBar.setProgress(progress);
                            }
                        });
                        super.onProgress(progress);
                    }

                    @Override
                    public void onFinish(final File file) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rl_notification.setTag(file.getPath());
                                tv_notification.setText("下载完成，点击开始安装！");
                                progressBar.setProgress(100);
                            }
                        });
                        isDownloadGame = false;
                        super.onFinish(file);
                    }

                    @Override
                    public void onError(String error) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rl_notification.setTag("false");
                                tv_notification.setText("下载失败，请稍候再试！");
                            }
                        });

                        isDownloadGame = false;
                        super.onError(error);
                    }
                });
                rl_notification.setVisibility(View.VISIBLE);
            }
        });
    }

    private void installGame(String file) {
        isDownloadGame = false;
        if (null != file && file.length() > 10) {
            File apkFile = new File(file);
            if (null != apkFile && apkFile.exists() && apkFile.isFile()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                getActivity().startActivity(intent);
            }
        }
        rl_notification.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onBackKeyPressed() {
        if (null != lv_mapList &&lv_mapList.getVisibility() == View.VISIBLE) {
            lv_mapList.setVisibility(View.GONE);
            return true;
        }
        return false;
    }
}
