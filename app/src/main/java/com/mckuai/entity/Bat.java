package com.mckuai.entity;

/**
 * Created by kyly on 2015/7/22.
 *网络ID:65  存档ID:Bat
 */
public class Bat extends Monster {
    /**
     * 1代表正在倒挂在方块上，0代表正在飞行
     */
    private boolean BatFlags;

    @Override
    public int getMaxHealth() {
        return 6;
    }
}
