package com.mckuai.item;

import com.mckuai.entity.EntityType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kyly on 2015/7/10.
 */
public class DataItem  implements Serializable{
    private Integer count;
    private EntityType entityType;
    private CharSequence name;
    private Integer tag;

    public Integer getCount()
    {
        return this.count;
    }

    public List<Map<String, String>> getData(List<DataItem> paramList)
    {
        ArrayList localArrayList = new ArrayList();
        int i = 0;
        while (i < paramList.size())
        {
            DataItem localDataItem = (DataItem)paramList.get(i);
            HashMap localHashMap = new HashMap();
            localHashMap.put("name", localDataItem.getName().toString());
            localHashMap.put("count", localDataItem.getCount().toString());
            localHashMap.put("tag", localDataItem.getTag().toString());
            localArrayList.add(localHashMap);
            i += 1;
        }
        return localArrayList;
    }

    public EntityType getEntityType()
    {
        return this.entityType;
    }

    public CharSequence getName()
    {
        return this.name;
    }

    public Integer getTag()
    {
        return this.tag;
    }

    public void setCount(Integer paramInteger)
    {
        this.count = paramInteger;
    }

    public void setEntityType(EntityType paramEntityType)
    {
        this.entityType = paramEntityType;
    }

    public void setName(CharSequence paramCharSequence)
    {
        this.name = paramCharSequence;
    }

    public void setTag(Integer paramInteger)
    {
        this.tag = paramInteger;
    }
}
