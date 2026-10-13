import ds.CustomQueue;
import model.Customer;
import model.MenuItem;
import model.OrderItem;
import model.Table;
import service.RestaurantManager;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Terminal Interface for Restaurant Order & Table Management App.
 * Runs backend logic interactively in the terminal window.
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RestaurantManager manager = new RestaurantManager(5); // 5 Tables

        printHeader();

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Select an option (1-9): ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    handleCustomerArrival(scanner, manager);
                    break;
                case "2":
                    displayTables(manager);
                    break;
                case "3":
                    displayWaitingQueue(manager);
                    break;
                case "4":
                    displayMenu(manager);
                    break;
                case "5":
                    handleTakeOrder(scanner, manager);
                    break;
                case "6":
                    displayTableOrder(scanner, manager);
                    break;
                case "7":
                    handleCheckout(scanner, manager);
                    break;
                case "8":
                    seedDemoData(manager);
                    break;
                case "9":
                    System.out.println("\nThank you for using Restaurant Management System. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("\n[!] Invalid selection. Please enter a number between 1 and 9.");
            }

            if (running) {
                System.out.print("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    private static void printHeader() {
        System.out.println("==========================================================================");
        System.out.println("      RESTAURANT ORDER & TABLE MANAGEMENT SYSTEM (DSA JAVA BACKEND)");
        System.out.println("   Data Structures: Custom Linked-List Queue & HashMap Order Storage");
        System.out.println("==========================================================================");
    }

    private static void printMenu() {
        System.out.println("\n----------------------------- MAIN MENU ----------------------------------");
        System.out.println("1. Customer Walk-in (Assign Table / Add to Waiting Line Queue)");
        System.out.println("2. View Table Statuses");
        System.out.println("3. View Customer Waiting Line (Custom Linked-List Queue)");
        System.out.println("4. View Restaurant Menu & Prices");
        System.out.println("5. Take Order for a Table");
        System.out.println("6. View Current Order for a Table (HashMap Storage)");
        System.out.println("7. Generate Bill & Checkout (Frees Table + Auto-Assigns Queue)");
        System.out.println("8. Load Quick Demo Data (Fills Tables & Waiting Line for Testing)");
        System.out.println("9. Exit");
        System.out.println("--------------------------------------------------------------------------");
    }

    private static void handleCustomerArrival(Scanner scanner, RestaurantManager manager) {
        System.out.println("\n--- CUSTOMER WALK-IN ---");
        System.out.print("Enter Customer Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("[!] Name cannot be empty.");
            return;
        }

        System.out.print("Enter Party Size: ");
        int partySize = 1;
        try {
            partySize = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid party size. Defaulting to 1.");
        }

        String result = manager.handleWalkIn(name, partySize);
        System.out.println("\n" + result);
    }

    private static void displayTables(RestaurantManager manager) {
        System.out.println("\n--- TABLE STATUSES ---");
        List<Table> tables = manager.getTables();
        for (Table table : tables) {
            System.out.println(table);
        }
    }

    private static void displayWaitingQueue(RestaurantManager manager) {
        System.out.println("\n--- CUSTOMER WAITING LINE (CUSTOM LINKED-LIST QUEUE) ---");
        CustomQueue<Customer> queue = manager.getWaitingQueue();
        if (queue.isEmpty()) {
            System.out.println("Queue is empty. No customers currently waiting.");
        } else {
            System.out.println("Total Waiting Customers: " + queue.size());
            List<Customer> waitingList = queue.getAllElements();
            for (int i = 0; i < waitingList.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + waitingList.get(i));
            }
        }
    }

    private static void displayMenu(RestaurantManager manager) {
        System.out.println("\n--- RESTAURANT MENU ---");
        List<MenuItem> menu = manager.getMenu();
        for (MenuItem item : menu) {
            System.out.println(item);
        }
    }

    private static void handleTakeOrder(Scanner scanner, RestaurantManager manager) {
        System.out.println("\n--- TAKE ORDER FOR A TABLE ---");
        System.out.print("Enter Table Number (1-5): ");
        int tableNum;
        try {
            tableNum = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid Table Number.");
            return;
        }

        displayMenu(manager);

        System.out.print("\nEnter Menu Item Code [1-8]: ");
        int itemCode;
        try {
            itemCode = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid Item Code.");
            return;
        }

        System.out.print("Enter Quantity: ");
        int quantity;
        try {
            quantity = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid Quantity.");
            return;
        }

        String status = manager.addOrderItem(tableNum, itemCode, quantity);
        System.out.println("\n" + status);
    }

    private static void displayTableOrder(Scanner scanner, RestaurantManager manager) {
        System.out.println("\n--- VIEW TABLE ORDERS (HASHMAP LOOKUP) ---");
        System.out.print("Enter Table Number (1-5): ");
        int tableNum;
        try {
            tableNum = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid Table Number.");
            return;
        }

        Table table = manager.getTable(tableNum);
        if (table == null) {
            System.out.println("[!] Invalid Table Number.");
            return;
        }
        if (!table.isOccupied()) {
            System.out.println("Table #" + tableNum + " is currently VACANT.");
            return;
        }

        System.out.println("Table #" + tableNum + " Customer: " + table.getCurrentCustomer().getName());
        Map<Integer, List<OrderItem>> tableOrders = manager.getTableOrders();
        List<OrderItem> orders = tableOrders.get(tableNum);

        if (orders == null || orders.isEmpty()) {
            System.out.println("No items ordered yet for Table #" + tableNum + ".");
        } else {
            System.out.println("Current Items Ordered:");
            double total = 0;
            for (OrderItem item : orders) {
                System.out.println("  - " + item);
                total += item.getTotalPrice();
            }
            System.out.printf("Current Subtotal: $%.2f\n", total);
        }
    }

    private static void handleCheckout(Scanner scanner, RestaurantManager manager) {
        System.out.println("\n--- GENERATE BILL & CHECKOUT ---");
        System.out.print("Enter Table Number (1-5): ");
        int tableNum;
        try {
            tableNum = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid Table Number.");
            return;
        }

        String billOutput = manager.generateBillAndCheckout(tableNum);
        System.out.println(billOutput);
    }

    private static void seedDemoData(RestaurantManager manager) {
        System.out.println("\n--- SEEDING DEMO DATA ---");
        // Fill all 5 tables
        manager.handleWalkIn("Alice Smith", 2);
        manager.handleWalkIn("Bob Johnson", 4);
        manager.handleWalkIn("Charlie Davis", 2);
        manager.handleWalkIn("Diana Prince", 3);
        manager.handleWalkIn("Ethan Hunt", 1);

        // Add 2 customers to the waiting queue
        manager.handleWalkIn("Fiona Gallagher", 2);
        manager.handleWalkIn("George Clark", 4);

        // Add orders for Table 1 and Table 2
        manager.addOrderItem(1, 1, 2); // 2 Pizza
        manager.addOrderItem(1, 6, 2); // 2 Tea
        manager.addOrderItem(2, 2, 3); // 3 Burgers
        manager.addOrderItem(2, 5, 1); // 1 Garlic bread
        manager.addOrderItem(2, 7, 3); // 3 Coffee

        System.out.println("Demo Data Loaded Successfully!");
        System.out.println("- 5 Tables Occupied (Alice, Bob, Charlie, Diana, Ethan)");
        System.out.println("- 2 Customers in Custom Linked-List Queue (Fiona, George)");
        System.out.println("- Orders added to Table 1 & Table 2.");
    }
}
