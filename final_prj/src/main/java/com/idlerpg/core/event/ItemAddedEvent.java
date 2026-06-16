package com.idlerpg.core.event;

import com.idlerpg.domain.item.ItemDefinition;

import java.time.Instant;

public record ItemAddedEvent(
        ItemDefinition item,
        int quantity,
        Instant occurredAt
) implements GameEvent {
    @Override
    public String message() {
        return "Added " + quantity + " x " + item.name();
    }
}
