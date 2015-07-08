package com.mckuai.material.icon;

import android.graphics.Bitmap;

import com.mckuai.material.MaterialKey;

import java.util.Map;

public class MaterialIcon {

	public static Map<MaterialKey, MaterialIcon> icons;

	public int typeId;

	public short damage;

	public Bitmap bitmap;

	public MaterialIcon(int typeId, short damage, Bitmap bmp) {
		this.typeId = typeId;
		this.damage = damage;
		this.bitmap = bmp;
	}
}
