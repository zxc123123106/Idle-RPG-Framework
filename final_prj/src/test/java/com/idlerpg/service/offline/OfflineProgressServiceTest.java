package com.idlerpg.service.offline;

import com.idlerpg.TestGameContextFactory;
import com.idlerpg.domain.item.ItemDefinition;
import com.idlerpg.domain.item.ItemType;
import com.idlerpg.domain.skill.ActionType;
import com.idlerpg.domain.skill.SkillDefinition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OfflineProgressServiceTest {
    @Test
    void offlineProgressAppliesRewardsAndCapsAtEightHours() {
        TestGameContextFactory.TestContext testContext = TestGameContextFactory.create();
        testContext.itemRegistry().register(new ItemDefinition("copper_ore", "Copper Ore", ItemType.RESOURCE, 5));
        SkillDefinition skill = new SkillDefinition("mine_copper", "Mine Copper", ActionType.MINING, 3, "copper_ore", 2, 12);
        testContext.skillRegistry().register(skill);
        long now = 1_000_000L;
        testContext.context().getPlayer().setActiveSkillId(skill.id());
        testContext.context().getPlayer().setLastSavedAtEpochSecond(now - 40_000L);

        OfflineProgressResult result = new OfflineProgressService().applyOfflineProgress(testContext.context(), now);

        assertEquals(28_800L, result.secondsApplied());
        assertEquals(9_600, result.cyclesCompleted());
        assertEquals(19_200, testContext.context().getPlayer().getInventory().getQuantity("copper_ore"));
    }
}
