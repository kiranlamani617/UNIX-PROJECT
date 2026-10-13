package model;

public class OrderItem {
    private MenuItem menuItem;
    private int quantity;

    public OrderItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void addQuantity(int count) {
        this.quantity += count;
    }

    public double getTotalPrice() {
        return menuItem.getPrice() * quantity;
    }

    @Override
    public String toString() {
        return String.format("%-22s x%2d   @ $%-6.2f = $%.2f", 
                menuItem.getName(), quantity, menuItem.getPrice(), getTotalPrice());
    }
}
