package com.quinbay.inventory.controller;

import com.quinbay.inventory.DTO.ProductRequestDTO;
import com.quinbay.inventory.model.Product;
import com.quinbay.inventory.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/getAll")
    public List<Product> getAll() {
        return productService.getAllProducts();
    }

    @GetMapping("/getByCode/{id}")
    public Product getProductByCode(@PathVariable("id") String productId) {
        return productService.getProductByCode(productId);
    }

    @PostMapping("/add")
    public String addNewProduct(@RequestBody ProductRequestDTO product) {

        return productService.addProduct(product);
    }

    @PutMapping("/updateById/{id}")
    public Product updateById(@RequestBody Product updateProduct) {

        Product existingProduct = productService.getProductByCode(updateProduct.getCode());
        if (existingProduct != null) {
            existingProduct.setId(updateProduct.getId());
            existingProduct.setCode(updateProduct.getCode());
            existingProduct.setName(updateProduct.getName());
            existingProduct.setPrice(updateProduct.getPrice());
            existingProduct.setCategory(updateProduct.getCategory());
            productService.updateProduct(existingProduct);
            return existingProduct;
        }
        return null;
    }

    @DeleteMapping("/deleteById/{id}")
    public String deleteProduct(@PathVariable("id") long productId) {
        return productService.deleteProduct(productId) ? "Deleted successfully" : "Deletion failed!";
    }
}