package com.nalex.rmims.gui;

import com.nalex.rmims.dao.ItemDAO;
import com.nalex.rmims.dao.TransactionDAO;
import com.nalex.rmims.model.Item;
import com.nalex.rmims.model.Transaction;
import com.nalex.rmims.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportsPanel extends JPanel {
    
    private User currentUser;
    private TransactionDAO transactionDAO;
    private ItemDAO itemDAO;
    
    private JTabbedPane reportTabs;
    private JTable transactionTable;
    private DefaultTableModel transactionModel;
    
    private JTable stockTable;
    private DefaultTableModel stockModel;
    
    private JTable lowStockTable;
    private DefaultTableModel lowStockModel;
    
    private JComboBox<String> monthCombo;
    private JComboBox<String> yearCombo;
    
    private String currentSummaryText;
    private JTextArea summaryArea;
    
    public ReportsPanel(User user) {
        this.currentUser = user;
        this.transactionDAO = new TransactionDAO();
        this.itemDAO = new ItemDAO();
        
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create tabbed pane for different reports
        reportTabs = new JTabbedPane();
        reportTabs.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Add report tabs
        reportTabs.addTab("Transaction History", createTransactionPanel());
        reportTabs.addTab("Current Stock", createStockPanel());
        reportTabs.addTab("Low Stock Report", createLowStockPanel());
        reportTabs.addTab("Monthly Summary", createMonthlySummaryPanel());
        
        add(reportTabs, BorderLayout.CENTER);
    }
    
    private JPanel createTransactionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel with filters
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filters"));
        
        JButton refreshBtn = new JButton("Refresh");
        com.nalex.rmims.gui.UITheme.styleButton(refreshBtn, "neutral");
        refreshBtn.addActionListener(e -> loadTransactionData());
        
        filterPanel.add(new JLabel("Show all transactions:"));
        filterPanel.add(refreshBtn);
        
        // Create table
        String[] columns = {"ID", "Date", "Item", "Type", "Quantity", "Staff Name", "Mobile", "Notes"};
        transactionModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        transactionTable = new JTable(transactionModel);
        transactionTable.setFont(new Font("Arial", Font.PLAIN, 12));
        transactionTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        transactionTable.setRowHeight(25);
        
        // Set column widths
        transactionTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        transactionTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        transactionTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        transactionTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        transactionTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        transactionTable.getColumnModel().getColumn(5).setPreferredWidth(120);
        transactionTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        transactionTable.getColumnModel().getColumn(7).setPreferredWidth(200);
        
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load data
        loadTransactionData();
        
        return panel;
    }
    
    private JPanel createStockPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel with export button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Refresh");
        com.nalex.rmims.gui.UITheme.styleButton(refreshBtn, "neutral");
        refreshBtn.addActionListener(e -> loadStockData());
        topPanel.add(refreshBtn);
        
        // Create table
        String[] columns = {"ID", "Item Name", "Category", "Unit", "Current Qty", "Min Level", "Status", "Location"};
        stockModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        stockTable = new JTable(stockModel);
        stockTable.setFont(new Font("Arial", Font.PLAIN, 12));
        stockTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        stockTable.setRowHeight(25);
        
        // Set column widths
        stockTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        stockTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        stockTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        stockTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        stockTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        stockTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        stockTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        stockTable.getColumnModel().getColumn(7).setPreferredWidth(150);
        
        // Color rows based on stock status
        stockTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, 
                        isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    String status = table.getValueAt(row, 6).toString();
                    if (status.contains("LOW")) {
                        c.setBackground(UITheme.DANGER);
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(stockTable);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load data
        loadStockData();
        
        return panel;
    }
    
    private JPanel createLowStockPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Refresh");
        com.nalex.rmims.gui.UITheme.styleButton(refreshBtn, "neutral");
        refreshBtn.addActionListener(e -> loadLowStockData());
        topPanel.add(refreshBtn);
        
        // Create table
        String[] columns = {"Item Name", "Current Qty", "Min Level", "Quantity Needed", "Unit", "Location"};
        lowStockModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        lowStockTable = new JTable(lowStockModel);
        lowStockTable.setFont(new Font("Arial", Font.PLAIN, 12));
        lowStockTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        lowStockTable.setRowHeight(25);
        
        // Set column widths
        lowStockTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        lowStockTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        lowStockTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        lowStockTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        lowStockTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        lowStockTable.getColumnModel().getColumn(5).setPreferredWidth(150);
        
        // Color rows red
        lowStockTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, 
                        isSelected, hasFocus, row, column);
                
                    if (!isSelected) {
                        c.setBackground(UITheme.DANGER);
                    }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(lowStockTable);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load data
        loadLowStockData();
        
        return panel;
    }
    
    private JPanel createMonthlySummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Select Month"));
        
        String[] months = {"January", "February", "March", "April", "May", "June", 
                          "July", "August", "September", "October", "November", "December"};
        monthCombo = new JComboBox<>(months);
        monthCombo.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        
        String[] years = {"2024", "2025", "2026"};
        yearCombo = new JComboBox<>(years);
        yearCombo.setSelectedItem(String.valueOf(LocalDate.now().getYear()));
        
        JButton generateBtn = new JButton("Generate Summary");
        com.nalex.rmims.gui.UITheme.styleButton(generateBtn, "primary");
        generateBtn.addActionListener(e -> generateMonthlySummary());
        
        JButton emailBtn = new JButton("Send Email to CEO");
        com.nalex.rmims.gui.UITheme.styleButton(emailBtn, "success");
        emailBtn.addActionListener(e -> sendMonthlyReportEmail());
        
        filterPanel.add(new JLabel("Month:"));
        filterPanel.add(monthCombo);
        filterPanel.add(new JLabel("Year:"));
        filterPanel.add(yearCombo);
        filterPanel.add(generateBtn);
        filterPanel.add(emailBtn);
        
        // Summary area
        summaryArea = new JTextArea(15, 50);
        summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        summaryArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(summaryArea);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadTransactionData() {
        transactionModel.setRowCount(0);
        List<Transaction> transactions = transactionDAO.getAllTransactions();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Transaction t : transactions) {
            String dateStr = t.getTransactionDate() != null ? 
                t.getTransactionDate().format(formatter) : "-";
            
            transactionModel.addRow(new Object[]{
                t.getTransactionId(),
                dateStr,
                t.getItemName() != null ? t.getItemName() : "Item " + t.getItemId(),
                t.getTransactionType(),
                t.getQuantity(),
                t.getStaffName(),
                t.getStaffMobile(),
                t.getNotes() != null ? t.getNotes() : "-"
            });
        }
    }
    
    private void loadStockData() {
        stockModel.setRowCount(0);
        List<Item> items = itemDAO.getAllItems();
        
        for (Item item : items) {
            String status = item.isLowStock() ? "⚠️ LOW STOCK" : "OK";
            
            stockModel.addRow(new Object[]{
                item.getItemId(),
                item.getItemName(),
                item.getCategoryName() != null ? item.getCategoryName() : "-",
                item.getUnitOfMeasure() != null ? item.getUnitOfMeasure() : "-",
                item.getCurrentQuantity(),
                item.getMinimumLevel(),
                status,
                item.getLocationInSchool() != null ? item.getLocationInSchool() : "-"
            });
        }
    }
    
    private void loadLowStockData() {
        lowStockModel.setRowCount(0);
        List<Item> items = itemDAO.getLowStockItems();
        
        for (Item item : items) {
            int needed = item.getMinimumLevel() - item.getCurrentQuantity();
            if (needed < 0) needed = 0;
            
            lowStockModel.addRow(new Object[]{
                item.getItemName(),
                item.getCurrentQuantity(),
                item.getMinimumLevel(),
                needed,
                item.getUnitOfMeasure() != null ? item.getUnitOfMeasure() : "-",
                item.getLocationInSchool() != null ? item.getLocationInSchool() : "-"
            });
        }
    }
    
    public void refreshData() {
        loadTransactionData();
        loadStockData();
        loadLowStockData();
    }
    
    private void generateMonthlySummary() {
        int month = monthCombo.getSelectedIndex() + 1;
        int year = Integer.parseInt((String) yearCombo.getSelectedItem());
        
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);
        
        List<Transaction> transactions = transactionDAO.getTransactionsByDateRange(start, end);
        
        StringBuilder summary = new StringBuilder();
        summary.append("========================================\n");
        summary.append("MONTHLY SUMMARY - ").append(monthCombo.getSelectedItem())
                .append(" ").append(year).append("\n");
        summary.append("========================================\n\n");
        
        // Calculate totals
        int totalWithdrawals = 0;
        int totalReceipts = 0;
        int uniqueItems = 0;
        
        for (Transaction t : transactions) {
            if ("OUT".equals(t.getTransactionType())) {
                totalWithdrawals += t.getQuantity();
            } else if ("IN".equals(t.getTransactionType())) {
                totalReceipts += t.getQuantity();
            }
        }
        
        summary.append("Total Transactions: ").append(transactions.size()).append("\n");
        summary.append("Total Items Withdrawn: ").append(totalWithdrawals).append("\n");
        summary.append("Total Items Received: ").append(totalReceipts).append("\n\n");
        
        summary.append("Top Withdrawals by Staff:\n");
        summary.append("----------------------------------------\n");
        
        // Group by staff (simplified)
        transactions.stream()
            .filter(t -> "OUT".equals(t.getTransactionType()))
            .limit(5)
            .forEach(t -> summary.append("• ").append(t.getStaffName())
                .append(" - ").append(t.getQuantity()).append(" ")
                .append(t.getItemName()).append("\n"));
        
        currentSummaryText = summary.toString();
        summaryArea.setText(currentSummaryText);
    }
    
    private void sendMonthlyReportEmail() {
        System.out.println("sendMonthlyReportEmail method called");
        if (currentSummaryText == null || currentSummaryText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please generate the summary first.", 
                    "No Summary", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int month = monthCombo.getSelectedIndex() + 1;
        int year = Integer.parseInt((String) yearCombo.getSelectedItem());
        
        try {
            System.out.println("Calling EmailService.sendMonthlyReportEmail...");
            boolean success = com.nalex.rmims.util.EmailService.sendMonthlyReportEmail(currentSummaryText, month, year);
            System.out.println("EmailService returned: " + success);
            if (success) {
                JOptionPane.showMessageDialog(this, "Email sent successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Email sending was cancelled.", 
                        "Cancelled", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            System.out.println("Exception caught: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to send email: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}