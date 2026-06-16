package com.idlerpg.service.inventory;

import com.idlerpg.core.event.EventBus;
import com.idlerpg.core.event.ItemAddedEvent;
import com.idlerpg.domain.item.InventoryStack;
import com.idlerpg.domain.item.ItemDefinition;
import com.idlerpg.domain.player.Player;

import java.time.Instant;
import java.util.List;

public final class InventoryService {
    private final EventBus eventBus;

    public InventoryService(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void addItem(Player player, ItemDefinition item, int quantity) {
        player.getInventory().addItem(item, quantity);
        eventBus.publish(new ItemAddedEvent(item, quantity, Instant.now()));
    }

    public List<InventoryStack> getInventory(Player player) {
        return player.getInventory().getStacks();
    }

    public int getTotalValue(Player player) {
        return player.getInventory().getTotalValue();
    }
}
