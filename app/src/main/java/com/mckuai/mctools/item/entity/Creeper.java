package com.mckuai.mctools.item.entity;

import com.mckuai.mctools.item.entity.Monster;

/*
 *  爬行者（Creeper）是一种常见的，接近玩家后爆炸的敌对生物。
 *  网络ID:50  存档ID:Creeper
 */
public class Creeper extends Monster {

	@Override
	public int getMaxHealth() {
		return 20;
	}
}
