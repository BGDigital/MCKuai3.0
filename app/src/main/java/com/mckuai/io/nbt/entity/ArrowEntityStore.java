package com.mckuai.io.nbt.entity;

import com.mckuai.mctools.item.entity.Arrow;

import java.util.List;

import org.spout.nbt.*;

public class ArrowEntityStore<T extends Arrow> extends ProjectileEntityStore<T> {

	@Override
	@SuppressWarnings("unchecked")
	public void loadTag(T entity, Tag tag) {
		String name = tag.getName();
		if (name.equals("inData")) {
			entity.setInData(((ByteTag) tag).getValue());
		} else if (name.equals("player")) {
			entity.setShotByPlayer(((ByteTag) tag).getValue() != 0);
		} else {
			super.loadTag(entity, tag);
		}
	}

	@Override
	public List<Tag> save(T entity) {
		List<Tag> tags = super.save(entity);
		tags.add(new ByteTag("inData", entity.getInData()));
		tags.add(new ByteTag("player", entity.isShotByPlayer()? (byte) 1: (byte) 0));
		return tags;
	}
}

