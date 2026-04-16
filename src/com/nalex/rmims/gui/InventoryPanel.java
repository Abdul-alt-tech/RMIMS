package com.nalex.rmims.gui;

import com.nalex.rmims.dao.ItemDAO;
import com.nalex.rmims.model.Item;
import com.nalex.rmims.model.User;
import com.nalex.rmims.gui.AddItemDialog;
import com.nalex.rmims.gui.WithdrawDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InventoryPanel extends JPanel {
    
    private ItemDAO itemDAO;
    private User currentUser;
    private JTable itemsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    
    public InventoryPanel(User user) {
        this.currentUser = user;
        this.itemDAO = new ItemDAO();
        
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
        add(new JScrollPane(itemsTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Load data
        refreshTable();
    }
    
    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterTable();
            }
        });
        searchPanel.add(searchField);
        
        // Refresh button
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshTable());
        
        toolbar.add(searchPanel, BorderLayout.WEST);
        toolbar.add(refreshBtn, BorderLayout.EAST);
        
        return toolbar;
    }
    
    private void createTable() {
        String[] columns = {"ID", "Item Name", "Category", "Unit", "Current Qty", 
                           "Min Level", "Status", "Location", "Preferred Store"};
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        itemsTable = new JTable(tableModel);
        itemsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        itemsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        itemsTable.setRowHeight(25);
        
        // Set column widths
        itemsTable.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID
        itemsTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Name
        itemsTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Category
        itemsTable.getColumnModel().getColumn(3).setPreferredWidth(60);  // Unit
        itemsTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Current Qty
        itemsTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Min Level
        itemsTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Status
        itemsTable.getColumnModel().getColumn(7).setPreferredWidth(150); // Location
        itemsTable.getColumnModel().getColumn(8).setPreferredWidth(150); // Store
        
        // Add double-click listener
        itemsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    viewSelectedItem();
                }
            }
        });
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton addBtn = new JButton("Add New Item");
        JButton editBtn = new JButton("Edit Item");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton deleteBtn = new JButton("Delete");
        JButton viewBtn = new JButton("View Details");

        // Style buttons consistently
        com.nalex.rmims.gui.UITheme.styleButton(addBtn, "success");
        com.nalex.rmims.gui.UITheme.styleButton(editBtn, "primary");
        com.nalex.rmims.gui.UITheme.styleButton(withdrawBtn, "accent");
        com.nalex.rmims.gui.UITheme.styleButton(deleteBtn, "danger");
        com.nalex.rmims.gui.UITheme.styleButton(viewBtn, "primary");

        // Add action listeners
        addBtn.addActionListener(e -> addNewItem());
        editBtn.addActionListener(e -> editSelectedItem());
        withdrawBtn.addActionListener(e -> withdrawSelectedItem());
        deleteBtn.addActionListener(e -> deleteSelectedItem());
        viewBtn.addActionListener(e -> viewSelectedItem());
        
        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(withdrawBtn);
        
        // Only CEO can delete
        if (currentUser.isCEO()) {
            panel.add(deleteBtn);
        }
        
        panel.add(viewBtn);
        
        return panel;
    }
    
    public void refreshTable() {
        tableModel.setRowCount(0);
        List<Item> items = itemDAO.getAllItems();
        
        for (Item item : items) {
            String status = item.isLowStock() ? "⚠️ LOW STOCK" : "OK";
            
            tableModel.addRow(new Object[]{
                item.getItemId(),
                item.getItemName(),
                item.getCategoryName() != null ? item.getCategoryName() : "Uncategorized",
                item.getUnitOfMeasure() != null ? item.getUnitOfMeasure() : "-",
                item.getCurrentQuantity(),
                item.getMinimumLevel(),
                status,
                item.getLocationInSchool() != null ? item.getLocationInSchool() : "-",
                item.getStoreName() != null ? item.getStoreName() : "-"
            });
        }
        
        // Color rows based on stock status
        itemsTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, 
                        isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String status = table.getValueAt(row, 6).toString();
                    if (status.contains("LOW STOCK")) {
                        c.setBackground(UITheme.DANGER);
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                
                return c;
            }
        });
    }
    
    private void filterTable() {
        String searchText = searchField.getText().toLowerCase();
        tableModel.setRowCount(0);
        
        List<Item> items = itemDAO.getAllItems();
        for (Item item : items) {
            if (item.getItemName().toLowerCase().contains(searchText) ||
                (item.getCategoryName() != null && item.getCategoryName().toLowerCase().contains(searchText))) {
                
                String status = item.isLowStock() ? "⚠️ LOW STOCK" : "OK";
                
                tableModel.addRow(new Object[]{
                    item.getItemId(),
                    item.getItemName(),
                    item.getCategoryName() != null ? item.getCategoryName() : "Uncategorized",
                    item.getUnitOfMeasure() != null ? item.getUnitOfMeasure() : "-",
                    item.getCurrentQuantity(),
                    item.getMinimumLevel(),
                    status,
                    item.getLocationInSchool() != null ? item.getLocationInSchool() : "-",
                    item.getStoreName() != null ? item.getStoreName() : "-"
                });
            }
        }
    }
    
    private void addNewItem() {
    AddItemDialog dialog = new AddItemDialog((JFrame) SwingUtilities.getWindowAncestor(this));
    dialog.setVisible(true);
    
    if (dialog.isSaved()) {
        refreshTable(); // Refresh the table if item was added
    }
}
    
    private void editSelectedItem() {
        int row = itemsTable.getSelectedRow();
        if (row >= 0) {
            int itemId = (int) tableModel.getValueAt(row, 0);
            JOptionPane.showMessageDialog(this, "Edit Item ID: " + itemId + " - Coming Soon!");
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to edit");
        }
    }
    
    public void withdrawSelectedItem() {
    int row = itemsTable.getSelectedRow();
    if (row >= 0) {
        int itemId = (int) tableModel.getValueAt(row, 0);
        String itemName = (String) tableModel.getValueAt(row, 1);
        
        // Get full item object
        Item item = itemDAO.getItemById(itemId);
        
        if (item != null) {
            WithdrawDialog dialog = new WithdrawDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), 
                item
            );
            dialog.setVisible(true);
            
            if (dialog.isSuccess()) {
                refreshTable(); // Refresh after successful withdrawal
            }
        }
    } else {
        JOptionPane.showMessageDialog(this, "Please select an item to withdraw");
    }
}
    
    private void deleteSelectedItem() {
        if (!currentUser.isCEO()) {
            JOptionPane.showMessageDialog(this, "Only CEO can delete items");
            return;
        }
        
        int row = itemsTable.getSelectedRow();
        if (row >= 0) {
            int itemId = (int) tableModel.getValueAt(row, 0);
            String itemName = (String) tableModel.getValueAt(row, 1);
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete " + itemName + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean deleted = itemDAO.deleteItem(itemId);
                if (deleted) {
                    JOptionPane.showMessageDialog(this, "Item deleted successfully");
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete item");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to delete");
        }
    }
    
    private void viewSelectedItem() {
        int row = itemsTable.getSelectedRow();
        if (row >= 0) {
            int itemId = (int) tableModel.getValueAt(row, 0);
            String itemName = (String) tableModel.getValueAt(row, 1);
            
            String message = "Item Details:\n\n" +
                           "ID: " + itemId + "\n" +
                           "Name: " + itemName + "\n" +
                           "Category: " + tableModel.getValueAt(row, 2) + "\n" +
                           "Unit: " + tableModel.getValueAt(row, 3) + "\n" +
                           "Current Quantity: " + tableModel.getValueAt(row, 4) + "\n" +
                           "Minimum Level: " + tableModel.getValueAt(row, 5) + "\n" +
                           "Status: " + tableModel.getValueAt(row, 6) + "\n" +
                           "Location: " + tableModel.getValueAt(row, 7) + "\n" +
                           "Preferred Store: " + tableModel.getValueAt(row, 8);
            
            JOptionPane.showMessageDialog(this, message, "Item Details", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to view");
        }
    }
}