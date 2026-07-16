package com.ecommerce;

/**
 * Represents a product available for purchase in the system.
 */
public class Product {
    private final String productId; // Immutable unique identifier
    private String name;
    private double price;

    public Product(String productId, String name, double price) {
        if (productId == null || productId.strip().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be null or empty.");
        }
        if (name == null || name.strip().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty.");
        }
        if (price < 0.0) {
            throw new IllegalArgumentException("Product price cannot be negative.");
        }
        this.productId = productId.strip();
        this.name = name.strip();
        this.price = price;
    }

    // Getters and Setters ensuring encapsulation
    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.strip().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty.");
        }
        this.name = name.strip();
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0.0) {
            throw new IllegalArgumentException("Product price cannot be negative.");
        }
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format("ID: %s | %s - $%.2f", productId, name, price);
    }
}
