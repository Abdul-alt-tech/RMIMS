package com.nalex.rmims.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Procurement {
    private int procurementId;
    private int initiatedBy;
    private String initiatorName; // for display
    private LocalDateTime initiationDate;
    private LocalDate expectedCompletionDate;
    private String status; // "Pending", "In Progress", "Completed"
    private String storeVisited;
    private double totalEstimatedCost;
    private LocalDateTime completedDate;
    
    public Procurement() {}
    
    // Getters and Setters
    public int getProcurementId() { return procurementId; }
    public void setProcurementId(int procurementId) { this.procurementId = procurementId; }
    
    public int getInitiatedBy() { return initiatedBy; }
    public void setInitiatedBy(int initiatedBy) { this.initiatedBy = initiatedBy; }
    
    public String getInitiatorName() { return initiatorName; }
    public void setInitiatorName(String initiatorName) { this.initiatorName = initiatorName; }
    
    public LocalDateTime getInitiationDate() { return initiationDate; }
    public void setInitiationDate(LocalDateTime initiationDate) { this.initiationDate = initiationDate; }
    
    public LocalDate getExpectedCompletionDate() { return expectedCompletionDate; }
    public void setExpectedCompletionDate(LocalDate expectedCompletionDate) { this.expectedCompletionDate = expectedCompletionDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getStoreVisited() { return storeVisited; }
    public void setStoreVisited(String storeVisited) { this.storeVisited = storeVisited; }
    
    public double getTotalEstimatedCost() { return totalEstimatedCost; }
    public void setTotalEstimatedCost(double totalEstimatedCost) { this.totalEstimatedCost = totalEstimatedCost; }
    
    public LocalDateTime getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDateTime completedDate) { this.completedDate = completedDate; }
}