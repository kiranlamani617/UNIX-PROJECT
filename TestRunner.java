import ds.CustomQueue;
import model.Customer;
import model.Table;
import service.RestaurantManager;

import java.util.List;

public class TestRunner {

    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println("            RUNNING BACKEND DSA & LOGIC VERIFICATION TESTS               ");
        System.out.println("==========================================================================\n");

        int passed = 0;
        int total = 0;

        // Test 1: Custom Linked-List Queue Operations
        total++;
        System.out.print("Test 1: Custom Linked-List Queue FIFO Operations... ");
        CustomQueue<String> testQueue = new CustomQueue<>();
        testQueue.enqueue("Alpha");
        testQueue.enqueue("Beta");
        testQueue.enqueue("Gamma");

        if (testQueue.size() == 3 
                && "Alpha".equals(testQueue.peek()) 
                && "Alpha".equals(testQueue.dequeue())
                && "Beta".equals(testQueue.dequeue())
                && testQueue.size() == 1) {
            System.out.println("PASSED [✓]");
            passed++;
        } else {
            System.out.println("FAILED [X]");
        }

        // Test 2: Table Assignment & Capacity Routing
        total++;
        System.out.print("Test 2: Walk-in Assignment & Queue Routing when full... ");
        RestaurantManager manager = new RestaurantManager(3); // 3 tables for quick test

        manager.handleWalkIn("User A", 2); // Table 1
        manager.handleWalkIn("User B", 2); // Table 2
        manager.handleWalkIn("User C", 4); // Table 3
        
        // 4th and 5th customer should go into CustomQueue
        manager.handleWalkIn("User D (Waiting)", 2);
        manager.handleWalkIn("User E (Waiting)", 3);

        if (manager.getTables().get(0).isOccupied() 
                && manager.getTables().get(1).isOccupied()
                && manager.getTables().get(2).isOccupied()
                && manager.getWaitingQueue().size() == 2) {
            System.out.println("PASSED [✓]");
            passed++;
        } else {
            System.out.println("FAILED [X]");
        }

        // Test 3: Order Taking with HashMap<Integer, List<OrderItem>>
        total++;
        System.out.print("Test 3: HashMap Table Order Storage... ");
        manager.addOrderItem(1, 1, 2); // 2 Pizzas for Table 1
        manager.addOrderItem(1, 6, 1); // 1 Tea for Table 1

        if (manager.getTableOrders().get(1).size() == 2) {
            System.out.println("PASSED [✓]");
            passed++;
        } else {
            System.out.println("FAILED [X]");
        }

        // Test 4: Checkout, Table Vacating & Auto-assignment from CustomQueue
        total++;
        System.out.print("Test 4: Checkout & Auto-Assign from CustomQueue... ");
        String bill = manager.generateBillAndCheckout(1);

        // After checkout: Table 1 should be occupied by User D (from waiting queue), and waiting queue size should be 1
        Table table1 = manager.getTable(1);
        if (table1.isOccupied() 
                && "User D (Waiting)".equals(table1.getCurrentCustomer().getName())
                && manager.getWaitingQueue().size() == 1
                && manager.getTableOrders().get(1).isEmpty()) {
            System.out.println("PASSED [✓]");
            passed++;
        } else {
            System.out.println("FAILED [X]");
        }

        System.out.println("\n--------------------------------------------------------------------------");
        System.out.println("TEST SUMMARY: " + passed + " / " + total + " TESTS PASSED SUCCESSFULLY.");
        System.out.println("--------------------------------------------------------------------------");
    }
}
