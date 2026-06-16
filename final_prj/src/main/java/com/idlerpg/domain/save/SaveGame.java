package com.idlerpg.domain.save;

import java.util.Map;
import java.util.Set;

public record SaveGame(
        int level,
        int experience,
        int gold,
        int currentHp,
        int baseMaxHp,
        String currentRegionId,
        Set<String> unlockedRegionIds,
        Map<String, Integer> inventory,
        Map<String, Integer> skillLevels,
        Map<String, Integer> skillExperience,
        Map<String, String> equipment,
        Map<String, Integer> questProgress,
        Set<String> completedQuestIds,
        Set<String> claimedQuestIds,
        String activeSkillId,
        long lastSavedAtEpochSecond
) {
    public SaveGame {
        currentRegionId = currentRegionId == null || currentRegionId.isBlank() ? "sunlit_meadow" : currentRegionId;
        unlockedRegionIds = unlockedRegionIds == null ? Set.of(currentRegionId) : Set.copyOf(unlockedRegionIds);
        inventory = inventory == null ? Map.of() : Map.copyOf(inventory);
        skillLevels = skillLevels == null ? Map.of() : Map.copyOf(skillLevels);
        skillExperience = skillExperience == null ? Map.of() : Map.copyOf(skillExperience);
        equipment = equipment == null ? Map.of() : Map.copyOf(equipment);
        questProgress = questProgress == null ? Map.of() : Map.copyOf(questProgress);
        completedQuestIds = completedQuestIds == null ? Set.of() : Set.copyOf(completedQuestIds);
        claimedQuestIds = claimedQuestIds == null ? Set.of() : Set.copyOf(claimedQuestIds);
        activeSkillId = activeSkillId == null ? "" : activeSkillId;
    }
}
