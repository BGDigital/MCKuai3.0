package com.mckuai.mctools.item.entity;


public class PigZombie extends com.mckuai.mctools.item.entity.Monster {

	private short anger = 0;

	@Override
	public int getMaxHealth() {
		return 20;
	}

	public short getAnger() {
		return anger;
	}

	public void setAnger(short anger) {
		this.anger = anger;
	}

}
