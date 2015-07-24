package com.mckuai.service_and_recevier;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.mckuai.bean.Map;
import com.mckuai.imc.MCkuai;
import com.mckuai.utils.MCMapManager;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by kyly on 2015/7/20.
 */
public class DownloadService extends Service {
    private ThinDownloadManager mDlManager;
    private MCMapManager mMapManager;
    private ArrayList<DownloadTask> mDownloadTaskMap;
    private final String flag = "com.mckuai.imc.downloadprogress";
    private final int keepAliveTime = 2*60*1000;
    private final String TAG = "DownloadService";

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
        Map map = (Map) intent.getSerializableExtra("MAP");
       if (null != map){
           downloadMap(map);
       }
    /*   else {
           String downloadUrl = (String)intent.getStringExtra("GAME_URL");
           if (null != downloadUrl && 10 < downloadUrl.length()) {
               downloadGame(downloadUrl);
           }
       }*/
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean stopService(Intent name) {
        if (null != mDlManager){
            mDlManager.cancelAll();
        }
        if (null != mMapManager){
            mMapManager.closeDB();
        }
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        if (null != mDownloadTaskMap){
            mDownloadTaskMap.clear();
            mDownloadTaskMap = null;
        }
        super.onDestroy();
    }

    private int downloadMap(Map map){
        if (null == map){
            return  -1;
        }
        else {
            if (null == mDownloadTaskMap){
                mDownloadTaskMap = new ArrayList<>();
            }
            else{
                DownloadTask task = getDownloadTask(map.getResId());
                if (null != task){
                    return task.downloadToken;
                }
            }
        }

        if (null == mDlManager){
            mDlManager = new ThinDownloadManager(3);
        }
        else {
            //删除可能存在的关闭服务的消息
            handler.removeMessages(1);
        }

        //下载和保存路径
        String url = map.getSavePath();
        try {
            url = url.substring(0, url.lastIndexOf("/") + 1) + URLEncoder.encode(url.substring(url.lastIndexOf("/") + 1, url.length()), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        url = url.replaceAll("\\+", "%20");
        String downloadDir = MCkuai.getInstance().getMapDownloadDir() + url.substring(url.lastIndexOf("/") + 1, url.length());

        DownloadRequest request = new DownloadRequest(Uri.parse(url)).setDestinationURI(Uri.parse(downloadDir));
        request.setDownloadListener(new DownloadStatusListener() {

            @Override
            public void onDownloadComplete(int i) {
                DownloadTask task = getDownloadTask(i);
                if (null != task) {
                    if (null == mMapManager) {
                        mMapManager = MCkuai.getInstance().getMapManager();
                    }
                    mMapManager.addDownloadMap(task.downloadMap);
                    mDownloadTaskMap.remove(task);
                    if (mDownloadTaskMap.isEmpty()){
                        handler.sendEmptyMessageDelayed(1,keepAliveTime);//延时2分钟关闭服务
                    }
                }
            }

            @Override
            public void onDownloadFailed(int i, int i1, String s) {
                DownloadTask task = getDownloadTask(i);
                if (null != task) {
                    if (null != mDownloadTaskMap && !mDownloadTaskMap.isEmpty()){
                        mDownloadTaskMap.remove(task);
                        if (mDownloadTaskMap.isEmpty()){
                            handler.sendEmptyMessageDelayed(1,keepAliveTime);//延时2分钟关闭服务
                        }
                    }
                }
            }

            @Override
            public void onProgress(int i, long l, int i1) {
                DownloadTask task = getDownloadTask(i);
                if (null != task) {
                    sendProgressBroadCast(task.downloadMap.getResId(), i1);
                }
            }
        });
        int token = mDlManager.add(request);
        if (token > 0){
            DownloadTask task = new DownloadTask();
            task.downloadToken = token;
            task.downloadMap = map;
            mDownloadTaskMap.add(task);
            handler.removeMessages(1);
        }
        return token;
    }

    private DownloadTask getDownloadTask(String resId){
        if (null == mDownloadTaskMap){
            return null;
        }
        for (DownloadTask task:mDownloadTaskMap){
            if (task.downloadMap.getResId().equals(resId)){
                return task;
            }
        }
        return null;
    }

    private DownloadTask getDownloadTask(int token){
        if (null == mDownloadTaskMap){
            return null;
        }

        for (DownloadTask task:mDownloadTaskMap){
            if (task.downloadToken == token) {
                return task;
            }
        }
        return null;
    }

    private void sendProgressBroadCast(String resId,int progress){
        Intent intent = new Intent();
        intent.putExtra("MAP_RESID",resId);
        intent.putExtra("MAP_PROGRESS", progress);
        intent.setAction(flag);
        sendBroadcast(intent);
    }

    class DownloadTask{
        public int downloadToken;
        public Map downloadMap;
    }

    /**
     * 关闭服务
     */
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 1:
                    if (null != mDlManager){
                        //mDlManager.release();
                    }
                    try{
                    stopSelf();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

}
