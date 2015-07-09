package com.mckuai.item;

import com.mckuai.entity.EntityType;

import java.io.Serializable;

/**
 * Created by kyly on 2015/7/10.
 */
public class AnimalDataItem implements Serializable {
    private EntityType animalType;
    private boolean checked;
    private String name;

    public EntityType getAnimalType()
    {
        return this.animalType;
    }

    public String getName()
    {
        return this.name;
    }

    public boolean isChecked()
    {
        return this.checked;
    }

    public void setAnimalType(EntityType paramEntityType)
    {
        this.animalType = paramEntityType;
    }

    public void setChecked(boolean paramBoolean)
    {
        this.checked = paramBoolean;
    }

    public void setName(String paramString)
    {
        this.name = paramString;
    }
}
