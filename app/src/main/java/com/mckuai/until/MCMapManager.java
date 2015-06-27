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

        byte data[]= db.get(mapDownloaded.getBytes());
        String maps = new String(data);

        Type listType = new TypeToken<ArrayList<String>>(){}.getType();
        downloadMaps = gson.fromJson(maps,listType);
        return  downloadMaps;
    }

    public ArrayList<String> getCurrentMaps(){
        if (null != curMaps){
            return  curMaps;
        }


        File[] files = new File(application.getGameProfileDir()).listFiles();
        if (null == files || 0 == files.length){
            return  null;
        }

        curMaps = new ArrayList<String>();
        for (File file:files){
            if (file.isDirectory()){
                curMaps.add(file.getName());
            }
        }
        return  curMaps;
    }

    public void importMap(String mapFile){
        File src = new File(mapFile);
        File dst = new File(application.getGameProfileDir());
        if (src.exists()){
            ZipUtil.unpack(src,dst);
        }
    }

    public void exportMap(String mapName,String dstFile){
        File src = new File(mapName);
        File dst = new File(dstFile);

        if (src.exists()){
            if (dst.exists()){
                dst.delete();
            }
            ZipUtil.pack(src,dst);
        }
    }

    public MCMapManager(){
        gson = new Gson();
        application = MCkuai.getInstance();
        saveDir = application.getMapDownloadDir();
        initDB();
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
                Log.e(TAG,"saveIndex: false,open db false!");
                return;
            }
        }

        String data = "";
        for (String curmap:index){
            data = curmap + "";
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


}
