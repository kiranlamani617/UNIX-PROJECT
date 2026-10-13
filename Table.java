package model;

public class Table {
    private int tableNumber;
    private int capacity;
    private boolean isOccupied;
    private Customer currentCustomer;

    public Table(int tableNumber, int capacity) {
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.isOccupied = false;
        this.currentCustomer = null;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public void assignCustomer(Customer customer) {
        this.currentCustomer = customer;
        this.isOccupied = true;
    }

    public Customer vacateTable() {
        Customer temp = this.currentCustomer;
        this.currentCustomer = null;
        this.isOccupied = false;
        return temp;
    }

    @Override
    public String toString() {
        if (isOccupied) {
            return String.format("Table #%d [Capacity: %d] - Occupied by %s", 
                    tableNumber, capacity, currentCustomer.getName());
        } else {
            return String.format("Table #%d [Capacity: %d] - [AVAILABLE]", tableNumber, capacity);
        }
    }
}
