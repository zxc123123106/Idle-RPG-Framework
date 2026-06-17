package com.idlerpg.service.progression;

import com.idlerpg.domain.item.EquipmentSlot;
import com.idlerpg.domain.item.ItemDefinition;
import com.idlerpg.domain.item.ItemRarity;
import com.idlerpg.domain.item.ItemType;
import com.idlerpg.domain.player.Player;
import com.idlerpg.domain.skill.ActionType;
import com.idlerpg.domain.skill.SkillDefinition;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SkillSpeedCalculatorTest {
    @Test
    void levelAndMatchingToolReduceRequiredTicks() {
        Player player = new Player();
        SkillDefinition skill = new SkillDefinition(
                "mine_copper",
                "Mine Copper",
                ActionType.MINING,
                10,
                "copper_ore",
                1,
                10
        );
        ItemDefinition pickaxe = new ItemDefinition(
                "bronze_pickaxe",
                "Bronze Pickaxe",
                ItemType.EQUIPMENT,
                80,
                "",
                "⛏",
                ItemRarity.UNCOMMON,
                EquipmentSlot.TOOL,
                0,
                0,
                0,
                0,
                ActionType.MINING,
                35
        );
        player.getEquipment().put(EquipmentSlot.TOOL, pickaxe.id());
        Map<String, ItemDefinition> itemLookup = Map.of(pickaxe.id(), pickaxe);

        int requiredTicks = SkillSpeedCalculator.requiredTicks(player, skill, itemLookup::get);

        assertEquals(7, requiredTicks);
    }

    @Test
    void unrelatedToolDoesNotSpeedOtherSkillTypes() {
        Player player = new Player();
        SkillDefinition skill = new SkillDefinition(
                "fish_river",
                "River Fishing",
                ActionType.FISHING,
                10,
                "river_fish",
                1,
                10
        );
        ItemDefinition pickaxe = new ItemDefinition(
                "bronze_pickaxe",
                "Bronze Pickaxe",
                ItemType.EQUIPMENT,
                80,
                "",
                "⛏",
                ItemRarity.UNCOMMON,
                EquipmentSlot.TOOL,
                0,
                0,
                0,
                0,
                ActionType.MINING,
                35
        );
        player.getEquipment().put(EquipmentSlot.TOOL, pickaxe.id());
        Map<String, ItemDefinition> itemLookup = Map.of(pickaxe.id(), pickaxe);

        int requiredTicks = SkillSpeedCalculator.requiredTicks(player, skill, itemLookup::get);

        assertEquals(10, requiredTicks);
    }
}
