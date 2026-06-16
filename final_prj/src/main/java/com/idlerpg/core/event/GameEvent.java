package com.idlerpg.core.event;

import java.time.Instant;

public interface GameEvent {
    Instant occurredAt();

    String message();
}
