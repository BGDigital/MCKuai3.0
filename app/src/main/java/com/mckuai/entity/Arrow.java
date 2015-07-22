package com.mckuai.entity;
/**
 *  射出的箭
 */
public class Arrow extends Projectile {

    /**
     * 1 或者 0 (true/false) – 如果pickup没有被使用，并且该项为true，那么箭可以被玩家捡起
     */
	private boolean player = false;

    /**
     * 弹射物存在的方块元数据
     */
	private byte inData = (byte) 0;

	public byte getInData() {
		return inData;
	}

	public void setInData(byte inData) {
		this.inData = inData;
	}

	public boolean isShotByPlayer() {
		return player;
	}

	public void setShotByPlayer(boolean player) {
		this.player = player;
	}

}
