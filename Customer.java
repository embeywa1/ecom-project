package com.ecommerce;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer with an active shopping cart context.
 */
public class Customer {
    private final String customerId;
    private final String name;
    private final List<Product> shoppingCart;

    public Customer(String customerId, String name) {
        if (customerId == null || customerId.strip().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty.");
        }
        if (name == null || name.strip().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be null or empty.");
        }
        this.customerId = customerId.strip();
        this.name = name.strip();
        this.shoppingCart = new ArrayList<>();
    }

    public void addProductToCart(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Cannot add a null product reference to cart.");
        }
        shoppingCart.add(product);
        System.out.println(">> Added to cart: " + product.getName());
    }

    public void removeProductFromCart(Product product) {
        if (!shoppingCart.remove(product)) {
            System.err.println(">> Warning: Product not found in cart.");
        } else {
            System.out.println(">> Removed from cart: " + product.getName());
        }
    }

    public double calculateTotalCost() {
        double total = 0.0;
        for (Product product : shoppingCart) {
            total += product.getPrice();
        }
        return total;
    }

    public void clearCart() {
        this.shoppingCart.clear();
    }

    // Getters
    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public List<Product> getShoppingCart() {
        // Return a copy to protect internal representation mutation from outside the package
        return new ArrayList<>(shoppingCart);
    }
}
