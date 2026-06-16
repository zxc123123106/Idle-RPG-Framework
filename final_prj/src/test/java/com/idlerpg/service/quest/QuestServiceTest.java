package com.idlerpg.service.quest;

import com.idlerpg.TestGameContextFactory;
import com.idlerpg.domain.item.ItemDefinition;
import com.idlerpg.domain.item.ItemType;
import com.idlerpg.domain.quest.QuestDefinition;
import com.idlerpg.domain.quest.QuestType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestServiceTest {
    @Test
    void questTracksGatheringAndClaimReward() {
        TestGameContextFactory.TestContext testContext = TestGameContextFactory.create();
        testContext.itemRegistry().register(new ItemDefinition("bronze_pickaxe", "Bronze Pickaxe", ItemType.RESOURCE, 50));
        QuestDefinition quest = new QuestDefinition(
                "first_ore",
                "First Ore",
                "",
                QuestType.GATHER_ITEM,
                "copper_ore",
                3,
                20,
                10,
                "bronze_pickaxe",
                1,
                "iron_ridge"
        );
        testContext.questRegistry().register(quest);

        testContext.questService().recordGathered(testContext.context().getPlayer(), "copper_ore", 3);
        boolean claimed = testContext.questService().claimReward(testContext.context(), quest);

        assertTrue(claimed);
        assertTrue(testContext.context().getPlayer().getClaimedQuestIds().contains("first_ore"));
        assertEquals(10, testContext.context().getPlayer().getGold());
        assertEquals(1, testContext.context().getPlayer().getInventory().getQuantity("bronze_pickaxe"));
        assertTrue(testContext.context().getPlayer().getUnlockedRegionIds().contains("iron_ridge"));
    }
}
