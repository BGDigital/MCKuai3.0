package com.mckuai.until;

import android.util.Log;

import com.google.gson.Gson;
import com.mckuai.WorldItem;
import com.mckuai.bean.Map;
import com.mckuai.io.db.DB;
import com.mckuai.imc.MCkuai;
import com.mckuai.io.db.Iterator;

import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.lang.reflect.Array;
import java.security.PrivateKey;
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

    private MCkuai application;

//    private ArrayList<String> curMaps;                        //当前游戏中已经有的地图名称
//    private ArrayList<String> curMapsDir;                   //当前游戏中地图的路径，与上面的一一对应
    private ArrayList<String> index;                             //这是所有的下载的地图的resid
    private ArrayList<Map> downloadMaps;                //已经下载了的
    private ArrayList<Map> newDownloadMaps;         //新下载的东西存在这里
    private ArrayList<WorldItem> worldItemList;//游戏中的地图
    private DB db;
    private File file;
    private String saveDir; //下载路径
    private Gson gson ;
    private final  String mapIndex = "MAP_INDEX";
    private final  String mapDownloaded = "DOWNLOAD_MAP";

    public void addDownloadMap(Map map){
        if (null == newDownloadMaps){
            newDownloadMaps = new ArrayList<Map>();
        }

        if (null == index){
            getIndex();
        }

        for (String resId:index){
            if (resId.equalsIgnoreCase(map.getResId())){
                Log.w("addDownloadMap", "地图已存在!");
                return;
            }
        }

        index.add(map.getResId());
        newDownloadMaps.add(map);
        if (null == downloadMaps){
            downloadMaps = new ArrayList<>();
        }
        downloadMaps.add(map);
    }

    public boolean delDownloadMap(String mapId){
        if (null == mapId || null == index || index.isEmpty()){
            return false;
        }
        for (String id:index){
            if (id.equalsIgnoreCase(mapId)){
                index.remove(id);  //删除索引
                for (Map map:downloadMaps){
                    if (map.getResId().equalsIgnoreCase(id)){
                        downloadMaps.remove(map);//删除已下载地图中的记录
                        break;
                    }
                }
                return deleteMapfromDb(mapId);  //删除数据库
            }
        }
        return  false;
    }

    private boolean deleteMapfromDb(String mapid){
        if (!isReady()){
            return  false;
        }
        try{
            db.open();
        }
        catch (Exception e){
            e.printStackTrace();
        }
            byte data[] = db.get(mapid.getBytes());
            if (null == data){
                Log.e("deleteMapfromDb","map not exist");
                return  false;
            }
            else {
                db.delete(mapid.getBytes());
                data = db.get(mapid.getBytes());
                return  null == data;
            }
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

        try {
            db.open();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        for (String resid:index){
            byte data[]= db.get(resid.getBytes());
            if (null == data){
                return  null;
            }
            String maps = new String(data);

            Map map = gson.fromJson(maps,Map.class);
            if (null == downloadMaps){
                downloadMaps = new ArrayList<>(5);
            }
            downloadMaps.add(map);
        }
        return  downloadMaps;
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
        File[] files = new File(application.getGameProfileDir()+"minecraftWorlds/").listFiles();
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

    private long getLastPlayTime(String path){
        path = path + "/level.dat";
        File file = new File(path);
        long time = file.lastModified();
        file = null;
        return time;
    }

    private void insertNewGameMap(WorldItem world){
        for (int i = 0; i < worldItemList.size();i++){
            if (worldItemList.get(i).lastPlayTime > world.lastPlayTime){
                worldItemList.add(i,world);
                return;
            }
        }
        worldItemList.add(world);
    }

    /**
     * 获取当前正在使用的地图目录
     * 从游戏存档目录中获取最近被修改过的地图,均获取不到则返回空
     * @return
     */
    public String getCurrentMapDir(){

        if (null == worldItemList){
            getCurrentMapDirList();
        }
        if (null != worldItemList && !worldItemList.isEmpty()){
            return  worldItemList.get(0).folder.toString();
        }
        else {
            return  null;
        }
    }

    /**
     * 获取当前的游戏地图的名称
     * 从当前正在使用的游戏存档中获取
     * @return
     */
    public String getCurrentMapName(){
        if (null == worldItemList){
            getCurrentMapDirList();
        }

        if (null != worldItemList && !worldItemList.isEmpty()){
            return  worldItemList.get(0).getName();
        }
        else {
            return null;
        }

    }

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
        File dst = new File(application.getGameProfileDir()+"minecraftWorlds/");
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
        File src = new File(MCkuai.getInstance().getGameProfileDir()+"minecraftWorlds/"+mapName);
        File dst = new File(dstFileDir,mapName +".zip");
        Log.e(TAG,"源路径："+src.getPath());
        Log.e(TAG,"目标路径："+dst.getPath());

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

    public MCMapManager(){
        gson = new Gson();
        application = MCkuai.getInstance();
        saveDir = application.getMapDownloadDir();
        initDB();
        File  file = new File(MCkuai.getInstance().getSDPath(),"games/com.mojang/minecraftWorlds");
        worldItemList = new ArrayList<>();
        if (file.exists()){
            File[] fileList = file.listFiles();
            for (File curFile:fileList){
                worldItemList.add(new WorldItem(curFile));
            }
        }
    }

    public boolean isReady(){
        return null != db;
    }


    private void initDB(){
        Log.w("initDB", "file:" + saveDir);
        file = new File(saveDir);
        if (!file.exists()){
            file.mkdirs();
        }
        db = new DB(file);
        db.open();
        if (!file.exists()){
            Log.w("initDB","error:file not exist");
        }
        else{
            Log.w("initDB","get file");
        }
        getIndex();
        downloadMaps = getDownloadMaps();
    }

    public void saveDB(){
        if (null != newDownloadMaps && !newDownloadMaps.isEmpty()){
            if (!isReady()){
                return;
            }
        }

        try {
            saveIndex();
            saveNewDownload();
        }
        catch (Exception e){

        }
    }


    public void closeDB(){
        saveDB();
        try{
            db.close();
        }
        catch (Exception e){
            Log.w("closeDB", e.getLocalizedMessage());
        }
    }

    private boolean getIndex(){
        if (!isReady()){
            Log.e(TAG, "getIndex: false,open db false!");
            return false;
        }

        if (null == index){
            index = new ArrayList<>();
        }

        byte data[] = db.get(mapIndex.getBytes());
        if (null != data){
            String mapindex = new String(data);
            String arr[] = mapindex.split(",");
            for (int i = 0;i <arr.length;i++){
                index.add(arr[i]);
            }
            return  true;
        }
        else {
            Log.e(TAG,"getDownloadMapsIndex: false,get null");
            return  false;
        }
    }

    private void saveIndex(){
        if (!isReady()){
            return;
        }

        String data = "";
        for (String curmap:index){
            data += curmap + ",";
        }
        if (data.length() > 1) {
            data = data.substring(0, data.length() - 1);
        }

        db.put(mapIndex.getBytes(), data.getBytes());
    }

    private void saveNewDownload(){
        if (null == newDownloadMaps || newDownloadMaps.isEmpty()){
            return;
        }

        if (!isReady()){
            Log.e(TAG,"saveNewDownload: false,open db false!");
            return;
        }

        for (Map map:newDownloadMaps){
            String data = gson.toJson(map);
            db.put(map.getResId().getBytes(),data.getBytes());
        }

        newDownloadMaps.clear();

    }




}
