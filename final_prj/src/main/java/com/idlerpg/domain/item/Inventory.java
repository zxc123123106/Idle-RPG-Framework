package com.idlerpg.domain.item;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Inventory {
    private final Map<String, InventoryStack> stacks = new LinkedHashMap<>();

    public void addItem(ItemDefinition item, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        InventoryStack stack = stacks.get(item.id());
        if (stack == null) {
            stacks.put(item.id(), new InventoryStack(item, quantity));
            return;
        }
        stack.addQuantity(quantity);
    }

    public boolean removeItem(String itemId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        InventoryStack stack = stacks.get(itemId);
        if (stack == null || stack.getQuantity() < quantity) {
            return false;
        }
        stack.removeQuantity(quantity);
        if (stack.getQuantity() == 0) {
            stacks.remove(itemId);
        }
        return true;
    }

    public int getQuantity(String itemId) {
        InventoryStack stack = stacks.get(itemId);
        return stack == null ? 0 : stack.getQuantity();
    }

    public void setItemQuantity(ItemDefinition item, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        if (quantity == 0) {
            stacks.remove(item.id());
            return;
        }
        stacks.put(item.id(), new InventoryStack(item, quantity));
    }

    public Map<String, Integer> asQuantityMap() {
        Map<String, Integer> quantities = new LinkedHashMap<>();
        stacks.forEach((id, stack) -> quantities.put(id, stack.getQuantity()));
        return quantities;
    }

    public void clear() {
        stacks.clear();
    }

    public List<InventoryStack> getStacks() {
        return new ArrayList<>(stacks.values());
    }

    public int getTotalValue() {
        return stacks.values().stream()
                .mapToInt(InventoryStack::getTotalValue)
                .sum();
    }
}
