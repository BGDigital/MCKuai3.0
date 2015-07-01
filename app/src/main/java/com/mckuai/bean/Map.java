package com.mckuai.bean;

import java.io.Serializable;

/**
 * Created by Zzz on 2015/6/24.
 */
public class Map implements Serializable {
    private String downNum; //下载数
    private String dres;//地图详情
    private int gameId;//
    private String icon;//封面图片
    private int id;//地图id
    private String insertTime;//更新时间
    private int isLock;//
    private String language;//地图语言
    private String pictures;//详细图片
    private String resCategroy;//大类型
    private String resCategroyTwo;//地图类型
    private String resId;//
    private String resSize;//大小
    private String resVersion;//版本号
    private String savePath;//下载路径
    private int serverId;//
    private String shortDres;//冒险地图
    private String updateTime;//
    private int updateVersion;//
    private String uploadMan;//作者
    private int userId;//
    private String viewName;//地图名
    private int viewNum;//
    private Boolean isSelected = false;


    public String getDownNum() {
        return downNum;
    }

    public void setDownNum(String downNum) {
        this.downNum = downNum;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getDres() {
        return dres;
    }

    public void setDres(String style) {
        this.dres = dres;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public int getIsLock() {
        return isLock;
    }

    public void setIsLock(int isLock) {
        this.isLock = isLock;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPictures() {
        return pictures;
    }

    public void setPictures(String insertTime) {
        this.pictures = pictures;
    }

    public String getResCategroy() {
        return resCategroy;
    }

    public void setResCategroy(String resCategroy) {
        this.resCategroy = resCategroy;
    }

    public String getResCategroyTwo() {
        return resCategroyTwo;
    }

    public void setResCategroyTwo(String resCategroyTwo) {
        this.resCategroyTwo = resCategroyTwo;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getResSize() {
        return resSize;
    }

    public void setResSize(String resSize) {
        this.resSize = resSize;
    }

    public String getResVersion() {
        return resVersion;
    }

    public void setResVersion(String resVersion) {
        this.resVersion = resVersion;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public int getUpdateVersion() {
        return updateVersion;
    }

    public void setUpdateVersion(int updateVersion) {
        this.updateVersion = updateVersion;
    }

    public String getShortDres() {
        return shortDres;
    }

    public void setShortDres(String shortDres) {
        this.shortDres = shortDres;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getViewNum() {
        return viewNum;
    }

    public void setViewNum(int viewNum) {
        this.viewNum = viewNum;
    }

    public String getUploadMan() {
        return uploadMan;
    }

    public void setUpdateVersion(String uploadMan) {
        this.uploadMan = uploadMan;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getFileName() {
        String tmp = savePath.substring(savePath.lastIndexOf("/") + 1, savePath.length());
        return tmp;
    }
}
