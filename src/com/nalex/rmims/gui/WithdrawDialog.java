package com.nalex.rmims.gui;

import com.nalex.rmims.dao.ItemDAO;
import com.nalex.rmims.dao.TransactionDAO;
import com.nalex.rmims.model.Item;

import javax.swing.*;
import java.awt.*;

public class WithdrawDialog extends JDialog {
    
    private Item item;
    private ItemDAO itemDAO;
    private TransactionDAO transactionDAO;
    
    private JLabel itemNameLabel;
    private JLabel currentQtyLabel;
    private JSpinner quantitySpinner;
    private JTextField staffNameField;
    private JTextField staffMobileField;
    private JTextArea notesArea;
    
    private boolean success = false;
    
    public WithdrawDialog(JFrame parent, Item item) {
        super(parent, "Withdraw Items", true);
        this.item = item;
        this.itemDAO = new ItemDAO();
        this.transactionDAO = new TransactionDAO();
        
        setSize(450, 400);
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
        gbc.gridwidth = 2;
        
        // Item info header
        gbc.gridx = 0;
        gbc.gridy = 0;
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Item Information"));
        
        infoPanel.add(new JLabel("Item:"));
        itemNameLabel = new JLabel(item.getItemName());
        itemNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(itemNameLabel);
        
        infoPanel.add(new JLabel("Current Quantity:"));
        currentQtyLabel = new JLabel(String.valueOf(item.getCurrentQuantity()));
        currentQtyLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(currentQtyLabel);
        
        panel.add(infoPanel, gbc);
        
        gbc.gridwidth = 1;
        
        // Quantity to withdraw
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Quantity to withdraw:*"), gbc);
        
        gbc.gridx = 1;
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, item.getCurrentQuantity(), 1));
        panel.add(quantitySpinner, gbc);
        
        // Staff Name
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Staff Name:*"), gbc);
        
        gbc.gridx = 1;
        staffNameField = new JTextField(20);
        panel.add(staffNameField, gbc);
        
        // Staff Mobile
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Staff Mobile:*"), gbc);
        
        gbc.gridx = 1;
        staffMobileField = new JTextField(20);
        panel.add(staffMobileField, gbc);
        
        // Notes
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Notes:"), gbc);
        
        gbc.gridx = 1;
        notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(notesArea);
        panel.add(scrollPane, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton withdrawButton = new JButton("Withdraw");
        JButton cancelButton = new JButton("Cancel");
        
        com.nalex.rmims.gui.UITheme.styleButton(withdrawButton, "accent");
        
        withdrawButton.addActionListener(e -> processWithdrawal());
        com.nalex.rmims.gui.UITheme.styleButton(cancelButton, "neutral");
        cancelButton.addActionListener(e -> dispose());
        
        panel.add(withdrawButton);
        panel.add(cancelButton);
        
        return panel;
    }
    
    private void processWithdrawal() {
        // Validate inputs
        int quantity = (Integer) quantitySpinner.getValue();
        String staffName = staffNameField.getText().trim();
        String staffMobile = staffMobileField.getText().trim();
        String notes = notesArea.getText().trim();
        
        if (staffName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter staff name");
            return;
        }
        
        if (staffMobile.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter staff mobile number");
            return;
        }
        
        if (quantity <= 0) {
            JOptionPane.showMessageDialog(this, "Quantity must be greater than 0");
            return;
        }
        
        if (quantity > item.getCurrentQuantity()) {
            JOptionPane.showMessageDialog(this, 
                "Cannot withdraw more than available quantity (" + item.getCurrentQuantity() + ")");
            return;
        }
        
        // Process withdrawal
        boolean recorded = transactionDAO.recordWithdrawal(
            item.getItemId(),
            quantity,
            staffName,
            staffMobile,
            notes
        );
        
        if (recorded) {
            JOptionPane.showMessageDialog(this, 
                "Withdrawal recorded successfully!\n" +
                "Item: " + item.getItemName() + "\n" +
                "Quantity: " + quantity + "\n" +
                "Staff: " + staffName + "\n" +
                "Mobile: " + staffMobile);
            
            success = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to record withdrawal");
        }
    }
    
    public boolean isSuccess() {
        return success;
    }
}