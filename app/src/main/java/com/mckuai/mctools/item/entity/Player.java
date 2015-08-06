package com.mckuai.mctools.item.entity;

import com.mckuai.mctools.InventorySlot;
import com.mckuai.mctools.ItemStack;

import java.io.Serializable;
import java.util.List;


public class Player extends LivingEntity implements Serializable {
    /**
     * 玩家正在携带或手持的物品
     */
    private List<InventorySlot> inventory;

    /**
     *这是一个长度4的列表。对应着头盔、胸甲、护腿和靴子
     */
    private List<ItemStack> armorSlots;
    private PlayerAbilities abilities;
    private int score;
    private int dimension;
    private int bedPositionX = 0, bedPositionY = 0, bedPositionZ = 0;
    private int spawnX = 0, spawnY = 64, spawnZ = 0;
    private short sleepTimer = 0;
    private boolean sleeping = false;

    public List<InventorySlot> getInventory() {
        return inventory;
    }

    public void setInventory(List<InventorySlot> inventory) {
        this.inventory = inventory;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public int getBedPositionX() {
        return bedPositionX;
    }

    public void setBedPositionX(int bedPositionX) {
        this.bedPositionX = bedPositionX;
    }

    public int getBedPositionY() {
        return bedPositionY;
    }

    public void setBedPositionY(int bedPositionY) {
        this.bedPositionY = bedPositionY;
    }

    public int getBedPositionZ() {
        return bedPositionZ;
    }

    public void setBedPositionZ(int bedPositionZ) {
        this.bedPositionZ = bedPositionZ;
    }

    public int getSpawnX() {
        return spawnX;
    }

    public void setSpawnX(int spawnX) {
        this.spawnX = spawnX;
    }

    public int getSpawnY() {
        return spawnY;
    }

    public void setSpawnY(int spawnY) {
        this.spawnY = spawnY;
    }

    public int getSpawnZ() {
        return spawnZ;
    }

    public void setSpawnZ(int spawnZ) {
        this.spawnZ = spawnZ;
    }

    public boolean isSleeping() {
        return sleeping;
    }

    public void setSleeping(boolean sleeping) {
        this.sleeping = sleeping;
    }

    public short getSleepTimer() {
        return sleepTimer;
    }

    public void setSleepTimer(short sleepTimer) {
        this.sleepTimer = sleepTimer;
    }

    public List<ItemStack> getArmor() {
        return armorSlots;
    }

    public void setArmor(List<ItemStack> armorSlots) {
        this.armorSlots = armorSlots;
    }

    public PlayerAbilities getAbilities() {
        return this.abilities;
    }

    public void setAbilities(PlayerAbilities abilities) {
        this.abilities = abilities;
    }

    public com.mckuai.mctools.item.entity.EntityType getEntityType() {
        return EntityType.PLAYER;
    }

}
