package com.mckuai.io.nbt.entity;

import com.mckuai.entity.Minecart;

import java.util.List;

import org.spout.nbt.*;

public class MinecartEntityStore<T extends Minecart> extends EntityStore<T> {

	@Override
	@SuppressWarnings("unchecked")
	public void loadTag(T entity, Tag tag) {
		//if (tag.getName().equals("Fuse")) {
		//	entity.setFuseTicks(((ByteTag) tag).getValue());
		//} else {
			super.loadTag(entity, tag);
		//}
	}

	@Override
	public List<Tag> save(T entity) {
		List<Tag> tags = super.save(entity);
		//tags.add(new ByteTag("Fuse", entity.getFuseTicks()));
		return tags;
	}
}
