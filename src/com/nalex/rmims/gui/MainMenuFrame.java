package com.nalex.rmims.gui;

import com.nalex.rmims.dao.ItemDAO;
import com.nalex.rmims.model.Item;
import com.nalex.rmims.model.User;
import com.nalex.rmims.gui.InventoryPanel;
import com.nalex.rmims.gui.ProcurementDialog;
import com.nalex.rmims.gui.StoresPanel;
import com.nalex.rmims.gui.ProcurementPanel;
import com.nalex.rmims.gui.ReportsPanel;
import com.nalex.rmims.gui.AdminPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MainMenuFrame extends JFrame {
    
    private User currentUser;
    private JLabel welcomeLabel;
    private JPanel dashboardPanel;
    private ItemDAO itemDAO;
    private InventoryPanel inventoryPanel;
    private ProcurementPanel procurementPanel;
    private ReportsPanel reportsPanel;
    private StoresPanel storesPanel;
    private JTabbedPane tabbedPane;
    
    public MainMenuFrame(User user) {
        this.currentUser = user;
        this.itemDAO = new ItemDAO();
        
        // Frame settings
        setTitle("RMIMS - GROUP_NO 01");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create menu bar
        createMenuBar();
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Top panel with welcome and logout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.PRIMARY);
        topPanel.setPreferredSize(new Dimension(getWidth(), 60));
        
        welcomeLabel = new JLabel("  Welcome, " + currentUser.getFullName() + " (" + currentUser.getRole() + ")");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        welcomeLabel.setForeground(Color.WHITE);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 12));
        logoutButton.addActionListener(e -> logout());
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(UITheme.PRIMARY);
        rightPanel.add(logoutButton);
        
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);
        
        // Create tabbed pane for main content
        this.tabbedPane = new JTabbedPane();
        // Use an emoji-capable font when available so tab emojis render correctly
        Font defaultTabFont = new Font("Arial", Font.PLAIN, 14);
        Font emojiFont = null;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 14);
        } else if (os.contains("mac")) {
            emojiFont = new Font("Apple Color Emoji", Font.PLAIN, 14);
        } else {
            emojiFont = new Font("Noto Color Emoji", Font.PLAIN, 14);
        }

        if (emojiFont != null && emojiFont.canDisplayUpTo("📊") == -1) {
            this.tabbedPane.setFont(emojiFont);
        } else {
            this.tabbedPane.setFont(defaultTabFont);
        }
        
        // Dashboard tab
        dashboardPanel = createDashboardPanel();
        tabbedPane.addTab("📊 Dashboard", dashboardPanel);
        
        // Inventory tab - use real InventoryPanel
        this.inventoryPanel = new InventoryPanel(currentUser);
        tabbedPane.addTab("📦 Inventory", this.inventoryPanel);

        this.storesPanel = new StoresPanel(currentUser);
        tabbedPane.addTab("🏪 Stores", this.storesPanel);

        this.procurementPanel = new ProcurementPanel(currentUser);
        tabbedPane.addTab("🛒 Procurement", this.procurementPanel);

        this.reportsPanel = new ReportsPanel(currentUser);
        tabbedPane.addTab("📈 Reports", this.reportsPanel);
        
        
        // Admin tab - only for CEO
        if (currentUser.isCEO()) {
            AdminPanel adminPanel = new AdminPanel(currentUser);
            tabbedPane.addTab("⚙️ Admin", adminPanel);
        }
        
        // Status bar
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        statusPanel.setBackground(UITheme.NEUTRAL);
        
        JLabel statusLabel = new JLabel("Connected to database | " + java.time.LocalDate.now());
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        statusPanel.add(statusLabel);
        
        // Assemble
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        logoutItem.addActionListener(e -> logout());
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Welcome message
        JLabel welcomeMsg = new JLabel("Welcome to Raw Material Inventory Management System");
        welcomeMsg.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeMsg.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        
        // Get data
        List<Item> allItems = itemDAO.getAllItems();
        List<Item> lowStockItems = itemDAO.getLowStockItems();
        
        // Total items card
        JPanel totalCard = createStatCard("Total Items", String.valueOf(allItems.size()), "📦", new Color(52, 152, 219));
        
        // Low stock card
        JPanel lowStockCard = createStatCard("Low Stock Items", String.valueOf(lowStockItems.size()), "⚠️", new Color(231, 76, 60));
        
        // Categories card (you can enhance this)
        JPanel categoriesCard = createStatCard("Categories", "3", "🏷️", new Color(46, 204, 113));
        
        statsPanel.add(totalCard);
        statsPanel.add(lowStockCard);
        statsPanel.add(categoriesCard);
        
        // Low stock table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Items Below Minimum Level"));
        
        String[] columns = {"Item Name", "Current Quantity", "Minimum Level", "Quantity Needed", "Location"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (Item item : lowStockItems) {
            model.addRow(new Object[]{
                item.getItemName(),
                item.getCurrentQuantity(),
                item.getMinimumLevel(),
                item.getQuantityNeeded(),
                item.getLocationInSchool() != null ? item.getLocationInSchool() : "Not Set"
            });
        }
        
        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 200));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Quick actions
        JPanel actionsPanel = new JPanel(new FlowLayout());
        actionsPanel.setBorder(BorderFactory.createTitledBorder("Quick Actions"));
        
        JButton refreshAllBtn = createActionButton("Refresh All", "🔄");
        JButton withdrawBtn = createActionButton("Withdraw Items", "📤");
        JButton receiveBtn = createActionButton("Receive Stock", "📥");
        JButton shoppingBtn = createActionButton("Shopping List", "🛒");
        JButton reportBtn = createActionButton("View Reports", "📊");

        refreshAllBtn.addActionListener(e -> refreshAllSections());
        
        withdrawBtn.addActionListener(e -> {
            // Switch to Inventory tab and open withdraw dialog for selected item
            tabbedPane.setSelectedComponent(this.inventoryPanel);
            this.inventoryPanel.withdrawSelectedItem();
        });

        receiveBtn.addActionListener(e -> {
            // Switch to Procurement tab (use procurement panel for receiving/stock actions)
            tabbedPane.setSelectedComponent(this.procurementPanel);
        });

        shoppingBtn.addActionListener(e -> openProcurementDialog());

        reportBtn.addActionListener(e -> {
            // Switch to Reports tab
            tabbedPane.setSelectedComponent(this.reportsPanel);
        });
        
        actionsPanel.add(refreshAllBtn);
        actionsPanel.add(withdrawBtn);
        actionsPanel.add(receiveBtn);
        actionsPanel.add(shoppingBtn);
        actionsPanel.add(reportBtn);
        
        // Assemble dashboard
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(statsPanel, BorderLayout.NORTH);
        centerPanel.add(tablePanel, BorderLayout.CENTER);
        centerPanel.add(actionsPanel, BorderLayout.SOUTH);
        
        panel.add(welcomeMsg, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void refreshAllSections() {
        if (inventoryPanel != null) inventoryPanel.refreshTable();
        if (storesPanel != null) storesPanel.refreshTable();
        if (procurementPanel != null) procurementPanel.refreshTable();
        if (reportsPanel != null) reportsPanel.refreshData();

        // Rebuild dashboard stats and low stock table
        dashboardPanel = createDashboardPanel();
        tabbedPane.setComponentAt(0, dashboardPanel);
        tabbedPane.revalidate();
        tabbedPane.repaint();

        JOptionPane.showMessageDialog(this, "All sections refreshed successfully.");
    }

    private JPanel createStatCard(String title, String value, String icon, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(getEmojiFont(40f));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setForeground(color);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(iconLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);
        
        return card;
    }

    // Returns a font that can display color emoji when available, falling back to the given default.
    private Font getEmojiFont(float size) {
        String os = System.getProperty("os.name").toLowerCase();
        Font candidate;
        if (os.contains("win")) {
            candidate = new Font("Segoe UI Emoji", Font.PLAIN, (int) size);
        } else if (os.contains("mac")) {
            candidate = new Font("Apple Color Emoji", Font.PLAIN, (int) size);
        } else {
            candidate = new Font("Noto Color Emoji", Font.PLAIN, (int) size);
        }

        if (candidate != null && candidate.canDisplayUpTo("📦") == -1) {
            return candidate.deriveFont(size);
        }

        return new Font("Arial", Font.PLAIN, (int) size);
    }
    
    private JButton createActionButton(String text, String icon) {
        JButton button = new JButton(icon + " " + text);
        button.setPreferredSize(new Dimension(150, 40));
        com.nalex.rmims.gui.UITheme.styleButton(button, "primary");
        return button;
    }
    
    private JPanel createPlaceholderPanel(String feature) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        JLabel label = new JLabel(feature + " - Coming Soon!");
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setForeground(new Color(150, 150, 150));
        
        panel.add(label, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void showAboutDialog() {
        String message = "Raw Material Inventory Management System\n" +
                        "Version 1.0\n" +
                        "Group: GROUP_NO\n" +
                        "Semester 1, 2026\n\n" +
                        "Developed for NALEX ACADEMIC SCHOOL\n" +
                        "© 2026 All Rights Reserved";
        
        JOptionPane.showMessageDialog(this,
            message,
            "About RMIMS",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Logout Confirmation",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }

    // Simple Icon implementation that draws a text string (used for emoji icons)
    private static class TextIcon implements Icon {
        private final String text;
        private final Font font;
        private final int width;
        private final int height;

        TextIcon(String text, Font font) {
            this.text = text;
            this.font = font != null ? font : new Font("Dialog", Font.PLAIN, 14);
            FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(this.font);
            this.width = fm.stringWidth(text);
            this.height = fm.getHeight();
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setFont(font);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            FontMetrics fm = g2.getFontMetrics();
            int ascent = fm.getAscent();
            g2.drawString(text, x, y + ascent);
            g2.dispose();
        }
    }
    private void openProcurementDialog() {
    ProcurementDialog dialog = new ProcurementDialog(this, currentUser);
    dialog.setVisible(true);
    
    if (dialog.getCreatedProcurementId() > 0) {
        JOptionPane.showMessageDialog(this, 
            "Shopping list created! You can now go to the store.", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}
}