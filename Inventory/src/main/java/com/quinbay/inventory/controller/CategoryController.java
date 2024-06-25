package com.quinbay.inventory.controller;

import com.quinbay.inventory.model.Category;
import com.quinbay.inventory.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService = new CategoryService();

    @GetMapping("/getAll")
    public List<Category> getAll() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/getCategoryById/{id}")
    public Category getCategoryById(@PathVariable("id") long id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping("/getCategoryByCategoryId/{categoryId}")
    public Category getCategoryByCategoryId(@PathVariable("categoryId") String categoryId) {
        return categoryService.getCategoryByCategoryId(categoryId);
    }

    @PostMapping("/addCategory")
    public String addCategory(@RequestBody Category category) {
        return categoryService.addCategory(category) ? "Category added successfully" : "Failed to add category";
    }

    @PutMapping("/updateCategory/{id}")
    public String updateCategory(@PathVariable("id") String id, @RequestBody Category category) {

        return categoryService.updateCategory(id, category);
    }

    @DeleteMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable("id") long id) {
        return categoryService.deleteCategory(id) ? "Deleted successfully" : "Deletion failed!";
    }
}
