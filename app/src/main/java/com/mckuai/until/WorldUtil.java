package com.mckuai.until;

import android.content.Context;
import android.util.Log;

import com.mckuai.WorldItem;
import com.mckuai.imc.MCkuai;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kyly on 2015/7/9.
 */
public class WorldUtil {
public static final String WORLD_FOLDER_NAME_SUFFIX = "-";
    public static final String TAG = "WorldUtil";

    //获取所有的地图列表
    public static List<WorldItem> getWorldItems(Context context){
        File  file = new File(MCkuai.getInstance().getSDPath(),"games/com.mojang/minecraftWorlds");
        ArrayList<WorldItem> items = new ArrayList<>();
        if (!file.exists()){
            Log.e(TAG,"no storage folder");
            return  items;
        }
        else {
        File[] fileList = file.listFiles();
            for (File curFile:fileList){
                items.add(new WorldItem(curFile));
            }
        }
        return  items;
    }


}
