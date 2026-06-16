package com.idlerpg.service.progression;

import com.idlerpg.core.event.EventBus;
import com.idlerpg.core.event.LevelUpEvent;
import com.idlerpg.domain.player.Player;

import java.time.Instant;

public final class ProgressionService {
    private final EventBus eventBus;

    public ProgressionService(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void addExperience(Player player, int amount) {
        player.addExperience(amount);
        while (player.getExperience() >= player.getExperienceToNextLevel()) {
            player.levelUp();
            eventBus.publish(new LevelUpEvent(player.getLevel(), Instant.now()));
        }
    }
}
