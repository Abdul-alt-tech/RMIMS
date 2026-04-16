package com.nalex.rmims.dao;

import com.nalex.rmims.model.Category;
import com.nalex.rmims.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    
    // Get all categories
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM Categories ORDER BY category_name";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("CategoryDAO.getAllCategories: DatabaseConnection.getConnection() returned null");
                return categories;
            }
            System.err.println("CategoryDAO.getAllCategories: Executing SQL -> " + sql);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            int rowCount = 0;
            while (rs.next()) {
                Category category = new Category();
                category.setCategoryId(rs.getInt("category_id"));
                category.setCategoryName(rs.getString("category_name"));
                categories.add(category);
                rowCount++;
            }
            System.err.println("CategoryDAO.getAllCategories: Rows fetched = " + rowCount);

        } catch (SQLException e) {
            System.err.println("CategoryDAO.getAllCategories: SQLException -> " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState() + " ErrorCode: " + e.getErrorCode());
            e.printStackTrace();
        } catch (Exception ex) {
            System.err.println("CategoryDAO.getAllCategories: Unexpected error -> " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
            // Do not close conn here; DatabaseConnection manages it if necessary
        }
        
        return categories;
    }
    
    // Get category by ID
    public Category getCategoryById(int categoryId) {
        String sql = "SELECT * FROM Categories WHERE category_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, categoryId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Category category = new Category();
                category.setCategoryId(rs.getInt("category_id"));
                category.setCategoryName(rs.getString("category_name"));
                return category;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Add category
    public boolean addCategory(Category category) {
        String sql = "INSERT INTO Categories (category_name) VALUES (?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category.getCategoryName());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update category
    public boolean updateCategory(Category category) {
        String sql = "UPDATE Categories SET category_name = ? WHERE category_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category.getCategoryName());
            pstmt.setInt(2, category.getCategoryId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete category
    public boolean deleteCategory(int categoryId) {
        String sql = "DELETE FROM Categories WHERE category_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, categoryId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Test method
    public static void main(String[] args) {
        CategoryDAO dao = new CategoryDAO();
        List<Category> categories = dao.getAllCategories();
        
        System.out.println("📋 Categories:");
        for (Category c : categories) {
            System.out.println("  - " + c.getCategoryName());
        }
        
        DatabaseConnection.closeConnection();
    }
}