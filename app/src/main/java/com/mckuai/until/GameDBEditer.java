package com.mckuai.until;

import android.util.Log;


import com.mckuai.InventorySlot;
import com.mckuai.Level;
import com.mckuai.entity.Entity;
import com.mckuai.entity.Player;
import com.mckuai.io.EntityDataConverter;
import com.mckuai.io.db.DB;
import com.mckuai.io.db.DBKey;
import com.mckuai.io.db.Iterator;
import com.mckuai.io.nbt.NBTConverter;
import com.mckuai.tileentity.TileEntity;

import org.spout.nbt.CompoundTag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by kyly on 2015/6/26.
 */
public class GameDBEditer {
    private static final String TAG = "GameDBEditer";

    public EntityDataConverter.EntityData readAllEntities(File file){
        DB db = openDB(file);
        if (null != db) {
            DBKey dbKey = new DBKey();
            ByteArrayInputStream byteArrayInputStream;
            NBTInputStream nbtis;
            Entity entity;
            TileEntity tileEntity;
            Iterator iterator = db.iterator();
            EntityDataConverter.EntityData data = new EntityDataConverter.EntityData(new ArrayList<Entity>(), new ArrayList<TileEntity>());
            for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                dbKey.fromBytes(iterator.getKey());
                switch (dbKey.getType()) {
                    case DBKey.CHUNK:
                        //48
                        break;
                    case DBKey.TILE_ENTITY:
                        //49
                        byteArrayInputStream = new ByteArrayInputStream(iterator.getValue());
                        try {
                            nbtis = new NBTInputStream(byteArrayInputStream, false, true);
                            tileEntity = NBTConverter.readSingleTileEntity((CompoundTag) nbtis.readTag());
                            data.tileEntities.add(tileEntity);
                        } catch (Exception e) {

                        }
                        break;
                    case DBKey.ENTITY:
                        //50
                        byteArrayInputStream = new ByteArrayInputStream(iterator.getValue());
                        try {
                            nbtis = new NBTInputStream(byteArrayInputStream, false, true);
                            entity = NBTConverter.readSingleEntity((CompoundTag) nbtis.readTag());
                            data.entities.add(entity);
                        } catch (Exception e) {

                        }

                        break;
                    case DBKey.PLACEHOLDER:
                        //118
                        break;
                }
            }
            return data;
        }
        else {
            return null;
        }
    }


    public static Player getPlayer(File dbFile){
        DB db = openDB(dbFile);
        Player player = null;
         if (null != db){
            try{
                byte[] data =db.get("~local_player".getBytes(Charset.forName("utf-8")));
                if (null != data) {
                    player = NBTConverter.readPlayer((CompoundTag) new NBTInputStream(new ByteArrayInputStream(data), false, true).readTag());
                }
            }
            catch (Exception e){
                Log.e(TAG,"读取角色信息时失败，原因："+e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        closeDB(db);
        return player;
    }

    public static Level getLevel(File dbFile){
        DB db = openDB(dbFile);
        Level level = null;
        if (null != db){
            try{
                byte[] data =db.get("~local_player".getBytes(Charset.forName("utf-8")));
                if (null != data) {
                    level = NBTConverter.readLevel((CompoundTag) new NBTInputStream(new ByteArrayInputStream(data), false, true).readTag());
                }
            }
            catch (Exception e){
                Log.e(TAG,"读取角色信息时失败，原因："+e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        closeDB(db);
        return level;
    }

    public static boolean setPlayer(Player player,File dbFile){
        DB db = openDB(dbFile);
        boolean result = false;
        if (null != db){
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                NBTOutputStream nbtOutputStream = new NBTOutputStream(outputStream, false, true);
                nbtOutputStream.writeTag(NBTConverter.writePlayer(player, "", true));
                result = true;
            }
            catch (Exception e){
                Log.e(TAG,"写入角色数据时出错，原因："+e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        closeDB(db);
        return result;
    }




    private static DB openDB(File dbFile){
        if (null != dbFile && dbFile.exists() && dbFile.isDirectory()){
           DB db =new DB(dbFile);
           db.open();
            return  db;
        }
        else {
            return null;
        }
    }

    private static void closeDB(DB db){
        if (null != db){
            db.close();
        }
    }

}



