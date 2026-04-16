package com.nalex.rmims.gui;

import com.nalex.rmims.dao.CategoryDAO;
import com.nalex.rmims.dao.ItemDAO;
import com.nalex.rmims.dao.StoreDAO;
import com.nalex.rmims.model.Category;
import com.nalex.rmims.model.Item;
import com.nalex.rmims.model.Store;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AddItemDialog extends JDialog {
    
    private JTextField nameField;
    private JComboBox<Category> categoryCombo;
    private JTextField unitField;
    private JSpinner quantitySpinner;
    private JSpinner minLevelSpinner;
    private JComboBox<Store> storeCombo;
    private JTextField locationField;
    
    private ItemDAO itemDAO;
    private CategoryDAO categoryDAO;
    private StoreDAO storeDAO;
    
    private boolean saved = false;
    
    public AddItemDialog(JFrame parent) {
        super(parent, "Add New Item", true);
        
        itemDAO = new ItemDAO();
        categoryDAO = new CategoryDAO();
        storeDAO = new StoreDAO();
        
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        
        // Create form panel
        JPanel formPanel = createFormPanel();
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Row 0: Item Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Item Name:*"), gbc);
        
        gbc.gridx = 1;
        nameField = new JTextField(20);
        panel.add(nameField, gbc);
        
        // Row 1: Category
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Category:"), gbc);
        
        gbc.gridx = 1;
        categoryCombo = new JComboBox<>();
        // renderer to show category name safely
        categoryCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Category) {
                    Category c = (Category) value;
                    setText(c.getCategoryName() != null ? c.getCategoryName() : "");
                }
                return this;
            }
        });
        loadCategories();
        if (categoryCombo.getItemCount() > 0) categoryCombo.setSelectedIndex(0);
        panel.add(categoryCombo, gbc);
        
        // Row 2: Unit of Measure
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Unit of Measure:"), gbc);
        
        gbc.gridx = 1;
        unitField = new JTextField(20);
        panel.add(unitField, gbc);
        
        // Row 3: Current Quantity
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Current Quantity:"), gbc);
        
        gbc.gridx = 1;
        quantitySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
        panel.add(quantitySpinner, gbc);
        
        // Row 4: Minimum Level
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Minimum Level:"), gbc);
        
        gbc.gridx = 1;
        minLevelSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
        panel.add(minLevelSpinner, gbc);
        
        // Row 5: Preferred Store
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Preferred Store:"), gbc);
        
        gbc.gridx = 1;
        storeCombo = new JComboBox<>();
        // renderer to show store name safely
        storeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Store) {
                    Store s = (Store) value;
                    setText(s.getStoreName() != null ? s.getStoreName() : "");
                }
                return this;
            }
        });
        storeCombo.addItem(new Store(0, "-- None --", ""));
        loadStores();
        if (storeCombo.getItemCount() > 0) storeCombo.setSelectedIndex(0);
        panel.add(storeCombo, gbc);

        // If no categories or stores were populated, warn the user so they can check console logs
        int availableCategories = Math.max(0, categoryCombo.getItemCount() - 1);
        int availableStores = Math.max(0, storeCombo.getItemCount() - 1);
        if (availableCategories == 0 || availableStores == 0) {
            String msg = "A required lookup list is empty:\n" +
                         "Categories: " + availableCategories + " available\n" +
                         "Stores: " + availableStores + " available\n\n" +
                         "Please check the application console for database debug output.";
            JOptionPane.showMessageDialog(this, msg, "Lookup Data Missing", JOptionPane.WARNING_MESSAGE);
        }
        
        // Row 6: Location in School
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Location:"), gbc);
        
        gbc.gridx = 1;
        locationField = new JTextField(20);
        panel.add(locationField, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        com.nalex.rmims.gui.UITheme.styleButton(saveButton, "success");
        
        saveButton.addActionListener(e -> saveItem());
        com.nalex.rmims.gui.UITheme.styleButton(cancelButton, "neutral");
        cancelButton.addActionListener(e -> dispose());
        
        panel.add(saveButton);
        panel.add(cancelButton);
        
        return panel;
    }
    
    private void loadCategories() {
        try {
            categoryCombo.removeAllItems();
            categoryCombo.addItem(new Category(0, "-- Select Category --"));
            List<Category> categories = categoryDAO.getAllCategories();
            System.err.println("AddItemDialog.loadCategories: categories fetched = " + (categories != null ? categories.size() : "null"));
            if (categories != null) {
                for (Category c : categories) {
                    categoryCombo.addItem(c);
                }
            }
        } catch (Exception e) {
            System.err.println("AddItemDialog.loadCategories: error -> " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadStores() {
        try {
            List<Store> stores = storeDAO.getAllStores();
            System.err.println("AddItemDialog.loadStores: stores fetched = " + (stores != null ? stores.size() : "null"));
            if (stores != null) {
                for (Store s : stores) {
                    storeCombo.addItem(s);
                }
            }
        } catch (Exception e) {
            System.err.println("AddItemDialog.loadStores: error -> " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void saveItem() {
        // Validate required fields
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Item Name is required");
            return;
        }
        
        // Create item object
        Item item = new Item();
        item.setItemName(nameField.getText().trim());
        
        Category selectedCat = (Category) categoryCombo.getSelectedItem();
        if (selectedCat != null && selectedCat.getCategoryId() > 0) {
            item.setCategoryId(selectedCat.getCategoryId());
        }
        
        item.setUnitOfMeasure(unitField.getText().trim());
        item.setCurrentQuantity((Integer) quantitySpinner.getValue());
        item.setMinimumLevel((Integer) minLevelSpinner.getValue());
        
        Store selectedStore = (Store) storeCombo.getSelectedItem();
        if (selectedStore != null && selectedStore.getStoreId() > 0) {
            item.setPreferredStoreId(selectedStore.getStoreId());
        }
        
        item.setLocationInSchool(locationField.getText().trim());
        
        // Save to database
        boolean success = itemDAO.addItem(item);
        
        if (success) {
            JOptionPane.showMessageDialog(this, "Item added successfully!");
            saved = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add item");
        }
    }
    
    public boolean isSaved() {
        return saved;
    }
}