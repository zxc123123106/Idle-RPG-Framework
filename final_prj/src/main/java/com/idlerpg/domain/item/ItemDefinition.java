package com.idlerpg.domain.item;

import com.idlerpg.domain.common.Identifiable;

public record ItemDefinition(
        String id,
        String name,
        ItemType type,
        int value,
        String description,
        String icon,
        ItemRarity rarity,
        EquipmentSlot slot,
        int attackBonus,
        int defenseBonus,
        int hpBonus
) implements Identifiable {
    public ItemDefinition(String id, String name, ItemType type, int value) {
        this(id, name, type, value, "", "◆", ItemRarity.COMMON, null, 0, 0, 0);
    }

    public ItemDefinition {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Item id is required.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Item name is required.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Item type is required.");
        }
        if (value < 0) {
            throw new IllegalArgumentException("Item value cannot be negative.");
        }
        description = description == null ? "" : description;
        icon = icon == null || icon.isBlank() ? "◆" : icon;
        rarity = rarity == null ? ItemRarity.COMMON : rarity;
        if (type == ItemType.EQUIPMENT && slot == null) {
            throw new IllegalArgumentException("Equipment item slot is required.");
        }
        if (attackBonus < 0 || defenseBonus < 0 || hpBonus < 0) {
            throw new IllegalArgumentException("Equipment bonuses cannot be negative.");
        }
    }

    public boolean isEquipment() {
        return type == ItemType.EQUIPMENT;
    }
}
