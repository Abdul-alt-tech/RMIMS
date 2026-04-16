package com.nalex.rmims.gui;

import com.nalex.rmims.dao.UserDAO;
import com.nalex.rmims.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminPanel extends JPanel {
    
    private User currentUser;
    private UserDAO userDAO;
    private JTable usersTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    
    public AdminPanel(User user) {
        if (!user.isCEO()) {
            throw new SecurityException("Only CEO can access Admin Panel");
        }
        
        this.currentUser = user;
        this.userDAO = new UserDAO();
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create toolbar
        JPanel toolbar = createToolbar();
        
        // Create table
        createTable();
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        
        // Add components
        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(usersTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Load data
        refreshTable();
    }
    
    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search User:"));
        searchField = new JTextField(20);
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterTable();
            }
        });
        searchPanel.add(searchField);
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTable());
        
        toolbar.add(searchPanel, BorderLayout.WEST);
        toolbar.add(refreshBtn, BorderLayout.EAST);
        
        return toolbar;
    }
    
    private void createTable() {
        String[] columns = {"ID", "Username", "Full Name", "Role", "Contact", "Email"};
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        usersTable = new JTable(tableModel);
        usersTable.setFont(new Font("Arial", Font.PLAIN, 12));
        usersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        usersTable.setRowHeight(25);
        
        // Set column widths
        usersTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        usersTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        usersTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        usersTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        usersTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        usersTable.getColumnModel().getColumn(5).setPreferredWidth(150);
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton addBtn = new JButton("Add User");
        JButton editBtn = new JButton("Edit User");
        JButton resetPasswordBtn = new JButton("Reset Password");
        JButton deleteBtn = new JButton("Delete User");
        
        com.nalex.rmims.gui.UITheme.styleButton(addBtn, "success");
        com.nalex.rmims.gui.UITheme.styleButton(editBtn, "primary");
        com.nalex.rmims.gui.UITheme.styleButton(resetPasswordBtn, "accent");
        com.nalex.rmims.gui.UITheme.styleButton(deleteBtn, "danger");
        
        addBtn.addActionListener(e -> addUser());
        editBtn.addActionListener(e -> editUser());
        resetPasswordBtn.addActionListener(e -> resetPassword());
        deleteBtn.addActionListener(e -> deleteUser());
        
        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(resetPasswordBtn);
        panel.add(deleteBtn);
        
        return panel;
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        List<User> users = userDAO.getAllUsers();
        
        for (User u : users) {
            tableModel.addRow(new Object[]{
                u.getUserId(),
                u.getUsername(),
                u.getFullName(),
                u.getRole(),
                u.getContactNumber() != null ? u.getContactNumber() : "-",
                u.getEmail() != null ? u.getEmail() : "-"
            });
        }
    }
    
    private void filterTable() {
        String searchText = searchField.getText().toLowerCase();
        tableModel.setRowCount(0);
        
        List<User> users = userDAO.getAllUsers();
        for (User u : users) {
            if (u.getUsername().toLowerCase().contains(searchText) ||
                u.getFullName().toLowerCase().contains(searchText) ||
                u.getRole().toLowerCase().contains(searchText)) {
                
                tableModel.addRow(new Object[]{
                    u.getUserId(),
                    u.getUsername(),
                    u.getFullName(),
                    u.getRole(),
                    u.getContactNumber() != null ? u.getContactNumber() : "-",
                    u.getEmail() != null ? u.getEmail() : "-"
                });
            }
        }
    }
    
    private void addUser() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField fullNameField = new JTextField();
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"CEO", "Headteacher", "Maintenance"});
        JTextField contactField = new JTextField();
        JTextField emailField = new JTextField();
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Username:*"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Password:*"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Full Name:*"), gbc);
        gbc.gridx = 1;
        panel.add(fullNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        panel.add(roleCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Contact:"), gbc);
        gbc.gridx = 1;
        panel.add(contactField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Add New User", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            if (usernameField.getText().trim().isEmpty() || 
                passwordField.getPassword().length == 0 ||
                fullNameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username, Password, and Full Name are required");
                return;
            }
            
            User user = new User();
            user.setUsername(usernameField.getText().trim());
            user.setPassword(new String(passwordField.getPassword()));
            user.setFullName(fullNameField.getText().trim());
            user.setRole((String) roleCombo.getSelectedItem());
            user.setContactNumber(contactField.getText().trim());
            user.setEmail(emailField.getText().trim());
            
            boolean success = userDAO.addUser(user);
            if (success) {
                JOptionPane.showMessageDialog(this, "User added successfully");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add user");
            }
        }
    }
    
    private void editUser() {
        int row = usersTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit");
            return;
        }
        
        int userId = (int) tableModel.getValueAt(row, 0);
        User user = userDAO.getUserById(userId);
        
        if (user == null) return;
        
        JTextField fullNameField = new JTextField(user.getFullName());
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"CEO", "Headteacher", "Maintenance"});
        roleCombo.setSelectedItem(user.getRole());
        JTextField contactField = new JTextField(user.getContactNumber());
        JTextField emailField = new JTextField(user.getEmail());
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(user.getUsername()), gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Full Name:*"), gbc);
        gbc.gridx = 1;
        panel.add(fullNameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        panel.add(roleCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Contact:"), gbc);
        gbc.gridx = 1;
        panel.add(contactField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Edit User", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            if (fullNameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Full Name is required");
                return;
            }
            
            user.setFullName(fullNameField.getText().trim());
            user.setRole((String) roleCombo.getSelectedItem());
            user.setContactNumber(contactField.getText().trim());
            user.setEmail(emailField.getText().trim());
            
            boolean success = userDAO.updateUser(user);
            if (success) {
                JOptionPane.showMessageDialog(this, "User updated successfully");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update user");
            }
        }
    }
    
    private void resetPassword() {
        int row = usersTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user");
            return;
        }
        
        int userId = (int) tableModel.getValueAt(row, 0);
        String username = (String) tableModel.getValueAt(row, 1);
        
        JPasswordField newPassField = new JPasswordField();
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(new JLabel("New password for " + username + ":"));
        panel.add(newPassField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Reset Password", 
                JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String newPass = new String(newPassField.getPassword());
            if (newPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Password cannot be empty");
                return;
            }
            
            // Direct password update (in real app, use hashed version)
            String sql = "UPDATE Users SET password = ? WHERE user_id = ?";
            try (java.sql.Connection conn = com.nalex.rmims.util.DatabaseConnection.getConnection();
                 java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setString(1, newPass);
                pstmt.setInt(2, userId);
                
                int updated = pstmt.executeUpdate();
                if (updated > 0) {
                    JOptionPane.showMessageDialog(this, "Password reset successful");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to reset password");
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private void deleteUser() {
        int row = usersTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete");
            return;
        }
        
        int userId = (int) tableModel.getValueAt(row, 0);
        String username = (String) tableModel.getValueAt(row, 1);
        
        // Prevent deleting yourself
        if (userId == currentUser.getUserId()) {
            JOptionPane.showMessageDialog(this, "You cannot delete your own account");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete user: " + username + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = userDAO.deleteUser(userId);
            if (success) {
                JOptionPane.showMessageDialog(this, "User deleted successfully");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete user");
            }
        }
    }
}