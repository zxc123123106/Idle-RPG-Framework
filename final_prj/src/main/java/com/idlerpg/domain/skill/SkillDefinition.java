package com.idlerpg.domain.skill;

import com.idlerpg.domain.common.Identifiable;

public record SkillDefinition(
        String id,
        String name,
        ActionType actionType,
        int durationTicks,
        String rewardItemId,
        int rewardQuantity,
        int expReward,
        String consumeItemId,
        int consumeQuantity
) implements Identifiable {
    public SkillDefinition(
            String id,
            String name,
            ActionType actionType,
            int durationTicks,
            String rewardItemId,
            int rewardQuantity,
            int expReward
    ) {
        this(id, name, actionType, durationTicks, rewardItemId, rewardQuantity, expReward, "", 0);
    }

    public SkillDefinition {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Skill id is required.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Skill name is required.");
        }
        if (actionType == null) {
            throw new IllegalArgumentException("Action type is required.");
        }
        if (durationTicks <= 0) {
            throw new IllegalArgumentException("Duration ticks must be positive.");
        }
        if (rewardItemId == null || rewardItemId.isBlank()) {
            throw new IllegalArgumentException("Reward item id is required.");
        }
        if (rewardQuantity <= 0) {
            throw new IllegalArgumentException("Reward quantity must be positive.");
        }
        if (expReward < 0) {
            throw new IllegalArgumentException("Experience reward cannot be negative.");
        }
        consumeItemId = consumeItemId == null ? "" : consumeItemId;
        if (consumeQuantity < 0) {
            throw new IllegalArgumentException("Consume quantity cannot be negative.");
        }
        if (consumeItemId.isBlank()) {
            consumeQuantity = 0;
        } else if (consumeQuantity <= 0) {
            throw new IllegalArgumentException("Consume quantity must be positive when consume item is set.");
        }
    }
}
