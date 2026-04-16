package com.nalex.rmims.dao;

import com.nalex.rmims.model.User;
import com.nalex.rmims.util.DatabaseConnection;
import com.nalex.rmims.util.PasswordHasher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    
    // Authenticate user login
    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM Users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("password");
                
                // Verify password using PasswordHasher
                if (PasswordHasher.verifyPassword(password, storedHash)) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setFullName(rs.getString("full_name"));
                    user.setRole(rs.getString("role"));
                    user.setContactNumber(rs.getString("contact_number"));
                    user.setEmail(rs.getString("email"));
                    return user;
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null; // authentication failed
    }
    
    // Get user by ID
    public User getUserById(int userId) {
        String sql = "SELECT * FROM Users WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setRole(rs.getString("role"));
                user.setContactNumber(rs.getString("contact_number"));
                user.setEmail(rs.getString("email"));
                return user;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Get all users
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users ORDER BY full_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setRole(rs.getString("role"));
                user.setContactNumber(rs.getString("contact_number"));
                user.setEmail(rs.getString("email"));
                users.add(user);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return users;
    }
    
    // Add new user (with hashed password)
    public boolean addUser(User user) {
        String sql = "INSERT INTO Users (username, password, full_name, role, contact_number, email) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            // Hash the password before storing
            pstmt.setString(2, PasswordHasher.hashPassword(user.getPassword()));
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getRole());
            pstmt.setString(5, user.getContactNumber());
            pstmt.setString(6, user.getEmail());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update user
    public boolean updateUser(User user) {
        String sql = "UPDATE Users SET full_name = ?, role = ?, contact_number = ?, email = ? " +
                     "WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getRole());
            pstmt.setString(3, user.getContactNumber());
            pstmt.setString(4, user.getEmail());
            pstmt.setInt(5, user.getUserId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Change password
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        // First get the user's current password hash
        String selectSql = "SELECT password FROM Users WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            
            selectStmt.setInt(1, userId);
            ResultSet rs = selectStmt.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("password");
                
                // Verify old password
                if (PasswordHasher.verifyPassword(oldPassword, storedHash)) {
                    // Update to new password
                    String updateSql = "UPDATE Users SET password = ? WHERE user_id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setString(1, PasswordHasher.hashPassword(newPassword));
                        updateStmt.setInt(2, userId);
                        
                        int rowsAffected = updateStmt.executeUpdate();
                        return rowsAffected > 0;
                    }
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false; // old password incorrect or error
    }
    
    // Delete user
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM Users WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Test method
    public static void main(String[] args) {
        UserDAO dao = new UserDAO();
        
        // Test authentication
        System.out.println("🔐 Testing authentication...");
        User admin = dao.authenticate("admin", "admin123");
        
        if (admin != null) {
            System.out.println("✅ Login successful: " + admin.getFullName() + " (" + admin.getRole() + ")");
        } else {
            System.out.println("❌ Login failed");
        }
        
        // Test getting all users
        System.out.println("\n👥 All Users:");
        List<User> users = dao.getAllUsers();
        for (User u : users) {
            System.out.println("  - " + u.getFullName() + " (" + u.getUsername() + ", " + u.getRole() + ")");
        }
        
        DatabaseConnection.closeConnection();
    }
}