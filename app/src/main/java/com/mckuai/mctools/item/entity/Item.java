package com.mckuai.mctools.item.entity;

import com.mckuai.mctools.ItemStack;
import com.mckuai.entity.*;
import com.mckuai.entity.Entity;

/**
 * 物品
 */

public class Item extends Entity {

    /**
     * 物品数据
     */
	private ItemStack stack;

    /**
     * 从5开始，目前仅当物品受到火焰伤害时降低。降至0时物品被摧毁。
     */
	private short health = 5;

    /**
     * 物品掉落在地上的时间.在2400刻或者说2分钟后，物品会被摧毁。
     */
	private short age = 0;

	public ItemStack getItemStack() {
		if (stack == null) stack = new ItemStack((short) 0, (short) 0, 0);
		return stack;
	}

	public void setItemStack(ItemStack stack) {
		this.stack = stack;
	}

	public short getAge() {
		return age;
	}

	public void setAge(short age) {
		this.age = age;
	}

	public short getHealth() {
		return health;
	}

	public void setHealth(short health) {
		this.health = health;
	}

}
