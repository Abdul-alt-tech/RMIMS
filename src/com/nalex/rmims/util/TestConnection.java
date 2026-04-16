package com.nalex.rmims.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("🔍 Testing database connection...");
        
        Connection conn = DatabaseConnection.getConnection();
        
        if (conn != null) {
            try {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM Users");
                
                if (rs.next()) {
                    int userCount = rs.getInt("count");
                    System.out.println("✅ Connection successful! Found " + userCount + " users.");
                }
                
                rs.close();
                stmt.close();
                
            } catch (Exception e) {
                System.out.println("❌ Error querying database:");
                e.printStackTrace();
            } finally {
                DatabaseConnection.closeConnection();
            }
        }
    }
}