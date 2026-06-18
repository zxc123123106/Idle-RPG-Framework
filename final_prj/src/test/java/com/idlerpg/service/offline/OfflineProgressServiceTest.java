package com.idlerpg.service.offline;

import com.idlerpg.TestGameContextFactory;
import com.idlerpg.domain.item.EquipmentSlot;
import com.idlerpg.domain.item.ItemDefinition;
import com.idlerpg.domain.item.ItemRarity;
import com.idlerpg.domain.item.ItemType;
import com.idlerpg.domain.skill.ActionType;
import com.idlerpg.domain.skill.SkillDefinition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OfflineProgressServiceTest {
    @Test
    void offlineProgressAppliesRewardsAndCapsAtEightHours() {
        TestGameContextFactory.TestContext testContext = TestGameContextFactory.create();
        testContext.itemRegistry().register(new ItemDefinition("copper_ore", "Copper Ore", ItemType.RESOURCE, 5));
        SkillDefinition skill = new SkillDefinition(
                "mine_copper",
                "Mine Copper",
                ActionType.MINING,
                3,
                "copper_ore",
                2,
                12
        );
        testContext.skillRegistry().register(skill);
        long now = 1_000_000L;
        testContext.context().getPlayer().setActiveSkillId(skill.id());
        testContext.context().getPlayer().setLastSavedAtEpochSecond(now - 40_000L);

        OfflineProgressResult result = new OfflineProgressService()
                .applyOfflineProgress(testContext.context(), now);

        assertEquals(OfflineProgressService.MAX_OFFLINE_SECONDS, result.secondsApplied());
        assertEquals(9_600, result.cyclesCompleted());
        assertEquals(19_200, testContext.context().getPlayer().getInventory().getQuantity("copper_ore"));
        assertFalse(result.stoppedForMissingMaterials());
    }

    @Test
    void offlineProgressUsesToolSpeedBonus() {
        TestGameContextFactory.TestContext testContext = TestGameContextFactory.create();
        ItemDefinition ore = new ItemDefinition("copper_ore", "Copper Ore", ItemType.RESOURCE, 5);
        ItemDefinition pickaxe = new ItemDefinition(
                "bronze_pickaxe",
                "Bronze Pickaxe",
                ItemType.EQUIPMENT,
                80,
                "",
                "P",
                ItemRarity.UNCOMMON,
                EquipmentSlot.TOOL,
                0,
                0,
                0,
                0,
                ActionType.MINING,
                50
        );
        SkillDefinition skill = new SkillDefinition(
                "mine_copper",
                "Mine Copper",
                ActionType.MINING,
                10,
                "copper_ore",
                1,
                10
        );
        testContext.itemRegistry().register(ore);
        testContext.itemRegistry().register(pickaxe);
        testContext.skillRegistry().register(skill);
        testContext.context().getPlayer().getEquipment().put(EquipmentSlot.TOOL, pickaxe.id());
        testContext.context().getPlayer().setActiveSkillId(skill.id());
        testContext.context().getPlayer().setLastSavedAtEpochSecond(100L);

        OfflineProgressResult result = new OfflineProgressService()
                .applyOfflineProgress(testContext.context(), 150L);

        assertEquals(10, result.cyclesCompleted());
        assertEquals(10, testContext.context().getPlayer().getInventory().getQuantity("copper_ore"));
    }

    @Test
    void materialConsumptionLimitsOfflineCookingAndStopsActivity() {
        TestGameContextFactory.TestContext testContext = TestGameContextFactory.create();
        ItemDefinition rawFish = new ItemDefinition("river_fish", "River Fish", ItemType.CONSUMABLE, 8);
        ItemDefinition cookedFish = new ItemDefinition("cooked_fish", "Cooked Fish", ItemType.CONSUMABLE, 14);
        SkillDefinition skill = new SkillDefinition(
                "cook_fish",
                "Cook Fish",
                ActionType.COOKING,
                5,
                "cooked_fish",
                1,
                18,
                "river_fish",
                1
        );
        testContext.itemRegistry().register(rawFish);
        testContext.itemRegistry().register(cookedFish);
        testContext.skillRegistry().register(skill);
        testContext.context().getPlayer().getInventory().addItem(rawFish, 3);
        testContext.context().getPlayer().setActiveSkillId(skill.id());
        testContext.context().getPlayer().setLastSavedAtEpochSecond(100L);

        OfflineProgressResult result = new OfflineProgressService()
                .applyOfflineProgress(testContext.context(), 200L);

        assertEquals(3, result.cyclesCompleted());
        assertEquals(0, testContext.context().getPlayer().getInventory().getQuantity("river_fish"));
        assertEquals(3, testContext.context().getPlayer().getInventory().getQuantity("cooked_fish"));
        assertTrue(result.stoppedForMissingMaterials());
        assertEquals("", testContext.context().getPlayer().getActiveSkillId());
    }
}
