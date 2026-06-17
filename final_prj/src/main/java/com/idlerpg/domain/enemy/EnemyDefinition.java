package com.idlerpg.domain.enemy;

import com.idlerpg.domain.common.Identifiable;

public record EnemyDefinition(
        String id,
        String name,
        int maxHp,
        int attack,
        int defense,
        int expReward,
        int goldReward
) implements Identifiable {
    public EnemyDefinition(String id, String name, int maxHp, int attack, int expReward, int goldReward) {
        this(id, name, maxHp, attack, 0, expReward, goldReward);
    }

    public EnemyDefinition {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Enemy id is required.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Enemy name is required.");
        }
        if (maxHp <= 0) {
            throw new IllegalArgumentException("Enemy max HP must be positive.");
        }
        if (attack < 0 || defense < 0 || expReward < 0 || goldReward < 0) {
            throw new IllegalArgumentException("Enemy stats and rewards cannot be negative.");
        }
    }
}
