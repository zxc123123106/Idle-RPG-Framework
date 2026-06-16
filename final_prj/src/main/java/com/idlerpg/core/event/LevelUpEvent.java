package com.idlerpg.core.event;

import java.time.Instant;

public record LevelUpEvent(int newLevel, Instant occurredAt) implements GameEvent {
    @Override
    public String message() {
        return "Level up! New level: " + newLevel;
    }
}
