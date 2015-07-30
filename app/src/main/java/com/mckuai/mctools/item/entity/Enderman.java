package com.mckuai.mctools.item.entity;

import com.mckuai.entity.*;
import com.mckuai.entity.Monster;

/**
 * Created by kyly on 2015/7/10.
 * 末影人（Enderman）是一只黑的、高度为3个方块的、可以拿起方块以及进行瞬移的中立型生物。
 */
public class Enderman extends Monster {
    /**
     * 被末影人拿起的方块的ID，0代表没有拿起任何方块
     */
    private short carried = 0;

    /**
     * 末影人所拿起的方块的额外数据，0代表没有拿起任何方块
     */
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
