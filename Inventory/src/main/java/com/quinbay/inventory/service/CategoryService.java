package com.quinbay.inventory.service;

import com.quinbay.inventory.DTO.CategoryRequestDTO;
import com.quinbay.inventory.DTO.ProductResponseDTO;
import com.quinbay.inventory.model.Category;
import com.quinbay.inventory.model.Product;

import java.util.List;

public interface CategoryService {

    public List<Category> getAll();

    public Category getByCode(String code);

    public Category addNewCategory(CategoryRequestDTO category);

    public List<ProductResponseDTO> getProducts(String categoryCode);
}
