package com.mckuai.mctools.io.nbt.entity;

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
import com.mckuai.entity.Sheep;
import com.mckuai.entity.Skeleton;
import com.mckuai.entity.Snowball;
import com.mckuai.entity.Spider;
import com.mckuai.entity.TNTPrimed;
import com.mckuai.entity.Zombie;

import java.util.HashMap;
import java.util.Map;

public class EntityStoreLookupService {

	public static Map<Integer, EntityStore> idMap = new HashMap<Integer, EntityStore>();

	public static EntityStore<Entity> defaultStore = new EntityStore<Entity>();

	public static void addStore(int id, EntityStore store) {
		idMap.put(id, store);
	}

	static {
		addStore(10, new AnimalEntityStore<Chicken>());
		addStore(11, new AnimalEntityStore<Cow>());
		addStore(12, new AnimalEntityStore<Pig>());
		addStore(13, new SheepEntityStore<Sheep>());
		addStore(32, new LivingEntityStore<Zombie>());
		addStore(33, new LivingEntityStore<Creeper>());
		addStore(34, new LivingEntityStore<Skeleton>());
		addStore(35, new LivingEntityStore<Spider>());
		addStore(36, new PigZombieEntityStore<PigZombie>());
		addStore(64, new ItemEntityStore<Item>());
		addStore(65, new TNTPrimedEntityStore<TNTPrimed>());
		addStore(66, new FallingBlockEntityStore<FallingBlock>());
		addStore(80, new ArrowEntityStore<Arrow>());
		addStore(81, new ProjectileEntityStore<Snowball>());
		addStore(82, new ProjectileEntityStore<Egg>());
		addStore(83, new PaintingEntityStore<Painting>());
		addStore(84, new MinecartEntityStore<Minecart>());
	}
}
