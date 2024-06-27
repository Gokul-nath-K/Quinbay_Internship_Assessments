package com.quinbay.inventory.service.implementation;

import com.quinbay.inventory.DTO.ProductHistoryDTO;
import com.quinbay.inventory.DTO.ProductRequestDTO;
import com.quinbay.inventory.DTO.ProductResponseDTO;
import com.quinbay.inventory.exceptions.CategoryException;
import com.quinbay.inventory.exceptions.ProductException;
import com.quinbay.inventory.exceptions.SellerException;
import com.quinbay.inventory.model.Category;
import com.quinbay.inventory.model.Product;
import com.quinbay.inventory.model.Seller;
import com.quinbay.inventory.repository.ProductRepository;
import com.quinbay.inventory.service.CategoryService;
import com.quinbay.inventory.service.ProductHistoryService;
import com.quinbay.inventory.service.ProductService;
import com.quinbay.inventory.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryService categoryService;

    @Autowired
    SellerService sellerService;

    @Autowired
    ProductHistoryService productHistoryService;

    @Override
    public List<ProductResponseDTO> getAllProducts() {

        List<Product> products = productRepository.findAll();
        List<ProductResponseDTO> productResponseDTOList = new ArrayList<>();

        for(Product product :products) {

            productResponseDTOList.add(
              ProductResponseDTO.builder()
                      .id(product.getId())
                      .code(product.getCode())
                      .name(product.getName())
                      .quantity(product.getQuantity())
                      .price(product.getPrice())
                      .categoryCode(product.getCategory().getCode())
                      .sellerId(product.getSeller().getId())
                      .build()
            );
        }

        return productResponseDTOList;
    }

    @Override
    public ProductResponseDTO getByCode(String code) {
        Product product = Optional.ofNullable(productRepository.findByCode(code))
                .orElseThrow(() -> new ProductException("Product with code: " + code + " not found!"));
        return ProductResponseDTO.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .quantity(product.getQuantity())
                .price(product.getPrice())
                .categoryCode(product.getCategory().getCode())
                .sellerId(product.getSeller().getId())
                .build();
    }

    @Override
    public Product getByProductCode(String code) {
        return Optional.ofNullable(productRepository.findByCode(code))
                .orElseThrow(() -> new ProductException("Product with code: " + code + " not found!"));
    }

    @Override
    public Product addNewProduct(ProductRequestDTO productDTO) {

        Category category = Optional.ofNullable(categoryService.getByCode(productDTO.getCategoryCode()))
                .orElseThrow(() -> new CategoryException("Category not exist!"));

        Seller seller = sellerService.getById(productDTO.getSellerId());

        Product product = Product.builder()
                .code(productDTO.getCode())
                .name(productDTO.getName())
                .quantity(productDTO.getQuantity())
                .price(productDTO.getPrice())
                .category(category)
                .seller(seller)
                .productHistories(new ArrayList<>())
                .build();

        return Optional.ofNullable(productRepository.save(product))
                .orElseThrow(() -> new ProductException("Failed to add product!"));
    }

    String message = "";

    @KafkaListener(topics = { "Product" }, containerFactory = "kafkaListenerContainerFactory")
    void listener(String msg) {
        this.message =  msg;
        System.out.println(message);
    }

    @Override
    public Product update(ProductRequestDTO product) {

        if(product == null) throw new ProductException("Product not found!");

        if(product.getQuantity() < 1) throw new ProductException("Quantity cannot be negative");

        if(product.getPrice() < 0) throw new ProductException("Price cannot be less than zero!");

        Product existingProduct = productRepository.findByCode(product.getCode());

        Optional.ofNullable(existingProduct).orElseThrow(() -> new ProductException("Product not found!"));

        ProductHistoryDTO productHistoryDTO = new ProductHistoryDTO();
        productHistoryDTO.setProductCode(product.getCode());

        CompletableFuture<Category> categoryFuture = CompletableFuture.supplyAsync(() ->
                        categoryService.getByCode(product.getCategoryCode()))
                .thenApply(category -> {
                    if (category == null) {
                        throw new CategoryException("Category not found with code: " + product.getCategoryCode());
                    }
                    return category;
                });

        CompletableFuture<Seller> sellerFuture = CompletableFuture.supplyAsync(() ->
                        sellerService.getById(product.getSellerId()))
                .thenApply(seller -> {
                    if (seller == null) {
                        throw new SellerException("Seller not found with id: " + product.getSellerId());
                    }
                    return seller;
                });

        CompletableFuture.allOf(categoryFuture, sellerFuture).join();

        String column = "";

        if (!existingProduct.getName().equals(product.getName())) {
            column = "name";
            productHistoryDTO.setColumn("name");
            productHistoryDTO.setOldValue(existingProduct.getName());
            productHistoryDTO.setNewValue(product.getName());
            existingProduct.setName(product.getName());
        }

        if (!existingProduct.getPrice().equals(product.getPrice())) {
            column = "price";
            productHistoryDTO.setColumn("price");
            productHistoryDTO.setOldValue(String.valueOf(existingProduct.getPrice()));
            productHistoryDTO.setNewValue(String.valueOf(product.getPrice()));
            existingProduct.setPrice(product.getPrice());
        }

        if (!existingProduct.getQuantity().equals(product.getQuantity())) {
            column = "quantity";
            productHistoryDTO.setColumn("quantity");
            productHistoryDTO.setOldValue(String.valueOf(existingProduct.getQuantity()));
            productHistoryDTO.setNewValue(String.valueOf(product.getQuantity()));
            existingProduct.setQuantity(product.getQuantity());
        }

        Category fetchedCategory = categoryFuture.join();
        if (!Objects.equals(existingProduct.getCategory().getCode(), fetchedCategory.getCode())) {
            column = "category_id";
            productHistoryDTO.setColumn("category_id");
            productHistoryDTO.setOldValue(existingProduct.getCategory().getCode());
            productHistoryDTO.setNewValue(product.getCategoryCode());
            existingProduct.setCategory(fetchedCategory);
        }

        Seller fetchedSeller = sellerFuture.join();
        if (!Objects.equals(existingProduct.getSeller().getId(), fetchedSeller.getId())) {
            column = "seller";
            productHistoryDTO.setColumn("seller_id");
            productHistoryDTO.setOldValue(String.valueOf(existingProduct.getSeller().getId()));
            productHistoryDTO.setNewValue(String.valueOf(product.getSellerId()));
            existingProduct.setSeller(fetchedSeller);
        }

        if(column.equals("")) {throw new ProductException("No field updated!");}
        productHistoryService.addProductHistoryEntry(productHistoryDTO);
        productRepository.save(existingProduct);
        return existingProduct;
    }

    @Override
    public boolean deleteProduct(String code) {

        Product product = Optional.ofNullable(getByProductCode(code))
                .orElseThrow(() -> new ProductException("Product not found with code: " + code));
        productRepository.delete(product);
        return true;
    }
}