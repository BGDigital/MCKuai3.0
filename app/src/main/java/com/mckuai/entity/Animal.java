package com.mckuai.entity;

/**
 *   可以繁衍的生物
 */
public class Animal extends LivingEntity {
    /**
     *生物的成长时间(刻)；负数代表此生物是婴儿，0以上代表此生物已成年。在高于0时，它代表此生物还有多久才能再次繁殖。
     */
	private int age = 0;

    /**
     *此生物冒出爱心并寻找配偶的剩余时间(刻)。0代表不会寻找配偶。
     */
	private int inLove = 0;

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getInLove() {
		return inLove;
	}

	public void setInLove(int inLove) {
		this.inLove = inLove;
	}
}
