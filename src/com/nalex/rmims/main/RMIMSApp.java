package com.nalex.rmims.main;

import com.nalex.rmims.gui.LoginFrame;
import com.nalex.rmims.util.DatabaseConnection;

import javax.swing.*;

public class RMIMSApp {
    
    public static void main(String[] args) {
        // Set Look and Feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Test database connection on startup
        System.out.println("🔍 Testing database connection...");
        if (DatabaseConnection.getConnection() != null) {
            System.out.println("✅ Database connection successful!");
        } else {
            System.err.println("❌ WARNING: Database connection failed!");
            int option = JOptionPane.showConfirmDialog(null,
                "Cannot connect to database. Do you want to continue anyway?",
                "Database Connection Error",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE);
            
            if (option == JOptionPane.NO_OPTION) {
                System.exit(1);
            }
        }
        
        // Launch login screen
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}