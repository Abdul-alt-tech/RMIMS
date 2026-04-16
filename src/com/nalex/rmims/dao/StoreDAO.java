package com.nalex.rmims.dao;

import com.nalex.rmims.model.Store;
import com.nalex.rmims.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StoreDAO {
    
    // Get all stores
    public List<Store> getAllStores() {
        List<Store> stores = new ArrayList<>();
        String sql = "SELECT * FROM Stores ORDER BY store_name";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("StoreDAO.getAllStores: DatabaseConnection.getConnection() returned null");
                return stores;
            }
            System.err.println("StoreDAO.getAllStores: Executing SQL -> " + sql);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            int rowCount = 0;
            while (rs.next()) {
                Store store = new Store();
                store.setStoreId(rs.getInt("store_id"));
                store.setStoreName(rs.getString("store_name"));
                store.setLocation(rs.getString("location"));
                store.setContactPerson(rs.getString("contact_person"));
                store.setPhone(rs.getString("phone"));
                store.setNotes(rs.getString("notes"));
                stores.add(store);
                rowCount++;
            }
            System.err.println("StoreDAO.getAllStores: Rows fetched = " + rowCount);

        } catch (SQLException e) {
            System.err.println("StoreDAO.getAllStores: SQLException -> " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState() + " ErrorCode: " + e.getErrorCode());
            e.printStackTrace();
        } catch (Exception ex) {
            System.err.println("StoreDAO.getAllStores: Unexpected error -> " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        }
        
        return stores;
    }
    
    // Get store by ID
    public Store getStoreById(int storeId) {
        String sql = "SELECT * FROM Stores WHERE store_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, storeId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Store store = new Store();
                store.setStoreId(rs.getInt("store_id"));
                store.setStoreName(rs.getString("store_name"));
                store.setLocation(rs.getString("location"));
                store.setContactPerson(rs.getString("contact_person"));
                store.setPhone(rs.getString("phone"));
                store.setNotes(rs.getString("notes"));
                return store;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Add store
    public boolean addStore(Store store) {
        String sql = "INSERT INTO Stores (store_name, location, contact_person, phone, notes) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, store.getStoreName());
            pstmt.setString(2, store.getLocation());
            pstmt.setString(3, store.getContactPerson());
            pstmt.setString(4, store.getPhone());
            pstmt.setString(5, store.getNotes());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update store
    public boolean updateStore(Store store) {
        String sql = "UPDATE Stores SET store_name = ?, location = ?, contact_person = ?, " +
                     "phone = ?, notes = ? WHERE store_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, store.getStoreName());
            pstmt.setString(2, store.getLocation());
            pstmt.setString(3, store.getContactPerson());
            pstmt.setString(4, store.getPhone());
            pstmt.setString(5, store.getNotes());
            pstmt.setInt(6, store.getStoreId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete store
    public boolean deleteStore(int storeId) {
        String sql = "DELETE FROM Stores WHERE store_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, storeId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Test method
    public static void main(String[] args) {
        StoreDAO dao = new StoreDAO();
        List<Store> stores = dao.getAllStores();
        
        System.out.println("🏪 Stores:");
        for (Store s : stores) {
            System.out.println("  - " + s.getStoreName() + " (" + s.getLocation() + ")");
        }
        
        DatabaseConnection.closeConnection();
    }
}