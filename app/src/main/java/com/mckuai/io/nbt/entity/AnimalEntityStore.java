package com.mckuai.io.nbt.entity;

import com.mckuai.entity.Animal;
import java.util.List;

import org.spout.nbt.*;


public class AnimalEntityStore<T extends Animal> extends LivingEntityStore<T> {

	@Override
	@SuppressWarnings("unchecked")
	public void loadTag(T entity, Tag tag) {
		if (tag.getName().equals("Age")) {
			entity.setAge(((IntTag) tag).getValue());
		} else if (tag.getName().equals("InLove")) {
			entity.setInLove(((IntTag) tag).getValue());
		} else {
			super.loadTag(entity, tag);
		}
	}

	@Override
	public List<Tag> save(T entity) {
		List<Tag> tags = super.save(entity);
		tags.add(new IntTag("Age", entity.getAge()));
		tags.add(new IntTag("InLove", entity.getInLove()));
		return tags;
	}
}
