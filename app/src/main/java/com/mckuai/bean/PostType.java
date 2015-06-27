package com.mckuai.bean;

import java.io.Serializable;

public class PostType implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8233251868432642149L;
	/**
	 * 版块包含的帖子类型
	 */
	private int id;
	private int smallId;
	private String smallName;
	
	
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public int getSmallId()
	{
		return smallId;
	}
	public void setSmallId(int smallId)
	{
		this.smallId = smallId;
	}
	public String getSmallName()
	{
		return smallName;
	}
	public void setSmallName(String smallName)
	{
		this.smallName = smallName;
	}
}
