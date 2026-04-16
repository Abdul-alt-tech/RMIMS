package com.nalex.rmims.gui;

import com.nalex.rmims.dao.ProcurementDAO;
import com.nalex.rmims.dao.ProcurementItemDAO;
import com.nalex.rmims.model.Procurement;
import com.nalex.rmims.model.ProcurementItem;
import com.nalex.rmims.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProcurementPanel extends JPanel {
    
    private User currentUser;
    private ProcurementDAO procurementDAO;
    private ProcurementItemDAO procurementItemDAO;
    private JTable procurementsTable;
    private DefaultTableModel tableModel;
    
    public ProcurementPanel(User user) {
        this.currentUser = user;
        this.procurementDAO = new ProcurementDAO();
        this.procurementItemDAO = new ProcurementItemDAO();
        
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
        add(new JScrollPane(procurementsTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Load data
        refreshTable();
    }
    
    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JButton newShoppingListBtn = new JButton("+ New Shopping List");
        com.nalex.rmims.gui.UITheme.styleButton(newShoppingListBtn, "success");
        newShoppingListBtn.addActionListener(e -> openNewShoppingList());
        
        JButton refreshBtn = new JButton("Refresh");
        com.nalex.rmims.gui.UITheme.styleButton(refreshBtn, "neutral");
        refreshBtn.addActionListener(e -> refreshTable());
        
        toolbar.add(newShoppingListBtn, BorderLayout.WEST);
        toolbar.add(refreshBtn, BorderLayout.EAST);
        
        return toolbar;
    }
    
    private void createTable() {
        String[] columns = {"ID", "Store", "Initiated By", "Date", "Expected", "Status", "Items"};
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        procurementsTable = new JTable(tableModel);
        procurementsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        procurementsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        procurementsTable.setRowHeight(25);
        
        // Add double-click listener
        procurementsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    viewProcurementDetails();
                }
            }
        });
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton viewBtn = new JButton("View Details");
        JButton receiveBtn = new JButton("Receive Stock");
        JButton deleteBtn = new JButton("Delete Shopping List");
        JButton printBtn = new JButton("Print Shopping List");

        com.nalex.rmims.gui.UITheme.styleButton(viewBtn, "primary");
        com.nalex.rmims.gui.UITheme.styleButton(receiveBtn, "accent");
        com.nalex.rmims.gui.UITheme.styleButton(deleteBtn, "danger");
        com.nalex.rmims.gui.UITheme.styleButton(printBtn, "neutral");

        viewBtn.addActionListener(e -> viewProcurementDetails());
        receiveBtn.addActionListener(e -> receiveStock());
        deleteBtn.addActionListener(e -> deleteShoppingList());
        printBtn.addActionListener(e -> printShoppingList());
        
        panel.add(viewBtn);
        panel.add(receiveBtn);
        panel.add(deleteBtn);
        panel.add(printBtn);
        
        return panel;
    }
    
    public void refreshTable() {
        tableModel.setRowCount(0);
        List<Procurement> procurements = procurementDAO.getAllProcurements();
        
        for (Procurement p : procurements) {
            List<ProcurementItem> items = procurementItemDAO.getItemsByProcurement(p.getProcurementId());
            long itemCount = items.stream().filter(i -> "To Buy".equals(i.getStatus())).count();
            
            tableModel.addRow(new Object[]{
                p.getProcurementId(),
                p.getStoreVisited() != null ? p.getStoreVisited() : "-",
                p.getInitiatorName(),
                p.getInitiationDate() != null ? p.getInitiationDate().toLocalDate() : "-",
                p.getExpectedCompletionDate() != null ? p.getExpectedCompletionDate() : "-",
                p.getStatus(),
                itemCount + " items"
            });
        }
    }
    
    private void openNewShoppingList() {
        ProcurementDialog dialog = new ProcurementDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), 
            currentUser
        );
        dialog.setVisible(true);
        
        if (dialog.getCreatedProcurementId() > 0) {
            refreshTable();
        }
    }
    
    private void viewProcurementDetails() {
        int row = procurementsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a procurement");
            return;
        }
        
        int procurementId = (int) tableModel.getValueAt(row, 0);
        String store = (String) tableModel.getValueAt(row, 1);
        String status = (String) tableModel.getValueAt(row, 5);
        
        List<ProcurementItem> items = procurementItemDAO.getItemsByProcurement(procurementId);
        
        StringBuilder details = new StringBuilder();
        details.append("Procurement #").append(procurementId).append("\n");
        details.append("Store: ").append(store).append("\n");
        details.append("Status: ").append(status).append("\n\n");
        details.append("Items:\n");
        
        for (ProcurementItem item : items) {
            details.append("• ").append(item.getItemName())
                   .append(" - Required: ").append(item.getQuantityRequired());
            
            if (item.getQuantityPurchased() > 0) {
                details.append(" (Purchased: ").append(item.getQuantityPurchased()).append(")");
            }
            
            details.append(" - ").append(item.getStatus()).append("\n");
        }
        
        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), 
                "Procurement Details", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void receiveStock() {
        int row = procurementsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a procurement");
            return;
        }

        int procurementId = (int) tableModel.getValueAt(row, 0);
        String status = (String) tableModel.getValueAt(row, 5);

        if (!"Pending".equals(status) && !"In Progress".equals(status)) {
            JOptionPane.showMessageDialog(this, "This procurement is already completed");
            return;
        }

        List<ProcurementItem> items = procurementItemDAO.getItemsToBuy(procurementId);

        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No items to receive");
            return;
        }

        int successCount = 0;
        for (ProcurementItem item : items) {
            boolean success = procurementItemDAO.markAsPurchased(
                    item.getProcurementItemId(),
                    item.getQuantityRequired(),
                    item.getUnitPrice() > 0 ? item.getUnitPrice() : 1.0
            );
            if (success) {
                successCount++;
            }
        }

        if (successCount == items.size()) {
            procurementDAO.updateProcurementStatus(procurementId, "Completed");
            JOptionPane.showMessageDialog(this, "All items received for procurement #" + procurementId);
        } else if (successCount > 0) {
            procurementDAO.updateProcurementStatus(procurementId, "In Progress");
            JOptionPane.showMessageDialog(this, String.format(
                    "Received %d of %d items for procurement #%d. Remaining items are still to buy.",
                    successCount, items.size(), procurementId
            ));
        } else {
            JOptionPane.showMessageDialog(this, "Failed to receive items for procurement #" + procurementId);
        }

        refreshTable();
    }

    private void deleteShoppingList() {
        int row = procurementsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a procurement");
            return;
        }

        if (!currentUser.isCEO()) {
            JOptionPane.showMessageDialog(this, "Only CEOs can delete shopping lists.");
            return;
        }

        int procurementId = (int) tableModel.getValueAt(row, 0);
        String status = (String) tableModel.getValueAt(row, 5);

        if (!"Pending".equals(status) && !"In Progress".equals(status)) {
            JOptionPane.showMessageDialog(this, "Only Pending/In Progress shopping lists can be deleted.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete procurement #" + procurementId + "? This cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean deleted = procurementDAO.deleteProcurement(procurementId);
        if (deleted) {
            JOptionPane.showMessageDialog(this, "Shopping list #" + procurementId + " deleted.");
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to delete shopping list #" + procurementId);
        }
    }

    private void printShoppingList() {
        int row = procurementsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a procurement");
            return;
        }

        int procurementId = (int) tableModel.getValueAt(row, 0);
        List<ProcurementItem> itemsToBuy = procurementItemDAO.getItemsToBuy(procurementId);
        if (itemsToBuy.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cannot print shopping list because there are no items.");
            return;
        }

        String list = procurementItemDAO.generateShoppingListText(procurementId);

        JTextArea textArea = new JTextArea(list);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), 
                "Shopping List", JOptionPane.INFORMATION_MESSAGE);
    }
}