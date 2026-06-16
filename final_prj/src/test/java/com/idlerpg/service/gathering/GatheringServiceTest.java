package com.idlerpg.service.gathering;

import com.idlerpg.TestGameContextFactory;
import com.idlerpg.core.event.ItemAddedEvent;
import com.idlerpg.core.event.SkillProgressEvent;
import com.idlerpg.domain.item.ItemDefinition;
import com.idlerpg.domain.item.ItemType;
import com.idlerpg.domain.skill.ActionType;
import com.idlerpg.domain.skill.SkillDefinition;
import com.idlerpg.factory.SkillFactory;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GatheringServiceTest {
    @Test
    void completedActionAddsRewardAndExperience() {
        TestGameContextFactory.TestContext testContext = TestGameContextFactory.create();
        ItemDefinition ore = new ItemDefinition("copper_ore", "Copper Ore", ItemType.RESOURCE, 5);
        SkillDefinition skill = new SkillDefinition(
                "mine_copper",
                "Mine Copper",
                ActionType.MINING,
                3,
                "copper_ore",
                2,
                12
        );
        testContext.itemRegistry().register(ore);
        testContext.skillRegistry().register(skill);
        GatheringService service = new GatheringService(new SkillFactory());
        AtomicInteger itemEvents = new AtomicInteger();
        AtomicInteger completedProgressEvents = new AtomicInteger();
        testContext.eventBus().subscribe(ItemAddedEvent.class, event -> itemEvents.incrementAndGet());
        testContext.eventBus().subscribe(SkillProgressEvent.class, event -> {
            if (event.complete()) {
                completedProgressEvents.incrementAndGet();
            }
        });

        service.start(skill);
        service.tick(testContext.context());
        service.tick(testContext.context());
        service.tick(testContext.context());

        assertEquals(1, itemEvents.get());
        assertEquals(1, completedProgressEvents.get());
        assertEquals(2, testContext.inventoryService()
                .getInventory(testContext.context().getPlayer())
                .getFirst()
                .getQuantity());
        assertEquals(12, testContext.context().getPlayer().getExperience());
    }
}
