package com.idlerpg.service.combat;

import com.idlerpg.TestGameContextFactory;
import com.idlerpg.core.event.CombatEvent;
import com.idlerpg.domain.enemy.EnemyDefinition;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CombatServiceTest {
    @Test
    void combatDefeatsEnemyAndAwardsProgression() {
        TestGameContextFactory.TestContext testContext = TestGameContextFactory.create();
        EnemyDefinition enemy = new EnemyDefinition(
                "training_slime",
                "Training Slime",
                30,
                1,
                25,
                6
        );
        AtomicInteger victories = new AtomicInteger();
        testContext.eventBus().subscribe(CombatEvent.class, event -> {
            if (event.type() == CombatEvent.Type.VICTORY) {
                victories.incrementAndGet();
            }
        });

        testContext.combatService().startCombat(enemy);
        int ticks = 0;
        while (testContext.combatService().getActiveEnemy().isPresent() && ticks < 10) {
            testContext.combatService().tick(testContext.context());
            ticks++;
        }

        assertTrue(testContext.combatService().getActiveEnemy().isEmpty());
        assertEquals(1, victories.get());
        assertEquals(25, testContext.context().getPlayer().getExperience());
        assertEquals(6, testContext.context().getPlayer().getGold());
    }
}
