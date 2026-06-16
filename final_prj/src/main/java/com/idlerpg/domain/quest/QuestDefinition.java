package com.idlerpg.domain.quest;

import com.idlerpg.domain.common.Identifiable;

public record QuestDefinition(
        String id,
        String title,
        String description,
        QuestType type,
        String targetId,
        int requiredCount,
        int rewardExp,
        int rewardGold,
        String rewardItemId,
        int rewardQuantity,
        String unlockRegionId
) implements Identifiable {
    public QuestDefinition {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Quest id is required.");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Quest title is required.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Quest type is required.");
        }
        if (requiredCount <= 0) {
            throw new IllegalArgumentException("Quest required count must be positive.");
        }
        description = description == null ? "" : description;
        targetId = targetId == null ? "" : targetId;
        rewardItemId = rewardItemId == null ? "" : rewardItemId;
        unlockRegionId = unlockRegionId == null ? "" : unlockRegionId;
    }
}
