package com.mckuai.until;

import android.util.Log;

import com.mckuai.Level;
import com.mckuai.bean.WorldInfo;
import com.mckuai.entity.Player;
import com.mckuai.imc.MCkuai;
import com.mckuai.io.LevelDataConverter;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by kyly on 2015/6/27.
 * 管理已有地图的总入口
 */
public class MCGameEditer {

    private static final  String TAG = "MCGameEditer";
    private static ArrayList<WorldInfo> worlds;   //所有的地图信息
    private static boolean isThirdViewEnable = false;
    private static OnWorldLoadListener mListener;

    private boolean isLevelDatChanged = false;
    private boolean isPlayerInfoChanged = false;
    private static boolean mIsThirdViewLoaded =false;

    public interface OnWorldLoadListener{
        public void OnComplete(ArrayList<WorldInfo> worldInfos,boolean isThirdView);
    }

    public void setOnWorldLoadListener(OnWorldLoadListener l){
        if (null != l){
            this.mListener = l;
        }
    }

    public MCGameEditer(OnWorldLoadListener loadListener){
        this.mListener = loadListener;
        isThirdViewEnable = OptionUntil.isThirdPerson();
        mIsThirdViewLoaded = true;
        getAllWorld(mListener);
    }


    public static boolean isThirdView(){
        if (!mIsThirdViewLoaded) {
            isThirdViewEnable = OptionUntil.isThirdPerson();
            mIsThirdViewLoaded = true;
        }
        return isThirdViewEnable;
    }

    public static boolean setThirdView(boolean isEnable){
        boolean curType = isThirdView();
        if (isEnable != curType){
            isThirdViewEnable = isEnable;
            OptionUntil.setThirdPerson(isEnable);
            return true;
        }
        return false;
    }

    /**
     * 获取游戏下所有的世界的信息
     * @return
     */
    public static void getAllWorld(OnWorldLoadListener listener){
         if (null == worlds){
             File[] subFile = new File(MCkuai.getInstance().getSDPath() + "/games/com.mojang/minecraftWorlds/").listFiles();
             if (null != subFile && subFile.length > 0){
                 if (null == worlds){
                     worlds = new ArrayList<>(subFile.length);
                 }

                 for (File curFile:subFile) {
                     if (curFile.exists() &&curFile.isDirectory()) {
                         loadData(curFile);      //读取单个世界的信息
                     }
                 }
             }
         }
        if (null != listener){
            listener.OnComplete(worlds, isThirdView());
        }
    }

    /**
     * 获取单个世界的信息
     * @param file  要获取信息的世界
     */
    private static void loadData(File file) {
        WorldInfo worldInfo = new WorldInfo();

        worldInfo.setDir(file.getName());       //文件夹名称
        worldInfo.setSize(getWorldSize(file)); //大小
        worldInfo.setHasPlayerInfo(loadPlayerFromDB(file,worldInfo));  //从数据库中获取角色信息
        //从level.dat文件中获取level信息（必有信息包括显示名，）
        loadLevelFromFile(file, worldInfo);

        worlds.add(worldInfo);
    }

    /**
     * 从数据库中获取角色信息
     * @param worldRoot 游戏世界的根目录
     * @return
     */
    private static boolean loadPlayerFromDB(File worldRoot,WorldInfo worldInfo){
        File[] subDir = worldRoot.listFiles();
        if (null != subDir){
            for (File curDir:subDir){
                //只有一个目录
                if (curDir.isDirectory()){
                    Player player = GameDBEditer.getPlayer(curDir);
                    if (null != player){
                        worldInfo.setPlayer(player);
                        return true;
                    }
                    else {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    private static boolean loadLevelFromFile(File worldRoot,WorldInfo worldInfo){
        Level level = null;
        File levelFile = new File(worldRoot,"level.dat");
        if (null != levelFile && levelFile.exists() && levelFile.isFile()){
            try {
                level = LevelDataConverter.read(levelFile);
                if (null != level){
                    worldInfo.setLastModife(level.getLastPlayed());
                    worldInfo.setIsCreative((level.getGameType() == 1));
                    //修改角色信息，由于之前已经尝试过从数据库中读取，则处理为以文件中的为优先
                    if (null == worldInfo.getPlayer()){
                        //之前没有角色信息，直接设置
                        worldInfo.setPlayer(level.getPlayer());
                    }
                    else {
                        //之前有角色信息，替换其能力和背包信息
                        if (null != level.getPlayer()){
                            //能力
                            if (null != level.getPlayer().getAbilities()){
                                worldInfo.getPlayer().setAbilities(level.getPlayer().getAbilities());
                            }
                            //背包
                            if (null != level.getPlayer().getInventory() && 0 < level.getPlayer().getInventory().size()){
                                worldInfo.getPlayer().setInventory(level.getPlayer().getInventory());
                            }
                        }
                    }
                    return true;
                }
            }
            catch (Exception e){
                Log.e(TAG,"读取level.dat文件失败，原因："+e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        return  false;
    }



    private static long getWorldSize(File file){
        Long size = 0l;
        File[] subFiles = file.listFiles();
        if (null != subFiles && subFiles.length >0){
            for (File curFile:subFiles){
                if (curFile.isFile()){
                    size += curFile.length();
                }
                else {
                    size += getWorldSize(curFile);
                }
            }
        }
        return size;
    }




   /* public void setMapDir(String mapDir){
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

    public void setThirdPerson(boolean enable){
        optionUntil.setThirdPerson(enable);
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
    }*/



}
