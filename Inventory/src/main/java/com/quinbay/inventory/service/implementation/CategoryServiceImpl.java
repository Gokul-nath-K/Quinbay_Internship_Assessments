package com.quinbay.inventory.service.implementation;

import com.quinbay.inventory.DTO.CategoryRequestDTO;
import com.quinbay.inventory.DTO.ProductResponseDTO;
import com.quinbay.inventory.exceptions.CategoryException;
import com.quinbay.inventory.model.Category;
import com.quinbay.inventory.model.Product;
import com.quinbay.inventory.repository.CategoryRepository;
import com.quinbay.inventory.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getByCode(String code) {
        return categoryRepository.findByCode(code);
    }

    @Override
    public Category addNewCategory(CategoryRequestDTO categoryDTO) {

        if(getByCode(categoryDTO.getCode()) != null) throw new CategoryException("Category already exist!");

        Category category = Category.builder()
                .name(categoryDTO.getName())
                .code(categoryDTO.getCode())
                .products(new ArrayList<>())
                .build();
        return Optional.ofNullable(categoryRepository.save(category))
                .orElseThrow(() -> new CategoryException("Unable add new category!"));
    }

    @Override
    public List<ProductResponseDTO> getProducts(String categoryCode) {

        Category category = getByCode(categoryCode);

        List<Product> products = category.getProducts();
        List<ProductResponseDTO> productResponseDTOList = new ArrayList<>();
        for(Product product : products) {

            productResponseDTOList.add(ProductResponseDTO.builder()
                    .id(product.getId())
                    .code(product.getCode())
                    .name(product.getName())
                    .quantity(product.getQuantity())
                    .price(product.getPrice())
                    .categoryCode(product.getCategory().getCode())
                    .sellerId(product.getSeller().getId())
                    .build());
        }
        return productResponseDTOList;
    }
}
