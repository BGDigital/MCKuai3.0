package com.mckuai.mctools.WorldUtil;

import android.util.Log;

import com.google.gson.Gson;
import com.mckuai.mctools.WorldItem;
import com.mckuai.bean.Map;
import com.mckuai.mctools.io.db.DB;
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

    private static final String TAG = "MCMapManager";

    private ArrayList<String> index;                             //这是所有的下载的地图的resid
    private ArrayList<Map> downloadMaps;                //已经下载了的
    private ArrayList<WorldItem> worldItemList;//游戏中的地图
    private DB db;
    private boolean isDBOpened = false;
    private String saveDir; //下载路径
    private String worldRoot;
    private Gson gson;
    private final String mapIndex = "MAP_INDEX";
    private final String mapDownloaded = "DOWNLOAD_MAP";


    public MCMapManager() {
        gson = new Gson();
        saveDir = MCkuai.getInstance().getMapDownloadDir();
        worldRoot = MCkuai.getInstance().getGameProfileDir() + "minecraftWorlds/";
        initDB();
    }

    public void addDownloadMap(Map map) {
        if (null != index && !index.isEmpty()) {
            for (String resId : index) {
                if (resId.equalsIgnoreCase(map.getResId())) {
                    Log.e("addDownloadMap", "地图已存在!");
                    return;
                }
            }
        } else {
            index = new ArrayList<>();
        }
        index.add(map.getResId());

        if (null == downloadMaps) {
            downloadMaps = new ArrayList<>();
        }
        downloadMaps.add(map);
        saveDB();
    }

    public boolean delDownloadMap(String mapId) {
        if (null == mapId || null == index || index.isEmpty()) {
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


    public ArrayList<Map> getDownloadMaps() {
        if (null != downloadMaps) {
            return downloadMaps;
        }

        if (null == index) {
            if (!getIndex()) {
                Log.e(TAG, "no map be downloaded!");
                return null;
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

    public boolean saveDB() {
        if (null == downloadMaps || downloadMaps.isEmpty() || null == index || index.isEmpty()) {
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

    public boolean closeDB() {
        if (isDBOpened && null != db) {
            try {
                db.close();
            } catch (Exception e) {
                Log.w("closeDB", e.getLocalizedMessage());
                e.printStackTrace();
            }
            isDBOpened = false;
        }
        return !isDBOpened;
    }

    /**
     * 从游戏存档目录获取所有的地图名称
     *
     * @return
     */
    public ArrayList<String> getCurrentMapDirList() {
        //已经有地图
        if (null != worldItemList) {
            ArrayList<String> dirs = new ArrayList<>(worldItemList.size());
            for (WorldItem item : worldItemList) {
                dirs.add(item.getFolder().toString());
            }
            return dirs;
        }
        //还未取出地图
        worldItemList = new ArrayList<>();
        File[] files = new File(worldRoot).listFiles();
        if (null == files || 0 == files.length) {
            return null;
        }

        ArrayList<String> dirs = new ArrayList<>();
        for (File file : files) {
            WorldItem item = new WorldItem(file);
            worldItemList.add(item);
        }
        files = null;
        return dirs;
    }

    /**
     * 获取指定目录下的level.dat文件中读取地图名称
     *
     * @param mapdir
     * @return
     */
    public String getMapName(String mapdir) {
        return MCWorldUtil.getWorldName(mapdir);
    }


    public boolean importMap(String mapFileName) {
        File src = new File(mapFileName);
        File dst = new File(worldRoot);
        if (!dst.exists() || null == src || !src.exists() || !src.isFile()) {
            Log.e(TAG, "目标目录不存在或要解压的文件不存在，可能是游戏未安装");
            return false;
        }
        try {
            ZipUtil.unpack(src, dst);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "解压地图时失败，原因：" + e.getLocalizedMessage());
            return false;
        }
    }

    /**
     * 导出地图
     *
     * @param mapDir    要导出的地图的目录
     * @param dstFileDir 导出到的目录
     */
    public boolean exportMap(String mapDir, String dstFileDir) {
        File src = new File(worldRoot, mapDir);
        File dst = new File(dstFileDir, mapDir + ".zip");

        if (src.exists() && src.isDirectory()) {
            if (dst.exists()) {
                dst.delete();
            }
            try {
                ZipUtil.pack(src, dst);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "解压文件时出错，原因：" + e.getLocalizedMessage());
                return false;
            }
        } else {
            Log.e(TAG, "导出地图失败，指定的地图不存在！");
            return false;
        }
    }

    private void initDB() {
        Log.w("initDB", "file:" + saveDir);
        File file = new File(saveDir);
        if (null == file || !file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        db = new DB(file);
        db.open();
        isDBOpened = true;

        getIndex();
        getDownloadMaps();
    }

    private boolean deleteMapfromDb(String mapid) {
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

    private boolean deleteMapfromDisk(Map map) {
        File file = new File(MCkuai.getInstance().getMapDownloadDir(), map.getFileName());
        if (null != file && file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }

    private boolean openDB() {
        if (!isDBOpened && null != db) {
            try {
                db.open();
            } catch (Exception e) {
                e.printStackTrace();
            }
            isDBOpened = true;
        }
        return isDBOpened;
    }

    private boolean getIndex() {
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
