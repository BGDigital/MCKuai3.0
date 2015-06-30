package com.mckuai.bean;

import java.util.ArrayList;

/**
 * Created by kyly on 2015/6/29.
 */
public class ServerBean {
    ArrayList<GameServerInfo> data;
    PageInfo pageBean;

    public ArrayList<GameServerInfo> getData() {
        return data;
    }

    public void setData(ArrayList<GameServerInfo> data) {
        this.data = data;
    }

    public PageInfo getPageBean() {
        return pageBean;
    }

    public void setPageBean(PageInfo pageBean) {
        this.pageBean = pageBean;
    }
}
