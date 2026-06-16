package com.idlerpg.domain.action;

public record ActionResult(
        String rewardItemId,
        int rewardQuantity,
        int expReward,
        String description
) {
}
