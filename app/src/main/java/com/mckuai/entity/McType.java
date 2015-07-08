package com.mckuai.entity;

import java.io.Serializable;

/**
 * Created by kyly on 2015/7/8.
 */
public class McType implements Serializable {
    private static final long serialVersionUID = 1427180648302L;
    private Integer baseTypeId;
    private String createTime;
    private Integer enable;
    private Integer id;
    private Integer level = Integer.valueOf(0);
    private Integer parentTypeId;
    private String typeCode;
    private String typeName;
    private Integer typeOrder;

    public McType() {}

    public McType(Integer paramInteger)
    {
        this.id = paramInteger;
    }

    public Integer getBaseTypeId()
    {
        return this.baseTypeId;
    }

    public String getCreateTime()
    {
        return this.createTime;
    }

    public Integer getEnable()
    {
        return this.enable;
    }

    public Integer getId()
    {
        return this.id;
    }

    public Integer getLevel()
    {
        return this.level;
    }

    public Integer getParentTypeId()
    {
        return this.parentTypeId;
    }

    public String getTypeCode()
    {
        return this.typeCode;
    }

    public String getTypeName()
    {
        return this.typeName;
    }

    public Integer getTypeOrder()
    {
        return this.typeOrder;
    }

    public void setBaseTypeId(Integer paramInteger)
    {
        this.baseTypeId = paramInteger;
    }

    public void setCreateTime(String paramString)
    {
        this.createTime = paramString;
    }

    public void setEnable(Integer paramInteger)
    {
        this.enable = paramInteger;
    }

    public void setId(Integer paramInteger)
    {
        this.id = paramInteger;
    }

    public void setLevel(Integer paramInteger)
    {
        this.level = paramInteger;
    }

    public void setParentTypeId(Integer paramInteger)
    {
        this.parentTypeId = paramInteger;
    }

    public void setTypeCode(String paramString)
    {
        this.typeCode = paramString;
    }

    public void setTypeName(String paramString)
    {
        this.typeName = paramString;
    }

    public void setTypeOrder(Integer paramInteger)
    {
        this.typeOrder = paramInteger;
    }
}
