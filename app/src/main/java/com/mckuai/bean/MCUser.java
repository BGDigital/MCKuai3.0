package com.mckuai.bean;

import java.io.Serializable;

/**
 * Created by kyly on 2015/6/23.
 */


public class MCUser implements Serializable {
    private int id;// 用户id
    private int score;// 当前积分
    private int level;// 当前等级
    private int isServerActor;// 是否是腐竹或者名人 1:腐竹 2:名人 3:腐竹申请 4:名人申请
    private int talkNum;// 发帖数
    private int homeNum;// 小屋数
    private int dynamicNum;// 动态数
    private int messageNum;// 消息数
    private int workNum;// 作品数
    private float process; // 等级积分进度
    private String nike;// 昵称，显示用
    private String headImg;// 头像
    private String gender;// 性别
    private String userType;
    private String addr;// 定位
    private String name;// 融云端的注册用户名，实为qq登录后的openid
    private String token;//融云的token

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getIsServerActor() {
        return isServerActor;
    }

    public void setIsServerActor(int isServerActor) {
        this.isServerActor = isServerActor;
    }

    public int getTalkNum() {
        return talkNum;
    }

    public void setTalkNum(int talkNum) {
        this.talkNum = talkNum;
    }

    public int getHomeNum() {
        return homeNum;
    }

    public void setHomeNum(int homeNum) {
        this.homeNum = homeNum;
    }

    public int getDynamicNum() {
        return dynamicNum;
    }

    public void setDynamicNum(int dynamicNum) {
        this.dynamicNum = dynamicNum;
    }

    public int getMessageNum() {
        return messageNum;
    }

    public void setMessageNum(int messageNum) {
        this.messageNum = messageNum;
    }

    public int getWorkNum() {
        return workNum;
    }

    public void setWorkNum(int workNum) {
        this.workNum = workNum;
    }

    public float getProcess() {
        return process;
    }

    public void setProcess(float process) {
        this.process = process;
    }

    public String getNike() {
        return nike;
    }

    public void setNike(String nike) {
        this.nike = nike;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
