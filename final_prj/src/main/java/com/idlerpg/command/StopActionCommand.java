package com.idlerpg.command;

import com.idlerpg.domain.player.Player;
import com.idlerpg.service.gathering.GatheringService;

public final class StopActionCommand implements GameCommand {
    private final GatheringService gatheringService;
    private final Player player;

    public StopActionCommand(GatheringService gatheringService) {
        this(gatheringService, null);
    }

    public StopActionCommand(GatheringService gatheringService, Player player) {
        this.gatheringService = gatheringService;
        this.player = player;
    }

    @Override
    public void execute() {
        if (player == null) {
            gatheringService.stop();
            return;
        }
        gatheringService.stop(player);
    }
}
