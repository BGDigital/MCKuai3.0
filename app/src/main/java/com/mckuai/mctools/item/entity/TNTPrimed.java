package com.mckuai.mctools.item.entity;

import com.mckuai.entity.Entity;

public class TNTPrimed extends Entity {

	private byte fuse = 0;

	public byte getFuseTicks() {
		return fuse;
	}

	public void setFuseTicks(byte fuse) {
		this.fuse = fuse;
	}
}
