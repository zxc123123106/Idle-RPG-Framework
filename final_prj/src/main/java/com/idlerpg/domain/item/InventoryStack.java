package com.idlerpg.domain.item;

public final class InventoryStack {
    private final ItemDefinition item;
    private int quantity;

    public InventoryStack(ItemDefinition item, int quantity) {
        if (item == null) {
            throw new IllegalArgumentException("Item is required.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        this.item = item;
        this.quantity = quantity;
    }

    public ItemDefinition getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void addQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        quantity += amount;
    }

    public void removeQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        if (amount > quantity) {
            throw new IllegalArgumentException("Cannot remove more items than the stack contains.");
        }
        quantity -= amount;
    }

    public int getTotalValue() {
        return item.value() * quantity;
    }
}
