package com.nalex.rmims.dao;

import com.nalex.rmims.model.Procurement;
import com.nalex.rmims.model.User;
import com.nalex.rmims.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProcurementDAO {
    
    // Create a new procurement batch
    public int createProcurement(Procurement procurement) {
        String sql = "INSERT INTO Procurement (initiated_by, expected_completion_date, status, store_visited, total_estimated_cost) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, procurement.getInitiatedBy());
            pstmt.setDate(2, procurement.getExpectedCompletionDate() != null ? 
                         Date.valueOf(procurement.getExpectedCompletionDate()) : null);
            pstmt.setString(3, procurement.getStatus() != null ? procurement.getStatus() : "Pending");
            pstmt.setString(4, procurement.getStoreVisited());
            pstmt.setDouble(5, procurement.getTotalEstimatedCost());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // Return the new procurement ID
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return -1; // Failed
    }
    
    // Get all procurement batches
    public List<Procurement> getAllProcurements() {
        List<Procurement> procurements = new ArrayList<>();
        String sql = "SELECT p.*, u.full_name as initiator_name " +
                     "FROM Procurement p " +
                     "JOIN Users u ON p.initiated_by = u.user_id " +
                     "ORDER BY p.initiation_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Procurement p = new Procurement();
                p.setProcurementId(rs.getInt("procurement_id"));
                p.setInitiatedBy(rs.getInt("initiated_by"));
                p.setInitiatorName(rs.getString("initiator_name"));
                p.setInitiationDate(rs.getTimestamp("initiation_date") != null ? 
                                    rs.getTimestamp("initiation_date").toLocalDateTime() : null);
                p.setExpectedCompletionDate(rs.getDate("expected_completion_date") != null ? 
                                           rs.getDate("expected_completion_date").toLocalDate() : null);
                p.setStatus(rs.getString("status"));
                p.setStoreVisited(rs.getString("store_visited"));
                p.setTotalEstimatedCost(rs.getDouble("total_estimated_cost"));
                p.setCompletedDate(rs.getTimestamp("completed_date") != null ? 
                                   rs.getTimestamp("completed_date").toLocalDateTime() : null);
                
                procurements.add(p);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return procurements;
    }
    
    // Get procurement by ID
    public Procurement getProcurementById(int procurementId) {
        String sql = "SELECT p.*, u.full_name as initiator_name " +
                     "FROM Procurement p " +
                     "JOIN Users u ON p.initiated_by = u.user_id " +
                     "WHERE p.procurement_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, procurementId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Procurement p = new Procurement();
                p.setProcurementId(rs.getInt("procurement_id"));
                p.setInitiatedBy(rs.getInt("initiated_by"));
                p.setInitiatorName(rs.getString("initiator_name"));
                p.setInitiationDate(rs.getTimestamp("initiation_date") != null ? 
                                    rs.getTimestamp("initiation_date").toLocalDateTime() : null);
                p.setExpectedCompletionDate(rs.getDate("expected_completion_date") != null ? 
                                           rs.getDate("expected_completion_date").toLocalDate() : null);
                p.setStatus(rs.getString("status"));
                p.setStoreVisited(rs.getString("store_visited"));
                p.setTotalEstimatedCost(rs.getDouble("total_estimated_cost"));
                p.setCompletedDate(rs.getTimestamp("completed_date") != null ? 
                                   rs.getTimestamp("completed_date").toLocalDateTime() : null);
                
                return p;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Get pending procurements
    public List<Procurement> getPendingProcurements() {
        List<Procurement> procurements = new ArrayList<>();
        String sql = "SELECT p.*, u.full_name as initiator_name " +
                     "FROM Procurement p " +
                     "JOIN Users u ON p.initiated_by = u.user_id " +
                     "WHERE p.status IN ('Pending', 'In Progress') " +
                     "ORDER BY p.initiation_date ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Procurement p = new Procurement();
                p.setProcurementId(rs.getInt("procurement_id"));
                p.setInitiatedBy(rs.getInt("initiated_by"));
                p.setInitiatorName(rs.getString("initiator_name"));
                p.setInitiationDate(rs.getTimestamp("initiation_date") != null ? 
                                    rs.getTimestamp("initiation_date").toLocalDateTime() : null);
                p.setExpectedCompletionDate(rs.getDate("expected_completion_date") != null ? 
                                           rs.getDate("expected_completion_date").toLocalDate() : null);
                p.setStatus(rs.getString("status"));
                p.setStoreVisited(rs.getString("store_visited"));
                p.setTotalEstimatedCost(rs.getDouble("total_estimated_cost"));
                p.setCompletedDate(rs.getTimestamp("completed_date") != null ? 
                                   rs.getTimestamp("completed_date").toLocalDateTime() : null);
                
                procurements.add(p);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return procurements;
    }
    
    // Update procurement status
    public boolean updateProcurementStatus(int procurementId, String status) {
        String sql = "UPDATE Procurement SET status = ? WHERE procurement_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, procurementId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Complete procurement
    public boolean completeProcurement(int procurementId, double actualCost) {
        String sql = "UPDATE Procurement SET status = 'Completed', completed_date = GETDATE(), " +
                     "total_estimated_cost = ? WHERE procurement_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, actualCost);
            pstmt.setInt(2, procurementId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update store visited
    public boolean updateStoreVisited(int procurementId, String storeName) {
        String sql = "UPDATE Procurement SET store_visited = ? WHERE procurement_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, storeName);
            pstmt.setInt(2, procurementId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete procurement (with any item details)
    public boolean deleteProcurement(int procurementId) {
        String deleteItemsSql = "DELETE FROM ProcurementItems WHERE procurement_id = ?";
        String deleteProcurementSql = "DELETE FROM Procurement WHERE procurement_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(deleteItemsSql)) {
                stmt1.setInt(1, procurementId);
                stmt1.executeUpdate();
            }

            try (PreparedStatement stmt2 = conn.prepareStatement(deleteProcurementSql)) {
                stmt2.setInt(1, procurementId);
                int rowsAffected = stmt2.executeUpdate();

                if (rowsAffected > 0) {
                    conn.commit();
                    return true;
                }
            }

            conn.rollback();
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Test method
    public static void main(String[] args) {
        ProcurementDAO dao = new ProcurementDAO();
        UserDAO userDAO = new UserDAO();
        
        // Get first user to test with
        List<User> users = userDAO.getAllUsers();
        if (!users.isEmpty()) {
            // Test creating a procurement
            System.out.println("📝 Creating procurement...");
            Procurement p = new Procurement();
            p.setInitiatedBy(users.get(0).getUserId());
            p.setExpectedCompletionDate(LocalDate.now().plusDays(3));
            p.setStatus("Pending");
            p.setStoreVisited("Jumbo Wholesales");
            p.setTotalEstimatedCost(0);
            
            int procurementId = dao.createProcurement(p);
            System.out.println("Procurement created with ID: " + (procurementId > 0 ? procurementId + " ✅" : "❌"));
            
            // Get all procurements
            System.out.println("\n📋 All Procurements:");
            List<Procurement> procurements = dao.getAllProcurements();
            for (Procurement proc : procurements) {
                System.out.println("  - ID: " + proc.getProcurementId() + 
                                 ", Status: " + proc.getStatus() + 
                                 ", Store: " + proc.getStoreVisited() +
                                 ", By: " + proc.getInitiatorName());
            }
        }
        
        DatabaseConnection.closeConnection();
    }
}