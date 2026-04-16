package com.nalex.rmims.dao;

import com.nalex.rmims.model.Item;
import com.nalex.rmims.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {
    
    // Get all items with category and store names
    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT i.*, c.category_name, s.store_name " +
                     "FROM Items i " +
                     "LEFT JOIN Categories c ON i.category_id = c.category_id " +
                     "LEFT JOIN Stores s ON i.preferred_store_id = s.store_id " +
                     "ORDER BY i.item_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Item item = new Item();
                item.setItemId(rs.getInt("item_id"));
                item.setItemName(rs.getString("item_name"));
                item.setCategoryId(rs.getInt("category_id"));
                item.setCategoryName(rs.getString("category_name"));
                item.setUnitOfMeasure(rs.getString("unit_of_measure"));
                item.setCurrentQuantity(rs.getInt("current_quantity"));
                item.setMinimumLevel(rs.getInt("minimum_level"));
                item.setPreferredStoreId(rs.getInt("preferred_store_id"));
                item.setStoreName(rs.getString("store_name"));
                item.setLocationInSchool(rs.getString("location_in_school"));
                
                items.add(item);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return items;
    }
    
    // Get items below minimum level (low stock)
    public List<Item> getLowStockItems() {
        List<Item> lowStockItems = new ArrayList<>();
        String sql = "SELECT i.*, c.category_name " +
                     "FROM Items i " +
                     "LEFT JOIN Categories c ON i.category_id = c.category_id " +
                     "WHERE i.current_quantity < i.minimum_level " +
                     "ORDER BY (i.current_quantity - i.minimum_level) ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Item item = new Item();
                item.setItemId(rs.getInt("item_id"));
                item.setItemName(rs.getString("item_name"));
                item.setCategoryId(rs.getInt("category_id"));
                item.setCategoryName(rs.getString("category_name"));
                item.setUnitOfMeasure(rs.getString("unit_of_measure"));
                item.setCurrentQuantity(rs.getInt("current_quantity"));
                item.setMinimumLevel(rs.getInt("minimum_level"));
                item.setLocationInSchool(rs.getString("location_in_school"));
                
                lowStockItems.add(item);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return lowStockItems;
    }
    
    // Get item by ID
    public Item getItemById(int itemId) {
        String sql = "SELECT i.*, c.category_name, s.store_name " +
                     "FROM Items i " +
                     "LEFT JOIN Categories c ON i.category_id = c.category_id " +
                     "LEFT JOIN Stores s ON i.preferred_store_id = s.store_id " +
                     "WHERE i.item_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, itemId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Item item = new Item();
                item.setItemId(rs.getInt("item_id"));
                item.setItemName(rs.getString("item_name"));
                item.setCategoryId(rs.getInt("category_id"));
                item.setCategoryName(rs.getString("category_name"));
                item.setUnitOfMeasure(rs.getString("unit_of_measure"));
                item.setCurrentQuantity(rs.getInt("current_quantity"));
                item.setMinimumLevel(rs.getInt("minimum_level"));
                item.setPreferredStoreId(rs.getInt("preferred_store_id"));
                item.setStoreName(rs.getString("store_name"));
                item.setLocationInSchool(rs.getString("location_in_school"));
                return item;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Add new item
    public boolean addItem(Item item) {
        String sql = "INSERT INTO Items (item_name, category_id, unit_of_measure, " +
                     "current_quantity, minimum_level, preferred_store_id, location_in_school) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, item.getItemName());
            
            if (item.getCategoryId() > 0) {
                pstmt.setInt(2, item.getCategoryId());
            } else {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            }
            
            pstmt.setString(3, item.getUnitOfMeasure());
            pstmt.setInt(4, item.getCurrentQuantity());
            pstmt.setInt(5, item.getMinimumLevel());
            
            if (item.getPreferredStoreId() > 0) {
                pstmt.setInt(6, item.getPreferredStoreId());
            } else {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            }
            
            pstmt.setString(7, item.getLocationInSchool());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update item
    public boolean updateItem(Item item) {
        String sql = "UPDATE Items SET item_name = ?, category_id = ?, unit_of_measure = ?, " +
                     "minimum_level = ?, preferred_store_id = ?, location_in_school = ? " +
                     "WHERE item_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, item.getItemName());
            
            if (item.getCategoryId() > 0) {
                pstmt.setInt(2, item.getCategoryId());
            } else {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            }
            
            pstmt.setString(3, item.getUnitOfMeasure());
            pstmt.setInt(4, item.getMinimumLevel());
            
            if (item.getPreferredStoreId() > 0) {
                pstmt.setInt(5, item.getPreferredStoreId());
            } else {
                pstmt.setNull(5, java.sql.Types.INTEGER);
            }
            
            pstmt.setString(6, item.getLocationInSchool());
            pstmt.setInt(7, item.getItemId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update quantity (for withdrawals and receipts)
    public boolean updateQuantity(int itemId, int newQuantity) {
        String sql = "UPDATE Items SET current_quantity = ? WHERE item_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, itemId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete item
    public boolean deleteItem(int itemId) {
        String sql = "DELETE FROM Items WHERE item_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, itemId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Test method
    public static void main(String[] args) {
        ItemDAO dao = new ItemDAO();
        
        // Test getAllItems
        List<Item> items = dao.getAllItems();
        System.out.println("📦 All Items:");
        for (Item item : items) {
            System.out.println("  - " + item.getItemName() + 
                             " (Qty: " + item.getCurrentQuantity() + 
                             ", Min: " + item.getMinimumLevel() + ")");
        }
        
        // Test getLowStockItems
        List<Item> lowStock = dao.getLowStockItems();
        System.out.println("\n⚠️ Low Stock Items:");
        for (Item item : lowStock) {
            System.out.println("  - " + item.getItemName() + 
                             " (Current: " + item.getCurrentQuantity() + 
                             ", Min: " + item.getMinimumLevel() + ")");
        }
        
        DatabaseConnection.closeConnection();
    }
}