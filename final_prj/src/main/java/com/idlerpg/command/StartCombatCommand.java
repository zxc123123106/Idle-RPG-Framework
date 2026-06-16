package com.idlerpg.command;

import com.idlerpg.domain.action.CombatStrategy;
import com.idlerpg.domain.context.GameContext;
import com.idlerpg.domain.enemy.EnemyDefinition;

public final class StartCombatCommand implements GameCommand {
    private final GameContext context;
    private final EnemyDefinition enemy;

    public StartCombatCommand(GameContext context, EnemyDefinition enemy) {
        this.context = context;
        this.enemy = enemy;
    }

    @Override
    public void execute() {
        new CombatStrategy(enemy).execute(context);
    }
}
