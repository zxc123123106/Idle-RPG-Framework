package com.idlerpg.domain.shop;

import com.idlerpg.domain.common.Identifiable;

public record ShopEntry(
        String id,
        String itemId,
        int price,
        String requiredRegionId
) implements Identifiable {
    public ShopEntry {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Shop entry id is required.");
        }
        if (itemId == null || itemId.isBlank()) {
            throw new IllegalArgumentException("Shop item id is required.");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Shop price cannot be negative.");
        }
        requiredRegionId = requiredRegionId == null ? "" : requiredRegionId;
    }
}
