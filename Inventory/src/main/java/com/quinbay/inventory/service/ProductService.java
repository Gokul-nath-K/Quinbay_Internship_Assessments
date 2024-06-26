package com.quinbay.inventory.service;

import com.quinbay.inventory.DTO.ProductRequestDTO;
import com.quinbay.inventory.config.DataSourceConfig;
import com.quinbay.inventory.exceptions.ProductNotFoundException;
import com.quinbay.inventory.model.Category;
import com.quinbay.inventory.model.Product;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    CategoryService categoryService = new CategoryService();

    @Autowired
    private DataSourceConfig dataSourceConfig;

    Connection conn;

    @PostConstruct
    void Initializer() throws SQLException {

        conn = dataSourceConfig.connection(dataSourceConfig.dataSource());
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM Product";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getLong("id"));
                product.setCode(rs.getString("productId"));
                product.setName(rs.getString("productName"));
                product.setPrice(rs.getDouble("productPrice"));
                product.setQuantity(rs.getLong("productQuantity"));
                product.setCategory(categoryService.getCategoryById(rs.getLong("categoryId")));
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public Product getProductByCode(String id) {
        Product product = null;
        String query = "SELECT * FROM Product WHERE productid = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    product = new Product();
                    product.setId(rs.getLong("id"));
                    product.setCode(rs.getString("productId"));
                    product.setName(rs.getString("productName"));
                    product.setPrice(rs.getDouble("productPrice"));
                    product.setQuantity(rs.getLong("productQuantity"));
                    product.setCategory(categoryService.getCategoryById(rs.getLong("categoryid")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (product == null)
            throw new ProductNotFoundException("Product with id " + id + " not found");
        else
            return product;
    }

    public String addProduct(ProductRequestDTO newProduct) {

        Category category = categoryService.getCategoryByCategoryId(newProduct.getCategoryId());
        if (category == null) {
            return "No category found! Please add new category first";
        }
        if(newProduct.getProductPrice() <= 0) {

            return "Price cannot be less than or equal to zero!";
        }
        Product product = Product.builder()
                .code(newProduct.getProductId())
                .name(newProduct.getProductName())
                .price(newProduct.getProductPrice())
                .quantity(newProduct.getProductQuantity())
                .category(category)
                .build();

        String query = "INSERT INTO Product (productId, productName, productPrice, productQuantity, categoryId) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, product.getCode());
            pstmt.setString(2, product.getName());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setLong(4, product.getQuantity());
            pstmt.setLong(5, product.getCategory().getId());

            return pstmt.executeUpdate() > 0 ? "Product added successfully" : "Failed to add product";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Failed to add product";
    }

    public boolean updateProduct(Product product) {
        String query = "UPDATE Product SET productId = ?, productName = ?, productPrice = ?, productQuantity = ?, categoryId = ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, product.getCode());
            pstmt.setString(2, product.getName());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setLong(4, product.getQuantity());
            pstmt.setLong(5, product.getCategory().getId());
            pstmt.setLong(6, product.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteProduct(long id) {
        String query = "DELETE FROM Product WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
