package com.eurotea.eurotea;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users") // create a table named "users" in the database
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private String email;
    private String password;
    private String pdfPath;    // the path to the uploaded PDF file
    private String status;     // the status of the account: PENDING (under review) or APPROVED (activated)

    // Constructors
    public User() {}

    public User(String companyName, String email, String password, String pdfPath, String status) {
        this.companyName = companyName;
        this.email = email;
        this.password = password;
        this.pdfPath = pdfPath;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPdfPath() { return pdfPath; }
    public void setPdfPath(String pdfPath) { this.pdfPath = pdfPath; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}