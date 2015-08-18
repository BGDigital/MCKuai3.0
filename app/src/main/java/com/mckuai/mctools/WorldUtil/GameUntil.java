package com.mckuai.mctools.WorldUtil;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.mckuai.mctools.item.GameItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by kyly on 2015/6/30.
 * 提供管理我的世界的相关功能，包括：
 * 检测游戏是否安装
 * 安装游戏
 * 检测游戏是否正在运行
 * 获取游戏版本号
 * 杀死游戏
 */
public class GameUntil {

    private static Context mContext;
    private static String packageName;

    public static ArrayList<GameItem> detectionGame(Context context) {
        ArrayList<GameItem> gameItems = detectionGameInstalled(context);
        if (null != gameItems && !gameItems.isEmpty()) {
            detectionGameRunning(gameItems, context);
            return gameItems;
        }
        return null;
    }

    private static ArrayList<GameItem> detectionGameInstalled(Context context) {
        ArrayList<GameItem> games = new ArrayList<>(3);
        PackageManager pm = context.getPackageManager();

        List<PackageInfo> packageInfoList = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : packageInfoList) {
            if (null != packageInfo && null != packageInfo.packageName && (packageInfo.packageName.indexOf("com.mojang.minecraftpe") > -1)) {
                GameItem item = new GameItem();
                item.setPackageName(packageInfo.packageName);
                item.setVersionName(packageInfo.versionName);
                if (item.getMinorVersion() > 8 && item.getMinorVersion() < 12) {
                    games.add(item);
                }
            }
        }
        return games;
    }

    private static void detectionGameRunning(ArrayList<GameItem> items, Context context) {
        if (null == items || items.isEmpty()) {
            return;
        }
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runApps = activityManager.getRunningAppProcesses();
        if (null != runApps && !runApps.isEmpty()) {
            for (GameItem game:items){
                for (ActivityManager.RunningAppProcessInfo app:runApps){
                    if (app.processName.equalsIgnoreCase(game.getPackageName())){
                        game.setIsRunning(true);
                        break;
                    }
                }
            }
        }
    }

    private static  void detectionIsVersionSuport(ArrayList<GameItem> gameItems){
        if (null != gameItems && !gameItems.isEmpty()){
            Iterator iterator = gameItems.iterator();
            while (iterator.hasNext()){
                GameItem item = (GameItem)iterator.next();
                if (item.getMinorVersion() < 9 || item.getMinorVersion() > 11){
                    iterator.remove();
                }
            }
        }
    }


    public static boolean detectionIsGameRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> run = activityManager.getRunningAppProcesses();
        if (null != run && !run.isEmpty()) {
            for (ActivityManager.RunningAppProcessInfo info : run) {
                if (info.processName.indexOf("com.mojang.minecraftpe") > -1) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean detectionIsGameInstalled(Context context) {
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> applist = pm.getInstalledApplications(0);

        if (null != applist && !applist.isEmpty()) {
            for (ApplicationInfo app : applist) {
                if ((0 != (app.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE)) && app.packageName.indexOf("com.mojang.minecraftpe") > -1) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean installGame(Context context, String gameFile) {
        File file = new File(gameFile);
        if (file.exists() && file.isFile()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            context.startActivity(intent);
            return true;
        } else {
            return false;
        }
    }

    public static int detectionGameVersion(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packagelist = pm.getInstalledPackages(0);
        if (null != packagelist && !packagelist.isEmpty()) {
            for (PackageInfo curpackage : packagelist) {
                if (curpackage.packageName.equalsIgnoreCase("com.mojang.minecraftpe")) {
                    String code = curpackage.versionName;
                    String[] version = code.split("\\.");
                    if (null != version && version.length == 3) {
                        return Integer.parseInt(version[1]);
                    }
                    return 0;
                }
            }
        }
        return 0;
    }

    public static int detectionGameVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packagelist = pm.getInstalledPackages(0);
        if (null != packagelist && !packagelist.isEmpty()) {
            for (PackageInfo curpackage : packagelist) {
                if (curpackage.packageName.equalsIgnoreCase("com.mojang.minecraftpe")) {
                    return curpackage.versionCode;
                }
            }
        }
        return 0;
    }

    public static boolean killGameTask(Context context) {
        if (detectionIsGameRunning(context)) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.killBackgroundProcesses("com.mojang.minecraftpe");
        }
        return !detectionIsGameRunning(context);
    }

    public static  boolean killGameTask(Context context, ArrayList<GameItem> games){
        if (null == context || null == games || games.isEmpty()){
            return true;
        }
        boolean result = true;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (GameItem item:games){
            if (item.isRunning()){
                activityManager.killBackgroundProcesses(item.getPackageName());
                item.setIsRunning(false);
            }
        }
        return true;
    }


    public static boolean detectionIsGameHasProfile(Context context) {
        String dir = getSDPath();
        if (null != dir) {
            dir += "/games/com.mojang/";
        }

        File profileDir = new File(dir);
        if (profileDir.exists() && profileDir.isDirectory()) {
            dir += "minecraftWorlds/";
            File mapFile = new File(dir);
            if (mapFile.exists() && mapFile.isDirectory() && null != mapFile.listFiles() && 0 < mapFile.listFiles().length) {
                return true;
            }
        }
        return false;
    }

    private static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取根目录
        }
        return sdDir.toString();
    }

    /**
     * 启动游戏，未指定版本，默认启动最高版本的游戏
     *
     * @param context
     */
    public static boolean startGame(Context context) {
        if (null != context ){
            mContext = context;
            ArrayList<GameItem> gameItems = detectionGame(context);
            int maxVersion = 0;
            if (null != gameItems && !gameItems.isEmpty()){
                for (GameItem item:gameItems){
                    if (item.getMinorVersion() > maxVersion) {
                        maxVersion = item.getMinorVersion();
                        packageName = item.getPackageName();
                    }
                }
                showStartHint(context);
                handler.sendEmptyMessageDelayed(1,1000);
                return true;
            }
        }
        return false;
    }

    /**
     * 启动特定版本的游戏
     * @param context
     * @param version 要启动的版本
     * @return 如果成功启动则返回true，否则返回false
     */
    public static boolean startGame(Context context,int version){
        if (null != context) {
            mContext = context;
            ArrayList<GameItem> gameItems = detectionGame(context);
            if (null != gameItems && !gameItems.isEmpty()){
                for (GameItem item:gameItems){
                    if (item.getMinorVersion() == version){
                        showStartHint(context);
                        packageName = item.getPackageName();
                        handler.sendEmptyMessageDelayed(1, 1000);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Intent intent = new Intent();
                ComponentName name = new ComponentName(packageName, "com.mojang.minecraftpe.MainActivity");
                intent.setComponent(name);
                intent.setAction(Intent.ACTION_VIEW);
                mContext.startActivity(intent);
            }
        }
    };

    static private void showStartHint(Context context) {
        Toast.makeText(context, "正在启动游戏，请稍候...", Toast.LENGTH_SHORT).show();
    }


}
