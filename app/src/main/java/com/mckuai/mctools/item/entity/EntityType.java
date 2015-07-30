package com.mckuai.mctools.item.entity;

import com.mckuai.mctools.item.entity.Arrow;
import com.mckuai.mctools.item.entity.Chicken;
import com.mckuai.mctools.item.entity.Cow;
import com.mckuai.mctools.item.entity.Creeper;
import com.mckuai.mctools.item.entity.Egg;
import com.mckuai.mctools.item.entity.Entity;
import com.mckuai.mctools.item.entity.FallingBlock;
import com.mckuai.mctools.item.entity.Item;
import com.mckuai.mctools.item.entity.Minecart;
import com.mckuai.mctools.item.entity.Painting;
import com.mckuai.mctools.item.entity.Pig;
import com.mckuai.mctools.item.entity.PigZombie;
import com.mckuai.mctools.item.entity.Player;
import com.mckuai.mctools.item.entity.Sheep;
import com.mckuai.mctools.item.entity.Skeleton;
import com.mckuai.mctools.item.entity.Snowball;
import com.mckuai.mctools.item.entity.Spider;
import com.mckuai.mctools.item.entity.TNTPrimed;
import com.mckuai.mctools.item.entity.Zombie;

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

	private static Map<Integer, com.mckuai.mctools.item.entity.EntityType> idMap = new HashMap<Integer, com.mckuai.mctools.item.entity.EntityType>();

	private static Map<Class<? extends Entity>, com.mckuai.mctools.item.entity.EntityType> classMap = new HashMap<Class<? extends Entity>, com.mckuai.mctools.item.entity.EntityType>();

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

	public static com.mckuai.mctools.item.entity.EntityType getById(int id) {
		com.mckuai.mctools.item.entity.EntityType type = idMap.get(id);
		if (type == null) return com.mckuai.mctools.item.entity.EntityType.UNKNOWN;
		return type;
	}

	public static com.mckuai.mctools.item.entity.EntityType getByClass(Class<? extends Entity> clazz) {
		com.mckuai.mctools.item.entity.EntityType type = classMap.get(clazz);
		if (type == null) return com.mckuai.mctools.item.entity.EntityType.UNKNOWN;
		return type;
	}


	static {
		for (com.mckuai.mctools.item.entity.EntityType type : com.mckuai.mctools.item.entity.EntityType.values()) {
			idMap.put(type.getId(), type);
			if (type.getEntityClass() != null) {
				classMap.put(type.getEntityClass(), type);
			}
		}
	}
}
