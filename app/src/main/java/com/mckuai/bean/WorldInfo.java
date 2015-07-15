package com.mckuai.bean;

import android.content.Intent;
import android.text.BoringLayout;
import android.util.Log;

import com.mckuai.InventorySlot;
import com.mckuai.Level;
import com.mckuai.entity.Player;
import com.mckuai.imc.MCkuai;
import com.mckuai.io.LevelDataConverter;
import com.mckuai.until.GameDBEditer;
import com.mckuai.until.OptionUntil;

import org.spout.nbt.ListTag;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;

/**
 * Created by kyly on 2015/7/13.
 */
public class WorldInfo implements Serializable{
    private String dir;                                 //子目录名称
    private Player player;                            //角色信息，优先来自数据库，如果没有再从level.dat中取
    private Level level;                                 //level.dat中的内容
    private long size;                                   //存档大小

    private final String TAG = "WorldInfo";
    private final String worldRoot = "/games/com.mojang/minecraftWorlds/";
    private boolean isSelected = false;

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
        if (null != level) {
            return level.getGameType() == 1;
        }
        return false;
    }

    public boolean setIsCreative(boolean isCreative) {
        if (null != level){
            level.setGameType(isCreative ? 1:0);
            if (null != level.getPlayer() && null != level.getPlayer().getAbilities()){
                level.getPlayer().getAbilities().setInvulnerable(true);
                level.getPlayer().getAbilities().setMayFly(true);
            }
            else {
                if (null != player && null != player.getAbilities()){
                    player.getAbilities().setMayFly(false);
                    player.getAbilities().setInvulnerable(false);
                    if (!saveDBData()){
                        return  false;
                    }
                }
            }
            return saveLevelData();
        }
        return false;
    }

    public boolean isDay() {
        long timeInDay = level.getTime() % 19200;
        return (timeInDay > (9600));
    }


    public String getTime() {
        if (isDay()) {
            return "黑夜";
        } else {
            return "白天";
        }
    }

    public boolean setIsDay(boolean isDay) {
        long time = level.getTime();
        if (isDay) {
            time = time - (time % 19200) + 19200;
        }
        else {
            time = time - (time % 19200) + 9600;
        }

        if (null != level){
            level.setTime(time);
            return saveLevelData();
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
        if (null != level) {
            return level.getLastPlayed();
        }
        return 0l;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public List<InventorySlot> getInventory(){
        if (null != level && null != level.getPlayer() && null != level.getPlayer().getInventory()){
            return level.getPlayer().getInventory();
        }
        if (null != player && null != player.getInventory()){
            return player.getInventory();
        }
        return null;
    }

    public List<InventorySlot> getRealInventory(List<InventorySlot> list){
        if (null != list && !list.isEmpty()){
            List<InventorySlot> inventorys = new ArrayList<>();
            for (InventorySlot item:list){
                if (item.getContents().getTypeId() != 255 && item.getContents().getAmount() != 255){
                    inventorys.add(item);
                }
            }
            return inventorys;
        }
        return null;
    }

    public int getInventoryTypeCount(){
        List<InventorySlot> list  = getRealInventory(getInventory());
        if (null == list || list.isEmpty()){
            return  0;
        }
        List<Short> type = new ArrayList<>(30);
        boolean result;
        for (InventorySlot item:list){
            result = true;
            for (Short id:type){
                if (id == item.getContents().getTypeId()){
                    result = false;
                    break;
                }
            }
            if (result){
                type.add(item.getContents().getTypeId());
            }
        }
        return type.size();
    }

    public boolean setInventory(List<InventorySlot> inventorySlots){
        if (null != level && null != level.getPlayer() && null != getPlayer().getInventory()){
            level.getPlayer().setInventory(inventorySlots);
            return saveLevelData();
        }
        if (null != player ){
            player.setInventory(inventorySlots);
            return saveDBData();
        }
        return false;
    }



    private boolean saveLevelData() {
        String path = MCkuai.getInstance().getSDPath() +worldRoot +dir;
        File file = new File(path,"level.dat");
        if (null != file && !file.exists()) {
            return false;
        }
        try {
            LevelDataConverter.write(level, file);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "save false," + e.getLocalizedMessage());
            return false;
        }
    }

    private boolean saveDBData(){
        String path = MCkuai.getInstance().getSDPath() + worldRoot +dir;
        File[] dbFiles = new File(path).listFiles();
        if (null != dbFiles && dbFiles.length > 0){
            for (File dbFile:dbFiles){
                if (dbFile.isDirectory()){
                    return  GameDBEditer.setPlayer(player,dbFile);
                }
            }
        }
        return false;
    }
}
