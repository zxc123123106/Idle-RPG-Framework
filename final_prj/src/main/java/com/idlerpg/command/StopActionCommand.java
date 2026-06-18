package com.idlerpg.command;

import com.idlerpg.service.gathering.GatheringService;

public final class StopActionCommand implements GameCommand {
    private final GatheringService gatheringService;

    public StopActionCommand(GatheringService gatheringService) {
        this.gatheringService = gatheringService;
    }

    @Override
    public void execute() {
        gatheringService.stop();
    }
}
