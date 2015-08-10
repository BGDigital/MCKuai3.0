package com.mckuai.io.db;

import android.util.Log;
import com.mckuai.mctools.item.entity.Entity;
import com.mckuai.io.EntityDataConverter;
import com.mckuai.io.nbt.NBTConverter;
import com.mckuai.mctools.item.entity.Player;
import com.mckuai.mctools.item.tileentity.TileEntity;

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
 * Created by kyly on 2015/7/12.
 */
    public class LevelDBConverter {

        private final static String TAG = "LevelDBConverter";

        public static DB openDataBase(File dbRoot){
            new File(dbRoot,"LOCK");
            try {
                DB db = new DB(dbRoot);
                db.open();
                return db;
            }catch (Exception e){
                return null;
            }
        }

    public static EntityDataConverter.EntityData readAllEntities(File dbRoot){
        DB db = openDataBase(dbRoot);

        ArrayList<TileEntity> tileEntities = new ArrayList<>();
        ArrayList<Entity> entities = new ArrayList<>();

        DBKey key = new DBKey();
        ByteArrayInputStream byteArrayInputStream;
        NBTInputStream nbtInputStream;
        Iterator iterator = db.iterator();
        for (iterator.seekToFirst();iterator.isValid();iterator.next()){
            key.fromBytes(iterator.getKey());
            switch (key.getType()){
                case 49:
                    byteArrayInputStream = new ByteArrayInputStream(iterator.getValue());
                    try {
                        nbtInputStream = new NBTInputStream(byteArrayInputStream, false, true);
                        if (byteArrayInputStream.available() > 0 ) {
                            TileEntity tileEntity = NBTConverter.readSingleTileEntity((CompoundTag) nbtInputStream.readTag());
                            if (null == tileEntities){
                                Log.e(TAG,"tileEntities is null");
                            }
                            else {
                                tileEntities.add(tileEntity);
                            }
                        }
                        nbtInputStream.close();
                        byteArrayInputStream.close();
                    }catch (Exception e){
                        nbtInputStream = null;
                        e.printStackTrace();
                    }

                    break;
                case 50:
                    byteArrayInputStream = new ByteArrayInputStream(iterator.getValue());
                    try {
                        nbtInputStream = new NBTInputStream(byteArrayInputStream, false, true);
                        Entity entity = NBTConverter.readSingleEntity((CompoundTag)nbtInputStream.readTag());
                        if (null == entities){
                            Log.e(TAG,"entities is null");
                        }
                        else {
                            entities.add(entity);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
        db.close();
        return new EntityDataConverter.EntityData(entities,tileEntities);
    }

    public static void writeAllEntities(List<Entity> entities,List<TileEntity> tileEntities,File dbRoot){

    }

    /**
     * 从数据库中读取player信息
     * @param worldRoot
     * @return 如果获取到player信息则返回，否则返回空
     */
    public static Player readPlayer(String worldRoot){
        if (null == worldRoot || worldRoot.isEmpty()){
            return null;
        }

        File dbFile = new File(worldRoot);
        if (null == dbFile || !dbFile.exists() || !dbFile.isDirectory()){
            return null;
        }
        com.mckuai.io.db.DB db = openDataBase(dbFile);
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
        db.close();
        return player;
    }

    public static boolean writeLevel(Player player,File dbRoot){
        DB db = openDataBase(dbRoot);
        boolean result = false;
        try{
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            NBTOutputStream nbtOutputStream = new NBTOutputStream(byteArrayOutputStream,false,true);
            nbtOutputStream.writeTag(NBTConverter.writePlayer(player, "", true));
            db.put(stringToByte("~local_player"), byteArrayOutputStream.toByteArray());
            byteArrayOutputStream.close();
            result = true;
        }catch (Exception e){
            result = false;
            e.printStackTrace();
        }
        db.close();
        return result;
    }

    private static byte[] stringToByte(String string){
        return string.getBytes();
    }


}
