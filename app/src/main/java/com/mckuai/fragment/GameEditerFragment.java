package com.mckuai.fragment;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mckuai.adapter.WorldAdapter;
import com.mckuai.mctools.item.WorldItem;
import com.mckuai.imc.GamePackageActivity;
import com.mckuai.imc.MCkuai;
import com.mckuai.imc.R;
import com.mckuai.mctools.WorldUtil.GameUntil;
import com.mckuai.mctools.WorldUtil.MCWorldUtil;
import com.mckuai.mctools.WorldUtil.OptionUntil;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;


public class GameEditerFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {
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

    private ImageView iv_gameMode;
    private ImageView iv_gameTime;
    private ImageView iv_thirdView;
    private ImageView iv_packageItem;
    private ListView lv_mapList;


    private MCWorldUtil gameEditer;
    private int mode;//地图模式
    private long size;//地图大小
    private String time;//白天黑夜
    private String viewName; //地图名字
    private boolean thirdPerson = false; //是否启用第三人称
    private ArrayList<WorldItem> worldItems;
    private int inventoryTypeCount;         //背包中物品种类数
    private int curWorldIndex;//当前显示的世界的索引
    private Integer[] res_Map = {R.drawable.background_map_0,R.drawable.background_map_1,R.drawable.background_map_2,R.drawable.background_map_3,R.drawable.background_map_4,R.drawable.background_map_5,R.drawable.background_map_6,R.drawable.background_map_7,R.drawable.background_map_8,R.drawable.background_map_9};
//    private int res_Map_index = 0;

    private ThinDownloadManager mDlManager;

    private boolean isShowUninstallAlert = true;
    private boolean isGameInstalled = false;
    private boolean isGameRunning = false;
    private boolean isGameVersionSupport = false;
    private boolean isDownloadGame = false;

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
                    Log.e(TAG, "地图数目：" + (null == worldItems ? 0: worldItems.size()));
                    setData(worldItems, isThirdView);
                }
            }, false);
        }
    }


    @Override
    public void onDestroy() {
        if (null!=mDlManager){
            mDlManager.cancelAll();
            mDlManager.release();
        }
        super.onDestroy();
    }


    private void initView() {
        tv_gameMode = (TextView) view.findViewById(R.id.tv_gameMode);
        tv_gameTime = (TextView) view.findViewById(R.id.tv_gameTime);
        tv_packageItemCount = (TextView) view.findViewById(R.id.tv_curItemCount);
        tv_thirdView = (TextView) view.findViewById(R.id.tv_curView);
        tv_mapName = (TextView) view.findViewById(R.id.tv_mapName);
        tv_notification = (TextView)view.findViewById(R.id.tv_notificationTitle);
        iv_map = (ImageView) view.findViewById(R.id.iv_map);
        iv_gameMode = (ImageView) view.findViewById(R.id.iv_gameMode);
        iv_gameTime = (ImageView) view.findViewById(R.id.iv_gameTime);
        iv_thirdView = (ImageView) view.findViewById(R.id.iv_thirdView);
        iv_packageItem = (ImageView) view.findViewById(R.id.iv_gamePackage);
        lv_mapList = (ListView) view.findViewById(R.id.lv_mapList);
        rl_notification = (RelativeLayout)view.findViewById(R.id.rl_notificationDownloadProgress);
        progressBar = (ProgressBar)view.findViewById(R.id.pb_downloadProgress);

        view.findViewById(R.id.rl_gameMode).setOnClickListener(this);
        view.findViewById(R.id.rl_gameTime).setOnClickListener(this);
        view.findViewById(R.id.rl_thirdView).setOnClickListener(this);
        view.findViewById(R.id.rl_gamePackage).setOnClickListener(this);
        view.findViewById(R.id.btn_startGame).setOnClickListener(this);
        view.findViewById(R.id.btn_selectMap).setOnClickListener(this);
        lv_mapList.setOnItemClickListener(this);
        rl_notification.setOnClickListener(this);
    }


    private void setData(ArrayList<WorldItem> worldList, boolean isThirdViewEnable) {
        this.worldItems = worldList;
        this.thirdPerson = isThirdViewEnable;
        if (null != worldList && !worldList.isEmpty()) {
            curWorldIndex = 0;
            getWorldInfo();
        }
        else {
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
        //背影
        iv_map.setBackgroundResource(res_Map[curWorldIndex % 10]);
    }

    private void switchGameMode() {
        mode = Math.abs(mode - 1);

        if (!worldItems.get(curWorldIndex).setGameMod(1 == mode)) {
            mode = Math.abs(mode - 1);
        }

        updateWorldInfo();
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
        if (MCkuai.getInstance().fragmentIndex != 0){
            return;
        }
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
//            int version = OptionUntil.getGameVersion_minor();

            int version = GameUntil.detectionGameVersion(getActivity());
            if (version < 9 || version > 10){
                isGameVersionSupport = false;
                showDownloadGame(false);
            }
            else {
                isGameVersionSupport = true;
            }
        } else {
            //showMessage("警告", "你还未安装游戏，不能修改游戏内容！");
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

        if (!isGameInstalled && rl_notification.getVisibility() != View.VISIBLE){
            showDownloadGame(true);
            return;
        }
        switch (v.getId()) {
            case R.id.rl_notificationDownloadProgress:
                //安装游戏
                MobclickAgent.onEvent(getActivity(),"installGame");
                String file = (String) v.getTag();
                if (null != file){
                    if (file.equals("false")){
                        rl_notification.setVisibility(View.INVISIBLE);
                    }
                    else {
                        installGame((String) v.getTag());
                    }
                }
                break;
            case R.id.rl_gameMode:
                //修改游戏模式
                MobclickAgent.onEvent(getActivity(),"switchGameMode");
                if (rl_notification.getVisibility() == View.VISIBLE){
                    showNotification(3, "正在下载游戏，请稍候！", R.id.fl_root);
                    return;
                }
                if (!checkGameVersion()){
                    return;
                }
                if (null != worldItems && worldItems.get(curWorldIndex).getLevel() != null) {
                    switchGameMode();
                } else {
                    showNotification(2, "提示：未检测到地图，点击地图获取更多精彩地图！", R.id.fl_root);
                }
                break;
            case R.id.rl_gameTime:
                //切换日夜
                MobclickAgent.onEvent(getActivity(),"switchTime");
                if (rl_notification.getVisibility() == View.VISIBLE){
                    showNotification(3, "正在下载游戏，请稍候！", R.id.fl_root);
                    return;
                }
                if (!checkGameVersion()){
                    return;
                }
                if (null != worldItems && worldItems.get(curWorldIndex).getLevel() != null) {
                    switchGameTime();
                } else {
                    showNotification(3, "提示：未检测到地图，点击地图获取更多精彩地图！", R.id.fl_root);
                }

                break;
            case R.id.rl_thirdView:
                MobclickAgent.onEvent(getActivity(),"switchView");
                if (rl_notification.getVisibility() == View.VISIBLE){
                    showNotification(3, "正在下载游戏，请稍候！", R.id.fl_root);
                    return;
                }
                //切换第三人称和第一人称
                if (null != worldItems && worldItems.get(curWorldIndex).getLevel() != null) {
                    switchView();
                } else {
                    showNotification(3, "提示：未检测到地图，点击地图获取更多精彩地图！", R.id.fl_root);
                }

                break;
            case R.id.rl_gamePackage:
                //修改背包
                MobclickAgent.onEvent(getActivity(),"showPackage");
                if (rl_notification.getVisibility() == View.VISIBLE){
                    showNotification(3, "正在下载游戏，请稍候！", R.id.fl_root);
                    return;
                }
                if (!checkGameVersion()){
                    return;
                }
                if (null != worldItems && null != worldItems.get(curWorldIndex)) {
                    changePackageItem();
                }
                else {
                    showNotification(3, "提示：未检测到地图，点击地图获取更多精彩地图！", R.id.fl_root);
                }
                break;
            case R.id.btn_startGame:
                //运行游戏
                MobclickAgent.onEvent(getActivity(), "startGame_tool");
                if (rl_notification.getVisibility() == View.VISIBLE){
                    showNotification(3, "正在下载游戏，请稍候！", R.id.fl_root);
                    return;
                }
                if (isGameInstalled) {
                    startGame();
                }
                break;
            case R.id.btn_selectMap:
                //选择地图
                MobclickAgent.onEvent(getActivity(),"selectMap");
                if (rl_notification.getVisibility() == View.VISIBLE){
                    showNotification(3, "正在下载游戏，请稍候！", R.id.fl_root);
                    return;
                }
                if (!checkGameVersion()){
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

    private boolean checkGameVersion(){
        if (!isGameVersionSupport ){
            showDownloadGame(true);
        }
        return isGameVersionSupport;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        lv_mapList.setVisibility(View.GONE);
        curWorldIndex = (int) id;
        getWorldInfo();
    }

    private void showDownloadGame(boolean showAlert){
        if (!showAlert && (isDownloadGame || !isShowUninstallAlert)){
            return;
        }
        isShowUninstallAlert = false;
        MobclickAgent.onEvent(getActivity(),"showDownloadGame");
        showAlert("提示", "为了更好的体验游戏，请下载《我的世界0.10.5》\n是否立即下载游戏？", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(),"downloadGame");
                String url = "http://softdown.mckuai.com:8081/wodeshijie_v_0_10_5.apk";
                final String downloadDir = MCkuai.getInstance().getMapDownloadDir() + url.substring(url.lastIndexOf("/") + 1, url.length());
                File file = new File(downloadDir);
                if (null != file && (!file.exists() || !file.isDirectory())){
                    file.mkdirs();
                }
                mDlManager = new ThinDownloadManager(1);
                DownloadRequest request = new DownloadRequest(Uri.parse(url)).setDestinationURI(Uri.parse(downloadDir));
                request.setDownloadListener(new DownloadStatusListener() {
                    @Override
                    public void onDownloadComplete(int i) {
                        rl_notification.setTag(downloadDir);
                        tv_notification.setText("下载完成，点击开始安装！");
                    }

                    @Override
                    public void onDownloadFailed(int i, int i1, String s) {
                        rl_notification.setTag("false");
                        tv_notification.setText("下载失败，请稍候再试！");
                    }

                    @Override
                    public void onProgress(int i, long l, int i1) {
                        progressBar.setProgress(i1);
                    }
                });
                if (mDlManager.add(request) > 0) {
                    rl_notification.setVisibility(View.VISIBLE);
                }
                isDownloadGame = true;
            }
        });
    }

    private void installGame(String file){
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
        if(lv_mapList.getVisibility() == View.VISIBLE){
            lv_mapList.setVisibility(View.GONE);
            return true;
        }
        return false;
    }
}
