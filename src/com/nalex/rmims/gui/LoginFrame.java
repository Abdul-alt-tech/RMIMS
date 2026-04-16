package com.nalex.rmims.gui;

import com.nalex.rmims.dao.UserDAO;
import com.nalex.rmims.model.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private UserDAO userDAO;
    
    public LoginFrame() {
        userDAO = new UserDAO();
        
        // Frame settings
        setTitle("RMIMS - GROUP_NO 1 - Login");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(UITheme.NEUTRAL);
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(UITheme.NEUTRAL);
        JLabel titleLabel = new JLabel("Raw Material Inventory Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(UITheme.PRIMARY);
        titlePanel.add(titleLabel);
        
        // Logo/Icon panel
        JPanel iconPanel = new JPanel();
        iconPanel.setBackground(UITheme.NEUTRAL);
        JLabel iconLabel = new JLabel("📦 RMIMS");
        iconLabel.setFont(UITheme.emojiFont(24f).deriveFont(Font.BOLD, 24f));
        iconLabel.setForeground(UITheme.PRIMARY);
        iconPanel.add(iconLabel);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UITheme.NEUTRAL);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(userLabel, gbc);
        
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(passLabel, gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(passwordField, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(UITheme.NEUTRAL);
        
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 35));
        com.nalex.rmims.gui.UITheme.styleButton(loginButton, "primary");
        
        cancelButton = new JButton("Exit");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        com.nalex.rmims.gui.UITheme.styleButton(cancelButton, "neutral");
        
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        
        // Add action listeners
        loginButton.addActionListener(e -> login());
        cancelButton.addActionListener(e -> System.exit(0));
        passwordField.addActionListener(e -> login());
        
        // Assemble
        mainPanel.add(iconPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter both username and password",
                "Login Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Disable login button while processing
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");
        
        // Authenticate in background thread
        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() {
                return userDAO.authenticate(username, password);
            }
            
            @Override
            protected void done() {
                try {
                    User user = get();
                    
                    if (user != null) {
                        JOptionPane.showMessageDialog(LoginFrame.this,
                            "Welcome, " + user.getFullName() + "!",
                            "Login Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Open main menu
                        MainMenuFrame mainMenu = new MainMenuFrame(user);
                        mainMenu.setVisible(true);
                        
                        // Close login frame
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(LoginFrame.this,
                            "Invalid username or password",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                        
                        // Clear fields
                        passwordField.setText("");
                        usernameField.requestFocus();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(LoginFrame.this,
                        "Error during login: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                } finally {
                    // Re-enable login button
                    loginButton.setEnabled(true);
                    loginButton.setText("Login");
                }
            }
        };
        
        worker.execute();
    }
}