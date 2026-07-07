package com.eurotea.eurotea;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity // 告訴 Spring Boot，這是一個資料庫資料表
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自動遞增 ID (1, 2, 3...)
    private Long id;
    
    private String name;
    private Double price;
    private Integer stockLevel;

    // Constructors 
    public Product() {}

    public Product(String name, Double price, Integer stockLevel) {
        this.name = name;
        this.price = price;
        this.stockLevel = stockLevel;
    }

    // Getters and Setters 
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getStockLevel() { return stockLevel; }
    public void setStockLevel(Integer stockLevel) { this.stockLevel = stockLevel; }
}