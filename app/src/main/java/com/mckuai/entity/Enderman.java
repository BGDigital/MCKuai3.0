package com.mckuai.entity;

/**
 * Created by kyly on 2015/7/10.
 */
public class Enderman extends  Monster {
    private short carried = 0;
    private short carriedData = 0;

    public short getCarried()
    {
        return this.carried;
    }

    public short getCarriedData()
    {
        return this.carriedData;
    }

    public int getMaxHealth()
    {
        return 40;
    }

    public void setCarried(short carried) {
        this.carried = carried;
    }

    public void setCarriedData(short data) {
        this.carriedData = data;
    }
}
