package com.idlerpg.service.region;

import com.idlerpg.core.event.EventBus;
import com.idlerpg.core.event.RegionUnlockedEvent;
import com.idlerpg.core.registry.RegionRegistry;
import com.idlerpg.domain.player.Player;
import com.idlerpg.domain.region.RegionDefinition;

import java.time.Instant;
import java.util.List;

public final class RegionService {
    private final RegionRegistry regionRegistry;
    private final EventBus eventBus;

    public RegionService(RegionRegistry regionRegistry, EventBus eventBus) {
        this.regionRegistry = regionRegistry;
        this.eventBus = eventBus;
    }

    public RegionDefinition getCurrentRegion(Player player) {
        return regionRegistry.getRequired(player.getCurrentRegionId());
    }

    public List<RegionDefinition> getUnlockedRegions(Player player) {
        return regionRegistry.getAll().stream()
                .filter(region -> player.getUnlockedRegionIds().contains(region.id()))
                .toList();
    }

    public boolean switchRegion(Player player, String regionId) {
        if (!player.getUnlockedRegionIds().contains(regionId)) {
            return false;
        }
        player.setCurrentRegionId(regionId);
        return true;
    }

    public void unlockEligibleRegions(Player player) {
        for (RegionDefinition region : regionRegistry.getAll()) {
            if (player.getUnlockedRegionIds().contains(region.id())) {
                continue;
            }
            boolean levelMet = player.getLevel() >= region.requiredLevel();
            boolean questMet = region.requiredQuestId().isBlank()
                    || player.getCompletedQuestIds().contains(region.requiredQuestId())
                    || player.getClaimedQuestIds().contains(region.requiredQuestId());
            if (levelMet && questMet) {
                player.getUnlockedRegionIds().add(region.id());
                eventBus.publish(new RegionUnlockedEvent(region, Instant.now()));
            }
        }
    }
}
