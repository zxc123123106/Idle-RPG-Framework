package com.idlerpg.factory;

import com.idlerpg.domain.enemy.EnemyDefinition;
import com.idlerpg.domain.enemy.EnemyInstance;

public final class EnemyFactory {
    public EnemyInstance createInstance(EnemyDefinition enemyDefinition) {
        return new EnemyInstance(enemyDefinition);
    }
}
