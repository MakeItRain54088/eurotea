package com.eurotea.eurotea;

// Pure Java object used to represent items inside the user's shopping cart (stored in Session)
public class CartItem {

    private Long productId;
    private String productName;
    private double priceUsed; // Can be regular price or wholesale price depending on business status
    private int quantity;

    public CartItem(Long productId, String productName, double priceUsed, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.priceUsed = priceUsed;
        this.quantity = quantity;
    }

    // Calculates subtotal for this item automatically
    public double getSubtotal() {
        return priceUsed * quantity;
    }

    // --- Getters and Setters ---

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPriceUsed() {
        return priceUsed;
    }

    public void setPriceUsed(double priceUsed) {
        this.priceUsed = priceUsed;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}