package com.mckuai.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Zzz on 2015/6/25.
 */
public class MapBean implements Serializable {
    ArrayList<Map> data = new ArrayList<>(10);
    PageInfo pageBean = new PageInfo();

    public ArrayList<Map> getData() {
        return data;
    }

    public void setData(ArrayList<Map> data) {
        this.data = data;
    }

    public PageInfo getPageBean() {
        return pageBean;
    }

    public void setPageBean(PageInfo pageBean) {
        this.pageBean = pageBean;
    }
}
