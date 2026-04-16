package com.nalex.rmims.gui;

import com.nalex.rmims.dao.ItemDAO;
import com.nalex.rmims.dao.ProcurementDAO;
import com.nalex.rmims.dao.ProcurementItemDAO;
import com.nalex.rmims.dao.StoreDAO;
import com.nalex.rmims.model.Item;
import com.nalex.rmims.model.Procurement;
import com.nalex.rmims.model.ProcurementItem;
import com.nalex.rmims.model.Store;
import com.nalex.rmims.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ProcurementDialog extends JDialog {
    
    private User currentUser;
    private ItemDAO itemDAO;
    private StoreDAO storeDAO;
    private ProcurementDAO procurementDAO;
    private ProcurementItemDAO procurementItemDAO;
    
    private JComboBox<Store> storeCombo;
    private JSpinner dateSpinner;
    private JTable lowStockTable;
    private DefaultTableModel tableModel;
    private JLabel totalItemsLabel;
    
    private int createdProcurementId = -1;
    
    public ProcurementDialog(JFrame parent, User user) {
        super(parent, "Create Shopping List", true);
        this.currentUser = user;
        
        itemDAO = new ItemDAO();
        storeDAO = new StoreDAO();
        procurementDAO = new ProcurementDAO();
        procurementItemDAO = new ProcurementItemDAO();
        
        setSize(700, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        
        // Create top panel
        JPanel topPanel = createTopPanel();
        
        // Create table panel
        JPanel tablePanel = createTablePanel();
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        
        add(topPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Load data
        loadLowStockItems();
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Procurement Details"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Store
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Store to visit:"), gbc);
        
        gbc.gridx = 1;
        storeCombo = new JComboBox<>();
        storeCombo.addItem(new Store(0, "-- Select Store --", ""));
        List<Store> stores = storeDAO.getAllStores();
        for (Store s : stores) {
            storeCombo.addItem(s);
        }
        panel.add(storeCombo, gbc);
        
        // Expected completion date
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Expected by:"), gbc);
        
        gbc.gridx = 1;
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new java.util.Date()); // Today
        panel.add(dateSpinner, gbc);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Low Stock Items to Reorder"));
        
        String[] columns = {"Item Name", "Current Qty", "Min Level", "Need", "Unit", "Location"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        lowStockTable = new JTable(tableModel);
        lowStockTable.setFont(new Font("Arial", Font.PLAIN, 12));
        lowStockTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        lowStockTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(lowStockTable);
        scrollPane.setPreferredSize(new Dimension(650, 300));
        
        // Summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalItemsLabel = new JLabel("Items to buy: 0");
        totalItemsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        summaryPanel.add(totalItemsLabel);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(summaryPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton generateBtn = new JButton("Generate Shopping List");
        JButton cancelBtn = new JButton("Cancel");

        com.nalex.rmims.gui.UITheme.styleButton(generateBtn, "success");
        com.nalex.rmims.gui.UITheme.styleButton(cancelBtn, "neutral");

        generateBtn.addActionListener(e -> generateShoppingList());
        cancelBtn.addActionListener(e -> dispose());

        panel.add(generateBtn);
        panel.add(cancelBtn);
        
        return panel;
    }
    
    private void loadLowStockItems() {
        tableModel.setRowCount(0);
        List<Item> lowStockItems = itemDAO.getLowStockItems();
        
        for (Item item : lowStockItems) {
            int needed = item.getMinimumLevel() - item.getCurrentQuantity();
            if (needed > 0) {
                tableModel.addRow(new Object[]{
                    item.getItemName(),
                    item.getCurrentQuantity(),
                    item.getMinimumLevel(),
                    needed,
                    item.getUnitOfMeasure() != null ? item.getUnitOfMeasure() : "-",
                    item.getLocationInSchool() != null ? item.getLocationInSchool() : "-"
                });
            }
        }
        
        totalItemsLabel.setText("Items to buy: " + tableModel.getRowCount());
    }
    
    private void generateShoppingList() {
        // Validate there are items to create a shopping list for
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No low stock items found. Cannot generate an empty shopping list.");
            return;
        }

        // Validate store selection
        Store selectedStore = (Store) storeCombo.getSelectedItem();
        if (selectedStore == null || selectedStore.getStoreId() == 0) {
            JOptionPane.showMessageDialog(this, "Please select a store");
            return;
        }
        
        // Get date
        java.util.Date selectedDate = (java.util.Date) dateSpinner.getValue();
        LocalDate expectedDate = new java.sql.Date(selectedDate.getTime()).toLocalDate();
        
        // Create procurement record
        Procurement procurement = new Procurement();
        procurement.setInitiatedBy(currentUser.getUserId());
        procurement.setExpectedCompletionDate(expectedDate);
        procurement.setStatus("Pending");
        procurement.setStoreVisited(selectedStore.getStoreName());
        procurement.setTotalEstimatedCost(0);
        
        int procurementId = procurementDAO.createProcurement(procurement);
        
        if (procurementId > 0) {
            // Add low stock items to procurement
            boolean itemsAdded = procurementItemDAO.addLowStockItemsToProcurement(procurementId);
            
            if (itemsAdded) {
                createdProcurementId = procurementId;
                
                // Show success message with shopping list preview
                StringBuilder message = new StringBuilder();
                message.append("✅ Shopping List Created!\n\n");
                message.append("Procurement ID: ").append(procurementId).append("\n");
                message.append("Store: ").append(selectedStore.getStoreName()).append("\n");
                message.append("Expected by: ").append(expectedDate).append("\n\n");
                message.append("Items to buy:\n");
                
                List<ProcurementItem> items = procurementItemDAO.getItemsByProcurement(procurementId);
                for (ProcurementItem pi : items) {
                    message.append("• ").append(pi.getItemName())
                           .append(" - ").append(pi.getQuantityRequired()).append(" units\n");
                }
                
                JOptionPane.showMessageDialog(this, message.toString());
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add items to shopping list");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create shopping list");
        }
    }
    
    public int getCreatedProcurementId() {
        return createdProcurementId;
    }
}