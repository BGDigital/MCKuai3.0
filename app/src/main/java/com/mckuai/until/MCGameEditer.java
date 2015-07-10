package com.mckuai.until;

import android.util.Log;

import com.mckuai.InventorySlot;
import com.mckuai.Level;
import com.mckuai.entity.Player;
import com.mckuai.io.LevelDataConverter;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kyly on 2015/6/27.
 */
public class MCGameEditer {

    private static final  String TAG = "MCGameEditer";
    private Level level;
    private Long lastModified;
    private static final int DAY_LENGTH = 19200;

//    private final  String fileName = "/storage/sdcard0/games/com.mojang/minecraftWorlds/My World/level.dat";
    private String fileName;
    private OptionUntil optionUntil;

    public MCGameEditer(String mapDir){
        fileName = mapDir+"/level.dat";
        loadData();
        optionUntil = new OptionUntil("/storage/sdcard0/games/com.mojang/minecraftpe/");
    }

    public MCGameEditer(){
    }

    public void setMapDir(String mapDir){
        fileName = mapDir + "/level.dat";
        loadData();
        optionUntil = new OptionUntil("/storage/sdcard0/games/com.mojang/minecraftpe/");
    }

    public boolean hasProfile(){
        return  null != level;
    }

    public int getGameMode(){
        if (null != level){
            return  level.getGameType();
        }
        return  -1;
    }

    public boolean setGameMode(int mode){
        if (null != level){
            level.setGameType(mode);
            saveData();
            return  true;
        }
        return  false;
    }

    public String getMapName(){
        if (null != level){
            return  level.getLevelName();
        }
        return  null;
    }

    public boolean setMapName(String name){
        return  false;
    }


    private void loadData(){
        File file = new File(fileName);
        if (!file.exists()){
            return;
        }
        try {
            this.level = LevelDataConverter.read(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean saveData(){
    File file = new File(fileName);
        if (!file.exists()){
            return false;
        }
        try {
            LevelDataConverter.write(level,file);
            file = null;
            return  true;
        }
        catch (Exception e){
            Log.e(TAG,"save false,"+e.getLocalizedMessage());
            file = null;
            return  false;
        }
    }

    public void setTimeToMorning(){
        long dayCount = (level.getTime() / DAY_LENGTH);
        if (level.getTime() % DAY_LENGTH > 0){
            dayCount++;
        }
        level.setTime(dayCount * DAY_LENGTH);
        saveData();
    }

    public void setTimeToNight(){
        level.setTime(((level.getTime() / DAY_LENGTH) * DAY_LENGTH) + (2 * DAY_LENGTH / 3));
        saveData();
    }

    public String getTime(){
        Log.e(TAG, "time:" + level.getTime());
        long timeInDay = level.getTime() % DAY_LENGTH;
        if (timeInDay > (DAY_LENGTH / 2)){
            return "黑夜";
        }
        else {
            return "白天";
        }
    }

    public Long getLastModified(){
        if (null != level) {
            return level.getLastPlayed();
        }
        else {
            return null;
        }
    }

    public List<InventorySlot> getInventory(){
        if (null != level && null != level.getPlayer()){
            return  level.getPlayer().getInventory();
        }
        return  null;
    }

    public void setInventory(List<InventorySlot> inventorySlotList){

    }

    public void switchThirdView(){
        level.setSpawnMobs(!level.getSpawnMobs());
        saveData();
    }

    public Level getLevel() {
        return level;
    }

    public boolean isThirdPerson(){
        if (null != optionUntil && optionUntil.isValid()){
            return optionUntil.isThirdPerson();
        }
        else {
            return false;
        }
    }
}
