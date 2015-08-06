package com.mckuai.mctools.item.entity;

/**
 *  玩家属性，用于存储玩家自身的一些特性
 */

import java.io.Serializable;

public class PlayerAbilities implements Serializable {
	/**
	 * 是否可以飞行
	 */
	public boolean mayFly = false;

	/**
	 * 是否正在飞行
	 */
	public boolean flying = false;

	/**
	 * 是否
	 */
	public boolean instabuild = false;

	/**
	 * 是否无敌
	 */
	public boolean invulnerable = false;


	public boolean isMayFly() {
		return mayFly;
	}

    /**
     * 设置是否可以飞行
     * @param mayFly 为真则可以飞行，否则不能飞行
     */
	public void setMayFly(boolean mayFly) {
		this.mayFly = mayFly;
	}

	public boolean isFlying() {
		return flying;
	}

	public void setFlying(boolean flying) {
		this.flying = flying;
	}

	public boolean isInstabuild() {
		return instabuild;
	}

	public void setInstabuild(boolean instabuild) {
		this.instabuild = instabuild;
	}

	public boolean isInvulnerable() {
		return invulnerable;
	}

    /**
     * 设置是否无敌
     * @param invulnerable 为真时无敌，否则非无敌
     */
	public void setInvulnerable(boolean invulnerable) {
		this.invulnerable = invulnerable;
	}


}
