package com.mckuai.mctools.item.entity;

import java.io.Serializable;

public class PlayerAbilities implements Serializable{
	public boolean mayFly = false;
	public boolean flying = false;
	public boolean instabuild = false;
	public boolean invulnerable = false;

	public void initForGameType(int gameType) {
		boolean creative = gameType == 1;
		mayFly = instabuild = invulnerable = creative;
		flying = flying && creative;
	}

	public boolean isMayFly() {
		return mayFly;
	}

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

	public void setInvulnerable(boolean invulnerable) {
		this.invulnerable = invulnerable;
	}


}
