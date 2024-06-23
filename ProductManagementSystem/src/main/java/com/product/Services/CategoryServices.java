package com.product.Services;

import com.product.Entity.Category;

import java.util.List;

public interface CategoryServices {

    public List<Category> getAllCategories();

    public Category getCategoryById(long id);

    public void addCategory(Category category);
}
