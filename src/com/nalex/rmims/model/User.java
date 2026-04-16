package com.nalex.rmims.model;

public class User {
    private int userId;
    private String username;
    private String password; // Note: We'll store hashed password
    private String fullName;
    private String role; // "CEO", "Headteacher", "Maintenance"
    private String contactNumber;
    private String email;
    
    // Constructors
    public User() {}
    
    public User(int userId, String username, String fullName, String role) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }
    
    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    // Helper method to check role permissions
    public boolean isCEO() {
        return "CEO".equals(this.role);
    }
    
    public boolean isHeadteacher() {
        return "Headteacher".equals(this.role);
    }
    
    public boolean isMaintenance() {
        return "Maintenance".equals(this.role);
    }
    
    @Override
    public String toString() {
        return fullName + " (" + role + ")";
    }
}