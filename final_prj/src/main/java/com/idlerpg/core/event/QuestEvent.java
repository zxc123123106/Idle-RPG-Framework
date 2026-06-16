package com.idlerpg.core.event;

import com.idlerpg.domain.quest.QuestDefinition;

import java.time.Instant;

public record QuestEvent(
        Type type,
        QuestDefinition quest,
        int progress,
        Instant occurredAt
) implements GameEvent {
    public enum Type {
        PROGRESSED,
        COMPLETED,
        CLAIMED
    }

    @Override
    public String message() {
        return switch (type) {
            case PROGRESSED -> "任務進度：" + quest.title() + " " + progress + "/" + quest.requiredCount();
            case COMPLETED -> "任務完成：" + quest.title();
            case CLAIMED -> "已領取任務獎勵：" + quest.title();
        };
    }
}
