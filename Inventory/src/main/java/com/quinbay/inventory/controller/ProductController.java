package com.quinbay.inventory.controller;

import com.quinbay.inventory.DTO.ProductRequestDTO;
import com.quinbay.inventory.DTO.ProductUpdateRequestDTO;
import com.quinbay.inventory.model.Product;
import com.quinbay.inventory.service.ProductService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService = new ProductService();

    @GetMapping("/getAll")
    public List<Product> getAll() {
        return productService.getAllProducts();
    }

    @GetMapping("/getProductById/{id}")
    public Product getProductById(@PathVariable("id") String productId) {
        return productService.getProductByCode(productId);
    }

    @PostMapping("/postProduct")
    public String addNewProduct(@RequestBody ProductRequestDTO product) {

        return productService.addProduct(product);
    }

    @PutMapping("/updateById/{id}")
    public Product updateProductNameById(@RequestBody Product updateProduct) {

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


    @DeleteMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable("id") long productId) {
        return productService.deleteProduct(productId) ? "Deleted successfully" : "Deletion failed!";
    }
}