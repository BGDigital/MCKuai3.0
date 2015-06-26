package com.mckuai.until;

import android.util.Log;

import com.mckuai.imc.MCkuai;

import org.iq80.leveldb.*;
import static org.iq80.leveldb.impl.Iq80DBFactory.*;
import org.iq80.leveldb.Options;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by kyly on 2015/6/26.
 */
public class GameEditer {
    private Options options;
    private DB db;
    private File file;

    private boolean isOpen = false;

    public GameEditer(){
        initDB();
    }

    private void initDB(){
        options = new Options();
        options.createIfMissing(true);
//        String name = MCkuai.getInstance().getMapDownloadDir()+"test.db";
        String name = "/storage/sdcard0/games/com.mojang/bak/World/";
        Log.w("initDB","file:"+name);
        file = new File(name);
        if (!file.exists()){
            Log.w("initDB","error:file not exist");
        }
        else{
            Log.w("initDB","get file");
        }

    }

    private void openDB(){
        if (!isOpen){
            try{
                System.setProperty("sun.arch.data.model", "32");
                System.setProperty("leveldb.mmap", "false");
                db = factory.open(file,options);
                isOpen = true;
            }
            catch (Exception e){
                Log.e("openDB",e.getLocalizedMessage());
            }

        }
    }

    private void closeDB(){
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


    public void addItem(String key,String value){
        if (!isOpen){
            openDB();
        }
        db.put(key.getBytes(),value.getBytes());
        closeDB();
    }

    public String getItem(String key){
        if (!isOpen){
            openDB();
        }
        byte b[] =db.get(key.getBytes());
        closeDB();
        if (null != b){
            return new String(b);
        }
        else{
            return null;
        }
    }

   public  class  Item{
        String key;
        String value;
        Item(String k,String v){
            this.key = k;
            this.value = v;
        }
    }

    public ArrayList<Item> getAllItem(){
        if (!isOpen){
            openDB();
        }
        ArrayList<Item> rst = new ArrayList<>(20);
        DBIterator iterator = db.iterator();
        for (iterator.seekToFirst();iterator.hasNext();iterator.next()){
            String key = new String(iterator.peekNext().getKey());
            String value = new String(iterator.peekNext().getValue());
            Item item = new Item(key,value);
            rst.add(item);
        }
        return rst;
    }



}



