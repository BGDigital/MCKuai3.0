package com.mckuai.service_and_recevier;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.mckuai.bean.Map;
import com.mckuai.bean.SkinItem;
import com.mckuai.imc.MCkuai;
import com.mckuai.mctools.WorldUtil.MCMapManager;
import com.mckuai.mctools.WorldUtil.MCSkinManager;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by kyly on 2015/7/20.
 */
public class DownloadService extends Service {
    private ThinDownloadManager mDlManager;
    private MCMapManager mMapManager;
    private MCSkinManager mSkinManager;
    private ArrayList<DownloadTask> mDownloadTaskMap;
    private final String flag = "com.mckuai.imc.downloadprogress";
    private final int keepAliveTime = 2*60*1000;
    private final String TAG = "DownloadService";
    private static final int RES_UNKNOW = 0;
    private static final int RES_MAP = 1;
    private static final int RES_SKIN=2;

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
        SkinItem skin = (SkinItem) intent.getSerializableExtra("SKIN");
       if (null != map){
           download(RES_MAP, map);
       }
        if (null != skin){
            download(RES_SKIN,skin);
        }

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

    private int download(final int type,Object downloadItem){
        Map map = null;
        SkinItem skin =null;
        String downloadUrl = null;
        String savePath = null;
        if (null == downloadItem || 0 == type || 2 < type){
            return  -1;
        }
        else {
            switch (type){
                case RES_MAP:
                    map = (Map) downloadItem;
                    break;
                case RES_SKIN:
                    skin = (SkinItem) downloadItem;
                    break;
            }
            if (null == mDownloadTaskMap){
                mDownloadTaskMap = new ArrayList<>();
            }
            else{
                DownloadTask task = null;
                switch (type){
                    case RES_MAP:
                        task = getDownloadTask(map.getResId());
                        break;

                    case RES_SKIN:
                        task = getDownloadTask(skin.getId()+"");
                        break;
                }
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
        switch (type){
            case RES_MAP:
                downloadUrl = map.getSavePath();
                try {
                    downloadUrl = downloadUrl.substring(0, downloadUrl.lastIndexOf("/") + 1) + URLEncoder.encode(downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1, downloadUrl.length()), "UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                downloadUrl = downloadUrl.replaceAll("\\+", "%20");
                savePath = MCkuai.getInstance().getMapDownloadDir() + downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1, downloadUrl.length());
                break;

            case RES_SKIN:
                downloadUrl = skin.getSavePath();
                savePath = MCkuai.getInstance().getSkinDownloadDir() + skin.getViewName()+".png";
                break;
        }



        DownloadRequest request = new DownloadRequest(Uri.parse(downloadUrl)).setDestinationURI(Uri.parse(savePath));
        request.setDownloadListener(new DownloadStatusListener() {

            @Override
            public void onDownloadComplete(int i) {
                DownloadTask task = getDownloadTask(i);
                if (null != task) {
                    switch (type){
                        case RES_MAP:
                            if (null == mMapManager) {
                                mMapManager = MCkuai.getInstance().getMapManager();
                            }
                            mMapManager.addDownloadMap((Map)task.downloadItem);
                            break;

                        case RES_SKIN:
                            if (null == mSkinManager){
                                mSkinManager = MCkuai.getInstance().getSkinManager();
                            }
                            mSkinManager.addSkin(((SkinItem)task.downloadItem));
                            break;
                    }

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
                    sendProgressBroadCast(task, i1);
                }
            }
        });
        int token = mDlManager.add(request);
        if (token > 0){
            DownloadTask task = new DownloadTask();
            task.downloadToken = token;
            task.downloadItem = downloadItem;
            task.resType = type;
            mDownloadTaskMap.add(task);
            handler.removeMessages(1);
            sendProgressBroadCast(task,1);
        }
        return token;
    }



    private DownloadTask getDownloadTask(String resId){
        if (null == mDownloadTaskMap){
            return null;
        }
        for (DownloadTask task:mDownloadTaskMap){
            switch (task.resType){
                case RES_MAP:
                    if (((Map)task.downloadItem).getResId().equals(resId)){
                        return task;
                    }
                    break;

                case RES_SKIN:
                    if (resId.equals(((SkinItem)task.downloadItem).getId()+"")){
                        return task;
                    }
                    break;
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

    private void sendProgressBroadCast(DownloadTask task,int progress){
        Intent intent = new Intent();
        switch (task.resType){
            case RES_MAP:
                intent.putExtra("RES_TYPE","MAP");
                intent.putExtra("RESID", ((Map)task.downloadItem).getResId());
                intent.putExtra("PROGRESS", progress);
                break;

            case RES_SKIN:
                intent.putExtra("RES_TYPE","SKIN");
                intent.putExtra("RESID", ((SkinItem)task.downloadItem).getId()+"");
                intent.putExtra("PROGRESS", progress);
                break;
        }
        intent.setAction(flag);
        sendBroadcast(intent);
    }



    class DownloadTask{
        public int downloadToken;
        public int resType;
        public Object downloadItem;
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
