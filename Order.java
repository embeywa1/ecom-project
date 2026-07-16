package com.ecommerce.orders;

import com.ecommerce.Customer;
import com.ecommerce.Product;
import java.util.List;

/**
 * Encapsulates an immutable snapshot of an order record.
 */
public class Order {
    private final String orderId;
    private final Customer customer;
    private final List<Product> orderedProducts;
    private final double orderTotal;
    private String orderStatus;

    public Order(String orderId, Customer customer) {
        if (orderId == null || orderId.strip().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be null or empty.");
        }
        if (customer == null) {
            throw new IllegalArgumentException("Order must be associated with a valid customer reference.");
        }
        if (customer.getShoppingCart().isEmpty()) {
            // Checked exception emulation or direct validation control
            throw new IllegalStateException("Cannot place an order with an empty shopping cart.");
        }

        this.orderId = orderId.strip();
        this.customer = customer;
        this.orderedProducts = customer.getShoppingCart(); // Snapshot current products
        this.orderTotal = customer.calculateTotalCost();
        this.orderStatus = "Pending Processing";
        
        // Post-order placement operation lifecycle
        customer.clearCart();
    }

    public void updateStatus(String status) {
        if (status == null || status.strip().isEmpty()) {
            throw new IllegalArgumentException("Status updates cannot be blank.");
        }
        this.orderStatus = status.strip();
    }

    public String generateOrderSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("========================================\n")
               .append("ORDER SUMMARY\n")
               .append("========================================\n")
               .append("Order ID   : ").append(orderId).append("\n")
               .append("Status     : ").append(orderStatus).append("\n")
               .append("Customer   : ").append(customer.getName()).append(" (ID: ")
               .append(customer.getCustomerId()).append(")\n")
               .append("Products Ordered:\n");

        for (Product product : orderedProducts) {
            summary.append(" - ").append(product.getName())
                   .append(" ($").append(String.format("%.2f", product.getPrice())).append(")\n");
        }
        
        summary.append("----------------------------------------\n")
               .append("Grand Total: $").append(String.format("%.2f", orderTotal)).append("\n")
               .append("========================================");
               
        return summary.toString();
    }

    // Getters
    public String getOrderId() {
        return orderId;
    }

    public double getOrderTotal() {
        return orderTotal;
    }
}
