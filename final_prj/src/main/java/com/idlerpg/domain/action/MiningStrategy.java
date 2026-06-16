package com.idlerpg.domain.action;

import com.idlerpg.domain.context.GameContext;
import com.idlerpg.domain.skill.SkillDefinition;

public final class MiningStrategy implements ActionStrategy {
    private final SkillDefinition skill;

    public MiningStrategy(SkillDefinition skill) {
        this.skill = skill;
    }

    @Override
    public ActionResult execute(GameContext context) {
        return new ActionResult(
                skill.rewardItemId(),
                skill.rewardQuantity(),
                skill.expReward(),
                "Mining completed: " + skill.name()
        );
    }
}
