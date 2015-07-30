package com.mckuai.mctools.item.tileentity;


import com.mckuai.mctools.InventorySlot;

import java.util.List;

public class ContainerTileEntity extends TileEntity {

	private List<InventorySlot> items;

	public List<InventorySlot> getItems() {
		return items;
	}

	public void setItems(List<InventorySlot> items) {
		this.items = items;
	}

	/** Get the maximum number of InventorySlots this container can hold. */
	public int getContainerSize() {
		return 27;
	}
}
