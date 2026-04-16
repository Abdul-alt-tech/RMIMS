package com.nalex.rmims.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // SQL Server connection with username and password
    private static final String URL = "jdbc:sqlserver://ABDUL-RAHMAN\\SQLEXPRESS;databaseName=RMIMS_DB;user=rmims_user;password=rmims123;encrypt=false;";
    
    private static Connection connection = null;
    
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                try {
                    // Load SQL Server JDBC Driver
                    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                } catch (ClassNotFoundException e) {
                    System.out.println("❌ SQL Server JDBC Driver not found!");
                    System.err.println("Make sure mssql-jdbc jar is in lib folder");
                    e.printStackTrace();
                    return null;
                }

                // Create connection
                try {
                    connection = DriverManager.getConnection(URL);
                    System.out.println("✅ Database connected successfully!");
                } catch (SQLException e) {
                    System.out.println("❌ Connection failed!");
                    System.err.println("Error: " + e.getMessage());
                    e.printStackTrace();
                    connection = null;
                }
            }
        } catch (SQLException sqle) {
            System.err.println("DatabaseConnection.getConnection: error checking connection state -> " + sqle.getMessage());
            sqle.printStackTrace();
            try { connection = DriverManager.getConnection(URL); } catch (Exception ignore) {}
        }

        return connection;
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
                // Clear the reference so future calls recreate the connection
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Test the connection
    public static void main(String[] args) {
        getConnection();
        closeConnection();
    }
}