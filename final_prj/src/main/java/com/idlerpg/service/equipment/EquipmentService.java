package com.idlerpg.service.equipment;

import com.idlerpg.domain.item.EquipmentSlot;
import com.idlerpg.domain.item.ItemDefinition;
import com.idlerpg.domain.player.Player;

import java.util.EnumMap;
import java.util.Map;

public final class EquipmentService {
    public boolean equip(Player player, ItemDefinition item) {
        return equip(player, item, Map.of(item.id(), item));
    }

    public boolean equip(Player player, ItemDefinition item, Map<String, ItemDefinition> itemLookup) {
        if (!item.isEquipment()) {
            return false;
        }
        String equippedItemId = player.getEquipment().get(item.slot());
        if (item.id().equals(equippedItemId)) {
            recalculateBonuses(player, itemLookup);
            return true;
        }
        if (player.getInventory().getQuantity(item.id()) <= 0) {
            return false;
        }
        if (!player.getInventory().removeItem(item.id(), 1)) {
            return false;
        }
        if (equippedItemId != null) {
            ItemDefinition equippedItem = itemLookup.get(equippedItemId);
            if (equippedItem != null) {
                player.getInventory().addItem(equippedItem, 1);
            }
        }
        player.getEquipment().put(item.slot(), item.id());
        recalculateBonuses(player, itemLookup);
        return true;
    }

    public void unequip(Player player, EquipmentSlot slot, Map<String, ItemDefinition> itemLookup) {
        String itemId = player.getEquipment().remove(slot);
        ItemDefinition item = itemId == null ? null : itemLookup.get(itemId);
        if (item != null) {
            player.getInventory().addItem(item, 1);
        }
        recalculateBonuses(player, itemLookup);
    }

    public void recalculateBonuses(Player player, Map<String, ItemDefinition> itemLookup) {
        int attack = 0;
        int defense = 0;
        int hp = 0;
        for (String itemId : player.getEquipment().values()) {
            ItemDefinition item = itemLookup.get(itemId);
            if (item != null && item.slot() != EquipmentSlot.TOOL) {
                attack += item.attackBonus();
                defense += item.defenseBonus();
                hp += item.hpBonus();
            }
        }
        player.applyEquipmentBonuses(attack, defense, hp);
    }

    public Map<EquipmentSlot, String> copyEquipment(Player player) {
        return new EnumMap<>(player.getEquipment());
    }
}
