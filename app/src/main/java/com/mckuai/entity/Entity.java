package com.mckuai.entity;

import com.mckuai.until.Vector3f;

import java.io.Serializable;

public class Entity implements Serializable {
    /**
     * 实体ID,不同的ID代表不同的类型的实体
     */
    private int typeId = 0;

    /**
     * 此实体的当前坐标(三维,每个维度为float) <维基百科中叫做Pos>
     */
	private Vector3f location = new Vector3f(0, 0, 0);

    /**
     * 此实体在三个维度的运动速度(0,0,0为静止)
     */
    private Vector3f motion = new Vector3f(0, 0, 0);
    //以下两个数据用于存储实体的旋转度  <维基百科中用Rotation这个结构体来封装这两个字段>
    /**
     * 俯仰(绕x轴的旋转)
     */
	private float pitch;
    /**
     * 航向(绕y轴的旋转)
     */
	private float yaw;

    /**
     * 下落高度，值越大在接触陆地时伤害越高
     */
	private float fallDistance;

    /**
     * 火被扑灭时的刻度值，负值代表该实体可以在火中支撑多久
     */
	private short fire;

    /**
     * 该实体拥有的空气数量。在空气中为300,在水下会减少
     */
	private short air = 300;

    /**
     * 是否在地面上,当实体接地时为1
     */
	private boolean onGround = false;

	private Entity riding = null;


	public Vector3f getLocation() {
		return location;
	}

	public void setLocation(Vector3f location) {
		this.location = location;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public Vector3f getVelocity() {
		return motion;
	}

	public void setVelocity(Vector3f velocity) {
		this.motion = velocity;
	}

	public float getFallDistance() {
		return fallDistance;
	}

	public void setFallDistance(float fallDistance) {
		this.fallDistance = fallDistance;
	}

	public short getFireTicks() {
		return fire;
	}

	public void setFireTicks(short fire) {
		this.fire = fire;
	}

	public short getAirTicks() {
		return air;
	}

	public void setAirTicks(short air) {
		this.air = air;
	}

	public boolean isOnGround() {
		return onGround;
	}

	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}

	public int getEntityTypeId() {
		return typeId;
	}

	public void setEntityTypeId(int typeId) {
		this.typeId = typeId;
	}

	public Entity getRiding() {
		return riding;
	}

	public void setRiding(Entity riding) {
		this.riding = riding;
	}

	public EntityType getEntityType() {
		EntityType type = EntityType.getById(typeId);
		if (type == null) type = EntityType.UNKNOWN;
		return type;
	}

	public Vector3f getMotion() {
		return motion;
	}

	public void setMotion(Vector3f motion) {
		this.motion = motion;
	}

	public short getFire() {
		return fire;
	}

	public void setFire(short fire) {
		this.fire = fire;
	}

	public short getAir() {
		return air;
	}

	public void setAir(short air) {
		this.air = air;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
}
