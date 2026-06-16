package com.idlerpg.service.offline;

import com.idlerpg.domain.context.GameContext;
import com.idlerpg.domain.item.ItemDefinition;
import com.idlerpg.domain.player.Player;
import com.idlerpg.domain.skill.SkillDefinition;

public final class OfflineProgressService {
    private static final long MAX_OFFLINE_SECONDS = 8L * 60L * 60L;

    public OfflineProgressResult applyOfflineProgress(GameContext context, long nowEpochSecond) {
        Player player = context.getPlayer();
        long elapsed = Math.max(0, nowEpochSecond - player.getLastSavedAtEpochSecond());
        long secondsApplied = Math.min(elapsed, MAX_OFFLINE_SECONDS);
        if (secondsApplied == 0 || player.getActiveSkillId().isBlank()) {
            return new OfflineProgressResult(secondsApplied, 0, "", 0, 0);
        }

        SkillDefinition skill = context.getSkillRegistry().get(player.getActiveSkillId()).orElse(null);
        if (skill == null) {
            return new OfflineProgressResult(secondsApplied, 0, "", 0, 0);
        }

        int cycles = (int) (secondsApplied / skill.durationTicks());
        if (cycles <= 0) {
            return new OfflineProgressResult(secondsApplied, 0, skill.rewardItemId(), 0, 0);
        }

        int quantity = cycles * skill.rewardQuantity();
        int experience = cycles * skill.expReward();
        ItemDefinition item = context.getItemRegistry().getRequired(skill.rewardItemId());
        context.getInventoryService().addItem(player, item, quantity);
        context.getProgressionService().addExperience(player, experience);
        player.addSkillExperience(skill.actionType(), experience);
        if (context.getQuestService() != null) {
            context.getQuestService().recordGathered(player, item.id(), quantity);
            context.getQuestService().recordLevel(player);
        }
        return new OfflineProgressResult(secondsApplied, cycles, skill.rewardItemId(), quantity, experience);
    }
}
