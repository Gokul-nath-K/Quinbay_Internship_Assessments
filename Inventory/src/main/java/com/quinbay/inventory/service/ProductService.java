package com.quinbay.inventory.service;

import com.quinbay.inventory.DTO.ProductRequestDTO;
import com.quinbay.inventory.DTO.ProductResponseDTO;
import com.quinbay.inventory.model.Product;
import java.util.List;

public interface ProductService {

    public List<ProductResponseDTO> getAllProducts();

    public ProductResponseDTO getByCode(String code);

    public Product getByProductCode(String code);

    public Product addNewProduct(ProductRequestDTO product);

    public Product update(ProductRequestDTO product);

    boolean deleteProduct(String code);

}