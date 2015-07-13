package com.mckuai.bean;

import android.text.BoringLayout;
import android.util.Log;

import com.mckuai.Level;
import com.mckuai.entity.Player;

import java.util.IllegalFormatCodePointException;

/**
 * Created by kyly on 2015/7/13.
 */
public class WorldInfo {
    private String dir;                                 //子目录名称
    private Player player;                            //角色信息，优先来自数据库，如果没有再从level.dat中取
    private Level level;                                 //level.dat中的内容
    private boolean hasPlayerInfo = true;   //数据库中是否有用户的信息
    private long size;                                   //存档大小

    private final int DAY_LENGTH = 19200;

    private final String TAG = "WorldInfo";

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isCreative() {
        if (null != level){
            return level.getGameType() == 1;
        }
        return false;
    }

    public boolean setIsCreative(boolean isCreative) {
        if (null != level){
            level.setGameType(isCreative?1:0);
            return true;
        }
        else {
            return false;
        }
    }

    public boolean isDay(){
        long timeInDay = level.getTime() % DAY_LENGTH;
        return (timeInDay> (DAY_LENGTH / 2));
    }


    public String getTime(){
        Log.e(TAG, "time:" + level.getTime());
        long timeInDay = level.getTime() % DAY_LENGTH;
        if (isDay()){
            return "黑夜";
        }
        else {
            return "白天";
        }
    }

    public boolean setIsDay(boolean isDay) {
        if (isDay() != isDay){
            level.setTime(((level.getTime() / DAY_LENGTH) * DAY_LENGTH) + (2 * DAY_LENGTH / 3));
            return  true;
        }
        return false;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getLastModife() {
        if (null != level){
            return level.getLastPlayed();
        }
        return 0l;
    }


    public boolean isHasPlayerInfo() {
        return hasPlayerInfo;
    }

    public void setHasPlayerInfo(boolean hasPlayerInfo) {
        this.hasPlayerInfo = hasPlayerInfo;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }
}
