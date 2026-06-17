package com.idlerpg.factory;

import com.idlerpg.domain.action.ActionStrategy;
import com.idlerpg.domain.action.CookingStrategy;
import com.idlerpg.domain.action.FishingStrategy;
import com.idlerpg.domain.action.MiningStrategy;
import com.idlerpg.domain.skill.SkillDefinition;

public final class SkillFactory {
    public ActionStrategy createStrategy(SkillDefinition skill) {
        return switch (skill.actionType()) {
            case MINING -> new MiningStrategy(skill);
            case FISHING -> new FishingStrategy(skill);
            case COOKING -> new CookingStrategy(skill);
        };
    }
}
