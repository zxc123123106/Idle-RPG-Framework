package com.idlerpg.command;

import com.idlerpg.service.combat.CombatService;

public final class StopCombatCommand implements GameCommand {
    private final CombatService combatService;

    public StopCombatCommand(CombatService combatService) {
        this.combatService = combatService;
    }

    @Override
    public void execute() {
        combatService.stopCombat();
    }
}
