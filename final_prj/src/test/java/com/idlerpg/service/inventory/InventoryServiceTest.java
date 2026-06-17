package com.idlerpg.service.inventory;

import com.idlerpg.core.event.EventBus;
import com.idlerpg.core.event.ItemAddedEvent;
import com.idlerpg.domain.item.ItemDefinition;
import com.idlerpg.domain.item.ItemType;
import com.idlerpg.domain.player.Player;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InventoryServiceTest {
    @Test
    void addItemStacksQuantityAndPublishesEvent() {
        EventBus eventBus = EventBus.createStandalone();
        InventoryService service = new InventoryService(eventBus);
        Player player = new Player();
        ItemDefinition ore = new ItemDefinition("iron_ore", "Iron Ore", ItemType.RESOURCE, 12);
        AtomicInteger eventCount = new AtomicInteger();
        eventBus.subscribe(ItemAddedEvent.class, event -> eventCount.incrementAndGet());

        service.addItem(player, ore, 1);
        service.addItem(player, ore, 2);

        assertEquals(1, service.getInventory(player).size());
        assertEquals(3, service.getInventory(player).getFirst().getQuantity());
        assertEquals(36, service.getTotalValue(player));
        assertEquals(2, eventCount.get());
    }

    @Test
    void removeItemDeletesQuantityFromStack() {
        EventBus eventBus = EventBus.createStandalone();
        InventoryService service = new InventoryService(eventBus);
        Player player = new Player();
        ItemDefinition fish = new ItemDefinition("river_fish", "River Fish", ItemType.CONSUMABLE, 8);
        service.addItem(player, fish, 3);

        assertTrue(service.removeItem(player, fish.id(), 3));

        assertEquals(0, player.getInventory().getQuantity(fish.id()));
        assertEquals(0, service.getInventory(player).size());
    }
}
