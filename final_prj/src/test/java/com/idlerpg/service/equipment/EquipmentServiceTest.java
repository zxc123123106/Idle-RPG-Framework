package com.idlerpg.service.equipment;

import com.idlerpg.domain.item.EquipmentSlot;
import com.idlerpg.domain.item.ItemDefinition;
import com.idlerpg.domain.item.ItemRarity;
import com.idlerpg.domain.item.ItemType;
import com.idlerpg.domain.player.Player;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EquipmentServiceTest {
    @Test
    void equipAndUnequipAffectsPlayerStats() {
        Player player = new Player();
        ItemDefinition sword = new ItemDefinition(
                "iron_sword",
                "Iron Sword",
                ItemType.EQUIPMENT,
                100,
                "",
                "⚔",
                ItemRarity.RARE,
                EquipmentSlot.WEAPON,
                8,
                0,
                0
        );
        player.getInventory().addItem(sword, 1);
        EquipmentService service = new EquipmentService();

        boolean equipped = service.equip(player, sword, Map.of(sword.id(), sword));

        assertTrue(equipped);
        assertEquals(23, player.getAttackPower());
        assertEquals(0, player.getInventory().getQuantity(sword.id()));

        service.unequip(player, EquipmentSlot.WEAPON, Map.of(sword.id(), sword));

        assertEquals(15, player.getAttackPower());
        assertEquals(1, player.getInventory().getQuantity(sword.id()));
    }

    @Test
    void toolEquipmentDoesNotIncreaseCombatStats() {
        Player player = new Player();
        ItemDefinition pickaxe = new ItemDefinition(
                "bronze_pickaxe",
                "Bronze Pickaxe",
                ItemType.EQUIPMENT,
                80,
                "",
                "⛏",
                ItemRarity.UNCOMMON,
                EquipmentSlot.TOOL,
                5,
                3,
                20
        );
        player.getInventory().addItem(pickaxe, 1);
        EquipmentService service = new EquipmentService();

        boolean equipped = service.equip(player, pickaxe, Map.of(pickaxe.id(), pickaxe));

        assertTrue(equipped);
        assertEquals(15, player.getAttackPower());
        assertEquals(0, player.getDefense());
        assertEquals(100, player.getMaxHp());
    }
}
