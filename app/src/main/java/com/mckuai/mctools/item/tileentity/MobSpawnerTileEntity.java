package com.mckuai.mctools.item.tileentity;

import com.mckuai.mctools.item.entity.Entity;
import com.mckuai.mctools.item.entity.EntityType;

import java.io.Serializable;

/**
 * Created by kyly on 2015/8/18.
 */
public class MobSpawnerTileEntity extends TileEntity implements Serializable {
    private short delay = 20;
    private short maxNearbyEntities = 6;
    private short maxSpawnDelay = 200;
    private short minSpawnDelay = 200;
    private short requiredPlayerRange = 16;
    private short spawnCount = 4;
    private short spawnRange = 4;
    private int entityId = 0;

    public MobSpawnerTileEntity(){

    }

    public short getDelay() {
        return delay;
    }

    public void setDelay(short delay) {
        this.delay = delay;
    }

    public short getMaxNearbyEntities() {
        return maxNearbyEntities;
    }

    public void setMaxNearbyEntities(short maxNearbyEntities) {
        this.maxNearbyEntities = maxNearbyEntities;
    }

    public short getMaxSpawnDelay() {
        return maxSpawnDelay;
    }

    public void setMaxSpawnDelay(short maxSpawnDelay) {
        this.maxSpawnDelay = maxSpawnDelay;
    }

    public short getMinSpawnDelay() {
        return minSpawnDelay;
    }

    public void setMinSpawnDelay(short minSpawnDelay) {
        this.minSpawnDelay = minSpawnDelay;
    }

    public short getRequiredPlayerRange() {
        return requiredPlayerRange;
    }

    public void setRequiredPlayerRange(short requiredPlayerRange) {
        this.requiredPlayerRange = requiredPlayerRange;
    }

    public short getSpawnCount() {
        return spawnCount;
    }

    public void setSpawnCount(short spawnCount) {
        this.spawnCount = spawnCount;
    }

    public short getSpawnRange() {
        return spawnRange;
    }

    public void setSpawnRange(short spawnRange) {
        this.spawnRange = spawnRange;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public String toString(){
        Class<? extends Entity> myclass =  EntityType.getById(this.entityId).getEntityClass();
        if (null == myclass){
            return super.toString();
        }
        return super.toString()+":"+myclass.getSimpleName();
    }
}
