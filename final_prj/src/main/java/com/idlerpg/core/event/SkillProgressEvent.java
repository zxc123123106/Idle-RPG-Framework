package com.idlerpg.core.event;

import com.idlerpg.domain.skill.SkillDefinition;

import java.time.Instant;

public record SkillProgressEvent(
        SkillDefinition skill,
        int progressTicks,
        int requiredTicks,
        boolean complete,
        Instant occurredAt
) implements GameEvent {
    @Override
    public String message() {
        if (complete) {
            return skill.name() + " completed.";
        }
        return skill.name() + " progress " + progressTicks + "/" + requiredTicks;
    }
}
