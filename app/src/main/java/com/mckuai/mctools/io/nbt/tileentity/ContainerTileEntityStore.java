package com.mckuai.mctools.io.nbt.tileentity;

import com.mckuai.mctools.io.nbt.NBTConverter;
import com.mckuai.mctools.item.tileentity.ContainerTileEntity;

import java.util.List;

import org.spout.nbt.*;


public class ContainerTileEntityStore<T extends ContainerTileEntity> extends TileEntityStore<T> {

	@Override
	@SuppressWarnings("unchecked")
	public void loadTag(T entity, Tag tag) {
		if (tag.getName().equals("Items")) {
			entity.setItems(NBTConverter.readInventory((ListTag<CompoundTag>) tag));
		} else {
			super.loadTag(entity, tag);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Tag> save(T entity) {
		List<Tag> tags = super.save(entity);
		tags.add(NBTConverter.writeInventory(entity.getItems(), "Items"));
		return tags;
	}
}
