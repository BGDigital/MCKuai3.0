package com.mckuai.mctools.item.entity;

import com.mckuai.entity.Animal;

public class Sheep extends Animal {
	/**
	 * 1或0 (true/false) - 已剪毛时为1。
	 */
	private boolean sheared = false;

    /**
     * 0到15 - 现有一个证据表明此值不影响羊的颜色，但影响羊的羊毛掉落的颜色。
     */
	private byte color = 0;

	public boolean isSheared() {
		return sheared;
	}

	public void setSheared(boolean sheared) {
		this.sheared = sheared;
	}

	public byte getColor() {
		return color;
	}

	public void setColor(byte color) {
		this.color = color;
	}

	@Override
	public int getMaxHealth() {
		return 8;
	}

}
