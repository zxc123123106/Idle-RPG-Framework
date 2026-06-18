package com.idlerpg.command;

import com.idlerpg.domain.skill.SkillDefinition;
import com.idlerpg.service.gathering.GatheringService;

public final class StartActionCommand implements GameCommand {
    private final GatheringService gatheringService;
    private final SkillDefinition skill;

    public StartActionCommand(GatheringService gatheringService, SkillDefinition skill) {
        this.gatheringService = gatheringService;
        this.skill = skill;
    }

    @Override
    public void execute() {
        gatheringService.start(skill);
    }
}
