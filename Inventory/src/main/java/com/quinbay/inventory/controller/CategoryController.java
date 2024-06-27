package com.quinbay.inventory.controller;

import com.quinbay.inventory.DTO.CategoryRequestDTO;
import com.quinbay.inventory.DTO.ProductResponseDTO;
import com.quinbay.inventory.exceptions.CategoryException;
import com.quinbay.inventory.model.Category;
import com.quinbay.inventory.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @GetMapping("/getAll")
    public List<Category> getAll() {
        return categoryService.getAll();
    }


    @GetMapping("/getByCode")
    public Category getCategoryById(@RequestParam(value = "code") String code) {
        return Optional.ofNullable(categoryService.getByCode(code))
                .orElseThrow(() -> new CategoryException("Category not found with code: " + code));
    }


    @GetMapping("/{code}/getProducts")
    public List<ProductResponseDTO> getProduct(@PathVariable("code") String categoryCode) {

        return categoryService.getProducts(categoryCode);
    }

    @PostMapping("/addCategory")
    public Category addCategory(@RequestBody CategoryRequestDTO category) {
        return categoryService.addNewCategory(category);
    }
}
