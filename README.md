Technical Documentation: Simple E-commerce System

Language: Java (JDK 11+)
Environment: Terminal-First Execution (Kali Linux)
1. Architectural Overview & Modularity
The primary objective of this architecture is to demonstrate strict object-oriented encapsulation and namespace isolation using Java packages. By mapping business domains directly to isolated packages, the system prevents namespace collisions and controls structural access limits.
├── Main.java (Application Orchestrator)
└── com
    └── ecommerce (Core Domain Layer)
        ├── Customer.java
        ├── Product.java
        └── orders (Transactional Layer)
            └── Order.java

Component Decoupling
    • com.ecommerce: Houses foundational domain entities (Product and Customer). This layer is independent of the order-fulfillment lifecycle.
    • com.ecommerce.orders: A specialized sub-package dedicated to transaction fulfillment. It explicitly imports references from com.ecommerce, demonstrating clean uni-directional dependency flow.
    • Main (Default Root Workspace): Acts as the external consumer. It has zero visibility into package-private mechanics and interacts solely with the exposed Public API.
2. Component Blueprint & Data Design
2.1 Product Class (com.ecommerce.Product)
Represents an immutable-key inventory asset. It enforces structural integrity at creation time via defensive entry-validation rules.
    • Attributes:
        ◦ productId (private final String): Uniquely identifies the asset.
        ◦ name (private String): Structural designation.
        ◦ price (private double): Financial valuation.
    • Encapsulation Strategy: Fields are restricted using the private modifier. State modifications are safely handled via public setters containing invariant boundary checks (e.g., rejecting negative pricing).
2.2 Customer Class (com.ecommerce.Customer)
Manages user context and encapsulates state operations for an active shopping cart session.
    • Attributes:
        ◦ customerId (private final String): Unique account identifier.
        ◦ name (private final String): Customer identity.
        ◦ shoppingCart (private final List<Product>): Internal tracking collection.
    • Key Operations:
        ◦ addProductToCart(Product p): Safely Appends items.
        ◦ removeProductFromCart(Product p): Deletes items with error validation if an item is missing.
        ◦ calculateTotalCost(): Computes subtotal values on demand.
2.3 Order Class (com.ecommerce.orders.Order)
Handles transaction snapshots. Once generated, an order copies the customer's current cart states to ensure chronological immutability.
    • Attributes:
        ◦ orderId (private final String)
        ◦ customer (private final Customer)
        ◦ orderedProducts (private final List<Product>)
        ◦ orderTotal (private final double)
        ◦ orderStatus (private String)
    • Optimization Layer: Uses StringBuilder to compile system strings. This prevents memory leaks caused by repeatedly allocating new string fragments inside loops.
3. Data Validation & Defensive Exception Design
To prevent corrupted runtimes, the system uses defensive checks rather than failing silently.

Trigger Condition	Intercepting Method	Exception Thrown	Resolution/Handling Strategy
price < 0.0	Product() / setPrice()	IllegalArgumentException	Rejects mutation; preserves original value state.
name == null or empty	All Constructors	IllegalArgumentException	Halts initialization; prompts user for input string.
Checkout with empty cart	Order() Constructor	IllegalStateException	Intercepts broken workflows early; blocks empty transactions.
4. Access Modifier Matrix
The system implements strict accessibility restrictions across package boundaries:
    • public: Used on classes and business methods (processLogEntry, generateOrderSummary) to define the external entry points.
    • private: Used on all instance variables (productId, shoppingCart) to block direct external mutations, adhering to the principle of least privilege.
    • private final: Used on primary keys to guarantee thread-safe read-only states once an object is initialized.
5. Compilation & Execution Guide
Compilation Target
From your project workspace root directory, run the compiler with explicit file wildcards to resolve dependencies:
javac Main.java com/ecommerce/.java com/ecommerce/orders/.java
Execution
Run the compiled JVM bytecode using the root package lookup pointer:
java Main

6. Program Code
1. The com.ecommerce Package Components
Product.java
This class represents an individual inventory item. It implements thorough data validation within the constructor and setters to avoid malformed entity states.
package com.ecommerce;
/**
    • Represents a product available for purchase in the system. */ public class Product { private final String productId; // Immutable unique identifier private String name; private double price;
public Product(String productId, String name, double price) { if (productId == null || productId.strip().isEmpty()) { throw new IllegalArgumentException("Product ID cannot be null or empty."); } if (name == null || name.strip().isEmpty()) { throw new IllegalArgumentException("Product name cannot be null or empty."); } if (price < 0.0) { throw new IllegalArgumentException("Product price cannot be negative."); } this.productId = productId.strip(); this.name = name.strip(); this.price = price; }
// Getters and Setters ensuring encapsulation public String getProductId() { return productId; }
public String getName() { return name; }
public void setName(String name) { if (name == null || name.strip().isEmpty()) { throw new IllegalArgumentException("Product name cannot be null or empty."); } this.name = name.strip(); }
public double getPrice() { return price; }
public void setPrice(double price) { if (price < 0.0) { throw new IllegalArgumentException("Product price cannot be negative."); } this.price = price; }
@Override public String toString() { return String.format("ID: %s | %s - $%.2f", productId, name, price); } }
Customer.java
This class encapsulates user information and a dynamic shopping cart using standard array resizings or Java lists. It manages the mathematical computations for the cart contents.
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

2. The com.ecommerce.orders Package Component
Order.java
This class lives inside a separate, dedicated sub-package for ordering. It references classes from the sibling package (com.ecommerce) and builds robust summaries leveraging memory-friendly StringBuilder mechanics.
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
3. Orchestration Engine (Outside of Packages)
Main.java
This is your application's entry point. It imports the public components from the packages, drives execution, and encapsulates execution edge cases in proper try..catch blocks.
import com.ecommerce.Product;
import com.ecommerce.Customer;
import com.ecommerce.orders.Order;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- INITIALIZING E-COMMERCE ECOSYSTEM ---\n");

        // 1. Establish Inventory (Products)
        Product laptop = new Product("PROD-001", "High-End Developer Laptop", 1250.00);
        Product mouse = new Product("PROD-002", "Ergonomic Wireless Mouse", 45.50);
        Product monitor = new Product("PROD-003", "4K UltraWide Monitor", 450.00);

        System.out.println("Available Catalog Entries Today:");
        System.out.println(laptop);
        System.out.println(mouse);
        System.out.println(monitor);
        System.out.println();

        // 2. Setup Customer Entity
        Customer customer = new Customer("CUST-9901", "Mildred Embeywa");
        System.out.println("Session active for customer: " + customer.getName() + "\n");

        // 3. User Behavior Simulation (Browsing & Cart Actions)
        System.out.println("Executing Cart Workflows:");
        customer.addProductToCart(laptop);
        customer.addProductToCart(mouse);
        customer.addProductToCart(monitor);
        
        // Accidentally added an extra monitor, removing it safely
        customer.removeProductFromCart(monitor);

        System.out.printf("Current Estimated Subtotal: $%.2f\n\n", customer.calculateTotalCost());

        // 4. Order Generation & Defensive Exception Verification Block
        System.out.println("Executing Transaction Checkout Sequence:");
        try {
            // Generating valid transaction record mapping
            Order checkoutOrder = new Order("ORD-2026-X1", customer);
            
            // Print the immediate summary breakdown 
            System.out.println(checkoutOrder.generateOrderSummary());
            
            // Advance order lifecycle state
            checkoutOrder.updateStatus("Dispatched / In Route (Nairobi Hub)");
            System.out.println("\nUpdated System Status Update Alert:");
            System.out.println(checkoutOrder.generateOrderSummary());

        } catch (IllegalStateException | IllegalArgumentException e) {
            System.err.println("CRITICAL FAULT: Order Processing Halt -> " + e.getMessage());
        }

        // 5. Explicitly Triggering Edge-Case Exception Handling to show validation robustness
        System.out.println("\nTesting System Safeguards (Attempting Invalid Order on Empty Cart):");
        try {
            // This should fail explicitly because the prior order invocation cleared out the cart safely
            Order invalidOrder = new Order("ORD-FAULT", customer);
            System.out.println(invalidOrder.generateOrderSummary());
        } catch (IllegalStateException e) {
            System.out.println("Success Status: System intercepted bad checkout state cleanly.");
            System.out.println("Caught Expected Error Message: " + e.getMessage());
        }
    }
}


