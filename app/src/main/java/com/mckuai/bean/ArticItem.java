package com.mckuai.bean;

/**
 * Created by kyly on 2015/6/25.
 * 物品
 */
public class ArticItem {
    private int id;
    private int categoryId;
    private int subCategoryId;
    private String name;

    public void setId(int id) {
        this.id = id;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setSubCategoryId(int subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;

    public int getId() {
        return id;
    }

    public String getIdEx(){
        return categoryId+":"+subCategoryId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getSubCategoryId() {
        return subCategoryId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
