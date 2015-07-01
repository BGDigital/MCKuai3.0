package com.mckuai.until;

import android.util.Log;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.mckuai.bean.Map;
import com.mckuai.imc.MCkuai;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.lang.ref.SoftReference;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

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

    private ArrayList<String> curMaps;
    private ArrayList<String> curMapsDir;
//    private String MapName;
//    private String mapDir;
    private ArrayList<String> index;
    private ArrayList<Map> downloadMaps;
    private ArrayList<Map> newDownloadMaps;
    private Options options;
    private DB db;
    private File file;
    private String saveDir;
    private boolean isOpen = false;
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
                Log.w("addDownloadMap","地图已存在!");
                return;
            }
        }

        index.add(map.getResId());
        newDownloadMaps.add(map);
    }

    private void delDownloadMap(Map map){

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

        for (String resid:index){
            byte data[]= db.get(resid.getBytes());
            if (null == data){
                return  null;
            }
            String maps = new String(data);

           // Type listType = new TypeToken<ArrayList<String>>(){}.getType();
            Map map = gson.fromJson(maps,Map.class);
            if (null == downloadMaps){
                downloadMaps = new ArrayList<>(5);
            }
            downloadMaps.add(map);
            //downloadMaps = gson.fromJson(maps,listType);
        }

        return  downloadMaps;
    }

    /**
     * 从游戏存档目录获取所有的地图名称
     * @return
     */
    public ArrayList<String> getCurrentMapDirList(){

        if (null != curMapsDir){
            return  curMapsDir;
        }


        File[] files = new File(application.getGameProfileDir()+"minecraftWorlds/").listFiles();
        if (null == files || 0 == files.length){
            return  null;
        }

        curMaps = new ArrayList<>();
        curMapsDir = new ArrayList<>();
        for (File file:files){
            if (file.isDirectory()){
                curMapsDir.add(file.getPath());
                curMaps.add(getMapName(file.getPath()));
            }
        }
        return  curMapsDir;
    }

    /**
     * 获取当前正在使用的地图目录
     * 首先尝试从数据库中获取，如果获取不到则从游戏存档目录中获取第一个地图,均获取不到则返回空
     * @return
     */
    public String getCurrentMapDir(){
        if (!isOpen){
            if (!openDB()){
                Log.e("getCurrentMapdir","open db false!");
                return  null;
            }
        }

        byte name[] = db.get("CURRENT_MAP_DIR".getBytes());
        if (null != name){
            return  new String(name);
        }
        else {
            ArrayList<String> maps = getCurrentMapDirList();
            if (null != maps && !maps.isEmpty() )  {
                db.put("CURRENT_MAP_DIR".getBytes(),maps.get(0).getBytes());
                return  maps.get(0);
            }
            else {
                return  null;
            }
        }
    }

    /**
     * 获取当前的游戏地图的名称
     * 首先尝试从数据库中获取，获取不了再从当前正在使用的游戏存档中获取
     * @return
     */
    public String getCurrentMapName(){
        if (!isOpen){
            if (!openDB()){
                Log.e(TAG,"getCurrentMapName,open db false!");
                return null;
            }
        }

        byte name[] = db.get("CURRENT_MAP_NAME".getBytes());
        if (null != name){
            return  new String(name);
        }
        else {
            String mapdir = getCurrentMapDir();
            if (null != mapdir){
                String mapname = getMapName(mapdir);
                if (null != mapname){
                    db.put("CURRENT_MAP_NAME".getBytes(),mapname.getBytes());
                    return getMapName(mapdir);
                }
            }
            return  null;
        }
    }

    /**
     * 获取指定目录下的level.dat文件中读取地图名称
     * @param mapdir
     * @return
     */
    public String getMapName(String mapdir){
        MCGameEditer editer = new MCGameEditer(mapdir);
        return  editer.getMapName() ;
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
        File src = new File(MCkuai.getInstance().getGameProfileDir()+"minecraftWorlds/");
        File dst = new File(dstFileDir+mapName +".zip");

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
    }

    public boolean isReady(){
        return openDB();
    }


    private void initDB(){
        options = new Options();
        options.createIfMissing(true);
        Log.w("initDB", "file:" + saveDir);
        file = new File(saveDir);
        if (!file.exists()){
            Log.w("initDB","error:file not exist");
        }
        else{
            Log.w("initDB","get file");
        }
        getIndex();
    }

    private boolean openDB(){
        if (!isOpen){
            try{
                System.setProperty("sun.arch.data.model", "32");
                System.setProperty("leveldb.mmap", "false");
                db = factory.open(file,options);
                isOpen = true;
            }
            catch (Exception e){
                Log.e("openDB", e.getLocalizedMessage());
            }
        }
        return  isOpen;
    }

    public void closeDB(){
        if (null != newDownloadMaps && !newDownloadMaps.isEmpty()){
            if (!isOpen){
                openDB();
            }
        }

        try {
            saveIndex();
            saveNewDownload();
        }
        catch (Exception e){

        }

        if (isOpen){
            try{
                db.close();
                isOpen = false;
            }
            catch (Exception e){
                Log.w("closeDB", e.getLocalizedMessage());
            }
        }
    }

    private boolean getIndex(){
        if (!isOpen){
            if (!openDB()){
                Log.e(TAG, "getIndex: false,open db false!");
                return false;
            }
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
        if (!isOpen){
            if (!openDB()){
                Log.e(TAG, "saveIndex: false,open db false!");
                return;
            }
        }

        String data = "";
        for (String curmap:index){
            data = curmap + ",";
        }
        data = data.substring(0,data.length() - 1);

        db.put(mapIndex.getBytes(), data.getBytes());
    }

    private void saveNewDownload(){
        if (null == newDownloadMaps || newDownloadMaps.isEmpty()){
            return;
        }

        if (!isOpen){
            if (!openDB()){
                Log.e(TAG,"saveNewDownload: false,open db false!");
                return;
            }
        }

        for (Map map:newDownloadMaps){
            String data = gson.toJson(map);
            db.put(map.getResId().getBytes(),data.getBytes());
        }

        newDownloadMaps.clear();

    }

    /*public String getCurMapDir(){
        if (null == mapDir){
            if (!isOpen){
                if (!openDB()){
                    Log.e(TAG,"getCurMapDir:open db false");
                    return  null;
                }
                byte dir[] = db.get("CURRENT_MAP_DIR".getBytes());
                if (null != dir){
                    mapDir = new String(dir);
                }
                else {
                    return  null;
                }
            }
        }
        return mapDir;
    }

    public void setCurMapDir(String mapdir){
        if (null != mapdir && 1 < mapdir.length()){
            this.mapDir = mapdir;
            if (!isOpen){
                openDB();
            }

            if (isOpen){
                db.put("CURRENT_MAP_DIR".getBytes(),mapDir.getBytes());
            }
        }
    }*/


}
