package com.idlerpg.core.event;

import com.idlerpg.domain.region.RegionDefinition;

import java.time.Instant;

public record RegionUnlockedEvent(
        RegionDefinition region,
        Instant occurredAt
) implements GameEvent {
    @Override
    public String message() {
        return "解鎖新區域：" + region.name();
    }
}
