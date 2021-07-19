package com.something.vendorapp.model;

public class CategoryHelper {

    String categoryName, categoryImage, categoryKey;

    public CategoryHelper(){}

    public CategoryHelper(String categoryName, String categoryImage, String categoryKey) {
        this.categoryName = categoryName;
        this.categoryImage = categoryImage;
        this.categoryKey = categoryKey;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public String getCategoryKey() {
        return categoryKey;
    }

    public void setCategoryKey(String categoryKey) {
        this.categoryKey = categoryKey;
    }
}
