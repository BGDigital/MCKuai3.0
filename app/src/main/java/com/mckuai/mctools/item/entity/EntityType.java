package com.mckuai.mctools.item.entity;

import com.mckuai.entity.Arrow;
import com.mckuai.entity.Chicken;
import com.mckuai.entity.Cow;
import com.mckuai.entity.Creeper;
import com.mckuai.entity.Egg;
import com.mckuai.entity.Entity;
import com.mckuai.entity.FallingBlock;
import com.mckuai.entity.Item;
import com.mckuai.entity.Minecart;
import com.mckuai.entity.Painting;
import com.mckuai.entity.Pig;
import com.mckuai.entity.PigZombie;
import com.mckuai.entity.Player;
import com.mckuai.entity.Sheep;
import com.mckuai.entity.Skeleton;
import com.mckuai.entity.Snowball;
import com.mckuai.entity.Spider;
import com.mckuai.entity.TNTPrimed;
import com.mckuai.entity.Zombie;

import java.util.HashMap;
import java.util.Map;

public enum EntityType {
	CHICKEN(10, Chicken.class),
	COW(11, Cow.class),
	PIG(12, Pig.class),
	SHEEP(13, Sheep.class),
	ZOMBIE(32, Zombie.class),
	CREEPER(33, Creeper.class),
	SKELETON(34, Skeleton.class),
	SPIDER(35, Spider.class),
	PIG_ZOMBIE(36, PigZombie.class),
	ITEM(64, Item.class),
	PRIMED_TNT(65, TNTPrimed.class),
	FALLING_BLOCK(66, FallingBlock.class),
	ARROW(80, Arrow.class),
	SNOWBALL(81, Snowball.class),
	EGG(82, Egg.class),
	PAINTING(83, Painting.class),
	MINECART(84, Minecart.class),
	UNKNOWN(-1, null),
	PLAYER(-2, Player.class);

	private static Map<Integer, com.mckuai.entity.EntityType> idMap = new HashMap<Integer, com.mckuai.entity.EntityType>();

	private static Map<Class<? extends Entity>, com.mckuai.entity.EntityType> classMap = new HashMap<Class<? extends Entity>, com.mckuai.entity.EntityType>();

	private int id;

	private Class<? extends Entity> entityClass;

	EntityType(int id, Class<? extends Entity> entityClass) {
		this.id = id;
		this.entityClass = entityClass;
	}

	public int getId() {
		return id;
	}

	public Class<? extends Entity> getEntityClass() {
		return entityClass;
	}

	public static com.mckuai.entity.EntityType getById(int id) {
		com.mckuai.entity.EntityType type = idMap.get(id);
		if (type == null) return com.mckuai.entity.EntityType.UNKNOWN;
		return type;
	}

	public static com.mckuai.entity.EntityType getByClass(Class<? extends Entity> clazz) {
		com.mckuai.entity.EntityType type = classMap.get(clazz);
		if (type == null) return com.mckuai.entity.EntityType.UNKNOWN;
		return type;
	}


	static {
		for (com.mckuai.entity.EntityType type : com.mckuai.entity.EntityType.values()) {
			idMap.put(type.getId(), type);
			if (type.getEntityClass() != null) {
				classMap.put(type.getEntityClass(), type);
			}
		}
	}
}
