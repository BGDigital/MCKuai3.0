package com.mckuai.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kyly on 2015/8/12.
 * 皮肤的bean文件
 */
public class SkinItem implements Serializable {
    private int id;
    private int progress = -1;
    private int downNum;
    private String viewName;
    private String uploadMan;
    private String version;
    private String insertTime;
    private String icon;
    private String pics;
    private String desc;
    private String savePath;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getUploadMan() {
        return null == uploadMan? "麦友":uploadMan;
    }

    public void setUploadMan(String uploadMan) {
        this.uploadMan = uploadMan;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public String getInsertTimeEx() {
        if (null != insertTime) {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat output = new SimpleDateFormat("MM月dd日");
            try {
                Date date = input.parse(insertTime);
                if (null != date) {
                    String time = output.format(date);
                    if (null != time && !time.isEmpty()) {
                        return time;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "未知";
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPics() {
        return pics;
    }

    public void setPics(String pics) {
        this.pics = pics;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getDownNum() {
        return downNum;
    }

    public void setDownNum(int downNum) {
        this.downNum = downNum;
    }
}
