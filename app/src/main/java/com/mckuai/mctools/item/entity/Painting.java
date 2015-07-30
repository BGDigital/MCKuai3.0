package com.mckuai.mctools.item.entity;


import com.mckuai.entity.*;
import com.mckuai.entity.Entity;
import com.mckuai.mctools.item.Vector3f;

public class Painting extends Entity {

	private Vector3f blockCoordinates = new Vector3f(0f, 0f, 0f);

	private String artType = "Alban";

	private byte direction = 0;

	public String getArt() {
		return artType;
	}

	public void setArt(String art) {
		artType = art;
	}

	public Vector3f getBlockCoordinates() {
		return blockCoordinates;
	}

	public byte getDirection() {
		return direction;
	}

	public void setDirection(byte dir) {
		this.direction = dir;
	}
}
