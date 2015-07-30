package com.mckuai.mctools.item.entity;


public class LivingEntity extends Entity {
    /**
     * 实体的"无敌盾牌"在受到打击后保持的刻形式的时间长度
     */
	private short attackTime;

    /**
     * 生物已死亡的刻数。控制死亡动画
     */
	private short deathTime;

    /**
     * 未知
     */
	private short hurtTime;

    /**
     * 生物拥有的健康值。玩家和敌对生物最高有20点，牲畜最高有10点。
     */
	private short health;

	public LivingEntity() {
		super();
		this.health = (short) this.getMaxHealth();
	}

	public short getAttackTime() {
		return attackTime;
	}

	public void setAttackTime(short attackTime) {
		this.attackTime = attackTime;
	}

	public short getDeathTime() {
		return deathTime;
	}

	public void setDeathTime(short deathTime) {
		this.deathTime = deathTime;
	}

	public short getHurtTime() {
		return hurtTime;
	}

	public void setHurtTime(short hurtTime) {
		this.hurtTime = hurtTime;
	}

	public short getHealth() {
		return health;
	}

	public void setHealth(short health) {
		this.health = health;
	}

	public int getMaxHealth() {
		return 20;
	}
}

