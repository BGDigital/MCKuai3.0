package com.mckuai.bean;

import android.util.Log;

import com.mckuai.io.db.DB;
import com.mckuai.mctools.InventorySlot;
import com.mckuai.mctools.Level;
import com.mckuai.imc.MCkuai;
import com.mckuai.io.LevelDataConverter;
import com.mckuai.io.db.LevelDBConverter;
import com.mckuai.mctools.item.entity.Player;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kyly on 2015/7/13.
 */
public class WorldInfo implements Serializable{
    private String dir;                                 //子目录名称
    private Level level;                                 //level信息
    private long size;                                   //存档大小

    private final String TAG = "WorldInfo";
    private final String worldRoot = MCkuai.getInstance().getSDPath()+"/games/com.mojang/minecraftWorlds/";
    private boolean isSelected = false;

    /**
     * 获取存档目录名称
     * @return 存档目录名称
     */
    public String getDir() {
        return dir;
    }

    /**
     * 设置存档目录
     * @param dir
     */
    public void setDir(String dir) {
        this.dir = dir;
    }

    /**
     * 获取玩家角色信息
     * @return  获取玩家角色信息
     */
    public Player getPlayer() {
        return null != level ? level.getPlayer():null;
    }

    /**
     * 设置玩家角色信息
     * @param player  获取玩家角色信息
     */
    public void setPlayer(Player player) {
        if (null != level){
            level.setPlayer(player);
        }
    }

    /**
     * 获取游戏模式模式
     * @return 如果是创造模式则返回真，否则返回假
     */
    public boolean isCreative() {
        if (null != level) {
            return level.getGameType() == 1;
        }
        return false;
    }

    /**
     * 设置游戏模式
     * @param isCreative
     * @return
     */
    public boolean setGameMod(boolean isCreative) {
        if (null != level){
            level.setGameType(isCreative ? 1:0);
            if (null != level.getPlayer() && null != level.getPlayer().getAbilities()){
                level.getPlayer().getAbilities().setInvulnerable(isCreative);
                level.getPlayer().getAbilities().setMayFly(isCreative);
                switch (level.getStorageVersion()){
                    case 4:
                        saveLevelData();
                        saveDBData(); //player中相关部分存储于数据库中
                        break;
                    default:
                        saveLevelData();
                        break;
                }
            }
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

    public String getSizeEx(){
         int times = 0;
        long mapSize = new Long(size);
        while(mapSize > 1024){
            mapSize = mapSize / 1024;
            times++;
        }
        switch (times){
            case 0:
                return "1KB";
            case 1:
                return mapSize+"KB";
            case 2:
                return mapSize+"MB";
            case 3:
                return mapSize+"GB";
            default:
                return mapSize+"PB";
        }
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
        List<InventorySlot> list  = getInventory();
        if (null == list || list.isEmpty()){
            return  0;
        }
        List<Short> type = new ArrayList<>(30);
        boolean result;
        for (InventorySlot item:list){
            if (item.getContents().getTypeId() == 255){
                continue;
            }
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
        if (null != player){
            player.setInventory(inventorySlots);
            return saveDBData();
        }
        return false;
    }

    private boolean saveData(){
        String path = worldRoot + dir;
        File levelFile = null;
        try {
            levelFile =new File(path,"level.dat");
            if (null == levelFile || !levelFile.exists() || !levelFile.isFile()){
                return false;
            }
            LevelDataConverter.write(level,levelFile);
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,"打开level文件时失败，原因："+e.getLocalizedMessage());
        }
        //处理0.81以后版本
        if (3 < level.getStorageVersion()){

        }

    }



    private boolean saveLevelData() {

            String path = worldRoot +dir;
            File file = new File(path,"level.dat");
            if (null == file || !file.exists()) {
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

    private boolean saveDBData() {
        String path = worldRoot + dir;
        File[] dbFiles = new File(path).listFiles();

        boolean result = false;
        for (int i = 0; i < dbFiles.length; i++) {
            if (dbFiles[i].isDirectory()) {
                try {
                    result = LevelDBConverter.writeLevel(player, dbFiles[i]);
                } catch (Exception e) {
                    e.printStackTrace();
                    result = false;
                }
            }
        }
        return result;
    }
}
