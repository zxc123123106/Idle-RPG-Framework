package com.idlerpg.domain.region;

import com.idlerpg.domain.common.Identifiable;

import java.util.List;

public record RegionDefinition(
        String id,
        String name,
        String description,
        String icon,
        int requiredLevel,
        String requiredQuestId,
        List<String> skillIds,
        List<String> enemyIds,
        List<String> shopItemIds,
        List<String> questIds
) implements Identifiable {
    public RegionDefinition {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Region id is required.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Region name is required.");
        }
        description = description == null ? "" : description;
        icon = icon == null || icon.isBlank() ? "◆" : icon;
        requiredQuestId = requiredQuestId == null ? "" : requiredQuestId;
        skillIds = skillIds == null ? List.of() : List.copyOf(skillIds);
        enemyIds = enemyIds == null ? List.of() : List.copyOf(enemyIds);
        shopItemIds = shopItemIds == null ? List.of() : List.copyOf(shopItemIds);
        questIds = questIds == null ? List.of() : List.copyOf(questIds);
    }
}
