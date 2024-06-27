package com.quinbay.inventory.service;

import com.quinbay.inventory.DTO.ProductHistoryDTO;
import com.quinbay.inventory.model.ProductHistory;
import java.util.List;

public interface ProductHistoryService {

    List<ProductHistory> getProductHistoryByProductId(Long productId);

    ProductHistory addProductHistoryEntry(ProductHistoryDTO productHistoryDTO);
}