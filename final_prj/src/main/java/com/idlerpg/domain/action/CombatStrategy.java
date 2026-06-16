package com.idlerpg.domain.action;

import com.idlerpg.domain.context.GameContext;
import com.idlerpg.domain.enemy.EnemyDefinition;

public final class CombatStrategy implements ActionStrategy {
    private final EnemyDefinition enemy;

    public CombatStrategy(EnemyDefinition enemy) {
        this.enemy = enemy;
    }

    @Override
    public ActionResult execute(GameContext context) {
        context.getCombatService().startCombat(enemy);
        return new ActionResult("", 0, 0, "Combat started: " + enemy.name());
    }
}
