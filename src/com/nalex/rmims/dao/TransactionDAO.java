package com.nalex.rmims.dao;

import com.nalex.rmims.model.Item;
import com.nalex.rmims.model.Transaction;
import com.nalex.rmims.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    
    // Record a withdrawal (OUT)
    public boolean recordWithdrawal(int itemId, int quantity, String staffName, 
                                   String staffMobile, String notes) {
        String sql = "INSERT INTO Transactions (item_id, transaction_type, quantity, " +
                     "staff_name, staff_mobile, notes) VALUES (?, 'OUT', ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement updateStmt = null;
        PreparedStatement selectStmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("TransactionDAO.recordWithdrawal: DB connection is null");
                return false;
            }

            conn.setAutoCommit(false);

            // Insert transaction record
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, itemId);
            pstmt.setInt(2, quantity);
            pstmt.setString(3, staffName);
            pstmt.setString(4, staffMobile);
            pstmt.setString(5, notes);

            int transactionInserted = pstmt.executeUpdate();
            System.err.println("TransactionDAO.recordWithdrawal: transactionInserted=" + transactionInserted);

            // Read current quantity using same connection
            String selSql = "SELECT current_quantity FROM Items WHERE item_id = ?";
            selectStmt = conn.prepareStatement(selSql);
            selectStmt.setInt(1, itemId);
            rs = selectStmt.executeQuery();

            if (rs.next()) {
                int currentQty = rs.getInt("current_quantity");
                int newQuantity = currentQty - quantity;

                if (newQuantity < 0) {
                    System.err.println("TransactionDAO.recordWithdrawal: newQuantity < 0, aborting");
                    conn.rollback();
                    return false;
                }

                // Update item quantity using same connection
                String updSql = "UPDATE Items SET current_quantity = ? WHERE item_id = ?";
                updateStmt = conn.prepareStatement(updSql);
                updateStmt.setInt(1, newQuantity);
                updateStmt.setInt(2, itemId);

                int updated = updateStmt.executeUpdate();
                System.err.println("TransactionDAO.recordWithdrawal: items updated=" + updated + ", newQuantity=" + newQuantity);

                if (transactionInserted > 0 && updated > 0) {
                    conn.commit();
                    return true;
                }
            } else {
                System.err.println("TransactionDAO.recordWithdrawal: Item not found: " + itemId);
            }

            conn.rollback();
            return false;

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.err.println("TransactionDAO.recordWithdrawal: SQLException -> " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (selectStmt != null) selectStmt.close(); } catch (SQLException ignored) {}
            try { if (updateStmt != null) updateStmt.close(); } catch (SQLException ignored) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException ignored) {}
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }
    
    // Record receiving stock (IN)
    public boolean recordReceipt(int itemId, int quantity, String staffName, 
                                String staffMobile, String notes) {
        String sql = "INSERT INTO Transactions (item_id, transaction_type, quantity, " +
                     "staff_name, staff_mobile, notes) VALUES (?, 'IN', ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement updateStmt = null;
        PreparedStatement selectStmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("TransactionDAO.recordReceipt: DB connection is null");
                return false;
            }

            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, itemId);
            pstmt.setInt(2, quantity);
            pstmt.setString(3, staffName);
            pstmt.setString(4, staffMobile);
            pstmt.setString(5, notes);

            int transactionInserted = pstmt.executeUpdate();
            System.err.println("TransactionDAO.recordReceipt: transactionInserted=" + transactionInserted);

            // Read current quantity using same connection
            String selSql = "SELECT current_quantity FROM Items WHERE item_id = ?";
            selectStmt = conn.prepareStatement(selSql);
            selectStmt.setInt(1, itemId);
            rs = selectStmt.executeQuery();

            if (rs.next()) {
                int currentQty = rs.getInt("current_quantity");
                int newQuantity = currentQty + quantity;

                String updSql = "UPDATE Items SET current_quantity = ? WHERE item_id = ?";
                updateStmt = conn.prepareStatement(updSql);
                updateStmt.setInt(1, newQuantity);
                updateStmt.setInt(2, itemId);

                int updated = updateStmt.executeUpdate();
                System.err.println("TransactionDAO.recordReceipt: items updated=" + updated + ", newQuantity=" + newQuantity);

                if (transactionInserted > 0 && updated > 0) {
                    conn.commit();
                    return true;
                }
            } else {
                System.err.println("TransactionDAO.recordReceipt: Item not found: " + itemId);
            }

            conn.rollback();
            return false;

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.err.println("TransactionDAO.recordReceipt: SQLException -> " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            try { if (selectStmt != null) selectStmt.close(); } catch (SQLException ignored) {}
            try { if (updateStmt != null) updateStmt.close(); } catch (SQLException ignored) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException ignored) {}
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }
    
    // Get all transactions for an item
    public List<Transaction> getTransactionsByItem(int itemId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, i.item_name " +
                     "FROM Transactions t " +
                     "JOIN Items i ON t.item_id = i.item_id " +
                     "WHERE t.item_id = ? " +
                     "ORDER BY t.transaction_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, itemId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Transaction t = new Transaction();
                t.setTransactionId(rs.getInt("transaction_id"));
                t.setItemId(rs.getInt("item_id"));
                t.setItemName(rs.getString("item_name"));
                t.setTransactionType(rs.getString("transaction_type"));
                t.setQuantity(rs.getInt("quantity"));
                t.setTransactionDate(rs.getTimestamp("transaction_date").toLocalDateTime());
                t.setStaffName(rs.getString("staff_name"));
                t.setStaffMobile(rs.getString("staff_mobile"));
                t.setNotes(rs.getString("notes"));
                
                transactions.add(t);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    // Get all transactions (for reports)
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, i.item_name " +
                     "FROM Transactions t " +
                     "JOIN Items i ON t.item_id = i.item_id " +
                     "ORDER BY t.transaction_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Transaction t = new Transaction();
                t.setTransactionId(rs.getInt("transaction_id"));
                t.setItemId(rs.getInt("item_id"));
                t.setItemName(rs.getString("item_name"));
                t.setTransactionType(rs.getString("transaction_type"));
                t.setQuantity(rs.getInt("quantity"));
                t.setTransactionDate(rs.getTimestamp("transaction_date").toLocalDateTime());
                t.setStaffName(rs.getString("staff_name"));
                t.setStaffMobile(rs.getString("staff_mobile"));
                t.setNotes(rs.getString("notes"));
                
                transactions.add(t);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    // Get transactions by date range
    public List<Transaction> getTransactionsByDateRange(LocalDateTime start, LocalDateTime end) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.*, i.item_name " +
                     "FROM Transactions t " +
                     "JOIN Items i ON t.item_id = i.item_id " +
                     "WHERE t.transaction_date BETWEEN ? AND ? " +
                     "ORDER BY t.transaction_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(start));
            pstmt.setTimestamp(2, Timestamp.valueOf(end));
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Transaction t = new Transaction();
                t.setTransactionId(rs.getInt("transaction_id"));
                t.setItemId(rs.getInt("item_id"));
                t.setItemName(rs.getString("item_name"));
                t.setTransactionType(rs.getString("transaction_type"));
                t.setQuantity(rs.getInt("quantity"));
                t.setTransactionDate(rs.getTimestamp("transaction_date").toLocalDateTime());
                t.setStaffName(rs.getString("staff_name"));
                t.setStaffMobile(rs.getString("staff_mobile"));
                t.setNotes(rs.getString("notes"));
                
                transactions.add(t);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    // Test method
    public static void main(String[] args) {
        TransactionDAO dao = new TransactionDAO();
        ItemDAO itemDAO = new ItemDAO();
        
        // Get first item to test with
        List<Item> items = itemDAO.getAllItems();
        if (!items.isEmpty()) {
            int testItemId = items.get(0).getItemId();
            
            // Test recording a withdrawal
            System.out.println("📝 Testing withdrawal...");
            boolean withdrawn = dao.recordWithdrawal(
                testItemId, 
                2, 
                "John Staff", 
                "0777123456", 
                "Test withdrawal"
            );
            System.out.println("Withdrawal recorded: " + (withdrawn ? "✅" : "❌"));
            
            // Get transactions for this item
            System.out.println("\n📋 Transactions for item:");
            List<Transaction> trans = dao.getTransactionsByItem(testItemId);
            for (Transaction t : trans) {
                System.out.println("  - " + t.getTransactionType() + 
                                 ": " + t.getQuantity() + 
                                 " by " + t.getStaffName() +
                                 " on " + t.getTransactionDate());
            }
        }
        
        DatabaseConnection.closeConnection();
    }
}