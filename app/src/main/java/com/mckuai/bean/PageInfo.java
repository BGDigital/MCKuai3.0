package com.mckuai.bean;

import java.io.Serializable;

/**
 * Created by Zzz on 2015/6/25.
 */
public class PageInfo implements Serializable{
    private int allCount;
    private int page;
    private int pageCount;
    private int pageSize;

    public int getAllCount() {
        return allCount;
    }

    public void setAllCount(int allCount) {
        this.allCount = allCount;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
