package com.quinbay.inventory.service;

import com.quinbay.inventory.DTO.ProductRequestDTO;
import com.quinbay.inventory.model.Category;
import com.quinbay.inventory.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductService {

    private final String url = "jdbc:postgresql://localhost:5432/e_commerce";
    private final String user = "gokulnathk";
    private final String password = "password";

    CategoryService categoryService = new CategoryService();

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM Product";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
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

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

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
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

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
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

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

    public boolean updateProductPrice(String id, double newPrice) {

        if (newPrice < 0) {
            throw new IllegalArgumentException("Product price cannot be negative");
        }

        Product existingProduct = getProductByCode(id);
        if (existingProduct == null) {
            return false;
        }

        String query = "UPDATE Product SET productPrice = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setDouble(1, newPrice);
            pstmt.setString(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean updateProductQuantity(Product newProduct) {

        if (newProduct.getQuantity() < 0) {
            throw new IllegalArgumentException("Product quantity cannot be less than zero");
        }

        Product existingProduct = getProductByCode(newProduct.getCode());
        if (existingProduct == null) {
            return false;
        }

        String query = "UPDATE Product SET productQuantity = ? WHERE productid = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, newProduct.getQuantity());
            pstmt.setString(2, newProduct.getCode());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateProductQuantity(String id, long quantity) {

        if (quantity < 0) {
            throw new IllegalArgumentException("Product quantity cannot be less than zero");
        }

        Product existingProduct = getProductByCode(id);
        if (existingProduct == null) {
            return false;
        }

        String query = "UPDATE Product SET productQuantity = ? WHERE productid = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, quantity);
            pstmt.setString(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteProduct(long id) {
        String query = "DELETE FROM Product WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
