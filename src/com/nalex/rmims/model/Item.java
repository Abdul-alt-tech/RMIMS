package com.nalex.rmims.model;

public class Item {
    private int itemId;
    private String itemName;
    private int categoryId;
    private String categoryName; // for display purposes
    private String unitOfMeasure;
    private int currentQuantity;
    private int minimumLevel;
    private int preferredStoreId;
    private String storeName; // for display purposes
    private String locationInSchool;
    
    // Constructors
    public Item() {}
    
    public Item(int itemId, String itemName, int currentQuantity, int minimumLevel) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.currentQuantity = currentQuantity;
        this.minimumLevel = minimumLevel;
    }
    
    // Getters and Setters
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    
    public String getUnitOfMeasure() { return unitOfMeasure; }
    public void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }
    
    public int getCurrentQuantity() { return currentQuantity; }
    public void setCurrentQuantity(int currentQuantity) { this.currentQuantity = currentQuantity; }
    
    public int getMinimumLevel() { return minimumLevel; }
    public void setMinimumLevel(int minimumLevel) { this.minimumLevel = minimumLevel; }
    
    public int getPreferredStoreId() { return preferredStoreId; }
    public void setPreferredStoreId(int preferredStoreId) { this.preferredStoreId = preferredStoreId; }
    
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    
    public String getLocationInSchool() { return locationInSchool; }
    public void setLocationInSchool(String locationInSchool) { this.locationInSchool = locationInSchool; }
    
    // Business logic methods
    public boolean isLowStock() {
        return currentQuantity < minimumLevel;
    }
    
    public int getQuantityNeeded() {
        if (currentQuantity >= minimumLevel) {
            return 0;
        }
        return minimumLevel - currentQuantity;
    }
    
    @Override
    public String toString() {
        return itemName + " (Qty: " + currentQuantity + ")";
    }
}