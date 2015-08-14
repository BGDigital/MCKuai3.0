package com.mckuai.bean;

import java.util.ArrayList;

/**
 * Created by kyly on 2015/8/14.
 */
public class SkinBean {
    private ArrayList<SkinItem> data;
    private PageInfo pageBean;

    public ArrayList<SkinItem> getData() {
        return data;
    }

    public void setData(ArrayList<SkinItem> data) {
        this.data = data;
    }

    public PageInfo getPageBean() {
        return pageBean;
    }

    public void setPageBean(PageInfo pageBean) {
        this.pageBean = pageBean;
    }
}
