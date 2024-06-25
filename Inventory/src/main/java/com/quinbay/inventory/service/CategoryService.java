package com.quinbay.inventory.service;

import com.quinbay.inventory.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryService {

    private final String url = "jdbc:postgresql://localhost:5432/e_commerce";
    private final String user = "gokulnathk";
    private final String password = "password";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT * FROM Category";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getLong("id"));
                category.setCategoryId(rs.getString("categoryId"));
                category.setCategoryName(rs.getString("categoryName"));
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    public Category getCategoryById(long id) {
        Category category = null;
        String query = "SELECT * FROM Category WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    category = new Category();
                    category.setId(rs.getLong("id"));
                    category.setCategoryId(rs.getString("categoryId"));
                    category.setCategoryName(rs.getString("categoryName"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return category;
    }

    public Category getCategoryByCategoryId(String categoryId) {
        Category category = null;
        String query = "SELECT * FROM Category WHERE categoryId = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, categoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    category = new Category();
                    category.setId(rs.getLong("id"));
                    category.setCategoryId(rs.getString("categoryId"));
                    category.setCategoryName(rs.getString("categoryName"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return category;
    }

    public boolean addCategory(Category category) {
        String query = "INSERT INTO Category (categoryId, categoryName) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, category.getCategoryId());
            pstmt.setString(2, category.getCategoryName());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String updateCategory(String id, Category category) {

        Category existingCategory = getCategoryByCategoryId(id);

        if (existingCategory != null) {
            existingCategory.setCategoryId(category.getCategoryId());
            existingCategory.setCategoryName(category.getCategoryName());
        }
        else {
            return "Category not found";
        }

        String query = "UPDATE Category SET categoryId = ?, categoryName = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, category.getCategoryId());
            pstmt.setString(2, category.getCategoryName());
            pstmt.setLong(3, category.getId());

            return pstmt.executeUpdate() > 0 ? "Category updated successfully" : "Failed to update category";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Failed to update category";
    }

    public boolean deleteCategory(long id) {
        String query = "DELETE FROM Category WHERE id = ?";
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
