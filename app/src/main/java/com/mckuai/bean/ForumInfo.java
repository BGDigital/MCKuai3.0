package com.mckuai.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class ForumInfo implements Serializable
{
	private static final long serialVersionUID = 3311829041014984013L;
	private int id;
	private int talkNum;
	private String name;
	private String includeTypeId;
	private String icon;
	private ArrayList<PostType> includeType;
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public int getTalkNum()
	{
		return talkNum;
	}
	public void setTalkNum(int talkNum)
	{
		this.talkNum = talkNum;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getIncludeTypeId()
	{
		return includeTypeId;
	}
	public void setIncludeTypeId(String includeTypeId)
	{
		this.includeTypeId = includeTypeId;
	}
	public ArrayList<PostType> getIncludeType()
	{
		return includeType;
	}
	public void setIncludeType(ArrayList<PostType> includeType)
	{
		this.includeType = includeType;
	}
	public String getIcon()
	{
		return icon;
	}
	public void setIcon(String icon)
	{
		this.icon = icon;
	}
}
