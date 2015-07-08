package com.mckuai.until;

import android.util.Log;


import com.mckuai.io.db.DB;
import com.mckuai.io.db.Iterator;

import java.io.File;


/**
 * Created by kyly on 2015/6/26.
 */
public class GameEditer {
    private DB db;
    private File file;

    public GameEditer(){
        initDB();
    }

    private void initDB(){
        String name = "/storage/sdcard0/games/com.mojang/minecraftWorlds/My World/db";
        Log.w("initDB","file:"+name);
        file = new File(name);
        if (!file.exists()){
            Log.w("initDB","error:file not exist");
            file.mkdirs();
        }
        Log.w("initDB","get file");
        db = new DB(file);
        db.open();

    }

    private void openDB(){
        if (!isReady()){
            try{
                System.setProperty("sun.arch.data.model", "32");
                System.setProperty("leveldb.mmap", "false");
                db.open();
            }
            catch (Exception e){
                Log.e("openDB",e.getLocalizedMessage());
            }

        }
    }

    public void closeDB(){
        if (isReady()){
            try{
                db.close();
                db = null;
            }
            catch (Exception e){
            Log.w("closeDB", e.getLocalizedMessage());
            }
        }
    }


    public void addItem(String key,String value){
        if (!isReady()){
            openDB();
        }
        db.put(key.getBytes(),value.getBytes());
        //closeDB();
    }

    public String getString(String key){
        if (!isReady()){
            openDB();
        }
        byte b[] =db.get(key.getBytes());
        //closeDB();
        if (null != b){
            return new String(b);
        }
        else{
            return null;
        }
    }

    public int getInt(String key){
        if (!isReady()){
            openDB();
        }
        byte[] mykey = key.getBytes();
        byte[] bytes = db.get(key.getBytes());
        closeDB();
        int value = 0;
        return  value;
    }


    public void getAllItem(){
        if (!isReady()){
            openDB();
        }
        Iterator iterator = db.iterator();
        for (iterator.seekToFirst();iterator.isValid();iterator.next()){
            final byte[] key = iterator.getKey();
            final byte[] value = iterator.getValue();

        }
    }

    private boolean isReady(){
        return  null != db;
    }



}



