package com.mckuai.bean;

import java.io.Serializable;

/**
 * Created by Zzz on 2015/6/24.
 */
public class MapBean implements Serializable{
    private String name;//地图名
    private String style;//地图类型
    private String time;//更新时间
    private int size;//地图大小
    public String getName(){
        return name;
    }
    public void setName(String name){
       this.name=name;
    }
    public String getStyle(){
        return style;
    }
    public void setStyle(String style){
        this.style=style;
    }
    public String getTimt(){
        return time;
    }
    public void setTime(String time){
        this.time=time;
    }
    public int getSize(){
       return size;
    }
    public void setSize(int size){
        this.size=size;
    }
}
