package com.idlerpg.service.progression;

import com.idlerpg.domain.item.EquipmentSlot;
import com.idlerpg.domain.item.ItemDefinition;
import com.idlerpg.domain.player.Player;
import com.idlerpg.domain.skill.SkillDefinition;

import java.util.function.Function;

public final class SkillSpeedCalculator {
    public static final double LEVEL_SPEED_PERCENT_PER_LEVEL = 0.1;
    public static final double MAX_SPEED_PERCENT = 90.0;

    private SkillSpeedCalculator() {
    }

    public static int requiredTicks(
            Player player,
            SkillDefinition skill,
            Function<String, ItemDefinition> itemLookup
    ) {
        double speedPercent = totalSpeedPercent(player, skill, itemLookup);
        double multiplier = Math.max(0.0, 1.0 - speedPercent / 100.0);
        return Math.max(1, (int) Math.ceil(skill.durationTicks() * multiplier));
    }

    public static double totalSpeedPercent(
            Player player,
            SkillDefinition skill,
            Function<String, ItemDefinition> itemLookup
    ) {
        return Math.min(MAX_SPEED_PERCENT, levelSpeedPercent(player, skill) + toolSpeedPercent(player, skill, itemLookup));
    }

    public static double levelSpeedPercent(Player player, SkillDefinition skill) {
        return Math.max(0.0, player.getSkillLevel(skill.actionType()) * LEVEL_SPEED_PERCENT_PER_LEVEL);
    }

    public static int toolSpeedPercent(
            Player player,
            SkillDefinition skill,
            Function<String, ItemDefinition> itemLookup
    ) {
        return player.getEquipment().values().stream()
                .map(itemLookup)
                .filter(item -> item != null && item.slot() == EquipmentSlot.TOOL)
                .filter(item -> item.speedBonusPercent() > 0)
                .filter(item -> item.speedActionType() == null || item.speedActionType() == skill.actionType())
                .mapToInt(ItemDefinition::speedBonusPercent)
                .sum();
    }
}
