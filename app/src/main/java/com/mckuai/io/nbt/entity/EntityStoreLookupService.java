package com.mckuai.io.nbt.entity;

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
import com.mckuai.mctools.item.entity.Sheep;
import com.mckuai.mctools.item.entity.Skeleton;
import com.mckuai.mctools.item.entity.Snowball;
import com.mckuai.mctools.item.entity.Spider;
import com.mckuai.mctools.item.entity.TNTPrimed;
import com.mckuai.mctools.item.entity.Zombie;

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
