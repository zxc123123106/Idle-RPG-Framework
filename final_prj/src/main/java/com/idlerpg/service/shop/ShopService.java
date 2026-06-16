package com.idlerpg.service.shop;

import com.idlerpg.core.event.EventBus;
import com.idlerpg.core.event.ShopPurchaseEvent;
import com.idlerpg.domain.context.GameContext;
import com.idlerpg.domain.item.ItemDefinition;
import com.idlerpg.domain.player.Player;
import com.idlerpg.domain.shop.ShopEntry;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class ShopService {
    private final EventBus eventBus;

    public ShopService(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public List<ShopEntry> getAvailableEntries(GameContext context) {
        String currentRegionId = context.getPlayer().getCurrentRegionId();
        Set<String> regionShopItems = context.getRegionRegistry().get(currentRegionId)
                .map(region -> region.shopItemIds().stream().collect(Collectors.toSet()))
                .orElse(Set.of());
        return context.getShopRegistry().getAll().stream()
                .filter(entry -> entry.requiredRegionId().isBlank() || entry.requiredRegionId().equals(currentRegionId))
                .filter(entry -> regionShopItems.isEmpty() || regionShopItems.contains(entry.itemId()))
                .toList();
    }

    public boolean buy(GameContext context, ShopEntry entry) {
        Player player = context.getPlayer();
        if (!player.spendGold(entry.price())) {
            return false;
        }
        ItemDefinition item = context.getItemRegistry().getRequired(entry.itemId());
        context.getInventoryService().addItem(player, item, 1);
        eventBus.publish(new ShopPurchaseEvent(item, entry.price(), Instant.now()));
        return true;
    }
}
