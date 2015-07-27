package com.mckuai.utils;

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
import android.widget.Toast;

import java.io.File;
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

    public static boolean detectionIsGameRunning(Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> run = activityManager.getRunningAppProcesses();
        if (null != run && !run.isEmpty()){
            for (ActivityManager.RunningAppProcessInfo info:run){
                if (info.processName.equalsIgnoreCase("com.mojang.minecraftpe")){
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean detectionIsGameInstalled(Context context){
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> applist = pm.getInstalledApplications(0);
        if (null != applist && !applist.isEmpty()){
            for (ApplicationInfo app:applist){
                if (app.packageName.equalsIgnoreCase("com.mojang.minecraftpe")){
                    return  true;
                }
            }
        }
        return  false;
    }

    public static  boolean installGame(Context context,String gameFile){
        File file = new File(gameFile) ;
        if (file.exists() && file.isFile()) {
            Intent intent = new Intent();
            intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
            context.startActivity(intent);
            return  true;
        }
        else {
            return  false;
        }
    }

    public static  String detectionGameVersion(Context context){
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packagelist = pm.getInstalledPackages(0);
        if (null != packagelist && !packagelist.isEmpty()){
            for (PackageInfo curpackage:packagelist){
                if (curpackage.packageName.equalsIgnoreCase("com.mojang.minecraftpe")){
                    return  curpackage.versionName;
                }
            }
        }
        return  null;
    }

    public static  boolean killGameTask(Context context){
        if (detectionIsGameRunning(context)) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.killBackgroundProcesses("com.mojang.minecraftpe");
        }
        return  !detectionIsGameRunning(context);
    }



    public static boolean  detectionIsGameHasProfile(Context context){
        String dir = getSDPath();
        if (null != dir){
            dir += "/games/com.mojang/";
        }

        File profileDir = new File(dir);
        if (profileDir.exists() && profileDir.isDirectory()) {
            dir+= "minecraftWorlds/";
            File mapFile = new File(dir);
            if (mapFile.exists() && mapFile.isDirectory() && null != mapFile.listFiles() && 0 < mapFile.listFiles().length){
                return  true;
            }
        }
        return  false;
    }

    private static String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在
        if   (sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//获取根目录
        }
        return sdDir.toString();
    }

    /**
     * 启动游戏
     * @param context
     */
    public static void startGame(Context context){
        if (detectionIsGameInstalled(context)) {
            mContext = context;

            showStartHint(context);

            Message msg = new Message();
            msg.what = 1;
            handler.sendEmptyMessageDelayed(1, 1000);
        }
        else {
            Toast.makeText(context,"你还未安装有游戏，请先下载...",Toast.LENGTH_SHORT).show();
        }
    }

    static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1){
                Intent intent = new Intent();
                ComponentName name = new ComponentName("com.mojang.minecraftpe","com.mojang.minecraftpe.MainActivity");
                intent.setComponent(name);
                intent.setAction(Intent.ACTION_VIEW);
                mContext.startActivity(intent);
            }
        }
    };

    static private void showStartHint(Context context){
        Toast.makeText(context,"正在启动游戏，请稍候...",Toast.LENGTH_SHORT).show();
    }


}
