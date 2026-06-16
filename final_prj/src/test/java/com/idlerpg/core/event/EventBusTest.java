package com.idlerpg.core.event;

import com.idlerpg.domain.item.ItemDefinition;
import com.idlerpg.domain.item.ItemType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventBusTest {
    @Test
    void publishNotifiesMatchingSubscribers() {
        EventBus eventBus = EventBus.createStandalone();
        ItemDefinition item = new ItemDefinition("iron_ore", "Iron Ore", ItemType.RESOURCE, 10);
        AtomicReference<String> capturedId = new AtomicReference<>();

        eventBus.subscribe(ItemAddedEvent.class, event -> capturedId.set(event.item().id()));
        eventBus.publish(new ItemAddedEvent(item, 1, Instant.now()));

        assertEquals("iron_ore", capturedId.get());
    }

    @Test
    void baseEventSubscriptionReceivesAllGameEvents() {
        EventBus eventBus = EventBus.createStandalone();
        AtomicInteger eventCount = new AtomicInteger();

        eventBus.subscribe(GameEvent.class, event -> eventCount.incrementAndGet());
        eventBus.publish(new GameTickEvent(1, Instant.now()));
        eventBus.publish(new LevelUpEvent(2, Instant.now()));

        assertEquals(2, eventCount.get());
    }
}
