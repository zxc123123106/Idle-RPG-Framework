package com.idlerpg.core.event;

import com.idlerpg.domain.item.ItemDefinition;

import java.time.Instant;

public record ShopPurchaseEvent(
        ItemDefinition item,
        int price,
        Instant occurredAt
) implements GameEvent {
    @Override
    public String message() {
        return "購買 " + item.name() + "，花費 " + price + " gold";
    }
}
