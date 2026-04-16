package com.nalex.rmims.model;

public class ProcurementItem {
    private int procurementItemId;
    private int procurementId;
    private int itemId;
    private String itemName; // for display
    private int quantityRequired;
    private int quantityPurchased;
    private double unitPrice;
    private String status; // "To Buy" or "Purchased"
    
    public ProcurementItem() {}
    
    // Getters and Setters
    public int getProcurementItemId() { return procurementItemId; }
    public void setProcurementItemId(int procurementItemId) { this.procurementItemId = procurementItemId; }
    
    public int getProcurementId() { return procurementId; }
    public void setProcurementId(int procurementId) { this.procurementId = procurementId; }
    
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    
    public int getQuantityRequired() { return quantityRequired; }
    public void setQuantityRequired(int quantityRequired) { this.quantityRequired = quantityRequired; }
    
    public int getQuantityPurchased() { return quantityPurchased; }
    public void setQuantityPurchased(int quantityPurchased) { this.quantityPurchased = quantityPurchased; }
    
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public double getTotalPrice() {
        return quantityPurchased * unitPrice;
    }
}