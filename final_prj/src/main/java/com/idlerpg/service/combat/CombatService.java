package com.idlerpg.service.combat;

import com.idlerpg.core.engine.Tickable;
import com.idlerpg.core.event.CombatEvent;
import com.idlerpg.core.event.EventBus;
import com.idlerpg.domain.context.GameContext;
import com.idlerpg.domain.enemy.EnemyDefinition;
import com.idlerpg.domain.enemy.EnemyInstance;
import com.idlerpg.domain.player.Player;
import com.idlerpg.factory.EnemyFactory;

import java.time.Instant;
import java.util.Optional;

public final class CombatService implements Tickable {
    private final EnemyFactory enemyFactory;
    private final EventBus eventBus;
    private EnemyInstance activeEnemy;

    public CombatService(EnemyFactory enemyFactory, EventBus eventBus) {
        this.enemyFactory = enemyFactory;
        this.eventBus = eventBus;
    }

    public synchronized void startCombat(EnemyDefinition enemyDefinition) {
        activeEnemy = enemyFactory.createInstance(enemyDefinition);
        eventBus.publish(new CombatEvent(
                CombatEvent.Type.STARTED,
                activeEnemy,
                0,
                "",
                Instant.now()
        ));
    }

    public synchronized void stopCombat() {
        activeEnemy = null;
        eventBus.publish(new CombatEvent(
                CombatEvent.Type.STOPPED,
                null,
                0,
                "",
                Instant.now()
        ));
    }

    public synchronized Optional<EnemyInstance> getActiveEnemy() {
        return Optional.ofNullable(activeEnemy);
    }

    @Override
    public synchronized void tick(GameContext context) {
        if (activeEnemy == null) {
            return;
        }

        Player player = context.getPlayer();
        int playerDamage = Math.max(1, player.getAttackPower() - activeEnemy.getDefinition().defense());
        activeEnemy.takeDamage(playerDamage);
        eventBus.publish(new CombatEvent(
                CombatEvent.Type.HIT,
                activeEnemy,
                playerDamage,
                "",
                Instant.now()
        ));

        if (activeEnemy.isDefeated()) {
            EnemyDefinition defeatedEnemy = activeEnemy.getDefinition();
            player.addGold(defeatedEnemy.goldReward());
            context.getProgressionService().addExperience(player, defeatedEnemy.expReward());
            if (context.getQuestService() != null) {
                context.getQuestService().recordEnemyDefeated(player, defeatedEnemy.id());
                context.getQuestService().recordLevel(player);
            }
            if (context.getRegionService() != null) {
                context.getRegionService().unlockEligibleRegions(player);
            }
            eventBus.publish(new CombatEvent(
                    CombatEvent.Type.VICTORY,
                    activeEnemy,
                    0,
                    "+" + defeatedEnemy.expReward() + " exp, +" + defeatedEnemy.goldReward() + " gold",
                    Instant.now()
            ));
            activeEnemy = null;
            return;
        }

        int enemyDamage = Math.max(1, activeEnemy.getDefinition().attack() - player.getDefense());
        player.takeDamage(enemyDamage);
        eventBus.publish(new CombatEvent(
                CombatEvent.Type.PLAYER_HIT,
                activeEnemy,
                enemyDamage,
                "",
                Instant.now()
        ));

        if (player.isDefeated()) {
            int lostExperience = player.reduceCurrentExperienceByPercent(0.5);
            player.restoreHpToPercent(0.5);
            eventBus.publish(new CombatEvent(
                    CombatEvent.Type.DEFEAT,
                    activeEnemy,
                    lostExperience,
                    "Lost " + lostExperience + " exp, HP restored to 50%",
                    Instant.now()
            ));
            activeEnemy = null;
        }
    }
}
