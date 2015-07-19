package com.mckuai.service_and_recevier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by kyly on 2015/7/20.
 */
public class DownloadProgressRecevier extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != intent){
            String resId = intent.getStringExtra("MAP_RESID");
            int progress = intent.getIntExtra("MAP_PROGRESS",-1);
            onProgress(resId,progress);
        }
    }

    /**
     * 获取下载 的进度
     * @param resId 地图的resId
     * @param progress 下载进度
     */
    public void onProgress(String resId,int progress){

    };
}
