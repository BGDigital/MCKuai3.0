package com.mckuai.mctools.WorldUtil;

import android.util.Log;

import com.mckuai.mctools.InventorySlot;
import com.mckuai.mctools.Level;
import com.mckuai.mctools.item.WorldItem;
import com.mckuai.mctools.item.entity.Player;
import com.mckuai.imc.MCkuai;
import com.mckuai.io.LevelDataConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kyly on 2015/6/27.
 * 管理已有地图的总入口
 */
public class MCWorldUtil {

    private static final String TAG = "MCWorldUtil";
    private static final String worldRoot = "/games/com.mojang/minecraftWorlds/";
    private static ArrayList<WorldItem> worlds;   //所有的地图信息
    private static boolean isThirdViewEnable = false;
    private static OnWorldLoadListener mListener;
    private static boolean mIsThirdViewLoaded = false;

    public interface OnWorldLoadListener {
        public void OnComplete(ArrayList<WorldItem> worldItems, boolean isThirdView);
    }

    public void setOnWorldLoadListener(OnWorldLoadListener l) {
        if (null != l) {
            this.mListener = l;
        }
    }

    public MCWorldUtil(OnWorldLoadListener loadListener, boolean getDetailed) {
        this.mListener = loadListener;
        isThirdViewEnable = OptionUntil.isThirdPerson();
        mIsThirdViewLoaded = true;
        getAllWorld(mListener, getDetailed);

    }


    public static String getWorldName(String path) {
        File file = new File(path, "level.dat");
        if (null != file && file.exists() && file.isFile()) {
            try {
                Level level = LevelDataConverter.read(file);
                if (null != level) {
                    return level.getLevelName();
                }
                return null;
            } catch (Exception e) {
                Log.e(TAG, "读取level.dat文件时失败，原因：" + e.getLocalizedMessage());
            }
        }
        return null;
    }


    public static boolean isThirdView() {
        if (!mIsThirdViewLoaded) {
            isThirdViewEnable = OptionUntil.isThirdPerson();
            mIsThirdViewLoaded = true;
        }
        return isThirdViewEnable;
    }

    /**
     * 获取游戏下所有的世界的信息
     *
     * @return
     */
    public static void getAllWorld(OnWorldLoadListener listener, boolean needPlayer) {
        if (null == worlds) {
            File[] subFile = new File(MCkuai.getInstance().getSDPath() + worldRoot).listFiles();
            if (null != subFile && subFile.length > 0) {
                worlds = new ArrayList<>(subFile.length);
                for (File curFile : subFile) {
                    if (curFile.exists() && curFile.isDirectory()) {
                        loadData(curFile, needPlayer);      //读取单个世界的完整信息
                    }
                }
            }
        }
        if (null != listener) {
            listener.OnComplete(worlds, isThirdView());
        }
    }

    public static ArrayList<WorldItem> getAllWorldLite() {
        File[] subFile = new File(MCkuai.getInstance().getSDPath() + worldRoot).listFiles();
        if (null != subFile && subFile.length > 0) {
            if (null == worlds) {
                worlds = new ArrayList<>(subFile.length);
            } else {
                worlds.clear();
            }
            for (File curFile : subFile) {
                WorldItem worldItem = new WorldItem();
                loadData(curFile, false);
            }
        }
        return worlds;
    }

    public ArrayList<WorldItem> getAllWorlds() {
        File[] subFile = new File(MCkuai.getInstance().getSDPath() + worldRoot).listFiles();
        if (null != subFile && subFile.length > 0) {
            if (null == worlds) {
                worlds = new ArrayList<>(subFile.length);
            } else {
                worlds.clear();
            }
            for (File curFile : subFile) {
                WorldItem worldItem = new WorldItem();
                loadData(curFile, false);
            }
        }
        return worlds;
    }

    public static Player getPlayer(String world) {
        if (null != worldRoot && !worldRoot.isEmpty()) {
            File file = new File(MCkuai.getInstance().getSDPath() + worldRoot + world);
            if (file != null && file.exists() && file.isDirectory()) {
                Player player = loadPlayerFromDB(file);
                return player;
            }
        }
        return null;
    }


    public static boolean setGameMode(String worldDir, int mode) {
        WorldItem world = getWorldByDir(worldDir);
        if (null != world) {
            world.getLevel().setGameType(mode);
            return saveLevelData(worldDir, world.getLevel());
        }
        return false;
    }

    public List<InventorySlot> getInventory(String worldDir) {
        WorldItem world = getWorldByDir(worldDir);
        if (null != world) {
            if (null != world.getLevel().getPlayer() && null != world.getLevel().getPlayer().getInventory()) {
                return world.getLevel().getPlayer().getInventory();
            } else {
                if (null != world.getPlayer()) {
                    return world.getPlayer().getInventory();
                }
            }
        }
        return null;
    }

    /**
     * 获取单个世界的信息
     *
     * @param file 要获取信息的世界
     */
    private static void loadData(File file, boolean needPlayer) {
        File featureFile = new File(file.getPath(), "level.dat");
        if (null != featureFile && featureFile.exists() && featureFile.isFile()) {
            WorldItem worldItem = new WorldItem();
            worldItem.setDir(file.getName());       //文件夹名称
            worldItem.setSize(getWorldSize(file)); //大小
            //从level.dat文件中获取level信息（必有信息包括显示名，）
            if (loadLevelFromFile(file, worldItem)) {
                switch (worldItem.getLevel().getStorageVersion()) {
                    case 4:
                        if (needPlayer) {
                            worldItem.setPlayer(loadPlayerFromDB(file));
                        }
                        break;
                }
                if (worlds.isEmpty()){
                    worlds.add(worldItem);
                }
                else {
                    boolean inster = false;
                    for (int i = 0;i < worlds.size();i++) {
                        if (worldItem.getLevel().getLastPlayed() > worlds.get(i).getLevel().getLastPlayed()){
                            worlds.add(i,worldItem);
                            inster = true;
                            break;
                        }
                    }
                    if (!inster){
                        worlds.add(worldItem);
                    }
                }


            }
        }

    }

    /**
     * 从数据库中获取角色信息
     *
     * @param worldRoot 游戏世界的根目录
     * @return
     */
    private static Player loadPlayerFromDB(File worldRoot) {
        File[] subDir = worldRoot.listFiles();
        if (null != subDir) {
            for (File curDir : subDir) {
                //只有一个目录
                if (curDir.isDirectory()) {
                    Player player = GameDBEditer.getPlayer(curDir);
//                    Player player = LevelDBConverter.read
                    if (null != player) {
                        return player;
                    }
                }
            }
        }
        return null;
    }

    private static boolean loadLevelFromFile(File worldRoot, WorldItem worldItem) {
        Level level = null;
        File levelFile = new File(worldRoot, "level.dat");
        if (null != levelFile && levelFile.exists() && levelFile.isFile()) {
            try {
                level = LevelDataConverter.read(levelFile);
                if (null != level) {
                    worldItem.setLevel(level);
                    return true;
                }
            } catch (Exception e) {
                Log.e(TAG, "读取level.dat文件失败，原因：" + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        return false;
    }


    private static long getWorldSize(File file) {
        Long size = 0l;
        File[] subFiles = file.listFiles();
        if (null != subFiles && subFiles.length > 0) {
            for (File curFile : subFiles) {
                if (curFile.isFile()) {
                    size += curFile.length();
                } else {
                    size += getWorldSize(curFile);
                }
            }
        }
        return size;
    }

    private static WorldItem getWorldByDir(String worldDir) {
        for (WorldItem world : worlds) {
            if (world.getDir().equals(worldDir)) {
                return world;
            }
        }
        return null;
    }

    private static boolean saveLevelData(String worldDir, Level level) {
        String path = MCkuai.getInstance().getSDPath() + worldRoot + worldDir;
        File file = new File(path, "level.dat");
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
}
