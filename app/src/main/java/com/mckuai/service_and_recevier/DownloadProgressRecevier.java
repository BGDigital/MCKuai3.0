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
            String resType = intent.getStringExtra("RES_TYPE");
            String resId = intent.getStringExtra("RESID");
            int progress = intent.getIntExtra("PROGRESS", -1);
            switch (resType){
                case "MAP":
                    onProgress(1,resId,progress < 1? 1:progress);
                    break;
                case "SKIN":
                    onProgress(2,resId,progress < 1? 1:progress);
                    break;
            }

        }
    }

    /**
     * 获取下载 的进度
     * @param resId 地图的resId
     * @param progress 下载进度
     */
    public void onProgress(int resType,String resId,int progress){

    };
}
