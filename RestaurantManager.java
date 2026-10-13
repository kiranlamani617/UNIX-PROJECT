package service;

import ds.CustomQueue;
import model.Customer;
import model.MenuItem;
import model.OrderItem;
import model.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class managing restaurant operations:
 * - Table assignments
 * - Custom Linked-List Waiting Queue
 * - HashMap for Table Orders mapping Table # -> List<OrderItem>
 * - Menu management and Billing
 */
public class RestaurantManager {
    private List<Table> tables;
    private CustomQueue<Customer> waitingQueue;
    private Map<Integer, List<OrderItem>> tableOrders; // HashMap requirement
    private List<MenuItem> menu;
    private int customerIdCounter;

    public RestaurantManager(int numberOfTables) {
        this.tables = new ArrayList<>();
        for (int i = 1; i <= numberOfTables; i++) {
            tables.add(new Table(i, 4)); // Default table capacity of 4
        }
        this.waitingQueue = new CustomQueue<>();
        this.tableOrders = new HashMap<>();
        for (int i = 1; i <= numberOfTables; i++) {
            tableOrders.put(i, new ArrayList<>());
        }
        this.menu = new ArrayList<>();
        this.customerIdCounter = 101;
        initMenu();
    }

    private void initMenu() {
        menu.add(new MenuItem(1, "Margherita Pizza", "Main", 12.99));
        menu.add(new MenuItem(2, "Cheeseburger Deluxe", "Main", 10.49));
        menu.add(new MenuItem(3, "Creamy Alfredo Pasta", "Main", 11.99));
        menu.add(new MenuItem(4, "Caesar Salad", "Appetizer", 6.99));
        menu.add(new MenuItem(5, "Garlic Bread", "Appetizer", 4.99));
        menu.add(new MenuItem(6, "Iced Lemon Tea", "Beverage", 2.99));
        menu.add(new MenuItem(7, "Fresh Espresso", "Beverage", 3.49));
        menu.add(new MenuItem(8, "Chocolate Lava Cake", "Dessert", 5.99));
    }

    public List<MenuItem> getMenu() {
        return menu;
    }

    public List<Table> getTables() {
        return tables;
    }

    public CustomQueue<Customer> getWaitingQueue() {
        return waitingQueue;
    }

    public Map<Integer, List<OrderItem>> getTableOrders() {
        return tableOrders;
    }

    /**
     * Handles walk-in customer arrival.
     * Assigns lowest available table or enqueues to waiting line if full.
     */
    public String handleWalkIn(String name, int partySize) {
        Customer customer = new Customer(customerIdCounter++, name, partySize);

        // Find available table
        for (Table table : tables) {
            if (!table.isOccupied()) {
                table.assignCustomer(customer);
                return "SUCCESS: Assigned " + customer.getName() + " to Table #" + table.getTableNumber() + ".";
            }
        }

        // All tables full -> Enqueue in Custom Queue
        waitingQueue.enqueue(customer);
        return "ALL TABLES FULL: Added " + customer.getName() + " to the Customer Waiting Line (Position #" + waitingQueue.size() + ").";
    }

    /**
     * Add order item to a table. Uses HashMap<Integer, List<OrderItem>>.
     */
    public String addOrderItem(int tableNumber, int menuItemId, int quantity) {
        Table table = getTable(tableNumber);
        if (table == null) {
            return "ERROR: Invalid Table Number #" + tableNumber;
        }
        if (!table.isOccupied()) {
            return "ERROR: Table #" + tableNumber + " is currently vacant. Assign customer before taking orders.";
        }

        MenuItem selectedItem = null;
        for (MenuItem item : menu) {
            if (item.getId() == menuItemId) {
                selectedItem = item;
                break;
            }
        }

        if (selectedItem == null) {
            return "ERROR: Menu Item ID " + menuItemId + " not found.";
        }

        List<OrderItem> orders = tableOrders.get(tableNumber);
        boolean exists = false;
        for (OrderItem orderItem : orders) {
            if (orderItem.getMenuItem().getId() == menuItemId) {
                orderItem.addQuantity(quantity);
                exists = true;
                break;
            }
        }

        if (!exists) {
            orders.add(new OrderItem(selectedItem, quantity));
        }

        return "SUCCESS: Added " + quantity + "x " + selectedItem.getName() + " to Table #" + tableNumber + ".";
    }

    /**
     * Generates bill for table, frees table, and auto-assigns next waiting customer from queue.
     */
    public String generateBillAndCheckout(int tableNumber) {
        Table table = getTable(tableNumber);
        if (table == null) {
            return "ERROR: Invalid Table Number #" + tableNumber;
        }
        if (!table.isOccupied()) {
            return "ERROR: Table #" + tableNumber + " is already vacant.";
        }

        Customer customer = table.getCurrentCustomer();
        List<OrderItem> orders = tableOrders.get(tableNumber);

        StringBuilder bill = new StringBuilder();
        bill.append("\n======================================================\n");
        bill.append("                 RESTAURANT RECEIPT                   \n");
        bill.append("======================================================\n");
        bill.append("Table #: ").append(tableNumber).append("\n");
        bill.append("Customer: ").append(customer.getName()).append(" (Party of ").append(customer.getPartySize()).append(")\n");
        bill.append("------------------------------------------------------\n");

        double subtotal = 0.0;
        if (orders.isEmpty()) {
            bill.append("No orders placed.\n");
        } else {
            for (OrderItem item : orders) {
                bill.append(String.format("%-22s x%-2d  $%-6.2f  $%.2f\n", 
                        item.getMenuItem().getName(), item.getQuantity(), 
                        item.getMenuItem().getPrice(), item.getTotalPrice()));
                subtotal += item.getTotalPrice();
            }
        }

        double tax = subtotal * 0.05; // 5% tax
        double serviceCharge = subtotal * 0.05; // 5% service charge
        double grandTotal = subtotal + tax + serviceCharge;

        bill.append("------------------------------------------------------\n");
        bill.append(String.format("Subtotal:       $%.2f\n", subtotal));
        bill.append(String.format("Tax (5%%):        $%.2f\n", tax));
        bill.append(String.format("Service (5%%):    $%.2f\n", serviceCharge));
        bill.append("------------------------------------------------------\n");
        bill.append(String.format("GRAND TOTAL:    $%.2f\n", grandTotal));
        bill.append("======================================================\n");

        // Vacate table and clear order history in HashMap
        table.vacateTable();
        tableOrders.put(tableNumber, new ArrayList<>());

        bill.append("\n[STATUS] Table #").append(tableNumber).append(" is now VACANT.");

        // Check if customers are waiting in the CustomQueue
        if (!waitingQueue.isEmpty()) {
            Customer nextCustomer = waitingQueue.dequeue();
            table.assignCustomer(nextCustomer);
            bill.append("\n[AUTO-ASSIGN] Dequeued ").append(nextCustomer.getName())
                .append(" from waiting line and assigned to Table #").append(tableNumber).append("!");
        }

        return bill.toString();
    }

    public Table getTable(int tableNumber) {
        for (Table t : tables) {
            if (t.getTableNumber() == tableNumber) {
                return t;
            }
        }
        return null;
    }
}
