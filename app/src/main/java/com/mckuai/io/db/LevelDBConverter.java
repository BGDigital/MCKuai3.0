package com.mckuai.io.db;

import android.util.Log;

import com.mckuai.Level;
import com.mckuai.entity.Entity;
import com.mckuai.io.EntityDataConverter;
import com.mckuai.io.nbt.NBTConverter;
import com.mckuai.tileentity.TileEntity;
import com.mckuai.until.OptionUntil;

import org.spout.nbt.CompoundTag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Array;
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

    public static void writeLevel(Level level,File dbRoot){
        DB db = openDataBase(dbRoot);
        try{
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            new NBTOutputStream(byteArrayOutputStream, false, true).writeTag(NBTConverter.writePlayer(level.getPlayer(), "", true));
            db.put(stringToByte("~local_player"), byteArrayOutputStream.toByteArray());
            byteArrayOutputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        db.close();
    }

    private static byte[] stringToByte(String string){
        return string.getBytes();
    }


}
