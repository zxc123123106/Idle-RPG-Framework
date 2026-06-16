package com.idlerpg.domain.enemy;

public final class EnemyInstance {
    private final EnemyDefinition definition;
    private int currentHp;

    public EnemyInstance(EnemyDefinition definition) {
        if (definition == null) {
            throw new IllegalArgumentException("Enemy definition is required.");
        }
        this.definition = definition;
        this.currentHp = definition.maxHp();
    }

    public EnemyDefinition getDefinition() {
        return definition;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    public void takeDamage(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Damage cannot be negative.");
        }
        currentHp = Math.max(0, currentHp - amount);
    }

    public boolean isDefeated() {
        return currentHp <= 0;
    }
}
