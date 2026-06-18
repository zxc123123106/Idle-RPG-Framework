package com.idlerpg.service.offline;

import com.idlerpg.domain.context.GameContext;
import com.idlerpg.domain.item.ItemDefinition;
import com.idlerpg.domain.player.Player;
import com.idlerpg.domain.skill.SkillDefinition;
import com.idlerpg.service.progression.SkillSpeedCalculator;

public final class OfflineProgressService {
    public static final long MAX_OFFLINE_SECONDS = 8L * 60L * 60L;

    public OfflineProgressResult applyOfflineProgress(GameContext context, long nowEpochSecond) {
        Player player = context.getPlayer();
        long elapsed = Math.max(0, nowEpochSecond - player.getLastSavedAtEpochSecond());
        long secondsApplied = Math.min(elapsed, MAX_OFFLINE_SECONDS);
        if (secondsApplied == 0 || player.getActiveSkillId().isBlank()) {
            return emptyResult(secondsApplied, false);
        }

        SkillDefinition skill = context.getSkillRegistry().get(player.getActiveSkillId()).orElse(null);
        if (skill == null) {
            player.setActiveSkillId("");
            return emptyResult(secondsApplied, false);
        }

        int requiredTicks = SkillSpeedCalculator.requiredTicks(
                player,
                skill,
                itemId -> context.getItemRegistry().get(itemId).orElse(null)
        );
        int timeLimitedCycles = (int) (secondsApplied / requiredTicks);
        if (timeLimitedCycles <= 0) {
            return new OfflineProgressResult(secondsApplied, 0, skill.rewardItemId(), 0, 0, false);
        }

        int completedCycles = timeLimitedCycles;
        boolean stoppedForMissingMaterials = false;
        if (!skill.consumeItemId().isBlank() && skill.consumeQuantity() > 0) {
            int materialLimitedCycles = player.getInventory().getQuantity(skill.consumeItemId())
                    / skill.consumeQuantity();
            completedCycles = Math.min(timeLimitedCycles, materialLimitedCycles);
            stoppedForMissingMaterials = materialLimitedCycles < timeLimitedCycles;
            if (completedCycles > 0) {
                context.getInventoryService().removeItem(
                        player,
                        skill.consumeItemId(),
                        completedCycles * skill.consumeQuantity()
                );
            }
            if (stoppedForMissingMaterials) {
                player.setActiveSkillId("");
            }
        }

        if (completedCycles <= 0) {
            return new OfflineProgressResult(
                    secondsApplied,
                    0,
                    skill.rewardItemId(),
                    0,
                    0,
                    stoppedForMissingMaterials
            );
        }

        int quantity = Math.multiplyExact(completedCycles, skill.rewardQuantity());
        int experience = Math.multiplyExact(completedCycles, skill.expReward());
        ItemDefinition reward = context.getItemRegistry().getRequired(skill.rewardItemId());
        context.getInventoryService().addItem(player, reward, quantity);
        context.getProgressionService().addExperience(player, experience);
        player.addSkillExperience(skill.actionType(), experience);
        if (context.getQuestService() != null) {
            context.getQuestService().recordGathered(player, reward.id(), quantity);
            context.getQuestService().recordLevel(player);
        }
        if (context.getRegionService() != null) {
            context.getRegionService().unlockEligibleRegions(player);
        }
        return new OfflineProgressResult(
                secondsApplied,
                completedCycles,
                skill.rewardItemId(),
                quantity,
                experience,
                stoppedForMissingMaterials
        );
    }

    private OfflineProgressResult emptyResult(long secondsApplied, boolean stoppedForMissingMaterials) {
        return new OfflineProgressResult(secondsApplied, 0, "", 0, 0, stoppedForMissingMaterials);
    }
}
