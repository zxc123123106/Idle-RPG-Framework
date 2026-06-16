package com.idlerpg.service.region;

import com.idlerpg.TestGameContextFactory;
import com.idlerpg.domain.region.RegionDefinition;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RegionServiceTest {
    @Test
    void unlockEligibleRegionsUsesLevelAndQuestRequirements() {
        TestGameContextFactory.TestContext testContext = TestGameContextFactory.create();
        RegionDefinition meadow = new RegionDefinition("sunlit_meadow", "Meadow", "", "☀", 1, "", List.of(), List.of(), List.of(), List.of());
        RegionDefinition ridge = new RegionDefinition("iron_ridge", "Ridge", "", "▲", 1, "first_ore", List.of(), List.of(), List.of(), List.of());
        testContext.regionRegistry().register(meadow);
        testContext.regionRegistry().register(ridge);
        testContext.context().getPlayer().getCompletedQuestIds().add("first_ore");

        testContext.regionService().unlockEligibleRegions(testContext.context().getPlayer());

        assertTrue(testContext.context().getPlayer().getUnlockedRegionIds().contains("iron_ridge"));
    }
}
