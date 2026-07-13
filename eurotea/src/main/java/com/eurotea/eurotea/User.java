package com.eurotea.eurotea;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users") // Maps this class to the "users" table inside MySQL
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID like 1, 2, 3...
    private Long id;

    private String email;
    private String password; // TODO: Change to hashed password later for safety
    private String companyName;
    
    // This status field is very important! 
    // It can be 'PENDING' when they just registered, or 'APPROVED' after admin checks their file.
    private String status; 
    
    private String documentPath; // Saves the folder path where the uploaded PDF/Image file is stored

    // --- Getters and Setters below (Very standard, just generated them) ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }
}