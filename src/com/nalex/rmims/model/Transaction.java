package com.nalex.rmims.model;

import java.time.LocalDateTime;

public class Transaction {
    private int transactionId;
    private int itemId;
    private String itemName; // for display
    private String transactionType; // "IN" or "OUT"
    private int quantity;
    private LocalDateTime transactionDate;
    private String staffName;
    private String staffMobile;
    private String notes;
    
    public Transaction() {}
    
    // Getters and Setters
    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }
    
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }
    
    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }
    
    public String getStaffMobile() { return staffMobile; }
    public void setStaffMobile(String staffMobile) { this.staffMobile = staffMobile; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}