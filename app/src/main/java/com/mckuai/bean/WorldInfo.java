package com.mckuai.bean;

import android.util.Log;

import com.mckuai.mctools.InventorySlot;
import com.mckuai.mctools.Level;
import com.mckuai.entity.Player;
import com.mckuai.imc.MCkuai;
import com.mckuai.mctools.io.LevelDataConverter;
import com.mckuai.mctools.io.db.LevelDBConverter;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kyly on 2015/7/13.
 */
public class WorldInfo implements Serializable{
    private String dir;                                 //子目录名称
    private Player player;                            //角色信息，优先来自数据库，如果没有再从level.dat中取
    private Level level;                                 //
    private long size;                                   //存档大小
    private int mapVersion;                         //地图版本，如果仅有level.dat则认为是
//    private boolean isSaveInDB;                   //是否是从数据库中获取的

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
            level.setGameType(isCreative ? 1:0);                     //level.dat

            if (null != level.getPlayer() && null != level.getPlayer().getAbilities()){
                //从文件中取出来的,仅写level.dat
                level.getPlayer().getAbilities().setInvulnerable(isCreative);
                level.getPlayer().getAbilities().setMayFly(isCreative);
                return saveLevelData();
            }
            else {
                //从数据库中读取的，需写level.dat和数据库
                if (null != player && null != player.getAbilities()){
                    player.getAbilities().setMayFly(isCreative);
                    player.getAbilities().setInvulnerable(isCreative);
//                    level.setPlayer(player);
//                    if (!saveDBData()){
//                        return  false;
//                    }
                    boolean result = saveDBData();
                    result = result && saveLevelData();
                    return result;
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

    private boolean saveDBData() {
        String path = MCkuai.getInstance().getSDPath() + worldRoot + dir;
        File[] dbFiles = new File(path).listFiles();
        //boolean isSaveInLevelDB = OptionUntil.isSaveInLevelDB();

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
