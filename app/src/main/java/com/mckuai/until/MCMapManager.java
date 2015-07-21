package com.mckuai.until;

import android.util.Log;

import com.google.gson.Gson;
import com.mckuai.WorldItem;
import com.mckuai.bean.Map;
import com.mckuai.io.db.DB;
import com.mckuai.imc.MCkuai;

import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by kyly on 2015/6/28.
 * 管理已下载的地图
 * 可获取已下载的地图,游戏中的地图
 * 可加入新下载的地图
 * 可导入地图(将地图导入到游戏目录)
 * 可导出地图(将游戏目录下的某个地图导出到指定的位置)
 */
public class MCMapManager {

    private static  final  String TAG = "MCMapManager";

    private ArrayList<String> index;                             //这是所有的下载的地图的resid
    private ArrayList<Map> downloadMaps;                //已经下载了的
    private ArrayList<WorldItem> worldItemList;//游戏中的地图
    private DB db;
    private boolean isDBOpened = false;
    private String saveDir; //下载路径
    private String worldRoot;
    private Gson gson ;
    private final  String mapIndex = "MAP_INDEX";
    private final  String mapDownloaded = "DOWNLOAD_MAP";


    public MCMapManager(){
        gson = new Gson();
        saveDir = MCkuai.getInstance().getMapDownloadDir();
        worldRoot = MCkuai.getInstance().getGameProfileDir()+"minecraftWorlds/";
        initDB();
       /* File  file = new File(MCkuai.getInstance().getSDPath(),"games/com.mojang/minecraftWorlds");
        worldItemList = new ArrayList<>();
        if (file.exists()){
            File[] fileList = file.listFiles();
            for (File curFile:fileList){
                worldItemList.add(new WorldItem(curFile));
            }
        }*/
    }

    public void addDownloadMap(Map map){
        if (null != index && !index.isEmpty()) {
            for (String resId : index) {
                if (resId.equalsIgnoreCase(map.getResId())) {
                    Log.e("addDownloadMap", "地图已存在!");
                    return;
                }
            }
        }else {
            index = new ArrayList<>();
        }
        index.add(map.getResId());

        if (null == downloadMaps){
            downloadMaps = new ArrayList<>();
        }
        downloadMaps.add(map);
        saveDB();
    }

    public boolean delDownloadMap(String mapId){
        if (null == mapId || null == index || index.isEmpty()){
            return false;
        }
        if (openDB()) {
            for (String id : index) {
                if (id.equalsIgnoreCase(mapId)) {
                    for (Map map : downloadMaps) {
                        if (map.getResId().equalsIgnoreCase(id)) {
                            if (deleteMapfromDb(mapId)) {//删除数据库
                                downloadMaps.remove(map);//删除已下载地图中的记录
                                index.remove(id);//删除索引
                                deleteMapfromDisk(map);//删除文件
                                saveDB();
                                closeDB();
                                return true;
                            }
                        }
                    }
                }
            }
        }
        closeDB();
        return false;
    }



    public ArrayList<Map> getDownloadMaps(){
        if (null != downloadMaps){
            return downloadMaps;
        }

        if (null == index){
            if (!getIndex()){
                Log.e(TAG, "no map be downloaded!");
                return  null;
            }
        }

        if (openDB()) {
            for (String resid : index) {
                byte data[] = db.get(resid.getBytes());
                if (null == data) {
                    closeDB();
                    return null;
                }
                String maps = new String(data);

                Map map = gson.fromJson(maps, Map.class);
                if (null == downloadMaps) {
                    downloadMaps = new ArrayList<>(index.size());
                }
                downloadMaps.add(map);
            }
            closeDB();
            return downloadMaps;
        }
        return null;
    }

    public boolean saveDB(){
        if (null == downloadMaps || downloadMaps.isEmpty() || null == index || index.isEmpty()){
            return false;
        }

        if (openDB()) {
            String data = "";
            for (String curmap : index) {
                data += curmap + ",";
            }
            if (data.length() > 1) {
                data = data.substring(0, data.length() - 1);
            }
            db.put(mapIndex.getBytes(), data.getBytes());

            for (Map map : downloadMaps) {
                data = gson.toJson(map);
                db.put(map.getResId().getBytes(), data.getBytes());
            }
            closeDB();
            return true;
        }
        return false;
    }

    public boolean closeDB(){
        if (isDBOpened && null != db) {
            try {
                db.close();
                isDBOpened = false;
            } catch (Exception e) {
                Log.w("closeDB", e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        return !isDBOpened;
    }

    /**
     * 从游戏存档目录获取所有的地图名称
     * @return
     */
    public ArrayList<String> getCurrentMapDirList(){
       //已经有地图
        if (null != worldItemList){
            ArrayList<String> dirs = new ArrayList<>(worldItemList.size());
            for (WorldItem item:worldItemList){
                dirs.add(item.getFolder().toString());
            }
            return  dirs;
        }
        //还未取出地图
        worldItemList = new ArrayList<>();
        File[] files = new File(worldRoot).listFiles();
        if (null == files || 0 == files.length){
            return  null;
        }

        ArrayList<String> dirs = new ArrayList<>();
        for (File file:files){
            WorldItem item = new WorldItem(file);
            worldItemList.add(item);
        }
        files = null;
        return  dirs;
    }

/*    private long getLastPlayTime(String path){
        path = path + "/level.dat";
        File file = new File(path);
        long time = file.lastModified();
        file = null;
        return time;
    }*/

 /*   private void insertNewGameMap(WorldItem world){
        for (int i = 0; i < worldItemList.size();i++){
            if (worldItemList.get(i).lastPlayTime > world.lastPlayTime){
                worldItemList.add(i,world);
                return;
            }
        }
        worldItemList.add(world);
    }*/

    /**
     * 获取当前正在使用的地图目录
     * 从游戏存档目录中获取最近被修改过的地图,均获取不到则返回空
     * @return
     */
 /*   public String getCurrentMapDir(){

        if (null == worldItemList){
            getCurrentMapDirList();
        }
        if (null != worldItemList && !worldItemList.isEmpty()){
            return  worldItemList.get(0).folder.toString();
        }
        else {
            return  null;
        }
    }*/

    /**
     * 获取当前的游戏地图的名称
     * 从当前正在使用的游戏存档中获取
     * @return
     */
    /*public String getCurrentMapName(){
        if (null == worldItemList){
            getCurrentMapDirList();
        }

        if (null != worldItemList && !worldItemList.isEmpty()){
            return  worldItemList.get(0).getName();
        }
        else {
            return null;
        }

    }*/

    /**
     * 获取指定目录下的level.dat文件中读取地图名称
     * @param mapdir
     * @return
     */
    public String getMapName(String mapdir){
        return  MCGameEditer.getWorldName(mapdir);
    }



    public void importMap(String mapFileName){
        File src = new File(mapFileName);
        File dst = new File(worldRoot);
        if (!dst.exists()){
            Log.e(TAG,"目标目录不存在，可能是游戏未安装");
            return;
        }

        if (src.exists()){
            ZipUtil.unpack(src, dst);
        }
    }

    /**
     * 导出地图
     *
     * @param mapName 要导出的地图名称
     * @param dstFileDir 导出到的目录
     */
    public void exportMap(String mapName,String dstFileDir){
        File src = new File(worldRoot,mapName);
        File dst = new File(dstFileDir,mapName +".zip");
        Log.e(TAG, "源路径：" + src.getPath());
        Log.e(TAG, "目标路径：" + dst.getPath());

        if (src.exists() && src.isDirectory()){
            if (dst.exists()){
                dst.delete();
            }
            ZipUtil.pack(src,dst);
        }
        else {
            Log.e(TAG, "导出地图失败，指定的地图不存在！");
        }
    }

    private void initDB(){
        Log.w("initDB", "file:" + saveDir);
        File file = new File(saveDir);
        if (!file.exists()){
            file.mkdirs();
        }
        db = new DB(file);
        db.open();
        isDBOpened =true;

        getIndex();
        getDownloadMaps();
    }

    private boolean deleteMapfromDb(String mapid){
        if (openDB()) {
            byte data[] = db.get(mapid.getBytes());
            if (null == data) {
                Log.e("deleteMapfromDb", "map not exist");
                return false;
            } else {
                db.delete(mapid.getBytes());
                data = db.get(mapid.getBytes());
                return null == data;
            }
        }
        return false;
    }

    private boolean deleteMapfromDisk(Map map){
        File file = new File(MCkuai.getInstance().getMapDownloadDir(),map.getFileName());
        if (null != file && file.exists() && file.isFile()){
            return  file.delete();
        }
        return  false;
    }

    private boolean openDB(){
        if (!isDBOpened && null != db){
            try{
                db.open();
                isDBOpened = true;
            }catch (Exception e){
                e.printStackTrace();
                isDBOpened = false;
            }
        }
        return isDBOpened;
    }

    private boolean getIndex(){
        if (openDB()) {
            index = new ArrayList<>();
            byte data[] = db.get(mapIndex.getBytes());
            if (null != data) {
                String mapindex = new String(data);
                String arr[] = mapindex.split(",");
                for (int i = 0; i < arr.length; i++) {
                    index.add(arr[i]);
                }
                return true;
            } else {
                Log.e(TAG, "getDownloadMapsIndex: false,get null");
                return false;
            }
        }
        return false;
    }
}
