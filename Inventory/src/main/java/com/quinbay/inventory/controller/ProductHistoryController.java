package com.quinbay.inventory.controller;


import com.quinbay.inventory.DTO.ProductHistoryDTO;
import com.quinbay.inventory.model.ProductHistory;
import com.quinbay.inventory.service.ProductHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product-history")
public class ProductHistoryController {

    @Autowired
    private ProductHistoryService productHistoryService;


    @GetMapping("/{productId}")
    public ResponseEntity<List<ProductHistory>> getProductHistoriesByProductCode(@PathVariable Long productId) {
        List<ProductHistory> productHistories = productHistoryService.getProductHistoryByProductId(productId);
        return ResponseEntity.ok(productHistories);
    }

    @PostMapping
    public ResponseEntity<ProductHistory> addProductHistory(@RequestBody ProductHistoryDTO productHistory) {
        ProductHistory createdProductHistory = productHistoryService.addProductHistoryEntry(productHistory);
        return ResponseEntity.ok(createdProductHistory);
    }
}