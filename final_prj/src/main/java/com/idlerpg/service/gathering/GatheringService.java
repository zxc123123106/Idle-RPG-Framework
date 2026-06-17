package com.idlerpg.service.gathering;

import com.idlerpg.core.engine.Tickable;
import com.idlerpg.core.event.SkillProgressEvent;
import com.idlerpg.domain.action.ActionResult;
import com.idlerpg.domain.action.ActionStrategy;
import com.idlerpg.domain.context.GameContext;
import com.idlerpg.domain.item.ItemDefinition;
import com.idlerpg.domain.skill.SkillDefinition;
import com.idlerpg.factory.SkillFactory;

import java.time.Instant;
import java.util.Optional;

public final class GatheringService implements Tickable {
    private final SkillFactory skillFactory;
    private SkillDefinition activeSkill;
    private ActionStrategy activeStrategy;
    private int progressTicks;

    public GatheringService(SkillFactory skillFactory) {
        this.skillFactory = skillFactory;
    }

    public synchronized void start(SkillDefinition skill) {
        activeSkill = skill;
        activeStrategy = skillFactory.createStrategy(skill);
        progressTicks = 0;
    }

    public synchronized void start(com.idlerpg.domain.player.Player player, SkillDefinition skill) {
        start(skill);
        player.setActiveSkillId(skill.id());
    }

    public synchronized void stop() {
        activeSkill = null;
        activeStrategy = null;
        progressTicks = 0;
    }

    public synchronized void stop(com.idlerpg.domain.player.Player player) {
        stop();
        player.setActiveSkillId("");
    }

    public synchronized Optional<SkillDefinition> getActiveSkill() {
        return Optional.ofNullable(activeSkill);
    }

    public synchronized double getProgressRatio() {
        if (activeSkill == null) {
            return 0.0;
        }
        return Math.min(1.0, (double) progressTicks / activeSkill.durationTicks());
    }

    public synchronized int getProgressTicks() {
        return progressTicks;
    }

    public synchronized int getRequiredTicks() {
        if (activeSkill == null) {
            return 0;
        }
        return activeSkill.durationTicks();
    }

    @Override
    public synchronized void tick(GameContext context) {
        if (activeSkill == null || activeStrategy == null) {
            return;
        }

        progressTicks++;
        boolean complete = progressTicks >= activeSkill.durationTicks();
        context.getEventBus().publish(new SkillProgressEvent(
                activeSkill,
                progressTicks,
                activeSkill.durationTicks(),
                complete,
                Instant.now()
        ));

        if (!complete) {
            return;
        }

        if (!consumeRequiredItems(context, activeSkill)) {
            stop(context.getPlayer());
            return;
        }

        ActionResult result = activeStrategy.execute(context);
        ItemDefinition reward = context.getItemRegistry().getRequired(result.rewardItemId());
        context.getInventoryService().addItem(context.getPlayer(), reward, result.rewardQuantity());
        context.getProgressionService().addExperience(context.getPlayer(), result.expReward());
        context.getPlayer().addSkillExperience(activeSkill.actionType(), result.expReward());
        if (context.getQuestService() != null) {
            context.getQuestService().recordGathered(context.getPlayer(), reward.id(), result.rewardQuantity());
            context.getQuestService().recordLevel(context.getPlayer());
        }
        if (context.getRegionService() != null) {
            context.getRegionService().unlockEligibleRegions(context.getPlayer());
        }
        progressTicks = 0;
    }

    private boolean consumeRequiredItems(GameContext context, SkillDefinition skill) {
        if (skill.consumeItemId().isBlank() || skill.consumeQuantity() <= 0) {
            return true;
        }
        return context.getPlayer().getInventory().removeItem(skill.consumeItemId(), skill.consumeQuantity());
    }
}
