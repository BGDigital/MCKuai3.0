package com.mckuai.until;


import cn.aigestudio.downloader.interfaces.DLTaskListener;

/**
 * Created by kyly on 2015/7/6.
 */
public class MCDTListener  extends DLTaskListener {
    private int process = 0;


    public void onProgress(int progress) {
        this.process = progress;
    }

    public int getProcess(){
        return process;
    }
}
