package com.idlerpg.command;

import com.idlerpg.domain.skill.SkillDefinition;
import com.idlerpg.domain.player.Player;
import com.idlerpg.service.gathering.GatheringService;

public final class StartActionCommand implements GameCommand {
    private final GatheringService gatheringService;
    private final SkillDefinition skill;
    private final Player player;

    public StartActionCommand(GatheringService gatheringService, SkillDefinition skill) {
        this(gatheringService, skill, null);
    }

    public StartActionCommand(GatheringService gatheringService, SkillDefinition skill, Player player) {
        this.gatheringService = gatheringService;
        this.skill = skill;
        this.player = player;
    }

    @Override
    public void execute() {
        if (player == null) {
            gatheringService.start(skill);
            return;
        }
        gatheringService.start(player, skill);
    }
}
