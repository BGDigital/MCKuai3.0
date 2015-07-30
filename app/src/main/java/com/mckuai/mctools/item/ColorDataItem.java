package com.mckuai.mctools.item;

/**
 * Created by kyly on 2015/7/10.
 */
public class ColorDataItem {
    private String a;
    private String b;
    private Integer c;
    private Integer d;
    private boolean e;

    public String getColor()
    {
        return this.b;
    }

    public Integer getColorId()
    {
        return this.c;
    }

    public String getColorName()
    {
        return this.a;
    }

    public Integer getId()
    {
        return this.d;
    }

    public boolean isChecked()
    {
        return this.e;
    }

    public void setChecked(boolean paramBoolean)
    {
        this.e = paramBoolean;
    }

    public void setColor(String paramString)
    {
        this.b = paramString;
    }

    public void setColorId(Integer paramInteger)
    {
        this.c = paramInteger;
    }

    public void setColorName(String paramString)
    {
        this.a = paramString;
    }

    public void setId(Integer paramInteger)
    {
        this.d = paramInteger;
    }
}
