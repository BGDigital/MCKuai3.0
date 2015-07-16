package com.mckuai.bean;

import java.io.Serializable;

/**
 * Created by kyly on 2015/6/28.
 */
public class GameServerInfo implements Serializable {
    private int gameId;
    private int gameSize;
    private int id;
    private int isApprove;
    private int isHuoDong;
    private int isLock;
    private int isNian;
    private int isOpen;
    private int isShowIp;
    private int onlineNum;              //在线人数
    private int peopleNum;              //容纳人数
    private int resCategroyId;
    private int serverPort;
    private int updateVersion;
    private int userId;
    private int position;
    private String resIp;                   //服务器地址
    private String resIp2;
    private String resIp3;
    private String resVersion;          //支持游戏的版本号
    private String serverTag;           //标签
    private String updateTime;       //更新时间
    private String viewName;         //显示名称，既地图名
    private String whiteList;
    private String dres;                //介绍
    private String icon;                //地图封面
    private String pictures;           //截图
    private String isOnline;            //是否在线，为no时是不在线
    private String userName;        //服主
    private String QQ;

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getGameSize() {
        return gameSize;
    }

    public void setGameSize(int gameSize) {
        this.gameSize = gameSize;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsApprove() {
        return isApprove;
    }

    public void setIsApprove(int isApprove) {
        this.isApprove = isApprove;
    }

    public int getIsHuoDong() {
        return isHuoDong;
    }

    public void setIsHuoDong(int isHuoDong) {
        this.isHuoDong = isHuoDong;
    }

    public int getIsLock() {
        return isLock;
    }

    public void setIsLock(int isLock) {
        this.isLock = isLock;
    }

    public int getIsNian() {
        return isNian;
    }

    public void setIsNian(int isNian) {
        this.isNian = isNian;
    }

    public int getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(int isOpen) {
        this.isOpen = isOpen;
    }

    public int getIsShowIp() {
        return isShowIp;
    }

    public void setIsShowIp(int isShowIp) {
        this.isShowIp = isShowIp;
    }

    public int getOnlineNum() {
        return onlineNum;
    }

    public void setOnlineNum(int onlineNum) {
        this.onlineNum = onlineNum;
    }

    public int getPeopleNum() {
        return peopleNum;
    }

    public void setPeopleNum(int peopleNum) {
        this.peopleNum = peopleNum;
    }

    public int getResCategroyId() {
        return resCategroyId;
    }

    public void setResCategroyId(int resCategroyId) {
        this.resCategroyId = resCategroyId;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public int getUpdateVersion() {
        return updateVersion;
    }

    public void setUpdateVersion(int updateVersion) {
        this.updateVersion = updateVersion;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getResIp() {
        return resIp;
    }

    public void setResIp(String resIp) {
        this.resIp = resIp;
    }

    public String getResIp2() {
        return resIp2;
    }

    public void setResIp2(String resIp2) {
        this.resIp2 = resIp2;
    }

    public String getResIp3() {
        return resIp3;
    }

    public void setResIp3(String resIp3) {
        this.resIp3 = resIp3;
    }

    public String getResVersion() {
        return resVersion;
    }

    public void setResVersion(String resVersion) {
        this.resVersion = resVersion;
    }

    public String getServerTag() {
        return serverTag;
    }

    public void setServerTag(String serverTag) {
        this.serverTag = serverTag;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(String whiteList) {
        this.whiteList = whiteList;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getDres() {
        return dres;
    }

    public void setDres(String dres) {
        this.dres = dres;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPictures() {
        return pictures;
    }

    public void setPictures(String pictures) {
        this.pictures = pictures;
    }

    public boolean getIsOnline() {
        return null == isOnline ? false: !isOnline.equalsIgnoreCase("no");
    }

    public void setIsOnline(String isOnline) {
        this.isOnline = isOnline;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getQQ() {
        return QQ;
    }

    public void setQQ(String QQ) {
        this.QQ = QQ;
    }
}
