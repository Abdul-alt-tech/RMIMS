package com.nalex.rmims.dao;

import com.nalex.rmims.model.Item;
import com.nalex.rmims.model.Procurement;
import com.nalex.rmims.model.ProcurementItem;
import com.nalex.rmims.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProcurementItemDAO {
    
    // Add item to procurement (shopping list)
    public boolean addItemToProcurement(int procurementId, int itemId, int quantityRequired) {
        String sql = "INSERT INTO ProcurementItems (procurement_id, item_id, quantity_required, status) " +
                     "VALUES (?, ?, ?, 'To Buy')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, procurementId);
            pstmt.setInt(2, itemId);
            pstmt.setInt(3, quantityRequired);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Add multiple items from low stock list
    public boolean addLowStockItemsToProcurement(int procurementId) {
        ItemDAO itemDAO = new ItemDAO();
        List<Item> lowStockItems = itemDAO.getLowStockItems();
        
        int addedCount = 0;
        boolean allSuccess = true;
        for (Item item : lowStockItems) {
            int quantityNeeded = item.getMinimumLevel() - item.getCurrentQuantity();
            if (quantityNeeded > 0) {
                boolean added = addItemToProcurement(procurementId, item.getItemId(), quantityNeeded);
                if (added) {
                    addedCount++;
                } else {
                    allSuccess = false;
                }
            }
        }
        
        // If no low-stock items exist, do not generate an empty list.
        return addedCount > 0 && allSuccess;
    }
    
    // Get all items in a procurement
    public List<ProcurementItem> getItemsByProcurement(int procurementId) {
        List<ProcurementItem> items = new ArrayList<>();
        String sql = "SELECT pi.*, i.item_name, i.unit_of_measure " +
                     "FROM ProcurementItems pi " +
                     "JOIN Items i ON pi.item_id = i.item_id " +
                     "WHERE pi.procurement_id = ? " +
                     "ORDER BY i.item_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, procurementId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                ProcurementItem pi = new ProcurementItem();
                pi.setProcurementItemId(rs.getInt("procurement_item_id"));
                pi.setProcurementId(rs.getInt("procurement_id"));
                pi.setItemId(rs.getInt("item_id"));
                pi.setItemName(rs.getString("item_name"));
                pi.setQuantityRequired(rs.getInt("quantity_required"));
                pi.setQuantityPurchased(rs.getInt("quantity_purchased"));
                pi.setUnitPrice(rs.getDouble("unit_price"));
                pi.setStatus(rs.getString("status"));
                
                items.add(pi);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return items;
    }
    
    // Get items still "To Buy" in a procurement
    public List<ProcurementItem> getItemsToBuy(int procurementId) {
        List<ProcurementItem> items = new ArrayList<>();
        String sql = "SELECT pi.*, i.item_name, i.unit_of_measure " +
                     "FROM ProcurementItems pi " +
                     "JOIN Items i ON pi.item_id = i.item_id " +
                     "WHERE pi.procurement_id = ? AND pi.status = 'To Buy' " +
                     "ORDER BY i.item_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, procurementId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                ProcurementItem pi = new ProcurementItem();
                pi.setProcurementItemId(rs.getInt("procurement_item_id"));
                pi.setProcurementId(rs.getInt("procurement_id"));
                pi.setItemId(rs.getInt("item_id"));
                pi.setItemName(rs.getString("item_name"));
                pi.setQuantityRequired(rs.getInt("quantity_required"));
                pi.setQuantityPurchased(rs.getInt("quantity_purchased"));
                pi.setUnitPrice(rs.getDouble("unit_price"));
                pi.setStatus(rs.getString("status"));
                
                items.add(pi);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return items;
    }
    
    // Mark item as purchased
    public boolean markAsPurchased(int procurementItemId, int quantityPurchased, double unitPrice) {
        if (quantityPurchased <= 0) {
            System.err.println("markAsPurchased: invalid quantityPurchased " + quantityPurchased);
            return false;
        }

        ProcurementItem pi = getProcurementItemById(procurementItemId);
        if (pi == null) {
            System.err.println("markAsPurchased: procurement item not found " + procurementItemId);
            return false;
        }

        String sql = "UPDATE ProcurementItems SET status = 'Purchased', " +
                     "quantity_purchased = ?, unit_price = ? " +
                     "WHERE procurement_item_id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Update procurement item
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, quantityPurchased);
            pstmt.setDouble(2, unitPrice);
            pstmt.setInt(3, procurementItemId);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                TransactionDAO transactionDAO = new TransactionDAO();
                boolean receiptRecorded = transactionDAO.recordReceipt(
                    pi.getItemId(),
                    quantityPurchased,
                    "System", // In real app, this would be the current user
                    "",
                    "Purchased from procurement #" + pi.getProcurementId()
                );

                if (receiptRecorded) {
                    conn.commit();
                    return true;
                }
            }

            conn.rollback();
            return false;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Get procurement item by ID
    public ProcurementItem getProcurementItemById(int procurementItemId) {
        String sql = "SELECT pi.*, i.item_name, i.unit_of_measure " +
                     "FROM ProcurementItems pi " +
                     "JOIN Items i ON pi.item_id = i.item_id " +
                     "WHERE pi.procurement_item_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, procurementItemId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                ProcurementItem pi = new ProcurementItem();
                pi.setProcurementItemId(rs.getInt("procurement_item_id"));
                pi.setProcurementId(rs.getInt("procurement_id"));
                pi.setItemId(rs.getInt("item_id"));
                pi.setItemName(rs.getString("item_name"));
                pi.setQuantityRequired(rs.getInt("quantity_required"));
                pi.setQuantityPurchased(rs.getInt("quantity_purchased"));
                pi.setUnitPrice(rs.getDouble("unit_price"));
                pi.setStatus(rs.getString("status"));
                
                return pi;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Update quantity required
    public boolean updateQuantityRequired(int procurementItemId, int newQuantity) {
        String sql = "UPDATE ProcurementItems SET quantity_required = ? WHERE procurement_item_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, procurementItemId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Remove item from procurement
    public boolean removeItemFromProcurement(int procurementItemId) {
        String sql = "DELETE FROM ProcurementItems WHERE procurement_item_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, procurementItemId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Generate shopping list text
    public String generateShoppingListText(int procurementId) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("           SHOPPING LIST\n");
        sb.append("========================================\n\n");
        
        ProcurementDAO procurementDAO = new ProcurementDAO();
        Procurement procurement = procurementDAO.getProcurementById(procurementId);
        
        if (procurement != null) {
            sb.append("Store: ").append(procurement.getStoreVisited()).append("\n");
            sb.append("Date: ").append(java.time.LocalDate.now()).append("\n");
            sb.append("Status: ").append(procurement.getStatus()).append("\n\n");
        }
        
        sb.append("Items to Buy:\n");
        sb.append("----------------------------------------\n");
        
        List<ProcurementItem> items = getItemsToBuy(procurementId);
        double total = 0;
        
        for (ProcurementItem item : items) {
            sb.append(String.format("%-30s %5d %s\n", 
                item.getItemName(), 
                item.getQuantityRequired(),
                "units"));
            total += item.getUnitPrice() * item.getQuantityPurchased();
        }
        
        sb.append("----------------------------------------\n");
        sb.append(String.format("Total Items: %d\n", items.size()));
        
        return sb.toString();
    }
    
    // Test method
    public static void main(String[] args) {
        ProcurementItemDAO dao = new ProcurementItemDAO();
        ProcurementDAO procurementDAO = new ProcurementDAO();
        ItemDAO itemDAO = new ItemDAO();
        
        // Get first procurement to test with
        List<Procurement> procurements = procurementDAO.getAllProcurements();
        if (!procurements.isEmpty()) {
            int testProcurementId = procurements.get(0).getProcurementId();
            
            // Get low stock items
            System.out.println("📝 Adding low stock items to procurement...");
            boolean added = dao.addLowStockItemsToProcurement(testProcurementId);
            System.out.println("Items added: " + (added ? "✅" : "❌"));
            
            // Show items in procurement
            System.out.println("\n📋 Items in procurement:");
            List<ProcurementItem> items = dao.getItemsByProcurement(testProcurementId);
            for (ProcurementItem pi : items) {
                System.out.println("  - " + pi.getItemName() + 
                                 ": Required " + pi.getQuantityRequired() +
                                 ", Status: " + pi.getStatus());
            }
            
            // Generate shopping list
            System.out.println("\n📄 Shopping List:");
            System.out.println(dao.generateShoppingListText(testProcurementId));
        }
        
        DatabaseConnection.closeConnection();
    }
}