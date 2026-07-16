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
