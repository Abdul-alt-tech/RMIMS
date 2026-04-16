package com.nalex.rmims.gui;

import com.nalex.rmims.dao.StoreDAO;
import com.nalex.rmims.model.Store;
import com.nalex.rmims.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StoresPanel extends JPanel {
    
    private User currentUser;
    private StoreDAO storeDAO;
    private JTable storesTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    
    public StoresPanel(User user) {
        this.currentUser = user;
        this.storeDAO = new StoreDAO();
        
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
        add(new JScrollPane(storesTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Load data
        refreshTable();
    }
    
    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search Store:"));
        searchField = new JTextField(20);
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterTable();
            }
        });
        searchPanel.add(searchField);
        
        JButton refreshBtn = new JButton("Refresh");
        com.nalex.rmims.gui.UITheme.styleButton(refreshBtn, "neutral");
        refreshBtn.addActionListener(e -> refreshTable());
        
        toolbar.add(searchPanel, BorderLayout.WEST);
        toolbar.add(refreshBtn, BorderLayout.EAST);
        
        return toolbar;
    }
    
    private void createTable() {
        String[] columns = {"ID", "Store Name", "Location", "Contact Person", "Phone", "Notes"};
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        storesTable = new JTable(tableModel);
        storesTable.setFont(new Font("Arial", Font.PLAIN, 12));
        storesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        storesTable.setRowHeight(25);
        
        // Set column widths
        storesTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        storesTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        storesTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        storesTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        storesTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        storesTable.getColumnModel().getColumn(5).setPreferredWidth(200);
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton addBtn = new JButton("Add Store");
        JButton editBtn = new JButton("Edit Store");
        JButton deleteBtn = new JButton("Delete Store");
        
        // Button colors styled via UITheme
        
        addBtn.addActionListener(e -> addStore());
        editBtn.addActionListener(e -> editStore());
        
        // Only CEO can delete
        if (currentUser.isCEO()) {
            deleteBtn.addActionListener(e -> deleteStore());
        } else {
            deleteBtn.setEnabled(false);
        }
        
        com.nalex.rmims.gui.UITheme.styleButton(addBtn, "success");
        com.nalex.rmims.gui.UITheme.styleButton(editBtn, "primary");
        com.nalex.rmims.gui.UITheme.styleButton(deleteBtn, "danger");

        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);
        
        return panel;
    }
    
    public void refreshTable() {
        tableModel.setRowCount(0);
        List<Store> stores = storeDAO.getAllStores();
        
        for (Store s : stores) {
            tableModel.addRow(new Object[]{
                s.getStoreId(),
                s.getStoreName(),
                s.getLocation() != null ? s.getLocation() : "-",
                s.getContactPerson() != null ? s.getContactPerson() : "-",
                s.getPhone() != null ? s.getPhone() : "-",
                s.getNotes() != null ? s.getNotes() : "-"
            });
        }
    }
    
    private void filterTable() {
        String searchText = searchField.getText().toLowerCase();
        tableModel.setRowCount(0);
        
        List<Store> stores = storeDAO.getAllStores();
        for (Store s : stores) {
            if (s.getStoreName().toLowerCase().contains(searchText) ||
                (s.getLocation() != null && s.getLocation().toLowerCase().contains(searchText))) {
                
                tableModel.addRow(new Object[]{
                    s.getStoreId(),
                    s.getStoreName(),
                    s.getLocation() != null ? s.getLocation() : "-",
                    s.getContactPerson() != null ? s.getContactPerson() : "-",
                    s.getPhone() != null ? s.getPhone() : "-",
                    s.getNotes() != null ? s.getNotes() : "-"
                });
            }
        }
    }
    
    private void addStore() {
        JTextField nameField = new JTextField();
        JTextField locationField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextArea notesArea = new JTextArea(3, 20);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Store Name:*"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1;
        panel.add(locationField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Contact Person:"), gbc);
        gbc.gridx = 1;
        panel.add(contactField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(notesArea), gbc);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Store", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Store name is required");
                return;
            }
            
            Store store = new Store();
            store.setStoreName(nameField.getText().trim());
            store.setLocation(locationField.getText().trim());
            store.setContactPerson(contactField.getText().trim());
            store.setPhone(phoneField.getText().trim());
            store.setNotes(notesArea.getText().trim());
            
            boolean success = storeDAO.addStore(store);
            if (success) {
                JOptionPane.showMessageDialog(this, "Store added successfully");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add store");
            }
        }
    }
    
    private void editStore() {
        int row = storesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a store to edit");
            return;
        }
        
        int storeId = (int) tableModel.getValueAt(row, 0);
        Store store = storeDAO.getStoreById(storeId);
        
        if (store == null) return;
        
        JTextField nameField = new JTextField(store.getStoreName());
        JTextField locationField = new JTextField(store.getLocation());
        JTextField contactField = new JTextField(store.getContactPerson());
        JTextField phoneField = new JTextField(store.getPhone());
        JTextArea notesArea = new JTextArea(store.getNotes(), 3, 20);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Store Name:*"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1;
        panel.add(locationField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Contact Person:"), gbc);
        gbc.gridx = 1;
        panel.add(contactField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(notesArea), gbc);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Store", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Store name is required");
                return;
            }
            
            store.setStoreName(nameField.getText().trim());
            store.setLocation(locationField.getText().trim());
            store.setContactPerson(contactField.getText().trim());
            store.setPhone(phoneField.getText().trim());
            store.setNotes(notesArea.getText().trim());
            
            boolean success = storeDAO.updateStore(store);
            if (success) {
                JOptionPane.showMessageDialog(this, "Store updated successfully");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update store");
            }
        }
    }
    
    private void deleteStore() {
        int row = storesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a store to delete");
            return;
        }
        
        int storeId = (int) tableModel.getValueAt(row, 0);
        String storeName = (String) tableModel.getValueAt(row, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete " + storeName + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = storeDAO.deleteStore(storeId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Store deleted successfully");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete store");
            }
        }
    }
}