package com.mckuai.mctools.WorldUtil;

import android.util.Log;

import com.google.gson.Gson;
import com.mckuai.bean.SkinItem;
import com.mckuai.imc.MCkuai;
import com.mckuai.io.db.DB;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by kyly on 2015/8/17.
 */
public class MCSkinManager {
    private ArrayList<String> index;
    private ArrayList<SkinItem> downloadSkins;
    private DB db;
    private boolean isDBOpened = false;
    private final String TAG = "MCSkinManager";

    public MCSkinManager() {
        getIndex();
        getDownloadSkins();
    }

    public boolean addSkin(SkinItem item){
        if (null != item){
            if (!hasSkin(item.getId()+"")) {
                index.add(item.getId() + "");
                downloadSkins.add(item);
                saveDB();
            }
        }
        return false;
    }

    public boolean removeSkin(SkinItem item){
        if (null == index || index.isEmpty() || null == item || !hasSkin(item.getId() +"")){
            return false;
        }

        index.remove(item.getId()+"");
        downloadSkins.remove(item);
        return saveDB();
    }

    public ArrayList<SkinItem> getDownloadSkins() {
        if (null != downloadSkins){
            return  downloadSkins;
        }
        if (null != index && !index.isEmpty()){
            downloadSkins = new ArrayList<>(index.size());
            Gson gson = new Gson();
            if (openDB()){
                for (String skinId:index){
                    byte[] data = db.get(skinId.getBytes());
                    if (null != data && data.length > 10){
                        SkinItem item = gson.fromJson(new String(data), SkinItem.class);
                        if (null != item){
                            downloadSkins.add(item);
                        }
                    }
                }
                closeDB();
            }
        }
        return downloadSkins;
    }

    public boolean hasSkin(String skinId){
        if (null == index || null == skinId || index.isEmpty()){
            return false;
        }
        for (String id:index){
            if (id.equals(skinId)){
                Log.e(TAG,"ID为"+skinId+"的皮肤已经存在！");
                return true;
            }
        }
        return false;
    }

    public boolean moveToGame(SkinItem item) {
        if (null != item){
            File srcfile = new File(MCkuai.getInstance().getSkinDownloadDir(),item.getViewName()+".png");
            if (null != srcfile && srcfile.exists() && srcfile.isFile()){
                File dstFile = new File(MCkuai.getInstance().getGameProfileDir()+"minecraftpe","custom.png");
                if (null != dstFile&& dstFile.exists()){
                    dstFile.delete();
                }
                try {
                    InputStream inputStream = new FileInputStream(srcfile);
                    FileOutputStream outputStream = new FileOutputStream(dstFile);
                    byte[] srcData= new byte[(int)srcfile.length()+1];
                    inputStream.read(srcData);
                    outputStream.write(srcData);
                    inputStream.close();
                    outputStream.close();
                    return true;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private void getIndex() {
        if (openDB()){
            index = new ArrayList<>();
            byte[] data = db.get("SKININDEX".getBytes());
            if (null != data && 0 != data.length){
                String[] skinsIndex = new String(data).split(",");
                for (String skin:skinsIndex){
                    index.add(skin);
                }
            }
            closeDB();
        }
    }

    private boolean saveDB(){
        if (null == index || index.isEmpty()){
            return true;
        }

        String indexString = "";
        for (String skinId:index){
            indexString += skinId+",";
        }
        indexString = indexString.substring(0,indexString.length() - 1);
        if (openDB()){
            //保存索引
            try {
                db.put("SKININDEX".getBytes(),indexString.getBytes());
            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG, "保存索引失败，原因："+e.getLocalizedMessage());
                closeDB();
                return false;
            }
            //保存皮肤信息
            Gson gson = new Gson();
            for (SkinItem item : downloadSkins) {
                try {
                    db.put((item.getId()+"").getBytes(),gson.toJson(item).getBytes());
                }catch (Exception e){
                    e.printStackTrace();
                    Log.e(TAG,"保存皮肤信息时失败，原因："+e.getLocalizedMessage());
                    closeDB();
                    return false;
                }
            }
            Log.w(TAG,"保存皮肤信息成功！");
            return closeDB();
        }
        closeDB();
        return false;
    }


    private boolean initDB() {
        File file = new File(MCkuai.getInstance().getSkinDownloadDir());
        boolean result = true;
        if (null == file || !file.exists() || !file.isDirectory()) {
            result = file.mkdirs();
        }
        if (result) {
            try {
                db = new DB(file);
                return true;
            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG,"初始化数据库时失败，原因："+e.getLocalizedMessage());
            }
        }
        return false;
    }

    private boolean openDB() {
        if (isDBOpened) {
            return true;
        }

        if (null == db) {
            if (!initDB()) {
                return false;
            }
        }

        try {
            db.open();
            isDBOpened = true;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "打开数据库失败，原因：" + e.getLocalizedMessage());
        }
        return false;
    }

    private boolean closeDB(){
        if (isDBOpened){
            try {
                db.close();
                isDBOpened = false;
                return true;
            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG,"关闭数据库失败，原因："+e.getLocalizedMessage());
                return false;
            }
        }
        else return true;
    }
}
