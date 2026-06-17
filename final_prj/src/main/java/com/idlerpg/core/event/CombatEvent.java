package com.idlerpg.core.event;

import com.idlerpg.domain.enemy.EnemyInstance;

import java.time.Instant;

public record CombatEvent(
        Type type,
        EnemyInstance enemy,
        int amount,
        String detail,
        Instant occurredAt
) implements GameEvent {
    public enum Type {
        STARTED,
        HIT,
        PLAYER_HIT,
        VICTORY,
        DEFEAT,
        STOPPED
    }

    @Override
    public String message() {
        String enemyName = enemy == null ? "None" : enemy.getDefinition().name();
        return switch (type) {
            case STARTED -> "Combat started: " + enemyName;
            case HIT -> "Hit " + enemyName + " for " + amount + " damage.";
            case PLAYER_HIT -> enemyName + " hit player for " + amount + " damage.";
            case VICTORY -> "Victory over " + enemyName + ". " + detail;
            case DEFEAT -> "Player was defeated by " + enemyName + ". " + detail;
            case STOPPED -> "Combat stopped.";
        };
    }
}
