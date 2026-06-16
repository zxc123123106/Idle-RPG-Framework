package com.idlerpg.core.event;

import java.time.Instant;

public record GameTickEvent(long tick, Instant occurredAt) implements GameEvent {
    @Override
    public String message() {
        return "Tick " + tick;
    }
}
