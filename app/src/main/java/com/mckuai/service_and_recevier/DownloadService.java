package com.mckuai.service_and_recevier;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.mckuai.bean.Map;
import com.mckuai.bean.SkinItem;
import com.mckuai.imc.MCkuai;
import com.mckuai.mctools.WorldUtil.MCMapManager;
import com.mckuai.mctools.WorldUtil.MCSkinManager;

import java.io.File;
import java.net.URLEncoder;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.DLTaskListener;

/**
 * Created by kyly on 2015/7/20.
 */
public class DownloadService extends Service {
    private MCMapManager mMapManager;
    private MCSkinManager mSkinManager;
    private final String flag = "com.mckuai.imc.downloadprogress";
//    private final int keepAliveTime = 2 * 60 * 1000;
    private final String TAG = "DownloadService";
    private static final int RES_UNKNOW = 0;
    private static final int RES_MAP = 1;
    private static final int RES_SKIN = 2;
    private static final int RES_GAME=3;

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int operation = intent.getIntExtra("OPERATION", 0);
        Map map = (Map) intent.getSerializableExtra("MAP");
        SkinItem skin = (SkinItem) intent.getSerializableExtra("SKIN");
        String game = intent.getStringExtra("GAME");
        switch (operation) {
            case 0:
                //下载
                if (null != map) {
                    downloadMap(map);
                    break;
                }
                if (null != skin) {
                    downloadSkin(skin);
                    break;
                }
                if (null != game){
                    downloadGame(game);
                    break;
                }
                break;
            case 1:
                //斩停
                break;
            case 2:
                //取消
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void downloadMap(final Map map) {
        String downloadUrl = map.getSavePath();
        try {
            downloadUrl = downloadUrl.substring(0, downloadUrl.lastIndexOf("/") + 1) + URLEncoder.encode(downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1, downloadUrl.length()), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        downloadUrl = downloadUrl.replaceAll("\\+", "%20");
        String savePath = MCkuai.getInstance().getMapDownloadDir() + downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1, downloadUrl.length());
        DLManager.getInstance(MCkuai.getInstance()).dlStart(downloadUrl, savePath, new DLTaskListener() {
            @Override
            public void onStart(String fileName, String url) {
                super.onStart(fileName, url);
                sendProgressBroadCast(RES_MAP, map.getResId(), 1);
            }

            @Override
            public boolean onConnect(int type, String msg) {
                sendProgressBroadCast(RES_MAP, map.getResId(), 1);
                return super.onConnect(type, msg);
            }

            @Override
            public void onProgress(int progress) {
                sendProgressBroadCast(RES_MAP, map.getResId(), progress < 1 ? 1 : progress);
                super.onProgress(progress);
            }

            @Override
            public void onFinish(File file) {
                sendProgressBroadCast(RES_MAP, map.getResId(), 100);
                if (null == mMapManager) {
                    mMapManager = MCkuai.getInstance().getMapManager();
                }
                mMapManager.addDownloadMap(map);
                super.onFinish(file);
            }

            @Override
            public void onError(String error) {
                sendProgressBroadCast(RES_MAP, map.getResId(), -2);
                super.onError(error);
            }
        });
    }

    private void downloadSkin(final SkinItem skin) {
        String downloadUrl = skin.getSavePath();
        String savePath = MCkuai.getInstance().getSkinDownloadDir();
        DLManager.getInstance(MCkuai.getInstance()).dlStart(downloadUrl, savePath, new DLTaskListener() {
            @Override
            public void onStart(String fileName, String url) {
                super.onStart(fileName, url);
                sendProgressBroadCast(RES_SKIN, skin.getId() + "", 1);
            }

            @Override
            public boolean onConnect(int type, String msg) {
                sendProgressBroadCast(RES_SKIN, skin.getId() + "", 1);
                return super.onConnect(type, msg);
            }

            @Override
            public void onProgress(int progress) {
                super.onProgress(progress);
                sendProgressBroadCast(RES_SKIN, skin.getId() + "", progress);
            }

            @Override
            public void onFinish(File file) {
                super.onFinish(file);
                File skinFile = new File(MCkuai.getInstance().getSkinDownloadDir(),skin.getViewName()+".png");
                file.renameTo(skinFile);
                sendProgressBroadCast(RES_SKIN, skin.getId() + "", 100);
                if (null == mSkinManager){
                    mSkinManager = MCkuai.getInstance().getSkinManager();
                }
                mSkinManager.addSkin(skin);
            }

            @Override
            public void onError(String error) {
                super.onError(error);
                sendProgressBroadCast(RES_SKIN, skin.getId() + "", -2);
                Log.e(TAG, "下载皮肤失败，原因：" + error);
            }
        });
    }

    private void downloadGame(String game){
        if (10 < game.length()){

        }
    }


    private void sendProgressBroadCast(int resType, String ResId, int progress) {
        Intent intent = new Intent();
        switch (resType) {
            case RES_MAP:
                intent.putExtra("RES_TYPE", "MAP");
                break;
            case RES_SKIN:
                intent.putExtra("RES_TYPE", "SKIN");
                break;
        }
        intent.putExtra("RESID", ResId);
        intent.putExtra("PROGRESS", progress);
        intent.setAction(flag);
        sendBroadcast(intent);
    }
}
